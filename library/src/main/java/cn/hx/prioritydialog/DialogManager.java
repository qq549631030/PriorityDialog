package cn.hx.prioritydialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface DialogManager {

    @NonNull
    PriorityDialogManager getPriorityDialogManager();

    @Nullable
    PriorityDialog getCurrentDialog();
}
