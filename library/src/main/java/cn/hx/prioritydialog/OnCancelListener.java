package cn.hx.prioritydialog;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

public abstract class OnCancelListener<T extends DialogHost> {

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void dispatch(DialogHost host) {
        try {
            T t = (T) host;
            if (t != null) {
                onCancel(t);
            }
        } catch (Exception e) {
        }
    }

    public abstract void onCancel(@NonNull T host);
}