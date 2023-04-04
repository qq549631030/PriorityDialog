package cn.hx.prioritydialog;

import androidx.annotation.NonNull;

public interface PriorityDialogListener {

    void onDialogShow(@NonNull PriorityDialog dialog);

    void onDialogDismiss(@NonNull PriorityDialog dialog);

    void onDialogAddToPending(@NonNull PriorityDialog dialog);
}
