package cn.hx.prioritydialog;

public class PriorityDialogDelegate {

    PriorityDialogManager dialogManager;

    private final PriorityDialogConfig config = new PriorityDialogConfig();

    /**
     * 是否是被其它对话框剂掉的
     */
    private boolean mDismissByHighPriorityDialog = false;

    /**
     * 当前对话框是否是在等待队列中的
     */
    private boolean isInPendingQueue = false;

    private OnCancelListener mOnCancelListener = null;

    private OnDismissListener mOnDismissListener = null;

    private OnDialogEventListener mOnDialogEventListener = null;

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

    public boolean isInPendingQueue() {
        return isInPendingQueue;
    }

    public void setInPendingQueue(boolean inPendingQueue) {
        isInPendingQueue = inPendingQueue;
    }

    public OnCancelListener getOnCancelListener() {
        return mOnCancelListener;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.mOnCancelListener = onCancelListener;
    }

    public OnDismissListener getOnDismissListener() {
        return mOnDismissListener;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public OnDialogEventListener getOnDialogEventListener() {
        return mOnDialogEventListener;
    }

    public void setOnDialogEventListener(OnDialogEventListener onDialogEventListener) {
        this.mOnDialogEventListener = onDialogEventListener;
    }
}
