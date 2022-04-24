package cn.hx.prioritydialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

public interface PriorityDialog {

    String BASE_DIALOG_TAG = "cn.hx.base.dialog.tag";

    String BASE_DIALOG_UUID = "cn.hx.base.dialog.uuid";
    String BASE_DIALOG_HOST_UUID = "cn.hx.base.dialog.host.uuid";
    String BASE_DIALOG_PRIORITY = "cn.hx.base.dialog.priority";
    String BASE_DIALOG_ONLY_DISMISS_BY_USER = "cn.hx.base.dialog.onlyDismissByUser";
    String BASE_DIALOG_LOCK_WINDOW = "cn.hx.base.dialog.lockWindow";
    String BASE_DIALOG_SUPPORT_RECREATE = "cn.hx.base.dialog.supportRecreate";

    //唯一标识
    @NonNull
    String getUuid();

    void setUuid(@NonNull String uuid);

    //Host唯一标识
    @Nullable
    String getHostUuid();

    void setHostUuid(@Nullable String uuid);

    //优先级，值越大优先级越高
    int getPriority();

    void setPriority(int priority);

    //对话框是否只有用户才能真正关闭
    boolean getOnlyDismissByUser();

    void setOnlyDismissByUser(boolean onlyDismissByUser);

    //是否锁定窗口，若为true则弹框显示时只能停留在当前页面，无法关闭无法跳走
    boolean getLockWindow();

    void setLockWindow(boolean lockWindow);

    boolean isSupportRecreate();

    void setSupportRecreate(boolean supportRecreate);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setDismissByHighPriorityDialog(boolean dismissByHighPriorityDialog);

    boolean isDismissByHighPriorityDialog();

    void setOnCancelListener(@Nullable OnCancelListener<? extends DialogHost> listener);

    void setOnDismissListener(@Nullable OnDismissListener<? extends DialogHost> listener);

    void setOnDialogEventListener(@Nullable OnDialogEventListener<? extends DialogHost> listener);

    @RestrictTo({RestrictTo.Scope.SUBCLASSES})
    void onDialogEvent(@NonNull Object event);

    void onReallyDismiss();
}