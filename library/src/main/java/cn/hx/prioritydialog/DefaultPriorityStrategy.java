package cn.hx.prioritydialog;

import androidx.annotation.NonNull;

public class DefaultPriorityStrategy implements PriorityStrategy {
    @Override
    public boolean canNewShow(@NonNull PriorityDialog preDialog, @NonNull PriorityDialog newDialog) {
        return newDialog.getPriority() >= preDialog.getPriority();
    }

    @Override
    public boolean shouldNewAddToPendingWhenCanNotShow(@NonNull PriorityDialog preDialog, @NonNull PriorityDialog newDialog) {
        return true;
    }

    @Override
    public boolean shouldPreAddToPendingWhenNewShow(@NonNull PriorityDialog preDialog, @NonNull PriorityDialog newDialog) {
        return preDialog.getOnlyDismissByUser();
    }
}
