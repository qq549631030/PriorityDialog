package cn.hx.dialogmanager

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.util.*

class ActivityDialogHostImpl : AbsDialogHost(), ActivityDialogHost {

    private lateinit var hostActivity: FragmentActivity

    private var pendingIntent: Intent?
        get() = pendingActions.getParcelable(BASE_PENDING_INTENT)
        set(value) {
            pendingActions.putParcelable(BASE_PENDING_INTENT, value)
        }
    private var pendingRequestCode: Int
        get() = pendingActions.getInt(BASE_PENDING_REQUEST_CODE, -1)
        set(value) {
            pendingActions.putInt(BASE_PENDING_REQUEST_CODE, value)
        }
    private var pendingOptions: Bundle?
        get() = pendingActions.getBundle(BASE_PENDING_OPTIONS)
        set(value) {
            pendingActions.putBundle(BASE_PENDING_OPTIONS, value)
        }

    private var pendingFinish: Boolean
        get() = pendingActions.getBoolean(BASE_PENDING_FINISH)
        set(value) {
            pendingActions.putBoolean(BASE_PENDING_FINISH, value)
        }

    override fun initDialogHost(activity: FragmentActivity) {
        hostActivity = activity
        val uuid = activity.savedStateRegistry.consumeRestoredStateForKey(KEY_DIALOG_HOST_STATE)?.getString(BASE_DIALOG_HOST_UUID)
                ?: UUID.randomUUID().toString()
        init(uuid, activity.supportFragmentManager)
        activity.savedStateRegistry.registerSavedStateProvider(KEY_DIALOG_HOST_STATE) {
            Bundle().apply {
                putString(BASE_DIALOG_HOST_UUID, uuid)
            }
        }
        activity.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    if (activity.isFinishing) {
                        cleanAllPending()
                    }
                }
            }
        })
    }

    @Suppress("DEPRECATION")
    override fun tryPendingAction(): Boolean {
        if (isWindowLocked()) {
            return false
        }
        var handle = false
        pendingIntent?.let {
            hostActivity.startActivityForResult(it, pendingRequestCode, pendingOptions)
            pendingIntent = null
            pendingRequestCode = -1
            pendingOptions = null
            handle = true
        }
        if (pendingFinish) {
            hostActivity.finish()
            pendingFinish = false
            handle = true
        }
        return handle
    }

    override fun warpStartActivityForResult(intent: Intent, requestCode: Int, options: Bundle?): Boolean {
        if (isWindowLocked()) {
            pendingIntent = intent
            pendingRequestCode = requestCode
            pendingOptions = options
            return true
        }
        return false
    }

    override fun warpFinish(): Boolean {
        if (isWindowLocked()) {
            pendingFinish = true
            return true
        }
        return false
    }

    companion object {
        private const val BASE_PENDING_INTENT = "cn.hx.base.activity.pendingIntent"
        private const val BASE_PENDING_REQUEST_CODE = "cn.hx.base.activity.pendingRequestCode"
        private const val BASE_PENDING_OPTIONS = "cn.hx.base.activity.pendingOptions"
        private const val BASE_PENDING_FINISH = "cn.hx.base.activity.pendingFinish"
    }
}