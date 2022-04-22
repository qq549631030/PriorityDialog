package cn.hx.prioritydialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;

import java.util.UUID;

public class FragmentDialogHostImpl extends AbsDialogHostImpl implements FragmentDialogHost {

    Fragment fragment;

    @Override
    public void initAsDialogHost(@NonNull Fragment fragment, @Nullable Bundle savedInstanceState) {
        if (!(fragment.requireActivity() instanceof DialogManager)) {
            throw new IllegalArgumentException("fragment's host activity must implements DialogManager");
        }
        this.fragment = fragment;
        if (savedInstanceState != null) {
            uuid = savedInstanceState.getString(BASE_DIALOG_HOST_UUID);
        }
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        init((DialogManager) fragment.requireActivity(), uuid, fragment.requireFragmentManager(), fragment.getChildFragmentManager());
        mDialogManager.registerDialogHost(uuid, this);
        fragment.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_START) {
                tryWarpPendingTransaction();
            }
            if (event == Lifecycle.Event.ON_DESTROY) {
                if (fragment.requireActivity().isFinishing() || (fragment.isRemoving() && !fragment.isStateSaved())) {
                    mDialogManager.unregisterDialogHost(uuid);
                    cleanAllPendingAction();
                }
            }
        });
    }

    @Override
    public boolean isReady() {
        if (!super.isReady()) {
            return false;
        }
        return fragment.isVisible();
    }
}
