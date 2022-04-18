package cn.hx.prioritydialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;


interface DialogHost {

    @NonNull
    FragmentManager getWarpParentFragmentManager();

    boolean showPriorityDialog(@NonNull PriorityDialog newDialog);

    @Nullable
    PriorityDialog getCurrentDialog();

    boolean isWindowLockedByDialog();

    void onDismiss(@NonNull PriorityDialog priorityDialog);

    void setPendingTransaction(@NonNull WarpBackStackRecord pendingTransaction);

    void setPendingPopBackStack(@NonNull Bundle pendingPopBackStack);

    void tryPendingAction();
}