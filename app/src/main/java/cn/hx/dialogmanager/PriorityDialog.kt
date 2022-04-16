package cn.hx.dialogmanager

import androidx.fragment.app.DialogFragment

interface PriorityDialog {
    //唯一标识
    var uuid: String?

    //优先级，值越大优先级越高
    var priority: Int

    //对话框是否只有用户才能真正关闭
    var onlyDismissByUser: Boolean

    //是否锁定窗口，若为true则弹框显示时只能停留在当前页面，无法关闭无法跳走
    var lockWindow: Boolean

    //被高优先级对话框关闭
    var dismissByHighPriorityDialog: Boolean

    fun initAsPriorityDialog(dialogFragment: DialogFragment)

    companion object {
        const val BASE_DIALOG_TAG = "cn.hx.base.dialog.tag"
    }
}