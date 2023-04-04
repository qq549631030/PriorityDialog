package cn.hx.prioritydialog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayDeque;
import java.util.UUID;

public class PriorityDialogHostDelegate {

    PriorityDialogManager mDialogManager;
    private String mUuid;
    FragmentManager parentFragmentManager;
    FragmentManager childFragmentManager;
    WarpFragmentManager mWarpParentFragmentManager;
    WarpFragmentManager mWarpChildFragmentManager;

    @NonNull
    String getUuid() {
        if (mUuid == null) {
            mUuid = UUID.randomUUID().toString();
        }
        return mUuid;
    }

    void init(@NonNull PriorityDialogManager dialogManager, @NonNull FragmentManager parentFragmentManager, @NonNull FragmentManager childFragmentManager, @Nullable Bundle savedInstanceState) {
        this.mDialogManager = dialogManager;
        this.parentFragmentManager = parentFragmentManager;
        this.childFragmentManager = childFragmentManager;
        if (savedInstanceState != null) {
            mUuid = savedInstanceState.getString(DialogHost.BASE_DIALOG_HOST_UUID);
        }
        this.mWarpParentFragmentManager = new WarpFragmentManager(parentFragmentManager, dialogManager, getUuid(), false);
        this.mWarpChildFragmentManager = new WarpFragmentManager(childFragmentManager, dialogManager, getUuid(), true);
    }

    boolean showPriorityDialog(@NonNull PriorityDialog newDialog, boolean allowStateLoss) {
        if (!mDialogManager.isReady(mUuid) || !allowStateLoss && childFragmentManager.isStateSaved()) {
            if (!newDialog.getPriorityDialogDelegate().isInPendingQueue()) {
                mDialogManager.release(newDialog);
            }
            return false;
        }
        newDialog.getPriorityDialogDelegate().getConfig().setHostUuid(mUuid);
        newDialog.getPriorityDialogDelegate().getConfig().setAllowStateLoss(allowStateLoss);
        PriorityDialog currentDialog = mDialogManager.getCurrentDialog();
        PriorityStrategy priorityStrategy = mDialogManager.getPriorityStrategy();
        if (currentDialog != null) {
            if (priorityStrategy.canNewShow(currentDialog, newDialog)) {
                if (currentDialog.getPriorityConfig().isAddToPendingWhenReplaceByOther()) {
                    currentDialog.getPriorityDialogDelegate().setDismissByHighPriorityDialog(true);
                    mDialogManager.addToPendingDialog(currentDialog);
                }
                if (currentDialog instanceof DialogFragment) {
                    DialogFragment current = (DialogFragment) currentDialog;
                    FragmentManager fm = current.getFragmentManager();
                    if (fm != null && !fm.isDestroyed()) {
                        fm.beginTransaction().remove(current).commitAllowingStateLoss();
                    }
                }
            } else {
                if (!newDialog.getPriorityDialogDelegate().isInPendingQueue()) {
                    if (newDialog.getPriorityConfig().isAddToPendingWhenCanNotShow()) {
                        mDialogManager.addToPendingDialog(newDialog);
                    } else {
                        mDialogManager.release(newDialog);
                    }
                }
                return false;
            }
        } else {
            if (newDialog.getPriorityConfig().isCasePending() && mDialogManager.getAllPendingDialog().size() > 0) {
                if (!priorityStrategy.canNewShowCasePending(mDialogManager.getAllPendingDialog(), newDialog)) {
                    if (!newDialog.getPriorityDialogDelegate().isInPendingQueue()) {
                        if (newDialog.getPriorityConfig().isAddToPendingWhenCanNotShow()) {
                            mDialogManager.addToPendingDialog(newDialog);
                        } else {
                            mDialogManager.release(newDialog);
                        }
                        mDialogManager.tryShowNextPendingDialog(false, true);
                    }
                    return false;
                }
            }
        }
        if (newDialog instanceof DialogFragment) {
            newDialog.getPriorityDialogDelegate().setDismissByHighPriorityDialog(false);
            mDialogManager.setPendingShowDialog(newDialog);
            FragmentTransaction transaction = childFragmentManager.beginTransaction();
            if (allowStateLoss) {
                transaction.add((DialogFragment) newDialog, PriorityDialog.BASE_DIALOG_TAG).commitAllowingStateLoss();
            } else {
                ((DialogFragment) newDialog).show(transaction, PriorityDialog.BASE_DIALOG_TAG);
            }
            if (!newDialog.getPriorityDialogDelegate().isInPendingQueue()) {
                mDialogManager.removePendingDialogByUuid(newDialog.getPriorityConfig().getUuid());//删除等待队列里相同uuid的对话框
            }
            newDialog.getPriorityDialogDelegate().setInPendingQueue(false);
            return true;
        }
        return false;
    }

    @NonNull
    WarpFragmentManager getWarpParentFragmentManager() {
        return mWarpParentFragmentManager;
    }

    @NonNull
    WarpFragmentManager getWarpChildFragmentManager() {
        return mWarpChildFragmentManager;
    }

    boolean warpStartActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
        if (mDialogManager.isWindowLockedByDialog()) {
            getPendingActions().addLast(new ActivityAction(intent, requestCode, options));
            return true;
        }
        return false;
    }

    boolean warpFinish() {
        if (mDialogManager.isWindowLockedByDialog()) {
            getPendingActions().add(new ActivityAction(ActivityAction.TYPE_FINISH_ACTIVITY));
            return true;
        }
        return false;
    }

    @NonNull
    private ArrayDeque<ActivityAction> getPendingActions() {
        return mDialogManager.getPendingActivityActions(getUuid());
    }
}
