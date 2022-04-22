package cn.hx.prioritydialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PriorityDialogImpl implements PriorityDialog, DialogInterface.OnCancelListener {

    String BASE_DIALOG_UUID = "cn.hx.base.dialog.uuid";
    String BASE_DIALOG_HOST_UUID = "cn.hx.base.dialog.host.uuid";
    String BASE_DIALOG_PRIORITY = "cn.hx.base.dialog.priority";
    String BASE_DIALOG_ONLY_DISMISS_BY_USER = "cn.hx.base.dialog.onlyDismissByUser";
    String BASE_DIALOG_LOCK_WINDOW = "cn.hx.base.dialog.lockWindow";
    String BASE_DIALOG_SUPPORT_RECREATE = "cn.hx.base.dialog.supportRecreate";

    private static final Map<String, OnCancelListener<? extends DialogHost>> onCancelListenerMap = new HashMap<>();
    private static final Map<String, OnDismissListener<? extends DialogHost>> onDismissListenerMap = new HashMap<>();
    private static final Map<String, OnDialogEventListener<? extends DialogHost>> onDialogEventListenerMap = new HashMap<>();

    private DialogFragment mDialogFragment;

    private String mUuid;
    private String mHostUuid;
    private int mPriority = 0;
    private boolean mOnlyDismissByUser;
    private boolean mLockWindow = false;
    private boolean mSupportRecreate = true;
    private boolean mDismissByHighPriorityDialog = false;

    public PriorityDialogImpl() {
        this(false);
    }

    public PriorityDialogImpl(boolean onlyDismissByUser) {
        this.mOnlyDismissByUser = onlyDismissByUser;
    }

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
    public boolean isSupportRecreate() {
        return mSupportRecreate;
    }

    @Override
    public void setSupportRecreate(boolean supportRecreate) {
        this.mSupportRecreate = supportRecreate;
    }

    @Override
    public void setDismissByHighPriorityDialog(boolean dismissByHighPriorityDialog) {
        mDismissByHighPriorityDialog = dismissByHighPriorityDialog;
    }

    @Override
    public boolean isDismissByHighPriorityDialog() {
        return mDismissByHighPriorityDialog;
    }

    @Override
    public void initAsPriorityDialog(@NonNull DialogFragment dialogFragment, @Nullable Bundle savedInstanceState) {
        if (!(dialogFragment instanceof PriorityDialog)) {
            throw new IllegalArgumentException("dialogFragment must implements PriorityDialog");
        }
        this.mDialogFragment = dialogFragment;
        if (savedInstanceState != null) {
            mUuid = savedInstanceState.getString(BASE_DIALOG_UUID);
            mHostUuid = savedInstanceState.getString(BASE_DIALOG_HOST_UUID);
            mPriority = savedInstanceState.getInt(BASE_DIALOG_PRIORITY, 0);
            mOnlyDismissByUser = savedInstanceState.getBoolean(BASE_DIALOG_ONLY_DISMISS_BY_USER, true);
            mLockWindow = savedInstanceState.getBoolean(BASE_DIALOG_LOCK_WINDOW);
            mSupportRecreate = savedInstanceState.getBoolean(BASE_DIALOG_SUPPORT_RECREATE, true);
            if (!mSupportRecreate) {
                dismissCurrent();
            }
        }
        mDialogFragment.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_START) {
                Dialog dialog = mDialogFragment.getDialog();
                if (dialog != null) {
                    //override the DialogFragment onCancelListener
                    dialog.setOnCancelListener(this);
                }
            }
            if (event == Lifecycle.Event.ON_DESTROY) {
                boolean reallyDismiss = false;
                if (mDialogFragment.getParentFragment() != null) {
                    if (mDialogFragment.getParentFragment().isRemoving()) {
                        reallyDismiss = true;//fragment Destroy lead dialog destroy
                    }
                } else if (mDialogFragment.requireActivity().isFinishing()) {
                    reallyDismiss = true;//Activity Destroy lead dialog destroy
                }
                if (!reallyDismiss) {
                    if (mDialogFragment.isRemoving() && !mDialogFragment.isStateSaved() && !mDismissByHighPriorityDialog) {
                        reallyDismiss = true;//dismiss by user
                    }
                }
                if (reallyDismiss) {
                    DialogHost dialogHost = getDialogHost();
                    if (dialogHost != null) {
                        dialogHost.onDismiss((PriorityDialog) mDialogFragment);
                        dispatchOnDismiss(dialogHost);
                    }
                    onCancelListenerMap.remove(getUuid());
                    onDismissListenerMap.remove(getUuid());
                    onDialogEventListenerMap.remove(getUuid());
                    reallyDismiss();
                    if (mDialogFragment.requireActivity() instanceof DialogManager) {
                        DialogManager dialogManager = (DialogManager) mDialogFragment.requireActivity();
                        dialogManager.setPendingDismissDialog((PriorityDialog) mDialogFragment);
                        FragmentManager fragmentManager = mDialogFragment.requireFragmentManager();
                        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                            @Override
                            public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                                if (f.equals(dialogManager.getPendingDismissDialog())) {
                                    dialogManager.setPendingDismissDialog(null);
                                    fragmentManager.unregisterFragmentLifecycleCallbacks(this);
                                }
                            }
                        }, false);
                        dialogManager.tryShowNextPendingDialog();
                        dialogManager.tryAllPendingAction();
                    }
                }
            }
        });
    }

    @Override
    public void onPriorityDialogSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(BASE_DIALOG_UUID, getUuid());
        outState.putString(BASE_DIALOG_HOST_UUID, mHostUuid);
        outState.putInt(BASE_DIALOG_PRIORITY, mPriority);
        outState.putBoolean(BASE_DIALOG_ONLY_DISMISS_BY_USER, mOnlyDismissByUser);
        outState.putBoolean(BASE_DIALOG_LOCK_WINDOW, mLockWindow);
        outState.putBoolean(BASE_DIALOG_SUPPORT_RECREATE, mSupportRecreate);
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener<? extends DialogHost> listener) {
        onCancelListenerMap.put(getUuid(), listener);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener<? extends DialogHost> listener) {
        onDismissListenerMap.put(getUuid(), listener);
    }

    @Override
    public void setOnDialogEventListener(@Nullable OnDialogEventListener<? extends DialogHost> listener) {
        onDialogEventListenerMap.put(getUuid(), listener);
    }

    @Nullable
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
            dispatchOnDialogEvent(dialogHost, event);
        }
    }

    @Override
    public void dismissCurrent() {
        mDialogFragment.dismiss();
    }

    @Override
    public void reallyDismiss() {

    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        mDialogFragment.onCancel(dialogInterface);
        DialogHost dialogHost = getDialogHost();
        if (dialogHost != null) {
            dialogHost.onCancel((PriorityDialog) mDialogFragment);
            dispatchOnCancel(dialogHost);
        }
    }

    private void dispatchOnCancel(@NonNull DialogHost dialogHost) {
        OnCancelListener<? extends DialogHost> listener = onCancelListenerMap.remove(getUuid());
        if (listener != null) {
            listener.dispatch(dialogHost);
        }
    }

    private void dispatchOnDismiss(@NonNull DialogHost dialogHost) {
        OnDismissListener<? extends DialogHost> listener = onDismissListenerMap.remove(getUuid());
        if (listener != null) {
            listener.dispatch(dialogHost);
        }
    }

    private void dispatchOnDialogEvent(@NonNull DialogHost dialogHost, @NonNull Object event) {
        OnDialogEventListener<? extends DialogHost> listener = onDialogEventListenerMap.remove(getUuid());
        if (listener != null) {
            listener.dispatch(dialogHost, event);
        }
    }
}