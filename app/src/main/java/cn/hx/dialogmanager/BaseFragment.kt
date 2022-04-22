package cn.hx.dialogmanager

import androidx.fragment.app.Fragment
import cn.hx.prioritydialog.FragmentDialogHost
import cn.hx.prioritydialog.FragmentDialogHostImpl

open class BaseFragment : Fragment(), FragmentDialogHost by FragmentDialogHostImpl() {

    val TAG = this::class.java.simpleName

    fun showAlertDialog(title: String? = null, message: String? = null, priority: Int = 0, onlyDismissByUser: Boolean = true, lockWindow: Boolean = false, uuid: String? = null) {
        val dialog = BaseAlertDialog.Builder()
                .title(title)
                .message(message)
                .positive("Confirm")
                .negative("Cancel")
                .create()
        dialog.priority = priority
        dialog.onlyDismissByUser = onlyDismissByUser
        dialog.lockWindow = lockWindow
        uuid?.let {
            dialog.uuid = it
        }
        showPriorityDialog(dialog)
    }
}