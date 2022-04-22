package cn.hx.dialogmanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.hx.prioritydialog.ActivityDialogHost
import cn.hx.prioritydialog.ActivityDialogHostImpl
import cn.hx.prioritydialog.DialogManager
import cn.hx.prioritydialog.DialogManagerImpl

open class BaseActivity : AppCompatActivity(), DialogManager by DialogManagerImpl(), ActivityDialogHost by ActivityDialogHostImpl() {

    val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        initAsDialogManager(this, savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        onDialogManagerSaveInstanceState(outState)
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

    fun showAlertDialog(title: String? = null, message: String, priority: Int = 0, onlyDismissByUser: Boolean = true, lockWindow: Boolean = false, isSupportRecreate: Boolean = true, uuid: String? = null) {
        val dialog = createAlertDialog(title, message, priority, onlyDismissByUser, lockWindow, isSupportRecreate, uuid)
        showPriorityDialog(dialog)
    }

    fun createAlertDialog(title: String? = null, message: String, priority: Int = 0, onlyDismissByUser: Boolean = true, lockWindow: Boolean = false, isSupportRecreate: Boolean = true, uuid: String? = null): BaseAlertDialog {
        val dialog = BaseAlertDialog.Builder()
                .title(title)
                .message(message)
                .positive("Confirm")
                .negative("Cancel")
                .create()
        dialog.priority = priority
        dialog.onlyDismissByUser = onlyDismissByUser
        dialog.lockWindow = lockWindow
        dialog.isSupportRecreate = isSupportRecreate
        uuid?.let {
            dialog.uuid = it
        }
        return dialog
    }

    companion object {
        const val EXTRA_IGNORE_DIALOG_LOCK = "extra_ignore_dialog_lock"
    }
}