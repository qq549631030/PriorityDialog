package cn.hx.prioritydialog

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.util.*

abstract class AbsDialogHostImpl : DialogHost {
    protected lateinit var uuid: String
    private lateinit var parentFragmentManager: FragmentManager
    protected lateinit var childFragmentManager: FragmentManager
    private lateinit var _warpParentFragmentManager: WarpFragmentManager
    private var init = false

    private var pendingShowDialog: PriorityDialog? = null
    private var pendingDismissDialog: PriorityDialog? = null

    //等待执行的Fragment切换
    var pendingTransaction: WarpFragmentTransaction?
        get() = pendingTransactionMap[uuid]
        set(value) {
            value?.let {
                pendingTransactionMap[uuid] = it
            } ?: pendingTransactionMap.remove(uuid)
        }

    //等待执行的Fragment因退
    var pendingPopBackStack: Bundle?
        get() = pendingPopBackStackMap[uuid]
        set(value) {
            value?.let {
                pendingPopBackStackMap[uuid] = it
            } ?: pendingPopBackStackMap.remove(uuid)
        }

    //等待显示对话框
    private val pendingDialogs: TreeMap<Int, Stack<PriorityDialog>>
        get() = pendingDialogMap[uuid] ?: TreeMap<Int, Stack<PriorityDialog>>().also {
            pendingDialogMap[uuid] = it
        }


    protected fun init(uuid: String, parentFragmentManager: FragmentManager, childFragmentManager: FragmentManager) {
        this.uuid = uuid
        this.parentFragmentManager = parentFragmentManager
        this.childFragmentManager = childFragmentManager
        this._warpParentFragmentManager = WarpFragmentManager(parentFragmentManager, this)
        pendingTransactionMap.remove(uuid)//重建后保存的Transaction是无效的
        init = true
    }

    override val warpParentFragmentManager: FragmentManager
        get() = _warpParentFragmentManager

    override fun showPriorityDialog(priorityDialog: PriorityDialog): Boolean {
        if (!init) {
            throw IllegalStateException("not init, Please call initAsDialogHost first")
        }
        if (childFragmentManager.isStateSaved || childFragmentManager.isDestroyed) {
            return false
        }
        val transaction = childFragmentManager.beginTransaction()
        currentDialog?.let { prev ->
            if (priorityDialog.priority < prev.priority) {//优先级比当前显示的小，加入等待队列
                addToPendingDialog(priorityDialog)
                return false
            } else {//优先级大于或者等于当前显示的，取代当前的显示
                if (prev.onlyDismissByUser) {//当前显示的对话框只能由用户关闭，加上标记加入等待队列
                    prev.dismissByHighPriorityDialog = true
                    addToPendingDialog(prev)
                }
                (prev as? DialogFragment)?.let {
                    transaction.remove(it)
                }
            }
        }
        priorityDialog.dismissByHighPriorityDialog = false
        pendingShowDialog = priorityDialog
        childFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                if (f == pendingShowDialog) {
                    pendingShowDialog = null
                    childFragmentManager.unregisterFragmentLifecycleCallbacks(this)
                }
            }
        }, false)
        (priorityDialog as? DialogFragment)?.let {
            it.show(transaction, PriorityDialog.BASE_DIALOG_TAG)
            return true
        }
        return false
    }

    //加入等待队列
    private fun addToPendingDialog(priorityDialog: PriorityDialog) {
        val stack = pendingDialogs[priorityDialog.priority] ?: run {
            val stack = Stack<PriorityDialog>()
            pendingDialogs[priorityDialog.priority] = stack
            stack
        }
        stack.push(priorityDialog)
    }

    //弹出最高优先级对话框
    private fun popPendingDialog(): PriorityDialog? {
        var priorityDialog: PriorityDialog? = null
        return pendingDialogs.lastEntry()?.let {
            if (it.value.isNotEmpty()) {
                priorityDialog = it.value.pop()
            }
            if (it.value.isEmpty()) {
                pendingDialogs.remove(it.key)
            }
            priorityDialog
        }
    }

    //当前显示的对话框
    override val currentDialog: PriorityDialog?
        get() {
            if (pendingShowDialog != null) {
                return pendingShowDialog
            }
            val priorityDialog = childFragmentManager.findFragmentByTag(PriorityDialog.BASE_DIALOG_TAG) as? PriorityDialog
            if (priorityDialog == null) {
                return priorityDialog
            }
            if (pendingDismissDialog != null && priorityDialog == pendingDismissDialog) {
                return null
            }
            return priorityDialog
        }

    //当前是否锁定页面
    override fun isWindowLockedByDialog(): Boolean {
        //current
        val locked = currentDialog?.lockWindow ?: false
        if (locked) {
            return true
        }
        //children
        return childFragmentManager.fragments.any {
            it is DialogHost && it.isVisible && it.isWindowLockedByDialog()
        }
    }

    override fun onDismiss(priorityDialog: PriorityDialog) {
        pendingDismissDialog = priorityDialog
        childFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                if (f == pendingDismissDialog) {
                    pendingDismissDialog = null
                    childFragmentManager.unregisterFragmentLifecycleCallbacks(this)
                }
            }
        }, false)
        //尝试显示等待队列的中最高优先级的对话框
        popPendingDialog()?.let {
            showPriorityDialog(it)
        }
        if (!isWindowLockedByDialog()) {//不再锁定页面
            tryPendingAction()
        }
    }

    @CallSuper
    override fun tryPendingAction() {
        if (isWindowLockedByDialog()) {
            return
        }
        if (parentFragmentManager.isStateSaved || parentFragmentManager.isDestroyed) {
            return
        }
        pendingTransaction?.let {
            if (it.tryPendingAction()) {
                pendingTransaction = null
            }
        }
        pendingPopBackStack?.let {
            if (_warpParentFragmentManager.tryPendingAction(it)) {
                pendingPopBackStack = null
            }
        }
    }

    @CallSuper
    protected open fun cleanAllPending() {
        pendingDialogMap.remove(uuid)
        pendingTransactionMap.remove(uuid)
        pendingPopBackStackMap.remove(uuid)
    }

    companion object {
        const val KEY_DIALOG_HOST_STATE = "cn.hx.base.dialogHost.state"
        const val BASE_DIALOG_HOST_UUID = "cn.hx.base.dialogHost.uuid"

        private val pendingDialogMap: MutableMap<String, TreeMap<Int, Stack<PriorityDialog>>> = mutableMapOf()
        private val pendingTransactionMap: MutableMap<String, WarpFragmentTransaction> = mutableMapOf()
        private val pendingPopBackStackMap: MutableMap<String, Bundle> = mutableMapOf()
    }
}