package cn.hx.prioritydialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;

import java.util.UUID;

public class FragmentDialogHostImpl extends AbsDialogHostImpl implements FragmentDialogHost {

    Fragment fragment;

    @Override
    public void initAsDialogHost(@NonNull Fragment fragment) {
        this.fragment = fragment;
        Bundle savedState = fragment.getSavedStateRegistry().consumeRestoredStateForKey(KEY_DIALOG_HOST_STATE);
        if (savedState != null) {
            uuid = savedState.getString(BASE_DIALOG_HOST_UUID);
        }
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        init(uuid, fragment.requireFragmentManager(), fragment.getChildFragmentManager());
        fragment.getSavedStateRegistry().registerSavedStateProvider(KEY_DIALOG_HOST_STATE, () -> {
            Bundle bundle = new Bundle();
            bundle.putString(BASE_DIALOG_HOST_UUID, uuid);
            return bundle;
        });
        fragment.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_START) {
                tryWarpPendingTransaction();
            }
            if (event == Lifecycle.Event.ON_DESTROY) {
                if (fragment.requireActivity().isFinishing() || (fragment.isRemoving() && !fragment.isStateSaved())) {
                    cleanAllPending();
                }
            }
        });
    }

    @Override
    public void tryPendingAction() {
        super.tryPendingAction();
        if (isWindowLockedByDialog()) {
            return;
        }
        if (fragment.getParentFragment() instanceof FragmentDialogHost) {
            ((FragmentDialogHost) fragment.getParentFragment()).tryPendingAction();
        } else if (fragment.requireActivity() instanceof ActivityDialogHost) {
            ((ActivityDialogHost) fragment.requireActivity()).tryPendingAction();
        }
    }
}
