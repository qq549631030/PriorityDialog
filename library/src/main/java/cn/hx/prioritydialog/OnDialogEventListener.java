package cn.hx.prioritydialog;

import androidx.annotation.NonNull;

public interface OnDialogEventListener {

    void onDialogEvent(@NonNull PriorityDialog dialog, @NonNull Object event);
}