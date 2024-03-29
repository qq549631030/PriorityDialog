package cn.hx.dialogmanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.hx.prioritydialog.DialogHost
import cn.hx.prioritydialog.DialogHostImpl
import cn.hx.prioritydialog.DialogManager
import cn.hx.prioritydialog.DialogManagerImpl

open class BaseActivity : AppCompatActivity(), DialogManager by DialogManagerImpl(), DialogHost by DialogHostImpl() {

    val TAG = this::class.java.simpleName

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

    fun showAlertDialog(title: String? = null, message: String, priority: Int = 0, isAddToPendingWhenReplaceByOther: Boolean = true, lockWindow: Boolean = false, isSupportRecreate: Boolean = true, uuid: String? = null, isCanBeReplace: Boolean = true) {
        val dialog = createAlertDialog(title, message, priority, isAddToPendingWhenReplaceByOther, lockWindow, isSupportRecreate, uuid, isCanBeReplace)
        showPriorityDialog(dialog)
    }

    fun createAlertDialog(title: String? = null, message: String, priority: Int = 0, isAddToPendingWhenReplaceByOther: Boolean = true, lockWindow: Boolean = false, isSupportRecreate: Boolean = true, uuid: String? = null, isCanBeReplace: Boolean = true): BaseAlertDialog {
        val dialog = BaseAlertDialog.Builder()
                .title(title)
                .message(message)
                .positive("Confirm")
                .negative("Cancel")
                .create()
        dialog.priorityConfig.priority = priority
        dialog.priorityConfig.isAddToPendingWhenReplaceByOther = isAddToPendingWhenReplaceByOther
        dialog.priorityConfig.isLockWindow = lockWindow
        dialog.priorityConfig.isSupportRecreate = isSupportRecreate
        dialog.priorityConfig.isCanBeReplace = isCanBeReplace
        uuid?.let {
            dialog.priorityConfig.uuid = it
        }
        return dialog
    }

    companion object {
        const val EXTRA_IGNORE_DIALOG_LOCK = "extra_ignore_dialog_lock"
    }
}