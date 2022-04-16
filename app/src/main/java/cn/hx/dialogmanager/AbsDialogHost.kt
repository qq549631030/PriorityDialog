package cn.hx.dialogmanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentOnAttachListener
import java.util.*

abstract class AbsDialogHost : DialogHost {
    private lateinit var uuid: String
    private lateinit var fragmentManager: FragmentManager
    private var inited = false

    private var pendingShowDialog: BaseDialog? = null
    private var pendingDismissDialog: BaseDialog? = null

    //等待显示对话框
    private val pendingDialogs: TreeMap<Int, Stack<BaseDialog>>
        get() = pendingDialogMap[uuid] ?: TreeMap<Int, Stack<BaseDialog>>().also {
            pendingDialogMap[uuid] = it
        }

    //等待进行的动作
    protected val pendingActions: Bundle
        get() = pendingActionMap[uuid] ?: Bundle().also { pendingActionMap[uuid] = it }

    protected fun init(uuid: String, fragmentManager: FragmentManager) {
        this.uuid = uuid
        this.fragmentManager = fragmentManager
        inited = true
    }

    override fun showBaseDialog(baseDialog: BaseDialog): Boolean {
        if (!inited) {
            throw IllegalStateException("not init, please call initDialogHost first")
        }
        if (fragmentManager.isStateSaved || fragmentManager.isDestroyed) {
            return false
        }
        val transaction = fragmentManager.beginTransaction()
        currentDialog?.let {
            if (baseDialog.priority < it.priority) {//优先级比当前显示的小，加入等待队列
                addToPendingDialog(baseDialog)
                return false
            } else {//优先级大于或者等于当前显示的，取代当前的显示
                if (it.onlyDismissByUser) {//当前显示的对话框只能由用户关闭，加上标记加入等待队列
                    it.dismissByHighPriorityDialog = true
                    addToPendingDialog(it)
                }
                transaction.remove(it)
            }
        }
        baseDialog.dismissByHighPriorityDialog = false
        pendingShowDialog = baseDialog
        fragmentManager.addFragmentOnAttachListener(object : FragmentOnAttachListener {
            override fun onAttachFragment(fragmentManager: FragmentManager, fragment: Fragment) {
                if (fragment == pendingShowDialog) {
                    pendingShowDialog = null
                    fragmentManager.removeFragmentOnAttachListener(this)
                }
            }
        })
        baseDialog.show(transaction, BaseDialog.BASE_DIALOG_TAG)
        return true
    }

    //加入等待队列
    private fun addToPendingDialog(baseDialog: BaseDialog) {
        val stack = pendingDialogs[baseDialog.priority] ?: run {
            val stack = Stack<BaseDialog>()
            pendingDialogs[baseDialog.priority] = stack
            stack
        }
        stack.push(baseDialog)
    }

    //弹出最高优先级对话框
    private fun popPendingDialog(): BaseDialog? {
        var baseDialog: BaseDialog? = null
        return pendingDialogs.lastEntry()?.let {
            if (it.value.isNotEmpty()) {
                baseDialog = it.value.pop()
            }
            if (it.value.isEmpty()) {
                pendingDialogs.remove(it.key)
            }
            baseDialog
        }
    }

    //当前显示的对话框
    private val currentDialog: BaseDialog?
        get() {
            if (pendingShowDialog != null) {
                return pendingShowDialog
            }
            val baseDialog = fragmentManager.findFragmentByTag(BaseDialog.BASE_DIALOG_TAG) as? BaseDialog
            if (baseDialog == null) {
                return baseDialog
            }
            if (pendingDismissDialog != null && baseDialog == pendingDismissDialog) {
                return null
            }
            return baseDialog
        }

    //当前是否锁定页面
    override fun isWindowLocked(): Boolean {
        //current
        val locked = currentDialog?.lockWindow ?: false
        if (locked) {
            return true
        }
        //children
        return fragmentManager.fragments.any {
            it is DialogHost && it.isVisible && it.isWindowLocked()
        }
    }

    override fun onDismiss(baseDialog: BaseDialog) {
        pendingDismissDialog = baseDialog
        fragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
                if (f == pendingDismissDialog) {
                    pendingDismissDialog = null
                    fragmentManager.unregisterFragmentLifecycleCallbacks(this)
                }
            }
        }, false)
        //尝试显示等待队列的中最高优先级的对话框
        popPendingDialog()?.let {
            showBaseDialog(it)
        }
        if (!isWindowLocked()) {//不再锁定页面
            tryPendingAction()
        }
    }

    protected fun cleanAllPending() {
        pendingDialogMap.remove(uuid)
        pendingActionMap.remove(uuid)
    }

    companion object {
        const val KEY_DIALOG_HOST_STATE = "cn.hx.base.dialogHost.state"
        const val BASE_DIALOG_HOST_UUID = "cn.hx.base.dialogHost.uuid"

        val pendingDialogMap: MutableMap<String, TreeMap<Int, Stack<BaseDialog>>> = mutableMapOf()
        val pendingActionMap: MutableMap<String, Bundle> = mutableMapOf()
    }
}