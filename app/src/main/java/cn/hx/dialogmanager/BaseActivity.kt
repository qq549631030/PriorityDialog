package cn.hx.dialogmanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.hx.prioritydialog.ActivityDialogHost
import cn.hx.prioritydialog.ActivityDialogHostImpl

open class BaseActivity : AppCompatActivity(), ActivityDialogHost by ActivityDialogHostImpl() {

    val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDialogHost(this)
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

    companion object {
        const val EXTRA_IGNORE_DIALOG_LOCK = "extra_ignore_dialog_lock"
    }
}