package cn.hx.prioritydialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;

import java.util.UUID;

public class PriorityDialogImpl implements PriorityDialog, DialogInterface.OnCancelListener {

    String KEY_DIALOG_STATE = "cn.hx.base.dialog.state";
    String BASE_DIALOG_UUID = "cn.hx.base.dialog.uuid";
    String BASE_DIALOG_HOST_UUID = "cn.hx.base.dialog.host.uuid";
    String BASE_DIALOG_PRIORITY = "cn.hx.base.dialog.priority";
    String BASE_DIALOG_ONLY_DISMISS_BY_USER = "cn.hx.base.dialog.onlyDismissByUser";
    String BASE_DIALOG_LOCK_WINDOW = "cn.hx.base.dialog.lockWindow";
    String BASE_DIALOG_DISMISS_BY_HIGH_PRIORITY_DIALOG = "cn.hx.base.dialog.dismissByHighPriorityDialog";

    private DialogFragment mDialogFragment;

    private String mUuid;
    private String mHostUuid;
    private int mPriority = 0;
    private boolean mOnlyDismissByUser = true;
    private boolean mLockWindow = false;
    private boolean mDismissByHighPriorityDialog = false;

    @NonNull
    @Override
    public String getUuid() {
        if (mUuid == null) {
            mUuid = UUID.randomUUID().toString();
        }
        return mUuid;
    }

    @Override
    public void setUuid(@NonNull String uuid) {
        mUuid = uuid;
    }

    @Nullable
    @Override
    public String getHostUuid() {
        return mHostUuid;
    }

    @Override
    public void setHostUuid(@Nullable String uuid) {
        mHostUuid = uuid;
    }

    @Override
    public int getPriority() {
        return mPriority;
    }

    @Override
    public void setPriority(int priority) {
        mPriority = priority;
    }

    @Override
    public boolean getOnlyDismissByUser() {
        return mOnlyDismissByUser;
    }

    @Override
    public void setOnlyDismissByUser(boolean onlyDismissByUser) {
        mOnlyDismissByUser = onlyDismissByUser;
    }

    @Override
    public boolean getLockWindow() {
        return mLockWindow;
    }

    @Override
    public void setLockWindow(boolean lockWindow) {
        mLockWindow = lockWindow;
    }

    @Override
    public void setDismissByHighPriorityDialog(boolean dismissByHighPriorityDialog) {
        mDismissByHighPriorityDialog = dismissByHighPriorityDialog;
    }

    public void initAsPriorityDialog(DialogFragment dialogFragment) {
        if (!(dialogFragment instanceof PriorityDialog)) {
            throw new IllegalArgumentException("dialogFragment must implements PriorityDialog");
        }
        this.mDialogFragment = dialogFragment;
        Bundle savedState = mDialogFragment.getSavedStateRegistry().consumeRestoredStateForKey(KEY_DIALOG_STATE);
        if (savedState != null) {
            mUuid = savedState.getString(BASE_DIALOG_UUID);
            mHostUuid = savedState.getString(BASE_DIALOG_HOST_UUID);
            mPriority = savedState.getInt(BASE_DIALOG_PRIORITY, 0);
            mOnlyDismissByUser = savedState.getBoolean(BASE_DIALOG_ONLY_DISMISS_BY_USER, true);
            mLockWindow = savedState.getBoolean(BASE_DIALOG_LOCK_WINDOW);
            mDismissByHighPriorityDialog = savedState.getBoolean(BASE_DIALOG_DISMISS_BY_HIGH_PRIORITY_DIALOG);
        }
        if (mDialogFragment.requireActivity() instanceof DialogManager) {
            ((DialogManager) mDialogFragment.requireActivity()).setCurrentDialog((PriorityDialog) mDialogFragment);
        }
        mDialogFragment.getSavedStateRegistry().registerSavedStateProvider(KEY_DIALOG_STATE, () -> {
            Bundle bundle = new Bundle();
            bundle.putString(BASE_DIALOG_UUID, mUuid);
            bundle.putString(BASE_DIALOG_HOST_UUID, mHostUuid);
            bundle.putInt(BASE_DIALOG_PRIORITY, mPriority);
            bundle.putBoolean(BASE_DIALOG_ONLY_DISMISS_BY_USER, mOnlyDismissByUser);
            bundle.putBoolean(BASE_DIALOG_LOCK_WINDOW, mLockWindow);
            bundle.putBoolean(BASE_DIALOG_DISMISS_BY_HIGH_PRIORITY_DIALOG, mDismissByHighPriorityDialog);
            return bundle;
        });
        mDialogFragment.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_START) {
                Dialog dialog = mDialogFragment.getDialog();
                if (dialog != null) {
                    //override the DialogFragment onCancelListener
                    dialog.setOnCancelListener(this);
                }
            }
            if (event == Lifecycle.Event.ON_DESTROY) {
                if (mDialogFragment.isRemoving() && !mDialogFragment.isStateSaved() && !mDismissByHighPriorityDialog) {
                    if (mDialogFragment.requireActivity() instanceof DialogManager) {
                        DialogManager dialogManager = (DialogManager) mDialogFragment.requireActivity();
                        dialogManager.setCurrentDialog(null);
                        dialogManager.tryShowNextPendingDialog();
                        dialogManager.tryAllPendingAction();
                    }
                    DialogHost dialogHost = getDialogHost();
                    if (dialogHost != null) {
                        dialogHost.onDismiss((PriorityDialog) mDialogFragment);
                    }
                }
            }
        });
    }

    private DialogHost getDialogHost() {
        DialogHost dialogHost = null;
        if (mDialogFragment.getParentFragment() instanceof FragmentDialogHost) {
            dialogHost = ((FragmentDialogHost) mDialogFragment.getParentFragment());
        } else if (mDialogFragment.getActivity() instanceof ActivityDialogHost) {
            dialogHost = ((ActivityDialogHost) mDialogFragment.getActivity());
        }
        return dialogHost;
    }

    @Override
    public void onDialogEvent(@NonNull Object event) {
        DialogHost dialogHost = getDialogHost();
        if (dialogHost != null) {
            dialogHost.onDialogEvent((PriorityDialog) mDialogFragment, event);
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        DialogHost dialogHost = getDialogHost();
        if (dialogHost != null) {
            dialogHost.onCancel((PriorityDialog) mDialogFragment);
        }
    }
}