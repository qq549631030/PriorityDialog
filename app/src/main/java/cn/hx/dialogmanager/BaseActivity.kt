package cn.hx.dialogmanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.run {
            restoreBaseDialogBundle(this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveBaseDialogBundle(outState)
    }

    @Suppress("DEPRECATION")
    override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        if (warpStartActivityForResult(intent, requestCode, options)) {
            return
        }
        super.startActivityForResult(intent, requestCode, options)
    }

    override fun finish() {
        if (warpFinish()) {
            return
        }
        super.finish()
    }
}