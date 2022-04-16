package cn.hx.dialogmanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.util.*

class FragmentDialogHostImpl : AbsDialogHost(), FragmentDialogHost {
    private lateinit var hostFragment: Fragment
    private var fragmentSavedState = false

    override fun initDialogHost(fragment: Fragment) {
        hostFragment = fragment
        val uuid = fragment.savedStateRegistry.consumeRestoredStateForKey(KEY_DIALOG_HOST_STATE)?.getString(BASE_DIALOG_HOST_UUID)
                ?: UUID.randomUUID().toString()
        init(uuid, fragment.childFragmentManager)
        fragment.savedStateRegistry.registerSavedStateProvider(KEY_DIALOG_HOST_STATE) {
            fragmentSavedState = true
            Bundle().apply {
                putString(BASE_DIALOG_HOST_UUID, uuid)
            }
        }
        fragment.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    if (fragment.requireActivity().isFinishing || (fragment.isRemoving && !fragmentSavedState)) {
                        cleanAllPending()
                    }
                }
            }
        })
    }

    override fun tryPendingAction(): Boolean {
        if (isWindowLockedByDialog()) {
            return false
        }
        return (hostFragment.requireActivity() as? DialogHost)?.tryPendingAction() ?: false
    }
}