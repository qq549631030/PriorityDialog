package cn.hx.prioritydialog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;


public interface DialogHost {

    String BASE_DIALOG_HOST_UUID = "cn.hx.base.dialogHost.uuid";

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @NonNull
    PriorityDialogHostDelegate getPriorityDialogHostDelegate();

    @NonNull
    String getUuid();

    @NonNull
    WarpFragmentManager getWarpParentFragmentManager();

    @NonNull
    WarpFragmentManager getWarpChildFragmentManager();

    boolean showPriorityDialog(@NonNull PriorityDialog newDialog);

    boolean showPriorityDialog(@NonNull PriorityDialog newDialog, boolean allowStateLoss);

    void dismissCurrentDialog(boolean allowStateLoss);

    void dismissByUuid(@NonNull String uuid, boolean allowStateLoss);

    void onDialogEvent(@NonNull PriorityDialog priorityDialog, @NonNull Object event);

    void onCancel(@NonNull PriorityDialog priorityDialog);

    void onDismiss(@NonNull PriorityDialog priorityDialog);

    boolean warpStartActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options);

    boolean warpFinish();
}