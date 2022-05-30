package cn.hx.prioritydialog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DialogHostImpl implements DialogHost {

    private final PriorityDialogHostDelegate mPriorityDialogHostDelegate = new PriorityDialogHostDelegate();

    @NonNull
    @Override
    public PriorityDialogHostDelegate getPriorityDialogHostDelegate() {
        return mPriorityDialogHostDelegate;
    }

    @NonNull
    @Override
    public String getUuid() {
        return mPriorityDialogHostDelegate.getUuid();
    }

    @NonNull
    @Override
    public WarpFragmentManager getWarpParentFragmentManager() {
        return mPriorityDialogHostDelegate.getWarpParentFragmentManager();
    }

    @NonNull
    @Override
    public WarpFragmentManager getWarpChildFragmentManager() {
        return mPriorityDialogHostDelegate.getWarpChildFragmentManager();
    }

    @Override
    public boolean showPriorityDialog(@NonNull PriorityDialog newDialog) {
        return showPriorityDialog(newDialog, false);
    }

    @Override
    public boolean showPriorityDialog(@NonNull PriorityDialog newDialog, boolean allowStateLoss) {
        return mPriorityDialogHostDelegate.showPriorityDialog(newDialog, allowStateLoss);
    }

    @Override
    public void dismissCurrentDialog(boolean allowStateLoss) {
        mPriorityDialogHostDelegate.mDialogManager.dismissCurrentDialog(allowStateLoss);
    }

    @Override
    public void dismissByUuid(@NonNull String uuid, boolean allowStateLoss) {
        mPriorityDialogHostDelegate.mDialogManager.dismissDialog(mPriorityDialogHostDelegate.getUuid(), uuid, allowStateLoss);
    }

    @Override
    public void onDialogEvent(@NonNull PriorityDialog priorityDialog, @NonNull Object event) {

    }

    @Override
    public void onCancel(@NonNull PriorityDialog priorityDialog) {

    }

    @Override
    public void onDismiss(@NonNull PriorityDialog priorityDialog) {

    }

    @Override
    public boolean warpStartActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
        return mPriorityDialogHostDelegate.warpStartActivityForResult(intent, requestCode, options);
    }

    @Override
    public boolean warpFinish() {
        return mPriorityDialogHostDelegate.warpFinish();
    }
}
