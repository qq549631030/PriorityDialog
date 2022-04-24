package cn.hx.prioritydialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DialogManagerImpl implements DialogManager {

    private final PriorityDialogManager dialogManager = new PriorityDialogManager();

    @NonNull
    @Override
    public PriorityDialogManager getPriorityDialogManager() {
        return dialogManager;
    }

    @Nullable
    @Override
    public PriorityDialog getCurrentDialog() {
        return dialogManager.getCurrentDialog();
    }
}
