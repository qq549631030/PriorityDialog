package cn.hx.prioritydialog;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public abstract class AbsDialogHostImpl implements DialogHost {

    public static String KEY_DIALOG_HOST_STATE = "cn.hx.base.dialogHost.state";
    public static String BASE_DIALOG_HOST_UUID = "cn.hx.base.dialogHost.uuid";

    private static final Map<String, ArrayDeque<FragmentAction>> pendingFragmentActionMap = new HashMap();

    DialogManager mDialogManager;
    protected String uuid;
    FragmentManager parentFragmentManager;
    FragmentManager childFragmentManager;
    WarpFragmentManager mWarpParentFragmentManager;
    WarpFragmentManager mWarpChildFragmentManager;
    boolean init = false;


    private boolean needWarpPendingTransaction = false;

    protected void init(@NonNull DialogManager dialogManager, @NonNull String uuid, @NonNull FragmentManager parentFragmentManager, @NonNull FragmentManager childFragmentManager) {
        this.mDialogManager = dialogManager;
        this.uuid = uuid;
        this.parentFragmentManager = parentFragmentManager;
        this.childFragmentManager = childFragmentManager;
        this.mWarpParentFragmentManager = new WarpFragmentManager(parentFragmentManager, this, false);
        this.mWarpChildFragmentManager = new WarpFragmentManager(childFragmentManager, this, true);
        needWarpPendingTransaction = true;
        init = true;
    }

    @NonNull
    private ArrayDeque<FragmentAction> getPendingFragmentActions() {
        ArrayDeque<FragmentAction> fragmentActions = pendingFragmentActionMap.get(uuid);
        if (fragmentActions != null) {
            return fragmentActions;
        }
        fragmentActions = new ArrayDeque<>();
        pendingFragmentActionMap.put(uuid, fragmentActions);
        return fragmentActions;
    }

    protected void tryWarpPendingTransaction() {
        if (needWarpPendingTransaction) {
            for (FragmentAction fragmentAction : getPendingFragmentActions()) {
                if (fragmentAction.type == FragmentAction.TYPE_TRANSACTION) {
                    FragmentManager fragmentManager;
                    if (fragmentAction.isChildFragmentManager) {
                        fragmentManager = childFragmentManager;
                    } else {
                        fragmentManager = parentFragmentManager;
                    }
                    fragmentAction.transaction = fragmentAction.transaction.warpWithNewFragmentManager(fragmentManager, this);
                }
            }
            needWarpPendingTransaction = false;
        }
    }

    @NonNull
    @Override
    public FragmentManager getWarpParentFragmentManager() {
        return mWarpParentFragmentManager;
    }

    @NonNull
    @Override
    public FragmentManager getWarpChildFragmentManager() {
        return mWarpChildFragmentManager;
    }

    @NonNull
    @Override
    public DialogManager getDialogManager() {
        return mDialogManager;
    }

    @Override
    public boolean showPriorityDialog(@NonNull PriorityDialog newDialog) {
        if (!init) {
            throw new IllegalStateException("not init, Please call initAsDialogHost first");
        }
        if (childFragmentManager.isStateSaved() || childFragmentManager.isDestroyed()) {
            return false;
        }
        newDialog.setHostUuid(uuid);
        FragmentTransaction transaction = childFragmentManager.beginTransaction();
        PriorityDialog currentDialog = mDialogManager.getCurrentDialog();
        if (currentDialog != null) {
            if (newDialog.getPriority() < currentDialog.getPriority()) {
                mDialogManager.addToPendingDialog(newDialog);
                return false;
            } else {
                if (currentDialog.getOnlyDismissByUser()) {
                    currentDialog.setDismissByHighPriorityDialog(true);
                    mDialogManager.addToPendingDialog(currentDialog);
                }
                if (currentDialog instanceof DialogFragment) {
                    DialogFragment current = (DialogFragment) currentDialog;
                    FragmentManager fm = current.getFragmentManager();
                    if (fm != null && !fm.isStateSaved() && !fm.isDestroyed()) {
                        fm.beginTransaction().remove(current).commit();
                    }
                }
            }
        }
        newDialog.setDismissByHighPriorityDialog(false);
        if (newDialog instanceof DialogFragment) {
            mDialogManager.setPendingShowDialog(newDialog);
            ((DialogFragment) newDialog).show(transaction, PriorityDialog.BASE_DIALOG_TAG);
            mWarpChildFragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
                    if (f.equals(mDialogManager.getPendingShowDialog())) {
                        mDialogManager.setPendingShowDialog(null);
                        mWarpChildFragmentManager.unregisterFragmentLifecycleCallbacks(this);
                    }
                }
            }, false);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public PriorityDialog findCurrentDialog() {
        Fragment fragment = childFragmentManager.findFragmentByTag(PriorityDialog.BASE_DIALOG_TAG);
        if (fragment instanceof PriorityDialog) {
            return (PriorityDialog) fragment;
        }
        for (Fragment childFragment : childFragmentManager.getFragments()) {
            if (childFragment.isVisible() && childFragment instanceof DialogHost) {
                PriorityDialog priorityDialog = ((DialogHost) childFragment).findCurrentDialog();
                if (priorityDialog != null) {
                    return priorityDialog;
                }
            }
        }
        return null;
    }

    @Override
    public void onDialogEvent(@NonNull PriorityDialog priorityDialog, @NonNull Object event) {

    }

    @Override
    public void onCancel(@NonNull PriorityDialog priorityDialog) {

    }

    @Override
    public void dismissCurrentDialog() {
        if (mDialogManager.getCurrentDialog() != null) {
            mDialogManager.getCurrentDialog().dismissCurrent();
        }
    }

    @Override
    public void onDismiss(@NonNull PriorityDialog priorityDialog) {

    }

    @Override
    public void setPendingTransaction(@NonNull WarpBackStackRecord pendingTransaction, boolean isChildFragmentManager) {
        getPendingFragmentActions().addLast(new FragmentAction(pendingTransaction, isChildFragmentManager));
    }

    @Override
    public void setPendingPopBackStack(@NonNull Bundle pendingPopBackStack, boolean isChildFragmentManager) {
        getPendingFragmentActions().addLast(new FragmentAction(pendingPopBackStack, isChildFragmentManager));
    }

    @Override
    public void tryPendingAction() {
        if (mDialogManager.isWindowLockedByDialog()) {
            return;
        }
        if (parentFragmentManager.isStateSaved() || parentFragmentManager.isDestroyed()) {
            return;
        }
        FragmentAction fragmentAction = getPendingFragmentActions().peekFirst();
        while (fragmentAction != null) {
            switch (fragmentAction.type) {
                case FragmentAction.TYPE_TRANSACTION:
                    if (fragmentAction.transaction.tryPendingAction()) {
                        getPendingFragmentActions().removeFirst();
                    } else {
                        return;
                    }
                    break;
                case FragmentAction.TYPE_POP_BACKSTACK:
                    WarpFragmentManager fragmentManager;
                    if (fragmentAction.isChildFragmentManager) {
                        fragmentManager = mWarpChildFragmentManager;
                    } else {
                        fragmentManager = mWarpParentFragmentManager;
                    }
                    if (fragmentManager.tryPendingAction(fragmentAction.popBackStack)) {
                        getPendingFragmentActions().removeFirst();
                    } else {
                        return;
                    }
                    break;
            }
            fragmentAction = getPendingFragmentActions().peekFirst();
        }
        for (Fragment fragment : childFragmentManager.getFragments()) {
            if (fragment instanceof DialogHost) {
                ((DialogHost) fragment).tryPendingAction();
            }
        }
    }

    @CallSuper
    protected void cleanAllPendingAction() {
        pendingFragmentActionMap.remove(uuid);
    }
}
