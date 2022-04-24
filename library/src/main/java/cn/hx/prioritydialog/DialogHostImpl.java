package cn.hx.prioritydialog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DialogHostImpl implements DialogHost {

    private final PriorityDialogHost mPriorityDialogHost = new PriorityDialogHost();

    @Override
    public PriorityDialogHost getPriorityDialogHost() {
        return mPriorityDialogHost;
    }

    @NonNull
    @Override
    public String getUuid() {
        return mPriorityDialogHost.getUuid();
    }

    @NonNull
    @Override
    public WarpFragmentManager getWarpParentFragmentManager() {
        return mPriorityDialogHost.getWarpParentFragmentManager();
    }

    @NonNull
    @Override
    public WarpFragmentManager getWarpChildFragmentManager() {
        return mPriorityDialogHost.getWarpChildFragmentManager();
    }

    @Override
    public boolean showPriorityDialog(@NonNull PriorityDialog newDialog) {
        return mPriorityDialogHost.showPriorityDialog(newDialog);
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
        return mPriorityDialogHost.warpStartActivityForResult(intent, requestCode, options);
    }

    @Override
    public boolean warpFinish() {
        return mPriorityDialogHost.warpFinish();
    }
}
