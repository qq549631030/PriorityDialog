package cn.hx.prioritydialog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayDeque;
import java.util.UUID;

public class PriorityDialogHost {

    PriorityDialogManager mDialogManager;
    private String mUuid;
    FragmentManager parentFragmentManager;
    FragmentManager childFragmentManager;
    WarpFragmentManager mWarpParentFragmentManager;
    WarpFragmentManager mWarpChildFragmentManager;

    @NonNull
    String getUuid() {
        if (mUuid == null) {
            mUuid = UUID.randomUUID().toString();
        }
        return mUuid;
    }

    void initAsDialogHost(@NonNull PriorityDialogManager dialogManager, @NonNull FragmentManager parentFragmentManager, @NonNull FragmentManager childFragmentManager, @Nullable Bundle savedInstanceState) {
        this.mDialogManager = dialogManager;
        this.parentFragmentManager = parentFragmentManager;
        this.childFragmentManager = childFragmentManager;
        if (savedInstanceState != null) {
            mUuid = savedInstanceState.getString(DialogHost.BASE_DIALOG_HOST_UUID);
        }
        this.mWarpParentFragmentManager = new WarpFragmentManager(parentFragmentManager, dialogManager, getUuid(), false);
        this.mWarpChildFragmentManager = new WarpFragmentManager(childFragmentManager, dialogManager, getUuid(), true);
    }

    boolean showPriorityDialog(@NonNull PriorityDialog newDialog) {
        if (!mDialogManager.isReady(mUuid)) {
            return false;
        }
        newDialog.setHostUuid(mUuid);
        PriorityDialog currentDialog = mDialogManager.getCurrentDialog();
        if (currentDialog != null) {
            PriorityStrategy priorityStrategy = mDialogManager.getPriorityStrategy();
            if (priorityStrategy.canNewShow(currentDialog, newDialog)) {
                if (priorityStrategy.shouldPreAddToPendingWhenNewShow(currentDialog, newDialog)) {
                    currentDialog.setDismissByHighPriorityDialog(true);
                    mDialogManager.addToPendingDialog(currentDialog);
                }
                if (currentDialog instanceof DialogFragment) {
                    DialogFragment current = (DialogFragment) currentDialog;
                    FragmentManager fm = current.getFragmentManager();
                    if (fm != null && !fm.isStateSaved() && !fm.isDestroyed()) {
                        fm.beginTransaction().remove(current).commit();
                    }
                }
            } else {
                if (priorityStrategy.shouldNewAddToPendingWhenCanNotShow(currentDialog, newDialog)) {
                    mDialogManager.addToPendingDialog(newDialog);
                }
                return false;
            }
        }
        if (newDialog instanceof DialogFragment) {
            newDialog.setDismissByHighPriorityDialog(false);
            mDialogManager.setPendingShowDialog(newDialog);
            FragmentTransaction transaction = childFragmentManager.beginTransaction();
            ((DialogFragment) newDialog).show(transaction, PriorityDialog.BASE_DIALOG_TAG);
            childFragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
                    if (f.equals(mDialogManager.getPendingShowDialog())) {
                        mDialogManager.setPendingShowDialog(null);
                        childFragmentManager.unregisterFragmentLifecycleCallbacks(this);
                    }
                }
            }, false);
            return true;
        }
        return false;
    }

    @NonNull
    WarpFragmentManager getWarpParentFragmentManager() {
        return mWarpParentFragmentManager;
    }

    @NonNull
    WarpFragmentManager getWarpChildFragmentManager() {
        return mWarpChildFragmentManager;
    }

    boolean warpStartActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
        if (mDialogManager.isWindowLockedByDialog()) {
            getPendingActions().addLast(new ActivityAction(intent, requestCode, options));
            return true;
        }
        return false;
    }

    boolean warpFinish() {
        if (mDialogManager.isWindowLockedByDialog()) {
            getPendingActions().add(new ActivityAction(ActivityAction.TYPE_FINISH_ACTIVITY));
            return true;
        }
        return false;
    }

    @NonNull
    private ArrayDeque<ActivityAction> getPendingActions() {
        return mDialogManager.getPendingActivityActions(getUuid());
    }
}