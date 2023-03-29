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
     * 获取对话框唯一标识
     */
    @NonNull
    String getUuid();

    /**
     * 设置对话框唯一标识
     */
    void setUuid(@NonNull String uuid);

    /**
     * 获取对话框宿主
     */
    @Nullable
    DialogHost getDialogHost();

    /**
     * 优先级，值越大优先级越高
     */
    int getPriority();

    /**
     * 设置优先级
     */
    void setPriority(int priority);

    /**
     * 对话框是否只有用户才能真正关闭
     */
    boolean getOnlyDismissByUser();

    /**
     * 设置对话框是否只有用户才能真正关闭
     */
    void setOnlyDismissByUser(boolean onlyDismissByUser);

    /**
     * 是否锁定窗口，若为true则弹框显示时只能停留在当前页面，无法关闭无法跳走
     */
    boolean getLockWindow();

    /**
     * 设置锁定窗口
     */
    void setLockWindow(boolean lockWindow);

    /**
     * 设置是否支持Activity重建后对话框也重新显示
     */
    void setSupportRecreate(boolean supportRecreate);

    /**
     * 是否支持Activity重建后对话框也重新显示
     */
    boolean isSupportRecreate();

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