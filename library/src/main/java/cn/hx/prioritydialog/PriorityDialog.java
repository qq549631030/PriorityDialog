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

    //唯一标识
    @NonNull
    String getUuid();

    void setUuid(@NonNull String uuid);

    //Host唯一标识
    @Nullable
    DialogHost getDialogHost();

    //优先级，值越大优先级越高
    int getPriority();

    void setPriority(int priority);

    //对话框是否只有用户才能真正关闭
    boolean getOnlyDismissByUser();

    void setOnlyDismissByUser(boolean onlyDismissByUser);

    //是否锁定窗口，若为true则弹框显示时只能停留在当前页面，无法关闭无法跳走
    boolean getLockWindow();

    void setLockWindow(boolean lockWindow);

    void setSupportRecreate(boolean supportRecreate);

    boolean isSupportRecreate();

    boolean isDismissByHighPriorityDialog();

    void setOnCancelListener(@Nullable OnCancelListener listener);

    void setOnDismissListener(@Nullable OnDismissListener listener);

    void setOnDialogEventListener(@Nullable OnDialogEventListener listener);

    @RestrictTo({RestrictTo.Scope.SUBCLASSES})
    void onDialogEvent(@NonNull Object event);

    void onReallyDismiss();
}