package cn.hx.dialogmanager

interface DialogHost {

    fun showPriorityDialog(priorityDialog: PriorityDialog): Boolean

    fun isWindowLockedByDialog(): Boolean

    fun onDismiss(priorityDialog: PriorityDialog)

    fun tryPendingAction(): Boolean
}