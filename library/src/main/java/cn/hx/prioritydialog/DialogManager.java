package cn.hx.prioritydialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

public interface DialogManager {

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @NonNull
    PriorityDialogManager getPriorityDialogManager();

    @Nullable
    PriorityDialog getCurrentDialog();
}
