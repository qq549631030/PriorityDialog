package cn.hx.prioritydialog;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * 对话框优先级策略
 */
public interface PriorityStrategy {

    /**
     * 新对话框是否能取代当前对话框而显示
     *
     * @param preDialog 当前对话框
     * @param newDialog 新对话框
     */
    boolean canNewShow(@NonNull PriorityDialog preDialog, @NonNull PriorityDialog newDialog);

    /**
     * 新对话框和等待队列里的比是否能显示，默认true
     *
     * @param pendingList 等待队列中的对话框
     * @param newDialog   新对话框
     */
    boolean canNewShowCasePending(@NonNull List<PendingDialogState> pendingList, @NonNull PriorityDialog newDialog);

    /**
     * 等待队列中相同优先级的对话框弹出顺序是不是先进先出，默认false
     */
    boolean firstInFirstOutWhenSamePriority();
}
