package cn.hx.prioritydialog;

import androidx.annotation.NonNull;

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
     * 新对话框不可显示时，是否要加入等待队列，默认true
     *
     * @param preDialog 当前对话框
     * @param newDialog 新对话框
     */
    boolean shouldNewAddToPendingWhenCanNotShow(@NonNull PriorityDialog preDialog, @NonNull PriorityDialog newDialog);

    /**
     * 新对话框可显示时，当前对话框是否要加入等待队列
     *
     * @param preDialog 当前对话框
     * @param newDialog 新对话框
     */
    boolean shouldPreAddToPendingWhenNewShow(@NonNull PriorityDialog preDialog, @NonNull PriorityDialog newDialog);

    /**
     * 等待队列中相同优先级的对话框弹出顺序是不是先进先出，默认false
     */
    boolean firstInFirstOutWhenSamePriority();

    /**
     * 等待队列中的对话框是否在当前对话框关闭时立即弹出，默认true
     */
    boolean showNextPendingImmediateAfterPreDismiss();
}
