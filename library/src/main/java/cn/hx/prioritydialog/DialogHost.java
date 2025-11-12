package cn.hx.prioritydialog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;


public interface DialogHost {

    String BASE_DIALOG_HOST_UUID = "cn.hx.base.dialogHost.uuid";

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @NonNull
    PriorityDialogHostDelegate getPriorityDialogHostDelegate();

    /**
     * 获取宿主唯一标识
     */
    @NonNull
    String getUuid();

    /**
     * 获取封装后的parentFragmentManager
     */
    @NonNull
    WarpFragmentManager getWarpParentFragmentManager();

    /**
     * 获取封装后的childFragmentManager
     */
    @NonNull
    WarpFragmentManager getWarpChildFragmentManager();

    /**
     * 显示对话框
     *
     * @return 是否显示成功
     */
    boolean showPriorityDialog(@NonNull PriorityDialog newDialog);

    /**
     * 显示对话框
     *
     * @return 是否显示成功
     */
    boolean showPriorityDialog(@NonNull PriorityDialog newDialog, boolean allowStateLoss);

    /**
     * 关闭当前对话框
     */
    void dismissCurrentDialog(boolean allowStateLoss);

    /**
     * 关闭指定uuid对话框
     */
    void dismissByUuid(@NonNull String uuid, boolean allowStateLoss);

    /**
     * 删除所有等待队列里的对话框
     */
    void removeAllPendingDialogs();

    /**
     * 删除等待队列里指定uuid的对话框
     *
     */
    void removePendingDialogByUuid(@NonNull String uuid);

    /**
     * 自定义对话框事件
     *
     * @param event 自定义事件
     */
    void onDialogEvent(@NonNull PriorityDialog priorityDialog, @NonNull Object event);

    /**
     * 对话框取消事件
     */
    void onCancel(@NonNull PriorityDialog priorityDialog);

    /**
     * 对话框关闭事件
     */
    void onDismiss(@NonNull PriorityDialog priorityDialog);

    /**
     * 封装startActivityForResult
     *
     * @return 是否消耗事件
     */
    boolean warpStartActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options);

    /**
     * 封装finish
     *
     * @return 是否消耗事件
     */
    boolean warpFinish();
}