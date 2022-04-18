package cn.hx.prioritydialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;

import java.util.UUID;

public class PriorityDialogImpl implements PriorityDialog, DialogInterface.OnCancelListener {

    String KEY_DIALOG_STATE = "cn.hx.base.dialog.state";
    String BASE_DIALOG_UUID = "cn.hx.base.dialog.uuid";
    String BASE_DIALOG_PRIORITY = "cn.hx.base.dialog.priority";
    String BASE_DIALOG_ONLY_DISMISS_BY_USER = "cn.hx.base.dialog.onlyDismissByUser";
    String BASE_DIALOG_LOCK_WINDOW = "cn.hx.base.dialog.lockWindow";
    String BASE_DIALOG_DISMISS_BY_HIGH_PRIORITY_DIALOG = "cn.hx.base.dialog.dismissByHighPriorityDialog";

    private DialogFragment mDialogFragment;

    private String mUuid;
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
        this.mDialogFragment = dialogFragment;
        Bundle savedState = mDialogFragment.getSavedStateRegistry().consumeRestoredStateForKey(KEY_DIALOG_STATE);
        if (savedState != null) {
            mUuid = savedState.getString(BASE_DIALOG_UUID);
            mPriority = savedState.getInt(BASE_DIALOG_PRIORITY, 0);
            mOnlyDismissByUser = savedState.getBoolean(BASE_DIALOG_ONLY_DISMISS_BY_USER, true);
            mLockWindow = savedState.getBoolean(BASE_DIALOG_LOCK_WINDOW);
            mDismissByHighPriorityDialog = savedState.getBoolean(BASE_DIALOG_DISMISS_BY_HIGH_PRIORITY_DIALOG);
        }
        mDialogFragment.getSavedStateRegistry().registerSavedStateProvider(KEY_DIALOG_STATE, () -> {
            Bundle bundle = new Bundle();
            bundle.putString(BASE_DIALOG_UUID, mUuid);
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
                    Pair<DialogHost, PriorityDialog> dialogAndHost = getDialogAndHost();
                    if (dialogAndHost != null) {
                        dialogAndHost.first.onDismiss(dialogAndHost.second);
                    }
                }
            }
        });
    }

    private Pair<DialogHost, PriorityDialog> getDialogAndHost() {
        if (mDialogFragment instanceof PriorityDialog) {
            PriorityDialog priorityDialog = (PriorityDialog) mDialogFragment;
            DialogHost dialogHost = null;
            if (mDialogFragment.getParentFragment() instanceof FragmentDialogHost) {
                dialogHost = ((FragmentDialogHost) mDialogFragment.getParentFragment());
            } else if (mDialogFragment.getActivity() instanceof ActivityDialogHost) {
                dialogHost = ((ActivityDialogHost) mDialogFragment.getActivity());
            }
            if (dialogHost != null) {
                return new Pair<>(dialogHost, priorityDialog);
            }
        }
        return null;
    }

    @Override
    public void onDialogEvent(@NonNull Object event) {
        Pair<DialogHost, PriorityDialog> dialogAndHost = getDialogAndHost();
        if (dialogAndHost != null) {
            dialogAndHost.first.onDialogEvent(dialogAndHost.second, event);
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        Pair<DialogHost, PriorityDialog> dialogAndHost = getDialogAndHost();
        if (dialogAndHost != null) {
            dialogAndHost.first.onCancel(dialogAndHost.second);
        }
    }
}