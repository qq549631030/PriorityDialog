package cn.hx.prioritydialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

public interface PriorityDialog {

    String BASE_DIALOG_TAG = "cn.hx.base.dialog.tag";

    String BASE_DIALOG_CONFIG = "cn.hx.base.dialog.config";

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @NonNull
    PriorityDialogDelegate getPriorityDialogDelegate();

    /**
     * 获取当前对话框配置
     */
    @NonNull
    PriorityDialogConfig getPriorityConfig();

    /**
     * 默认对话框配置
     */
    void defaultPriorityConfig(@NonNull PriorityDialogConfig config);

    /**
     * 获取对话框宿主
     */
    @Nullable
    DialogHost getDialogHost();

    /**
     * 当前对话框是否是被高优先级对话框关闭的
     */
    boolean isDismissByHighPriorityDialog();

    /**
     * 设置取消监听
     */
    void setOnCancelListener(@Nullable OnCancelListener listener);

    /**
     * 设置关闭监听
     */
    void setOnDismissListener(@Nullable OnDismissListener listener);

    /**
     * 设置自定义事件监听
     */
    void setOnDialogEventListener(@Nullable OnDialogEventListener listener);

    /**
     * 对话框自定义事件触发
     *
     * @param event 自定义事件
     */
    @RestrictTo({RestrictTo.Scope.SUBCLASSES})
    void onDialogEvent(@NonNull Object event);

    /**
     * 对话框真正关闭（不是被高优先级关闭）
     */
    void onReallyDismiss();
}