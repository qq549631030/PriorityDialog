package cn.hx.dialogmanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import cn.hx.prioritydialog.FragmentDialogHost
import cn.hx.prioritydialog.FragmentDialogHostImpl

open class BaseFragment : Fragment(), FragmentDialogHost by FragmentDialogHostImpl() {

    val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAsDialogHost(this)
    }

    protected fun showDialog(title: String, message: String, priority: Int = 0, onlyDismissByUser: Boolean = true, lockWindow: Boolean = false) {
        val dialog = BaseAlertDialog.Builder()
                .title(title)
                .message(message)
                .positive("Confirm")
                .negative("Cancel")
                .create()
        dialog.priority = priority
        dialog.onlyDismissByUser = onlyDismissByUser
        dialog.lockWindow = lockWindow
        showPriorityDialog(dialog)
    }
}