package cn.hx.prioritydialog;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class PriorityDialogManager {
    private static final String BASE_DIALOG_MANAGER_UUID = "cn.hx.base.dialogManager.uuid";
    private static final Map<String, DialogHost> dialogHostMap = new HashMap<>();
    private static final Map<String, TreeMap<Integer, LinkedList<PriorityDialog>>> pendingDialogMap = new HashMap<>();
    private static final Map<String, ArrayDeque<ActivityAction>> pendingActivityActionMap = new HashMap<>();
    private static final Map<String, ArrayDeque<FragmentAction>> pendingFragmentActionMap = new HashMap<>();

    static final Map<String, OnCancelListener<? extends DialogHost>> onCancelListenerMap = new HashMap<>();
    static final Map<String, OnDismissListener<? extends DialogHost>> onDismissListenerMap = new HashMap<>();
    static final Map<String, OnDialogEventListener<? extends DialogHost>> onDialogEventListenerMap = new HashMap<>();

    private static PriorityStrategy mPriorityStrategy = new DefaultPriorityStrategy();

    private String managerUuid;
    FragmentActivity activity;

    PriorityDialog pendingShowDialog;

    public static void init(Application application, @NonNull PriorityStrategy priorityStrategy) {
        mPriorityStrategy = priorityStrategy;
        init(application);
    }

    public static void init(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                if (activity instanceof DialogManager && activity instanceof FragmentActivity) {
                    PriorityDialogManager dialogManager = ((DialogManager) activity).getPriorityDialogManager();
                    FragmentActivity fragmentActivity = (FragmentActivity) activity;
                    //init DialogManager
                    dialogManager.initAsDialogManager(fragmentActivity, savedInstanceState);
                    if (activity instanceof DialogHost) {//init Activity DialogHost
                        DialogHost dialogHost = (DialogHost) activity;
                        dialogHost.getPriorityDialogHost().initAsDialogHost(dialogManager, fragmentActivity.getSupportFragmentManager(), fragmentActivity.getSupportFragmentManager(), savedInstanceState);
                        dialogManager.registerDialogHost(dialogHost.getUuid(), dialogHost);
                    }
                }
            }

            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
                if (activity instanceof DialogManager && activity instanceof FragmentActivity) {
                    PriorityDialogManager dialogManager = ((DialogManager) activity).getPriorityDialogManager();
                    //save the manager uuid
                    bundle.putString(BASE_DIALOG_MANAGER_UUID, dialogManager.managerUuid);
                    if (activity instanceof DialogHost) {
                        // save activity host uuid
                        bundle.putString(DialogHost.BASE_DIALOG_HOST_UUID, ((DialogHost) activity).getUuid());
                    }
                }
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
            }
        });
    }

    public static void updatePriorityStrategy(@NonNull PriorityStrategy priorityStrategy) {
        mPriorityStrategy = priorityStrategy;
    }

    /**
     * init dialog manager
     */
    void initAsDialogManager(@NonNull FragmentActivity activity, @Nullable Bundle savedInstanceState) {
        this.activity = activity;
        if (savedInstanceState != null) {
            managerUuid = savedInstanceState.getString(BASE_DIALOG_MANAGER_UUID);
        }
        if (managerUuid == null) {
            managerUuid = UUID.randomUUID().toString();
        }
        activity.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (activity instanceof DialogHost) {
                DialogHost dialogHost = (DialogHost) activity;
                if (event == Lifecycle.Event.ON_CREATE && savedInstanceState != null) {
                    //reassign FragmentManager for pending transactions when recreate
                    tryWarpPendingTransaction(dialogHost.getUuid(), activity.getSupportFragmentManager(), activity.getSupportFragmentManager());
                }
                if (event == Lifecycle.Event.ON_DESTROY && activity.isFinishing()) {
                    //clean activity dialogHost
                    unregisterDialogHost(dialogHost.getUuid());
                }
            }

        });
        activity.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentPreCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
                if (f instanceof DialogHost) {//init Fragment DialogHost
                    DialogHost dialogHost = (DialogHost) f;
                    dialogHost.getPriorityDialogHost().initAsDialogHost(PriorityDialogManager.this, f.requireFragmentManager(), f.getChildFragmentManager(), savedInstanceState);
                    registerDialogHost(dialogHost.getUuid(), dialogHost);
                }
                if (f instanceof PriorityDialog && f instanceof DialogFragment) {//init PriorityDialog
                    PriorityDialog priorityDialog = (PriorityDialog) f;
                    if (savedInstanceState != null) {
                        priorityDialog.setUuid(savedInstanceState.getString(PriorityDialog.BASE_DIALOG_UUID));
                        priorityDialog.setHostUuid(savedInstanceState.getString(PriorityDialog.BASE_DIALOG_HOST_UUID));
                        priorityDialog.setPriority(savedInstanceState.getInt(PriorityDialog.BASE_DIALOG_PRIORITY, 0));
                        priorityDialog.setOnlyDismissByUser(savedInstanceState.getBoolean(PriorityDialog.BASE_DIALOG_ONLY_DISMISS_BY_USER, true));
                        priorityDialog.setLockWindow(savedInstanceState.getBoolean(PriorityDialog.BASE_DIALOG_LOCK_WINDOW));
                        priorityDialog.setSupportRecreate(savedInstanceState.getBoolean(PriorityDialog.BASE_DIALOG_SUPPORT_RECREATE, true));
                        if (!priorityDialog.isSupportRecreate()) {
                            //dismiss when un support recreate
                            ((DialogFragment) f).dismiss();
                        }
                    }
                    f.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
                        if (event == Lifecycle.Event.ON_START) {
                            Dialog dialog = ((DialogFragment) f).getDialog();
                            if (dialog != null) {
                                //override the DialogFragment onCancelListener
                                dialog.setOnCancelListener(dialogInterface -> {
                                    ((DialogFragment) f).onCancel(dialogInterface);
                                    dispatchDialogCancel((PriorityDialog) f);
                                });
                            }
                        }
                    });
                }
            }

            @Override
            public void onFragmentCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
                if (f instanceof DialogHost && savedInstanceState != null) {
                    //reassign FragmentManager for pending transactions when recreate
                    tryWarpPendingTransaction(((DialogHost) f).getUuid(), f.getFragmentManager(), f.getChildFragmentManager());
                }
            }

            @Override
            public void onFragmentSaveInstanceState(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Bundle outState) {
                if (f instanceof DialogHost) {
                    //save fragment host uuid when recreate
                    outState.putString(DialogHost.BASE_DIALOG_HOST_UUID, ((DialogHost) f).getUuid());
                }
                if (f instanceof PriorityDialog) {
                    //save dialog info when recreate
                    outState.putString(PriorityDialog.BASE_DIALOG_UUID, ((PriorityDialog) f).getUuid());
                    outState.putString(PriorityDialog.BASE_DIALOG_HOST_UUID, ((PriorityDialog) f).getHostUuid());
                    outState.putInt(PriorityDialog.BASE_DIALOG_PRIORITY, ((PriorityDialog) f).getPriority());
                    outState.putBoolean(PriorityDialog.BASE_DIALOG_ONLY_DISMISS_BY_USER, ((PriorityDialog) f).getOnlyDismissByUser());
                    outState.putBoolean(PriorityDialog.BASE_DIALOG_LOCK_WINDOW, ((PriorityDialog) f).getLockWindow());
                    outState.putBoolean(PriorityDialog.BASE_DIALOG_SUPPORT_RECREATE, ((PriorityDialog) f).isSupportRecreate());
                }
            }

            @Override
            public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                if (f instanceof DialogHost) {
                    if (f.requireActivity().isFinishing() || (f.isRemoving() && !f.isStateSaved())) {
                        unregisterDialogHost(((DialogHost) f).getUuid());
                    }
                }
                if (f instanceof PriorityDialog && f instanceof DialogFragment) {
                    boolean reallyDismiss = false;
                    if (f.getParentFragment() != null) {
                        if (f.getParentFragment().isRemoving()) {
                            reallyDismiss = true;//fragment Destroy lead dialog destroy
                        }
                    } else if (f.requireActivity().isFinishing()) {
                        reallyDismiss = true;//Activity Destroy lead dialog destroy
                    }
                    if (!reallyDismiss) {
                        if (f.isRemoving() && !f.isStateSaved() && !((PriorityDialog) f).isDismissByHighPriorityDialog()) {
                            reallyDismiss = true;//dismiss by user
                        }
                    }
                    if (reallyDismiss) {
                        dispatchDialogDismiss(((PriorityDialog) f));
                        ((PriorityDialog) f).onReallyDismiss();
                        onCancelListenerMap.remove(((PriorityDialog) f).getUuid());
                        onDismissListenerMap.remove(((PriorityDialog) f).getUuid());
                        onDialogEventListenerMap.remove(((PriorityDialog) f).getUuid());
                        tryShowNextPendingDialog();
                        tryAllPendingAction();
                    }
                }
            }
        }, true);
    }

    @NonNull
    PriorityStrategy getPriorityStrategy() {
        return mPriorityStrategy;
    }

    void registerDialogHost(@NonNull String uuid, @NonNull DialogHost dialogHost) {
        dialogHostMap.put(uuid, dialogHost);
    }

    void unregisterDialogHost(@NonNull String uuid) {
        dialogHostMap.remove(uuid);
        pendingActivityActionMap.remove(uuid);
        pendingFragmentActionMap.remove(uuid);
        //remove related pendingDialog
        TreeMap<Integer, LinkedList<PriorityDialog>> treeMap = getPendingDialog();
        Map.Entry<Integer, LinkedList<PriorityDialog>> lastEntry = treeMap.lastEntry();
        while (lastEntry != null) {
            LinkedList<PriorityDialog> linkedList = lastEntry.getValue();
            if (!linkedList.isEmpty()) {
                ListIterator<PriorityDialog> listIterator = linkedList.listIterator(linkedList.size());
                while (listIterator.hasPrevious()) {
                    PriorityDialog priorityDialog = listIterator.previous();
                    if (uuid.equals(priorityDialog.getHostUuid())) {
                        listIterator.remove();
                    }
                }
            }
            if (linkedList.isEmpty()) {
                treeMap.remove(lastEntry.getKey());
                lastEntry = treeMap.lastEntry();
            } else {
                lastEntry = treeMap.lowerEntry(lastEntry.getKey());
            }
        }
    }

    PriorityDialog getPendingShowDialog() {
        return pendingShowDialog;
    }

    void setPendingShowDialog(PriorityDialog pendingShowDialog) {
        this.pendingShowDialog = pendingShowDialog;
    }

    @Nullable
    PriorityDialog getCurrentDialog() {
        if (pendingShowDialog != null) {
            return pendingShowDialog;
        }
        return findCurrentDialog();
    }

    @Nullable
    private PriorityDialog findCurrentDialog() {
        if (activity instanceof DialogHost) {
            Fragment dialog = activity.getSupportFragmentManager().findFragmentByTag(PriorityDialog.BASE_DIALOG_TAG);
            if (dialog instanceof PriorityDialog && !dialog.isRemoving() && !dialog.isStateSaved()) {
                return (PriorityDialog) dialog;
            }
        }
        for (Fragment child : activity.getSupportFragmentManager().getFragments()) {
            PriorityDialog childDialog = findCurrentDialogInFragment(child);
            if (childDialog != null) {
                return childDialog;
            }
        }

        return null;
    }

    @Nullable
    private PriorityDialog findCurrentDialogInFragment(Fragment fragment) {
        if (fragment instanceof DialogHost) {
            Fragment dialog = fragment.getChildFragmentManager().findFragmentByTag(PriorityDialog.BASE_DIALOG_TAG);
            if (dialog instanceof PriorityDialog && !dialog.isRemoving() && !dialog.isStateSaved()) {
                return (PriorityDialog) dialog;
            }
            for (Fragment child : fragment.getChildFragmentManager().getFragments()) {
                PriorityDialog childDialog = findCurrentDialogInFragment(child);
                if (childDialog != null) {
                    return childDialog;
                }
            }
        }
        return null;
    }

    @NonNull
    private TreeMap<Integer, LinkedList<PriorityDialog>> getPendingDialog() {
        TreeMap<Integer, LinkedList<PriorityDialog>> treeMap = pendingDialogMap.get(managerUuid);
        if (treeMap != null) {
            return treeMap;
        }
        treeMap = new TreeMap<>();
        pendingDialogMap.put(managerUuid, treeMap);
        return treeMap;
    }

    synchronized void addToPendingDialog(@NonNull PriorityDialog priorityDialog) {
        if (priorityDialog.getHostUuid() == null) {
            return;
        }
        LinkedList<PriorityDialog> linkedList = getPendingDialog().get(priorityDialog.getPriority());
        if (linkedList == null) {
            linkedList = new LinkedList<>();
            getPendingDialog().put(priorityDialog.getPriority(), linkedList);
        }
        linkedList.addLast(priorityDialog);
    }

    synchronized void tryShowNextPendingDialog() {
        if (isWindowLockedByDialog()) {
            return;
        }
        TreeMap<Integer, LinkedList<PriorityDialog>> treeMap = getPendingDialog();
        Map.Entry<Integer, LinkedList<PriorityDialog>> lastEntry = treeMap.lastEntry();
        while (lastEntry != null) {
            LinkedList<PriorityDialog> linkedList = lastEntry.getValue();
            if (!linkedList.isEmpty()) {
                LinkedList<PriorityDialog> temp = new LinkedList<>(linkedList);
                PriorityDialog priorityDialog;
                if (mPriorityStrategy.firstInFirstOutWhenSamePriority()) {
                    priorityDialog = temp.pollFirst();
                } else {
                    priorityDialog = temp.pollLast();
                }
                while (priorityDialog != null) {
                    String hostUuid = priorityDialog.getHostUuid();
                    if (hostUuid != null) {
                        DialogHost dialogHost = dialogHostMap.get(hostUuid);
                        if (dialogHost != null) {
                            if (isReady(hostUuid) && canPendingDialogShow(priorityDialog)) {
                                if (dialogHost.showPriorityDialog(priorityDialog)) {
                                    linkedList.remove(priorityDialog);
                                    if (linkedList.isEmpty()) {
                                        treeMap.remove(lastEntry.getKey());
                                    }
                                    return;
                                }
                            }
                        }
                    }
                    if (mPriorityStrategy.firstInFirstOutWhenSamePriority()) {
                        priorityDialog = temp.pollFirst();
                    } else {
                        priorityDialog = temp.pollLast();
                    }
                }
                lastEntry = treeMap.lowerEntry(lastEntry.getKey());
            } else {
                treeMap.remove(lastEntry.getKey());
                lastEntry = treeMap.lastEntry();
            }
        }
    }

    private boolean canPendingDialogShow(@NonNull PriorityDialog newDialog) {
        PriorityDialog currentDialog = getCurrentDialog();
        if (currentDialog != null) {
            if (mPriorityStrategy.canNewShow(currentDialog, newDialog)) {
                return true;
            }
        }
        return true;
    }

    boolean isReady(String hostUuid) {
        DialogHost dialogHost = dialogHostMap.get(hostUuid);
        if (dialogHost instanceof Fragment) {
            Fragment fragment = (Fragment) dialogHost;
            if (fragment.isRemoving() || fragment.isStateSaved() || !fragment.isVisible()) {
                return false;
            }
        }
        return !activity.isFinishing();
    }

    @NonNull
    ArrayDeque<ActivityAction> getPendingActivityActions(@NonNull String hostUuid) {
        ArrayDeque<ActivityAction> activityActions = pendingActivityActionMap.get(hostUuid);
        if (activityActions != null) {
            return activityActions;
        }
        activityActions = new ArrayDeque<>();
        pendingActivityActionMap.put(hostUuid, activityActions);
        return activityActions;
    }

    @NonNull
    ArrayDeque<FragmentAction> getPendingFragmentActions(@NonNull String hostUuid) {
        ArrayDeque<FragmentAction> fragmentActions = pendingFragmentActionMap.get(hostUuid);
        if (fragmentActions != null) {
            return fragmentActions;
        }
        fragmentActions = new ArrayDeque<>();
        pendingFragmentActionMap.put(hostUuid, fragmentActions);
        return fragmentActions;
    }

    void setPendingFragmentAction(@NonNull FragmentAction fragmentAction) {
        getPendingFragmentActions(fragmentAction.hostUuid).addLast(fragmentAction);
    }

    void tryWarpPendingTransaction(String hostUuid, FragmentManager parentFragmentManager, FragmentManager childFragmentManager) {
        for (FragmentAction fragmentAction : getPendingFragmentActions(hostUuid)) {
            if (fragmentAction.type == FragmentAction.TYPE_TRANSACTION) {
                FragmentManager fragmentManager;
                if (fragmentAction.isChildFragmentManager) {
                    fragmentManager = childFragmentManager;
                } else {
                    fragmentManager = parentFragmentManager;
                }
                fragmentAction.transaction = fragmentAction.transaction.warpWithNewFragmentManager(fragmentManager, this, hostUuid);
            }
        }
    }

    void tryAllPendingAction() {
        if (isWindowLockedByDialog()) {
            return;
        }
        for (Fragment fragment : activity.getSupportFragmentManager().getFragments()) {
            tryPendingFragmentActionsForFragment(fragment);
        }
        tryPendingFragmentActionsForActivity();
        tryPendingActivityActions();
    }

    private void tryPendingFragmentActionsForActivity() {
        if (activity.isFinishing()) {
            return;
        }
        if (activity instanceof DialogHost) {
            tryPendingFragmentActions((DialogHost) activity);
        }
    }

    private void tryPendingFragmentActionsForFragment(Fragment fragment) {
        FragmentManager fragmentFragmentManager = fragment.getFragmentManager();
        if (fragmentFragmentManager != null) {
            if (fragmentFragmentManager.isStateSaved() || fragmentFragmentManager.isDestroyed()) {
                return;
            }
        }
        if (fragment instanceof DialogHost) {
            tryPendingFragmentActions((DialogHost) fragment);
        }
        for (Fragment child : fragment.getChildFragmentManager().getFragments()) {
            tryPendingFragmentActionsForFragment(child);
        }
    }

    private void tryPendingFragmentActions(DialogHost dialogHost) {
        ArrayDeque<FragmentAction> fragmentActions = pendingFragmentActionMap.get(dialogHost.getUuid());
        if (fragmentActions != null) {
            FragmentAction fragmentAction = fragmentActions.peekFirst();
            while (fragmentAction != null) {
                switch (fragmentAction.type) {
                    case FragmentAction.TYPE_TRANSACTION:
                        if (fragmentAction.transaction.tryPendingAction()) {
                            fragmentActions.removeFirst();
                        } else {
                            return;
                        }
                        break;
                    case FragmentAction.TYPE_POP_BACKSTACK:
                        WarpFragmentManager fragmentManager;
                        if (fragmentAction.isChildFragmentManager) {
                            fragmentManager = dialogHost.getWarpChildFragmentManager();
                        } else {
                            fragmentManager = dialogHost.getWarpParentFragmentManager();
                        }
                        if (fragmentManager.tryPendingAction(fragmentAction.popBackStack)) {
                            fragmentActions.removeFirst();
                        } else {
                            return;
                        }
                        break;
                }
                fragmentAction = fragmentActions.peekFirst();
            }
        }
    }

    private void tryPendingActivityActions() {
        if (activity instanceof DialogHost) {
            DialogHost dialogHost = (DialogHost) activity;
            ArrayDeque<ActivityAction> activityActions = pendingActivityActionMap.remove(dialogHost.getUuid());
            if (activityActions != null && !activityActions.isEmpty()) {
                ActivityAction activityAction = activityActions.pollFirst();
                while (activityAction != null) {
                    switch (activityAction.type) {
                        case ActivityAction.TYPE_START_ACTIVITY:
                            activity.startActivityForResult(activityAction.intent, activityAction.requestCode, activityAction.options);
                            break;
                        case ActivityAction.TYPE_FINISH_ACTIVITY:
                            activity.finish();
                            break;
                    }
                    activityAction = activityActions.pollFirst();
                }
            }
        }
    }

    public boolean isWindowLockedByDialog() {
        if (getCurrentDialog() != null) {
            return getCurrentDialog().getLockWindow();
        }
        return false;
    }

    static void dispatchDialogEvent(@NonNull PriorityDialog dialog, @NonNull Object event) {
        DialogHost dialogHost = dialogHostMap.get(dialog.getHostUuid());
        if (dialogHost != null) {
            dialogHost.onDialogEvent(dialog, event);
            OnDialogEventListener<? extends DialogHost> listener = onDialogEventListenerMap.get(dialog.getUuid());
            if (listener != null) {
                listener.dispatch(dialogHost, event);
            }
        }
    }

    static void dispatchDialogCancel(@NonNull PriorityDialog dialog) {
        DialogHost dialogHost = dialogHostMap.get(dialog.getHostUuid());
        if (dialogHost != null) {
            dialogHost.onCancel(dialog);
            OnCancelListener<? extends DialogHost> listener = onCancelListenerMap.get(dialog.getUuid());
            if (listener != null) {
                listener.dispatch(dialogHost);
            }
        }
    }

    static void dispatchDialogDismiss(@NonNull PriorityDialog dialog) {
        DialogHost dialogHost = dialogHostMap.get(dialog.getHostUuid());
        if (dialogHost != null) {
            dialogHost.onDismiss(dialog);
            OnDismissListener<? extends DialogHost> listener = onDismissListenerMap.get(dialog.getUuid());
            if (listener != null) {
                listener.dispatch(dialogHost);
            }
        }
    }
}
