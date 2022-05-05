package cn.hx.prioritydialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PriorityDialogImpl implements PriorityDialog {

    private final PriorityDialogDelegate delegate = new PriorityDialogDelegate();

    public PriorityDialogImpl() {
        this(false);
    }

    public PriorityDialogImpl(boolean onlyDismissByUser) {
        delegate.getConfig().setOnlyDismissByUser(onlyDismissByUser);
    }

    @NonNull
    @Override
    public PriorityDialogDelegate getPriorityDialogDelegate() {
        return delegate;
    }

    @NonNull
    @Override
    public String getUuid() {
        return delegate.getConfig().getUuid();
    }

    @Override
    public void setUuid(@NonNull String uuid) {
        delegate.getConfig().setUuid(uuid);
    }

    @Nullable
    @Override
    public DialogHost getDialogHost() {
        return delegate.getDialogManager().getDialogHost(delegate.getConfig().getHostUuid());
    }

    @Override
    public int getPriority() {
        return delegate.getConfig().getPriority();
    }

    @Override
    public void setPriority(int priority) {
        delegate.getConfig().setPriority(priority);
    }

    @Override
    public boolean getOnlyDismissByUser() {
        return delegate.getConfig().isOnlyDismissByUser();
    }

    @Override
    public void setOnlyDismissByUser(boolean onlyDismissByUser) {
        delegate.getConfig().setOnlyDismissByUser(onlyDismissByUser);
    }

    @Override
    public boolean getLockWindow() {
        return delegate.getConfig().isLockWindow();
    }

    @Override
    public void setLockWindow(boolean lockWindow) {
        delegate.getConfig().setLockWindow(lockWindow);
    }

    @Override
    public void setSupportRecreate(boolean supportRecreate) {
        delegate.getConfig().setSupportRecreate(supportRecreate);
    }

    @Override
    public boolean isSupportRecreate() {
        return delegate.getConfig().isSupportRecreate();
    }

    @Override
    public boolean isDismissByHighPriorityDialog() {
        return delegate.isDismissByHighPriorityDialog();
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        PriorityDialogManager.onCancelListenerMap.put(getUuid(), listener);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        PriorityDialogManager.onDismissListenerMap.put(getUuid(), listener);
    }

    @Override
    public void setOnDialogEventListener(@Nullable OnDialogEventListener listener) {
        PriorityDialogManager.onDialogEventListenerMap.put(getUuid(), listener);
    }

    @Override
    public void onDialogEvent(@NonNull Object event) {
        getPriorityDialogDelegate().getDialogManager().dispatchDialogEvent(this, event);
    }

    @Override
    public void onReallyDismiss() {
    }
}