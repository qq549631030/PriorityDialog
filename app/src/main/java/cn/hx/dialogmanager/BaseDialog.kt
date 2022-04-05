package cn.hx.dialogmanager

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager

open class BaseDialog : AppCompatDialogFragment() {

    //优先级，值越大优先级越高
    var priority: Int = 0

    //对话框是否只有用户才能真正关闭
    var onlyDismissByUser: Boolean = true

    private var baseDismissed = false
    private var baseShownByMe = false

    //被高优先级对话框关闭
    private var dismissByHighPriorityDialog: Boolean = false


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (!baseShownByMe) {
            baseDismissed = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.run {
            priority = getInt(PRIORITY, 0)
            onlyDismissByUser = getBoolean(ONLY_DISMISS_BY_USER, true)
            dismissByHighPriorityDialog = getBoolean(DISMISS_BY_HIGH_PRIORITY_DIALOG, false)
        }
    }

    fun showBaseDialog(fragmentManager: FragmentManager): Boolean {
        if (fragmentManager.isDestroyed || fragmentManager.isStateSaved) {
            return false
        }
        baseDismissed = false
        baseShownByMe = true
        val transaction = fragmentManager.beginTransaction()
        val currentShowingDialog = fragmentManager.currentDialog
        currentShowingDialog?.let {
            if (priority < it.priority) {//优先级比当前显示的小，加入等待队列
                fragmentManager.addPendingDialog(this)
                return false
            } else {//优先级大于或者等于当前显示的，取代当前的显示
                if (it.onlyDismissByUser) {//当前显示的对话框只能由用户关闭，加上标记加入等待队列
                    it.dismissByHighPriorityDialog = true
                    fragmentManager.addPendingDialog(it)
                }
                transaction.remove(it)
            }
        }
        show(transaction, BASE_DIALOG_TAG)
        dismissByHighPriorityDialog = false
        return true
    }

    fun isShowing(): Boolean {
        return !baseDismissed
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        baseDismissed = true
        baseShownByMe = false
        //非正常关闭，Activity销毁重建后dialog也会重建
        if (parentFragmentManager.isDestroyed || parentFragmentManager.isStateSaved) {
            return
        }
        if (!dismissByHighPriorityDialog) {//正常关闭后尝试显示等待队列的中最高优先级的对话框
            parentFragmentManager.popPendingDialog()?.showBaseDialog(parentFragmentManager)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PRIORITY, priority)
        outState.putBoolean(ONLY_DISMISS_BY_USER, onlyDismissByUser)
        outState.putBoolean(DISMISS_BY_HIGH_PRIORITY_DIALOG, dismissByHighPriorityDialog)
    }

    override fun onDetach() {
        super.onDetach()
        if (!baseShownByMe && !baseDismissed) {
            baseDismissed = true
        }
    }

    companion object {
        const val BASE_DIALOG_TAG = "baseDialog:tag"
        private const val PRIORITY = "baseDialog:priority"
        private const val ONLY_DISMISS_BY_USER = "baseDialog:only_dismiss_by_user"
        private const val DISMISS_BY_HIGH_PRIORITY_DIALOG =
            "baseDialog:dismiss_by_high_priority_dialog"
    }
}