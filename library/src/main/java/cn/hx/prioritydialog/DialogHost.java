package cn.hx.prioritydialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.FragmentManager;


interface DialogHost {

    @NonNull
    FragmentManager getWarpParentFragmentManager();

    @NonNull
    FragmentManager getWarpChildFragmentManager();

    @NonNull
    DialogManager getDialogManager();

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    boolean isReady();

    boolean showPriorityDialog(@NonNull PriorityDialog newDialog);

    void onDialogEvent(@NonNull PriorityDialog priorityDialog, @NonNull Object event);

    void onCancel(@NonNull PriorityDialog priorityDialog);

    void dismissCurrentDialog();

    void onDismiss(@NonNull PriorityDialog priorityDialog);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setPendingTransaction(@NonNull WarpBackStackRecord pendingTransaction, boolean isChildFragmentManager);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setPendingPopBackStack(@NonNull Bundle pendingPopBackStack, boolean isChildFragmentManager);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void tryPendingAction();
}