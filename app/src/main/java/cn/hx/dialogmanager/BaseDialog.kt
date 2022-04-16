package cn.hx.dialogmanager

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

open class BaseDialog : DialogFragment() {

    //唯一标识
    var uuid: String? = null

    //优先级，值越大优先级越高
    var priority: Int = 0

    //对话框是否只有用户才能真正关闭
    var onlyDismissByUser: Boolean = true

    //是否锁定窗口，若为true则弹框显示时只能停留在当前页面，无法关闭无法跳走
    var lockWindow: Boolean = false

    //被高优先级对话框关闭
    var dismissByHighPriorityDialog: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.run {
            uuid = getString(BASE_DIALOG_UUID)
            priority = getInt(PRIORITY, 0)
            onlyDismissByUser = getBoolean(ONLY_DISMISS_BY_USER, true)
            lockWindow = getBoolean(LOCK_WINDOW, false)
            dismissByHighPriorityDialog = getBoolean(DISMISS_BY_HIGH_PRIORITY_DIALOG, false)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        //非正常关闭，Activity销毁重建后dialog也会重建
        if (parentFragmentManager.isDestroyed || parentFragmentManager.isStateSaved) {
            return
        }
        if (!dismissByHighPriorityDialog) {//正常关闭
            (parentFragment as? DialogHost)?.onDismiss(this)
                    ?: (activity as? DialogHost)?.onDismiss(this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(BASE_DIALOG_UUID, uuid)
        outState.putInt(PRIORITY, priority)
        outState.putBoolean(ONLY_DISMISS_BY_USER, onlyDismissByUser)
        outState.putBoolean(LOCK_WINDOW, lockWindow)
        outState.putBoolean(DISMISS_BY_HIGH_PRIORITY_DIALOG, dismissByHighPriorityDialog)
    }

    companion object {
        const val BASE_DIALOG_TAG = "cn.hx.base.dialog.tag"
        private const val BASE_DIALOG_UUID = "cn.hx.base.dialog.uuid"
        private const val PRIORITY = "cn.hx.base.dialog.priority"
        private const val ONLY_DISMISS_BY_USER = "cn.hx.base.dialog.onlyDismissByUser"
        private const val LOCK_WINDOW = "cn.hx.base.dialog.lockWindow"
        private const val DISMISS_BY_HIGH_PRIORITY_DIALOG = "cn.hx.base.dialog.dismissByHighPriorityDialog"
    }
}