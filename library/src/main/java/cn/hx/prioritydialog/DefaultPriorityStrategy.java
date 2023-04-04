package cn.hx.prioritydialog;

import androidx.annotation.NonNull;

import java.util.List;

public class DefaultPriorityStrategy implements PriorityStrategy {
    @Override
    public boolean canNewShow(@NonNull PriorityDialog preDialog, @NonNull PriorityDialog newDialog) {
        return newDialog.getPriorityConfig().getPriority() >= preDialog.getPriorityConfig().getPriority();
    }

    @Override
    public boolean canNewShowCasePending(@NonNull List<PendingDialogState> pendingList, @NonNull PriorityDialog newDialog) {
        if (newDialog.getPriorityConfig().isCasePending()) {
            for (PendingDialogState dialogState : pendingList) {
                if (dialogState.config.getPriority() > newDialog.getPriorityConfig().getPriority()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean firstInFirstOutWhenSamePriority() {
        return false;
    }
}
