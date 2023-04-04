package cn.hx.prioritydialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PriorityDialogImpl implements PriorityDialog {

    private final PriorityDialogDelegate delegate = new PriorityDialogDelegate();

    public PriorityDialogImpl() {
        defaultPriorityConfig(delegate.getConfig());
    }

    @NonNull
    @Override
    public PriorityDialogDelegate getPriorityDialogDelegate() {
        return delegate;
    }

    @NonNull
    @Override
    public PriorityDialogConfig getPriorityConfig() {
        return delegate.getConfig();
    }

    @Override
    public void defaultPriorityConfig(@NonNull PriorityDialogConfig config) {

    }

    @Nullable
    @Override
    public DialogHost getDialogHost() {
        if (delegate.dialogManager != null) {
            return delegate.dialogManager.getDialogHost(delegate.getConfig().getHostUuid());
        }
        return null;
    }

    @Override
    public boolean isDismissByHighPriorityDialog() {
        return delegate.isDismissByHighPriorityDialog();
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        PriorityDialogManager.onCancelListenerMap.put(delegate.getConfig().getUuid(), listener);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        PriorityDialogManager.onDismissListenerMap.put(delegate.getConfig().getUuid(), listener);
    }

    @Override
    public void setOnDialogEventListener(@Nullable OnDialogEventListener listener) {
        PriorityDialogManager.onDialogEventListenerMap.put(delegate.getConfig().getUuid(), listener);
    }

    @Override
    public void onDialogEvent(@NonNull Object event) {
        if (delegate.dialogManager != null) {
            delegate.dialogManager.dispatchDialogEvent(this, event);
        }
    }

    @Override
    public void onReallyDismiss() {
    }
}