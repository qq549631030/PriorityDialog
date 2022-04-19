package cn.hx.dialogmanager

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.hx.prioritydialog.*

open class BaseActivity : AppCompatActivity(), DialogManager by DialogManagerImpl(), ActivityDialogHost by ActivityDialogHostImpl() {

    val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAsDialogManager(this)
        initAsDialogHost(this)
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        val ignoreHoldWindow = intent.getBooleanExtra(EXTRA_IGNORE_DIALOG_LOCK, false)
        if (!ignoreHoldWindow && warpStartActivityForResult(intent, requestCode, options)) {
            return
        }
        super.startActivityForResult(intent, requestCode, options)
    }

    override fun finish() {
        val ignoreHoldWindow = intent.getBooleanExtra(EXTRA_IGNORE_DIALOG_LOCK, false)
        if (!ignoreHoldWindow && warpFinish()) {
            return
        }
        super.finish()
    }

    fun showAlertDialog(title: String? = null, message: String, priority: Int = 0, onlyDismissByUser: Boolean = true, lockWindow: Boolean = false, uuid: String? = null) {
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

    override fun onCancel(priorityDialog: PriorityDialog) {
        if (priorityDialog.uuid == "custom_uuid") {
            //next
        }
    }

    override fun onDismiss(priorityDialog: PriorityDialog) {
        if (priorityDialog.uuid == "custom_uuid") {
            //next
        }
    }

    override fun onDialogEvent(priorityDialog: PriorityDialog, event: Any) {
        if (priorityDialog.uuid == "custom_uuid") {
            when (event) {
                is BaseAlertDialog.AlertDialogClickEvent -> {
                    if (event.which == DialogInterface.BUTTON_POSITIVE) {
                        //next
                    }
                }
                else -> {}
            }
        }
    }

    companion object {
        const val EXTRA_IGNORE_DIALOG_LOCK = "extra_ignore_dialog_lock"
    }
}