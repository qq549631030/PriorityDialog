package cn.hx.dialogmanager

import android.os.Bundle
import androidx.fragment.app.DialogFragment

open class BaseDialog : DialogFragment(), PriorityDialog by PriorityDialogImpl() {

    val TAG = this::class.simpleName + this.hashCode()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAsPriorityDialog(this)
    }
}