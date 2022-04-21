package cn.hx.prioritydialog;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

public abstract class OnDismissListener<T extends DialogHost> {

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void dispatch(@NonNull DialogHost host) {
        try {
            T t = (T) host;
            if (t != null) {
                onDismiss(t);
            }
        } catch (Exception e) {
        }
    }

    public abstract void onDismiss(@NonNull T host);
}