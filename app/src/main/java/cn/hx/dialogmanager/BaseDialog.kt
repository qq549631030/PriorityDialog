package cn.hx.dialogmanager

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import cn.hx.prioritydialog.PriorityDialog
import cn.hx.prioritydialog.PriorityDialogImpl

open class BaseDialog : DialogFragment(), PriorityDialog by PriorityDialogImpl() {

    val TAG = this::class.java.simpleName + this.hashCode()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAsPriorityDialog(this)
    }
}