package cn.hx.prioritydialog

import androidx.fragment.app.FragmentManager

interface DialogHost {

    val warpParentFragmentManager: FragmentManager

    fun showPriorityDialog(priorityDialog: PriorityDialog): Boolean

    fun isWindowLockedByDialog(): Boolean

    fun onDismiss(priorityDialog: PriorityDialog)

    fun tryPendingAction()
}