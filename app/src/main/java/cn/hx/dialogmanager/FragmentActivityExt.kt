package cn.hx.dialogmanager

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

val FragmentActivity.baseDialogBundle: Bundle by lazy { Bundle() }

var FragmentActivity.pendingIntent: Intent?
    get() = baseDialogBundle.getParcelable(BASE_PENDING_INTENT) as? Intent
    set(value) {
        baseDialogBundle.putParcelable(BASE_PENDING_INTENT, value)
    }
var FragmentActivity.pendingRequestCode: Int
    get() = baseDialogBundle.getInt(BASE_PENDING_REQUEST_CODE, -1)
    set(value) {
        baseDialogBundle.putInt(BASE_PENDING_REQUEST_CODE, value)
    }
var FragmentActivity.pendingOptions: Bundle?
    get() = baseDialogBundle.getBundle(BASE_PENDING_OPTIONS)
    set(value) {
        baseDialogBundle.putBundle(BASE_PENDING_OPTIONS, value)
    }

var FragmentActivity.pendingFinish: Boolean
    get() = baseDialogBundle.getBoolean(BASE_PENDING_FINISH, false)
    set(value) {
        baseDialogBundle.putBoolean(BASE_PENDING_FINISH, value)
    }

val FragmentActivity.isDialogHoldWindow: Boolean
    get() {
        if (supportFragmentManager.lockedWindow()) {
            return true
        }
        return supportFragmentManager.fragments.any {
            it.lockedWindow()
        }
    }


fun FragmentActivity.warpStartActivityForResult(
    intent: Intent,
    requestCode: Int,
    options: Bundle?
): Boolean {
    if (isDialogHoldWindow) {
        val ignoreHoldWindow = intent.getBooleanExtra(EXTRA_IGNORE_DIALOG_LOCK, false)
        if (!ignoreHoldWindow) {
            pendingIntent = intent
            pendingRequestCode = requestCode
            pendingOptions = options
            return true
        }
    }
    return false
}

fun FragmentActivity.warpFinish(): Boolean {
    if (isDialogHoldWindow) {
        val ignoreHoldWindow = intent.getBooleanExtra(EXTRA_IGNORE_DIALOG_LOCK, false)
        if (!ignoreHoldWindow) {
            pendingFinish = true
            return true
        }
    }
    return false
}

fun FragmentActivity.tryPendingAction() {
    pendingIntent?.run {
        startActivityForResult(this, pendingRequestCode, pendingOptions)
        pendingIntent = null
        pendingRequestCode = -1
        pendingOptions = null
    }
    if (pendingFinish) {
        finish()
        pendingFinish = false
    }
}

fun FragmentActivity.saveBaseDialogBundle(outState: Bundle) {
    outState.putAll(baseDialogBundle)
}

fun FragmentActivity.restoreBaseDialogBundle(savedInstanceState: Bundle) {
    baseDialogBundle.putParcelable(
        BASE_PENDING_INTENT,
        savedInstanceState.getParcelable(BASE_PENDING_INTENT)
    )
    baseDialogBundle.putInt(
        BASE_PENDING_REQUEST_CODE,
        savedInstanceState.getInt(BASE_PENDING_REQUEST_CODE, -1)
    )
    baseDialogBundle.putBundle(
        BASE_PENDING_OPTIONS,
        savedInstanceState.getBundle(BASE_PENDING_OPTIONS)
    )
    baseDialogBundle.putBoolean(
        BASE_PENDING_FINISH,
        savedInstanceState.getBoolean(BASE_PENDING_FINISH, false)
    )
}

const val BASE_PENDING_INTENT = "base_pending_intent"
const val BASE_PENDING_REQUEST_CODE = "base_pending_request_code"
const val BASE_PENDING_OPTIONS = "base_pending_options"
const val BASE_PENDING_FINISH = "base_pending_finish"
const val EXTRA_IGNORE_DIALOG_LOCK = "extra_ignore_dialog_lock"