package cn.hx.prioritydialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import java.util.List;

public interface DialogManager {

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @NonNull
    PriorityDialogManager getPriorityDialogManager();

    /**
     * 获取当前正在显示的对话框
     */
    @Nullable
    PriorityDialog getCurrentPriorityDialog();

    /**
     * 单独设置当前的优先级策略，默认使用全局的
     */
    void setCurrentPriorityStrategy(@Nullable PriorityStrategy priorityStrategy);

    /**
     * 获取所有等待队列对话框
     */
    @NonNull
    List<PendingDialogState> getAllPendingDialog();

    /**
     * 显示等待队列中下一个对话框
     */
    void tryShowNextPendingDialog();

    /**
     * 显示等待队列中指定对话框
     *
     * @param dialogInfo 对话框信息
     * @return 是否显示成功
     */
    boolean tryShowPendingDialog(@NonNull PendingDialogState dialogInfo);

    /**
     * 注册监听
     */
    void registerPriorityDialogListener(@NonNull PriorityDialogListener priorityDialogListener);

    /**
     * 取消监听
     */
    void unregisterPriorityDialogListener(@NonNull PriorityDialogListener priorityDialogListener);
}
