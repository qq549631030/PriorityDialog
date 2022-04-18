package cn.hx.prioritydialog;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.DialogFragment;

public interface PriorityDialog {

    String BASE_DIALOG_TAG = "cn.hx.base.dialog.tag";

    //唯一标识
    @NonNull
    String getUuid();

    void setUuid(@NonNull String uuid);

    //优先级，值越大优先级越高
    int getPriority();

    void setPriority(int priority);

    //对话框是否只有用户才能真正关闭
    boolean getOnlyDismissByUser();

    void setOnlyDismissByUser(boolean onlyDismissByUser);

    //是否锁定窗口，若为true则弹框显示时只能停留在当前页面，无法关闭无法跳走
    boolean getLockWindow();

    void setLockWindow(boolean lockWindow);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setDismissByHighPriorityDialog(boolean dismissByHighPriorityDialog);

    void initAsPriorityDialog(DialogFragment dialogFragment);

    void onDialogEvent(@NonNull Object event);
}