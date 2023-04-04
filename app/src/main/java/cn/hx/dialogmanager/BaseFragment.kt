package cn.hx.dialogmanager

import androidx.fragment.app.Fragment
import cn.hx.prioritydialog.DialogHost
import cn.hx.prioritydialog.DialogHostImpl

open class BaseFragment : Fragment(), DialogHost by DialogHostImpl() {

    val TAG = this::class.java.simpleName

    fun showAlertDialog(title: String? = null, message: String? = null, priority: Int = 0, isAddToPendingWhenReplaceByOther: Boolean = true, lockWindow: Boolean = false, uuid: String? = null) {
        val dialog = BaseAlertDialog.Builder()
                .title(title)
                .message(message)
                .positive("Confirm")
                .negative("Cancel")
                .create()
        dialog.priorityConfig.priority = priority
        dialog.priorityConfig.isAddToPendingWhenReplaceByOther = isAddToPendingWhenReplaceByOther
        dialog.priorityConfig.isLockWindow = lockWindow
        uuid?.let {
            dialog.priorityConfig.uuid = it
        }
        showPriorityDialog(dialog)
    }
}