package cn.hx.prioritydialog;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

public abstract class OnDialogEventListener<T extends DialogHost> {

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void dispatch(@NonNull DialogHost host, @NonNull Object event) {
        try {
            T t = (T) host;
            if (t != null) {
                onDialogEvent(t, event);
            }
        } catch (Exception e) {
        }
    }

    public abstract void onDialogEvent(@NonNull T host, @NonNull Object event);
}