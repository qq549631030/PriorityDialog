package cn.hx.dialogmanager

interface DialogHost {

    fun showBaseDialog(baseDialog: BaseDialog): Boolean

    fun isWindowLocked(): Boolean

    fun onDismiss(baseDialog: BaseDialog)


    fun tryPendingAction(): Boolean
}