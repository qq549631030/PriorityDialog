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
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

public abstract class AbsDialogHostImpl implements DialogHost {

    public static String KEY_DIALOG_HOST_STATE = "cn.hx.base.dialogHost.state";
    public static String BASE_DIALOG_HOST_UUID = "cn.hx.base.dialogHost.uuid";

    private static final Map<String, TreeMap<Integer, Stack<PriorityDialog>>> pendingDialogMap = new HashMap();
    private static final Map<String, ArrayDeque<FragmentAction>> pendingFragmentActionMap = new HashMap();

    protected String uuid;
    FragmentManager parentFragmentManager;
    FragmentManager childFragmentManager;
    WarpFragmentManager mWarpParentFragmentManager;
    boolean init = false;

    PriorityDialog pendingShowDialog;
    PriorityDialog pendingDismissDialog;

    private boolean needWarpPendingTransaction = false;

    protected void init(@NonNull String uuid, @NonNull FragmentManager parentFragmentManager, @NonNull FragmentManager childFragmentManager) {
        this.uuid = uuid;
        this.parentFragmentManager = parentFragmentManager;
        this.childFragmentManager = childFragmentManager;
        this.mWarpParentFragmentManager = new WarpFragmentManager(parentFragmentManager, this);
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
                    fragmentAction.transaction = fragmentAction.transaction.warpWithNewFragmentManager(parentFragmentManager, this);
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
    private TreeMap<Integer, Stack<PriorityDialog>> getPendingDialog() {
        TreeMap<Integer, Stack<PriorityDialog>> treeMap = pendingDialogMap.get(uuid);
        if (treeMap != null) {
            return treeMap;
        }
        treeMap = new TreeMap<>();
        pendingDialogMap.put(uuid, treeMap);
        return treeMap;
    }

    private void addToPendingDialog(@NonNull PriorityDialog priorityDialog) {
        Stack<PriorityDialog> stack = getPendingDialog().get(priorityDialog.getPriority());
        if (stack == null) {
            stack = new Stack<>();
            getPendingDialog().put(priorityDialog.getPriority(), stack);
        }
        stack.push(priorityDialog);
    }

    @Nullable
    private PriorityDialog popPendingDialog() {
        Map.Entry<Integer, Stack<PriorityDialog>> lastEntry = getPendingDialog().lastEntry();
        if (lastEntry != null) {
            PriorityDialog dialog;
            if (!lastEntry.getValue().isEmpty()) {
                dialog = lastEntry.getValue().pop();
                if (lastEntry.getValue().isEmpty()) {
                    getPendingDialog().remove(lastEntry.getKey());
                }
                return dialog;
            }
        }
        return null;
    }

    @Override
    public boolean showPriorityDialog(@NonNull PriorityDialog newDialog) {
        if (!init) {
            throw new IllegalStateException("not init, Please call initAsDialogHost first");
        }
        if (childFragmentManager.isStateSaved() || childFragmentManager.isDestroyed()) {
            return false;
        }
        FragmentTransaction transaction = childFragmentManager.beginTransaction();
        PriorityDialog currentDialog = getCurrentDialog();
        if (currentDialog != null) {
            if (newDialog.getPriority() < currentDialog.getPriority()) {
                addToPendingDialog(newDialog);
                return false;
            } else {
                if (currentDialog.getOnlyDismissByUser()) {
                    currentDialog.setDismissByHighPriorityDialog(true);
                    addToPendingDialog(currentDialog);
                }
                if (currentDialog instanceof DialogFragment) {
                    transaction.remove((DialogFragment) currentDialog);
                }
            }
        }
        newDialog.setDismissByHighPriorityDialog(false);
        pendingShowDialog = newDialog;
        childFragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
                if (f == pendingShowDialog) {
                    pendingShowDialog = null;
                    childFragmentManager.unregisterFragmentLifecycleCallbacks(this);
                }
            }
        }, false);
        if (newDialog instanceof DialogFragment) {
            ((DialogFragment) newDialog).show(transaction, PriorityDialog.BASE_DIALOG_TAG);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public PriorityDialog getCurrentDialog() {
        if (pendingShowDialog != null) {
            return pendingShowDialog;
        }
        Fragment fragment = childFragmentManager.findFragmentByTag(PriorityDialog.BASE_DIALOG_TAG);
        if (fragment == null) {
            return null;
        }
        if (fragment instanceof PriorityDialog) {
            PriorityDialog priorityDialog = (PriorityDialog) fragment;
            if (pendingDismissDialog != null && priorityDialog == pendingDismissDialog) {
                return null;
            }
            return priorityDialog;
        }
        return null;
    }

    @Override
    public boolean isWindowLockedByDialog() {
        PriorityDialog currentDialog = getCurrentDialog();
        if (currentDialog != null) {
            if (currentDialog.getLockWindow()) {
                return true;
            }
        }
        List<Fragment> fragments = childFragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment.isVisible() && fragment instanceof DialogHost && ((DialogHost) fragment).isWindowLockedByDialog()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDismiss(@NonNull PriorityDialog priorityDialog) {
        pendingDismissDialog = priorityDialog;
        childFragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                if (f == pendingDismissDialog) {
                    pendingDismissDialog = null;
                    childFragmentManager.unregisterFragmentLifecycleCallbacks(this);
                }
            }
        }, false);
        PriorityDialog pendingDialog = popPendingDialog();
        if (pendingDialog != null) {
            showPriorityDialog(pendingDialog);
        }
        if (!isWindowLockedByDialog()) {
            tryPendingAction();
        }
    }

    @Override
    public void setPendingTransaction(@NonNull WarpBackStackRecord pendingTransaction) {
        getPendingFragmentActions().addLast(new FragmentAction(pendingTransaction));
    }

    @Override
    public void setPendingPopBackStack(@NonNull Bundle pendingPopBackStack) {
        getPendingFragmentActions().addLast(new FragmentAction(pendingPopBackStack));
    }

    @Override
    public void tryPendingAction() {
        if (isWindowLockedByDialog()) {
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
                    if (mWarpParentFragmentManager.tryPendingAction(fragmentAction.popBackStack)) {
                        getPendingFragmentActions().removeFirst();
                    } else {
                        return;
                    }
                    break;
            }
            fragmentAction = getPendingFragmentActions().peekFirst();
        }
    }

    @CallSuper
    protected void cleanAllPending() {
        pendingDialogMap.remove(uuid);
        pendingFragmentActionMap.remove(uuid);
    }

}
