package cn.hx.dialogmanager

import androidx.fragment.app.DialogFragment
import cn.hx.prioritydialog.PriorityDialog
import cn.hx.prioritydialog.PriorityDialogImpl

open class BaseDialog : DialogFragment(), PriorityDialog by PriorityDialogImpl() {

    val TAG = this::class.java.simpleName + this.hashCode()
}