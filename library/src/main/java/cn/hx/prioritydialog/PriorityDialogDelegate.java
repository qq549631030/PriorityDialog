package cn.hx.prioritydialog;

public class PriorityDialogDelegate {

    private PriorityDialogManager mDialogManager;

    private final PriorityDialogConfig config = new PriorityDialogConfig();

    private boolean mDismissByHighPriorityDialog = false;

    void init(PriorityDialogManager dialogManager) {
        mDialogManager = dialogManager;
    }

    public PriorityDialogManager getDialogManager() {
        return mDialogManager;
    }

    public void setDialogManager(PriorityDialogManager mDialogManager) {
        this.mDialogManager = mDialogManager;
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
