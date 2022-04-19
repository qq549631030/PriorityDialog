package cn.hx.prioritydialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.FragmentActivity;

public interface DialogManager {

    void initAsDialogManager(@NonNull FragmentActivity activity);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void registerDialogHost(@NonNull String uuid, @NonNull DialogHost dialogHost);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void unregisterDialogHost(@NonNull String uuid);

    @Nullable
    PriorityDialog getCurrentDialog();

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setCurrentDialog(@Nullable PriorityDialog priorityDialog);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void addToPendingDialog(@NonNull PriorityDialog priorityDialog);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    boolean isWindowLockedByDialog();

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void tryShowNextPendingDialog();

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void tryAllPendingAction();
}
