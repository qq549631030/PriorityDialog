package cn.hx.prioritydialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

public class PriorityDialogImpl implements PriorityDialog {

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
    public void setOnCancelListener(@Nullable OnCancelListener<? extends DialogHost> listener) {
        PriorityDialogManager.onCancelListenerMap.put(getUuid(), listener);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener<? extends DialogHost> listener) {
        PriorityDialogManager.onDismissListenerMap.put(getUuid(), listener);
    }

    @Override
    public void setOnDialogEventListener(@Nullable OnDialogEventListener<? extends DialogHost> listener) {
        PriorityDialogManager.onDialogEventListenerMap.put(getUuid(), listener);
    }

    @Override
    public void onDialogEvent(@NonNull Object event) {
        PriorityDialogManager.dispatchDialogEvent(this, event);
    }

    @Override
    public void onReallyDismiss() {
    }
}