package cn.hx.dialogmanager

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class PriorityDialogImpl : PriorityDialog {

    private lateinit var dialogFragment: DialogFragment

    override var uuid: String? = null
    override var priority: Int = 0
    override var onlyDismissByUser: Boolean = true
    override var lockWindow: Boolean = false
    override var dismissByHighPriorityDialog: Boolean = false

    override fun initAsPriorityDialog(dialogFragment: DialogFragment) {
        this.dialogFragment = dialogFragment
        dialogFragment.savedStateRegistry.consumeRestoredStateForKey(KEY_DIALOG_STATE)?.run {
            uuid = getString(BASE_DIALOG_UUID)
            priority = getInt(BASE_DIALOG_PRIORITY, 0)
            onlyDismissByUser = getBoolean(BASE_DIALOG_ONLY_DISMISS_BY_USER, true)
            lockWindow = getBoolean(BASE_DIALOG_LOCK_WINDOW)
            dismissByHighPriorityDialog = getBoolean(BASE_DIALOG_DISMISS_BY_HIGH_PRIORITY_DIALOG)
        }
        dialogFragment.savedStateRegistry.registerSavedStateProvider(KEY_DIALOG_STATE) {
            Bundle().apply {
                putString(BASE_DIALOG_UUID, uuid)
                putInt(BASE_DIALOG_PRIORITY, priority)
                putBoolean(BASE_DIALOG_ONLY_DISMISS_BY_USER, onlyDismissByUser)
                putBoolean(BASE_DIALOG_LOCK_WINDOW, lockWindow)
                putBoolean(BASE_DIALOG_DISMISS_BY_HIGH_PRIORITY_DIALOG, dismissByHighPriorityDialog)
            }
        }
        dialogFragment.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    if (dialogFragment.isRemoving && !dialogFragment.isStateSaved && !dismissByHighPriorityDialog) {
                        (dialogFragment as? PriorityDialog)?.let {
                            (dialogFragment.parentFragment as? FragmentDialogHost)?.onDismiss(it)
                                    ?: (dialogFragment.activity as? ActivityDialogHost)?.onDismiss(it)
                        }
                    }
                }
            }
        })
    }

    companion object {
        const val KEY_DIALOG_STATE = "cn.hx.base.dialog.state"
        private const val BASE_DIALOG_UUID = "cn.hx.base.dialog.uuid"
        private const val BASE_DIALOG_PRIORITY = "cn.hx.base.dialog.priority"
        private const val BASE_DIALOG_ONLY_DISMISS_BY_USER = "cn.hx.base.dialog.onlyDismissByUser"
        private const val BASE_DIALOG_LOCK_WINDOW = "cn.hx.base.dialog.lockWindow"
        private const val BASE_DIALOG_DISMISS_BY_HIGH_PRIORITY_DIALOG = "cn.hx.base.dialog.dismissByHighPriorityDialog"
    }
}