package cn.hx.prioritydialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class DialogManagerImpl implements DialogManager {

    private final PriorityDialogManager dialogManager = new PriorityDialogManager();

    @NonNull
    @Override
    public PriorityDialogManager getPriorityDialogManager() {
        return dialogManager;
    }

    @Nullable
    @Override
    public PriorityDialog getCurrentPriorityDialog() {
        return dialogManager.getCurrentDialog();
    }

    @Override
    public void setCurrentPriorityStrategy(@Nullable PriorityStrategy priorityStrategy) {
        dialogManager.setCurrentPriorityStrategy(priorityStrategy);
    }

    @NonNull
    @Override
    public List<PendingDialogState> getAllPendingDialog() {
        return dialogManager.getAllPendingDialog();
    }

    @Override
    public void tryShowNextPendingDialog() {
        dialogManager.tryShowNextPendingDialog(false, false);
    }

    @Override
    public boolean tryShowPendingDialog(@NonNull PendingDialogState dialogInfo) {
        return dialogManager.tryShowPendingDialog(dialogInfo);
    }

    @Override
    public void registerPriorityDialogListener(@NonNull PriorityDialogListener priorityDialogListener) {
        dialogManager.registerPriorityDialogListener(priorityDialogListener);
    }

    @Override
    public void unregisterPriorityDialogListener(@NonNull PriorityDialogListener priorityDialogListener) {
        dialogManager.unregisterPriorityDialogListener(priorityDialogListener);
    }
}
