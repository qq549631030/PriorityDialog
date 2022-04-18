package cn.hx.prioritydialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.FragmentManager;


interface DialogHost {

    @NonNull
    FragmentManager getWarpParentFragmentManager();

    @NonNull
    FragmentManager getWarpChildFragmentManager();

    boolean showPriorityDialog(@NonNull PriorityDialog newDialog);

    @Nullable
    PriorityDialog getCurrentDialog();

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    boolean isWindowLockedByDialog();

    void onDialogEvent(@NonNull PriorityDialog priorityDialog, @NonNull Object event);

    void onCancel(@NonNull PriorityDialog priorityDialog);

    void onDismiss(@NonNull PriorityDialog priorityDialog);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setPendingTransaction(@NonNull WarpBackStackRecord pendingTransaction, boolean isChildFragmentManager);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setPendingPopBackStack(@NonNull Bundle pendingPopBackStack, boolean isChildFragmentManager);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void tryPendingAction();
}