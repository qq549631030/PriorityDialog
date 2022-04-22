package cn.hx.prioritydialog;

import androidx.annotation.NonNull;

public interface PriorityStrategy {

    boolean canNewShow(@NonNull PriorityDialog preDialog, @NonNull PriorityDialog newDialog);

    boolean shouldNewAddToPendingWhenCanNotShow(@NonNull PriorityDialog preDialog, @NonNull PriorityDialog newDialog);

    boolean shouldPreAddToPendingWhenNewShow(@NonNull PriorityDialog preDialog, @NonNull PriorityDialog newDialog);

    boolean firstInFirstOutWhenSamePriority();
}
