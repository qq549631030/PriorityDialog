package cn.hx.prioritydialog;

public class PriorityDialogDelegate {

    PriorityDialogManager dialogManager;

    private final PriorityDialogConfig config = new PriorityDialogConfig();

    private boolean mDismissByHighPriorityDialog = false;

    void init(PriorityDialogManager dialogManager) {
        this.dialogManager = dialogManager;
    }

    public PriorityDialogConfig getConfig() {
        return config;
    }

    public boolean isDismissByHighPriorityDialog() {
        return mDismissByHighPriorityDialog;
    }

    public void setDismissByHighPriorityDialog(boolean dismissByHighPriorityDialog) {
        this.mDismissByHighPriorityDialog = dismissByHighPriorityDialog;
    }
}
