package cn.hx.prioritydialog;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PriorityDialogManager {

    private static final String TAG = "PriorityDialogManager";

    private static final String PENDING_DIALOGS = "cn.hx.base.dialogManager.pendingDialogs";
    private static final String PENDING_ACTIVITY_ACTIONS = "cn.hx.base.dialogManager.pendingActivityActions";
    private static final String PENDING_FRAGMENT_ACTIONS = "cn.hx.base.dialogManager.pendingFragmentActions";
    private static PriorityStrategy globalPriorityStrategy = new DefaultPriorityStrategy();

    private final Map<String, DialogHost> dialogHostMap = new HashMap<>();
    private final Map<String, PriorityDialog> cachePendingDialogMap = new TreeMap<>();
    private final TreeMap<Integer, LinkedList<PendingDialogState>> pendingDialogMap = new TreeMap<>();
    private final Map<String, ArrayDeque<ActivityAction>> pendingActivityActionMap = new HashMap<>();
    private final Map<String, ArrayDeque<FragmentAction>> pendingFragmentActionMap = new HashMap<>();

    static final Map<String, Set<String>> pendingDismissDialogUuidMap = new HashMap<>();
    static final Map<String, OnCancelListener> onCancelListenerMap = new HashMap<>();
    static final Map<String, OnDismissListener> onDismissListenerMap = new HashMap<>();
    static final Map<String, OnDialogEventListener> onDialogEventListenerMap = new HashMap<>();

    FragmentActivity activity;

    PriorityDialog pendingShowDialog;

    private PriorityStrategy currentPriorityStrategy = null;

    private Set<PriorityDialogListener> priorityDialogListeners = new HashSet<>();

    public static void init(Context context, @NonNull PriorityStrategy priorityStrategy) {
        globalPriorityStrategy = priorityStrategy;
        init(context);
    }

    public static void init(Context context) {
        ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
                if (activity instanceof DialogManager && activity instanceof FragmentActivity) {
                    PriorityDialogManager dialogManager = ((DialogManager) activity).getPriorityDialogManager();
                    FragmentActivity fragmentActivity = (FragmentActivity) activity;
                    //init DialogManager
                    dialogManager.init(fragmentActivity, bundle);
                    if (activity instanceof DialogHost) {//init Activity DialogHost
                        DialogHost dialogHost = (DialogHost) activity;
                        dialogHost.getPriorityDialogHostDelegate().init(dialogManager, fragmentActivity.getSupportFragmentManager(), fragmentActivity.getSupportFragmentManager(), bundle);
                        dialogManager.registerDialogHost(dialogHost.getUuid(), dialogHost);
                    }
                }
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
                    dialogManager.savePendingDialogs(bundle);
                    dialogManager.savePendingActivityActions(bundle);
                    dialogManager.savePendingFragmentActions(bundle);
                    if (activity instanceof DialogHost) {
                        // save activity host uuid
                        bundle.putString(DialogHost.BASE_DIALOG_HOST_UUID, ((DialogHost) activity).getUuid());
                    }
                }
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                if (activity instanceof DialogHost) {
                    DialogHost dialogHost = (DialogHost) activity;
                    //clean activity dialogHost
                    dialogHost.getPriorityDialogHostDelegate().mDialogManager.unregisterDialogHost(dialogHost.getUuid());
                    if (activity.isFinishing()) {
                        //remove related pendingDialog
                        dialogHost.getPriorityDialogHostDelegate().mDialogManager.removePendingDialogByHostUuid(dialogHost.getUuid());
                        pendingDismissDialogUuidMap.remove(dialogHost.getUuid());
                    }
                }
            }
        });
    }

    public static void updatePriorityStrategy(@NonNull PriorityStrategy priorityStrategy) {
        globalPriorityStrategy = priorityStrategy;
    }

    public void setCurrentPriorityStrategy(@Nullable PriorityStrategy priorityStrategy) {
        currentPriorityStrategy = priorityStrategy;
    }

    public void registerPriorityDialogListener(@NonNull PriorityDialogListener priorityDialogListener) {
        priorityDialogListeners.add(priorityDialogListener);
    }

    public void unregisterPriorityDialogListener(@NonNull PriorityDialogListener priorityDialogListener) {
        priorityDialogListeners.remove(priorityDialogListener);
    }

    /**
     * init dialog manager
     */
    void init(@NonNull FragmentActivity activity, @Nullable Bundle savedInstanceState) {
        this.activity = activity;
        restorePendingDialogs(savedInstanceState);
        restorePendingActivityActions(savedInstanceState);
        restorePendingFragmentActions(savedInstanceState);
        activity.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
                if (f instanceof DialogHost) {//init Fragment DialogHost
                    DialogHost dialogHost = (DialogHost) f;
                    if (savedInstanceState != null) {
                        Bundle arguments = f.getArguments();
                        if (arguments != null) {
                            String uuid = arguments.getString(DialogHost.BASE_DIALOG_HOST_UUID);
                            if (uuid != null) {
                                savedInstanceState.putString(DialogHost.BASE_DIALOG_HOST_UUID, uuid);
                            }
                        }
                    }

                    dialogHost.getPriorityDialogHostDelegate().init(PriorityDialogManager.this, f.requireFragmentManager(), f.getChildFragmentManager(), savedInstanceState);
                    if (savedInstanceState == null) {
                        Bundle arguments = f.getArguments();
                        if (arguments == null) {
                            arguments = new Bundle();
                            f.setArguments(arguments);
                        }
                        arguments.putString(DialogHost.BASE_DIALOG_HOST_UUID, dialogHost.getUuid());
                    }
                    registerDialogHost(dialogHost.getUuid(), dialogHost);
                }
                if (f instanceof PriorityDialog && f instanceof DialogFragment) {//init PriorityDialog
                    PriorityDialog priorityDialog = (PriorityDialog) f;
                    priorityDialog.getPriorityDialogDelegate().init(PriorityDialogManager.this);
                    if (priorityDialog.equals(pendingShowDialog)) {
                        pendingShowDialog = null;
                    }
                    if (savedInstanceState != null) {
                        PriorityDialogConfig savedConfig = null;
                        Bundle arguments = f.getArguments();
                        if (arguments != null) {
                            savedConfig = arguments.getParcelable(PriorityDialog.BASE_DIALOG_CONFIG);
                        }
                        if (savedConfig != null) {
                            priorityDialog.getPriorityConfig().copyFrom(savedConfig);
                        }
                        PriorityDialogConfig config = priorityDialog.getPriorityConfig();
                        priorityDialog.setOnCancelListener(onCancelListenerMap.remove(config.getUuid()));
                        priorityDialog.setOnDismissListener(onDismissListenerMap.remove(config.getUuid()));
                        priorityDialog.setOnDialogEventListener(onDialogEventListenerMap.remove(config.getUuid()));
                        if (!config.isSupportRecreate()) {
                            //dismiss when un support recreate
                            ((DialogFragment) f).dismiss();
                            return;
                        }
                        if (isPendingDismissDialog(config.getHostUuid(), config.getUuid())) {
                            ((DialogFragment) f).dismiss();
                            removePendingDismissDialog(config.getHostUuid(), config.getUuid());
                            return;
                        }
                    }
                    for (PriorityDialogListener listener : priorityDialogListeners) {
                        listener.onDialogShow(priorityDialog);
                    }
                }
            }

            @Override
            public void onFragmentStarted(@NonNull FragmentManager fm, @NonNull Fragment f) {
                super.onFragmentStarted(fm, f);
                if (f instanceof PriorityDialog && f instanceof DialogFragment) {
                    Dialog dialog = ((DialogFragment) f).getDialog();
                    if (dialog != null) {
                        //override the DialogFragment onCancelListener
                        dialog.setOnCancelListener(dialogInterface -> {
                            ((DialogFragment) f).onCancel(dialogInterface);
                            dispatchDialogCancel((PriorityDialog) f);
                        });
                    }
                }
            }

            @Override
            public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                if (f instanceof DialogHost) {
                    DialogHost dialogHost = (DialogHost) f;
                    unregisterDialogHost(dialogHost.getUuid());
                    if (f.requireActivity().isFinishing() || f.isRemoving()) {
                        //remove related pendingDialog
                        removePendingDialogByHostUuid(dialogHost.getUuid());
                        pendingDismissDialogUuidMap.remove(dialogHost.getUuid());
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
                        if (f.isRemoving() && !((PriorityDialog) f).isDismissByHighPriorityDialog()) {
                            reallyDismiss = true;//dismiss by user
                        }
                    }
                    PriorityDialogConfig config = ((PriorityDialog) f).getPriorityConfig();
                    if (reallyDismiss) {
                        dispatchDialogDismiss((PriorityDialog) f);
                        for (PriorityDialogListener listener : priorityDialogListeners) {
                            listener.onDialogDismiss((PriorityDialog) f);
                        }
                        tryShowNextPendingDialog(true, false);
                        tryAllPendingAction();
                    } else {
                        onCancelListenerMap.put(config.getUuid(), ((PriorityDialog) f).getPriorityDialogDelegate().getOnCancelListener());
                        onDismissListenerMap.put(config.getUuid(), ((PriorityDialog) f).getPriorityDialogDelegate().getOnDismissListener());
                        onDialogEventListenerMap.put(config.getUuid(), ((PriorityDialog) f).getPriorityDialogDelegate().getOnDialogEventListener());
                    }
                }
            }
        }, true);
    }

    @NonNull
    PriorityStrategy getPriorityStrategy() {
        if (currentPriorityStrategy != null) {
            return currentPriorityStrategy;
        }
        return globalPriorityStrategy;
    }

    void registerDialogHost(@NonNull String uuid, @NonNull DialogHost dialogHost) {
        dialogHostMap.put(uuid, dialogHost);
    }

    void unregisterDialogHost(@NonNull String hostUuid) {
        dialogHostMap.remove(hostUuid);
        pendingActivityActionMap.remove(hostUuid);
        pendingFragmentActionMap.remove(hostUuid);
    }

    private void removePendingDialogByHostUuid(String hostUuid) {
        Map.Entry<Integer, LinkedList<PendingDialogState>> lastEntry = pendingDialogMap.lastEntry();
        while (lastEntry != null) {
            LinkedList<PendingDialogState> linkedList = lastEntry.getValue();
            if (!linkedList.isEmpty()) {
                ListIterator<PendingDialogState> listIterator = linkedList.listIterator(linkedList.size());
                while (listIterator.hasPrevious()) {
                    PendingDialogState dialogInfo = listIterator.previous();
                    if (hostUuid.equals(dialogInfo.config.getHostUuid())) {
                        PriorityDialog removed = cachePendingDialogMap.remove(dialogInfo.config.getUuid());
                        if (removed != null) {
                            dispatchDialogDismiss(removed);
                        } else {
                            removeDialogListeners(dialogInfo.config.getUuid());
                        }
                        listIterator.remove();
                    }
                }
            }
            if (linkedList.isEmpty()) {
                pendingDialogMap.remove(lastEntry.getKey());
                lastEntry = pendingDialogMap.lastEntry();
            } else {
                lastEntry = pendingDialogMap.lowerEntry(lastEntry.getKey());
            }
        }
    }

    boolean removePendingDialogByUuid(String uuid) {
        Map.Entry<Integer, LinkedList<PendingDialogState>> lastEntry = pendingDialogMap.lastEntry();
        while (lastEntry != null) {
            LinkedList<PendingDialogState> linkedList = lastEntry.getValue();
            if (!linkedList.isEmpty()) {
                ListIterator<PendingDialogState> listIterator = linkedList.listIterator(linkedList.size());
                while (listIterator.hasPrevious()) {
                    PendingDialogState dialogInfo = listIterator.previous();
                    if (uuid.equals(dialogInfo.config.getUuid())) {
                        PriorityDialog removed = cachePendingDialogMap.remove(dialogInfo.config.getUuid());
                        if (removed != null) {
                            dispatchDialogDismiss(removed);
                        } else {
                            removeDialogListeners(dialogInfo.config.getUuid());
                        }
                        listIterator.remove();
                        return true;
                    }
                }
            }
            if (linkedList.isEmpty()) {
                pendingDialogMap.remove(lastEntry.getKey());
                lastEntry = pendingDialogMap.lastEntry();
            } else {
                lastEntry = pendingDialogMap.lowerEntry(lastEntry.getKey());
            }
        }
        return false;
    }

    @Nullable
    DialogHost getDialogHost(String hostUuid) {
        return dialogHostMap.get(hostUuid);
    }

    void setPendingShowDialog(PriorityDialog pendingShowDialog) {
        this.pendingShowDialog = pendingShowDialog;
    }

    @Nullable
    PriorityDialog getCurrentDialog() {
        if (pendingShowDialog != null) {
            return pendingShowDialog;
        }
        return findCurrentDialog(activity.getSupportFragmentManager());
    }

    @Nullable
    private PriorityDialog findCurrentDialog(@NonNull FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment instanceof DialogHost) {
                if (fragment.getHost() == null) {
                    continue;
                }
                PriorityDialog dialog = findCurrentDialog(fragment.getChildFragmentManager());
                if (dialog != null) {
                    return dialog;
                }
            }
            if (fragment instanceof PriorityDialog && !fragment.isRemoving()) {
                return (PriorityDialog) fragment;
            }
        }
        return null;
    }

    void dismissCurrentDialog(boolean allowStateLoss) {
        PriorityDialog currentDialog = getCurrentDialog();
        if (currentDialog != null) {
            dismissDialog(currentDialog.getPriorityConfig().getHostUuid(), currentDialog.getPriorityConfig().getUuid(), allowStateLoss);
        }
    }

    void dismissDialog(String hostUuid, @NonNull String uuid, boolean allowStateLoss) {
        PriorityDialog currentDialog = getCurrentDialog();
        if (currentDialog != null && uuid.equals(currentDialog.getPriorityConfig().getUuid())) {
            if (currentDialog instanceof DialogFragment) {
                if (allowStateLoss) {
                    ((DialogFragment) currentDialog).dismissAllowingStateLoss();
                } else {
                    ((DialogFragment) currentDialog).dismiss();
                }
            }
        }
        boolean removed = removePendingDialogByUuid(uuid);
        if (!removed) {
            addPendingDismissDialog(hostUuid, uuid);
        }
    }

    void addPendingDismissDialog(String hostUuid, String uuid) {
        Set<String> dialogUuids = pendingDismissDialogUuidMap.get(hostUuid);
        if (dialogUuids == null) {
            dialogUuids = new HashSet<>();
            pendingDismissDialogUuidMap.put(hostUuid, dialogUuids);
        }
        dialogUuids.add(uuid);
    }

    void removePendingDismissDialog(String hostUuid, String uuid) {
        Set<String> dialogUuids = pendingDismissDialogUuidMap.get(hostUuid);
        if (dialogUuids != null) {
            dialogUuids.remove(uuid);
        }
    }

    boolean isPendingDismissDialog(String hostUuid, String uuid) {
        Set<String> dialogUuids = pendingDismissDialogUuidMap.get(hostUuid);
        if (dialogUuids != null) {
            return dialogUuids.contains(uuid);
        }
        return false;
    }

    synchronized void addToPendingDialog(@NonNull PriorityDialog priorityDialog) {
        if (priorityDialog.getPriorityConfig().getHostUuid() == null || !(priorityDialog instanceof DialogFragment)) {
            return;
        }
        FragmentStateData fragmentStateData = FragmentUtil.saveFragment((Fragment) priorityDialog);
        if (fragmentStateData != null) {
            removePendingDialogByUuid(priorityDialog.getPriorityConfig().getUuid());
            LinkedList<PendingDialogState> linkedList = pendingDialogMap.get(priorityDialog.getPriorityConfig().getPriority());
            if (linkedList == null) {
                linkedList = new LinkedList<>();
                pendingDialogMap.put(priorityDialog.getPriorityConfig().getPriority(), linkedList);
            }
            linkedList.addLast(new PendingDialogState(priorityDialog.getPriorityConfig(), fragmentStateData));
            cachePendingDialogMap.put(priorityDialog.getPriorityConfig().getUuid(), priorityDialog);
            for (PriorityDialogListener listener : priorityDialogListeners) {
                listener.onDialogAddToPending(priorityDialog);
            }
        }
    }

    @NonNull
    synchronized List<PendingDialogState> getAllPendingDialog() {
        ArrayList<PendingDialogState> list = new ArrayList<>();
        Collection<LinkedList<PendingDialogState>> values = pendingDialogMap.descendingMap().values();
        for (LinkedList<PendingDialogState> value : values) {
            list.addAll(value);
        }
        return list;
    }

    synchronized void tryShowNextPendingDialog(boolean afterPreDismiss, boolean afterCanNotShowCasePending) {
        if (isWindowLockedByDialog()) {
            return;
        }
        Map.Entry<Integer, LinkedList<PendingDialogState>> lastEntry = pendingDialogMap.lastEntry();
        while (lastEntry != null) {
            LinkedList<PendingDialogState> linkedList = lastEntry.getValue();
            if (!linkedList.isEmpty()) {
                LinkedList<PendingDialogState> temp = new LinkedList<>(linkedList);
                PendingDialogState dialogInfo;
                do {
                    if (getPriorityStrategy().firstInFirstOutWhenSamePriority()) {
                        dialogInfo = temp.pollFirst();
                    } else {
                        dialogInfo = temp.pollLast();
                    }
                    if (dialogInfo != null) {
                        if ((!afterPreDismiss || dialogInfo.config.isShowImmediateAfterPreDismiss()) && (!afterCanNotShowCasePending || dialogInfo.config.isShowImmediateAfterPreCanNotShowCasePending())) {
                            if (tryShowPendingDialog(dialogInfo)) {
                                return;
                            }
                        }
                    }
                } while (dialogInfo != null);
                lastEntry = pendingDialogMap.lowerEntry(lastEntry.getKey());
            } else {
                pendingDialogMap.remove(lastEntry.getKey());
                lastEntry = pendingDialogMap.lastEntry();
            }
        }
    }

    boolean tryShowPendingDialog(PendingDialogState dialogInfo) {
        if (isWindowLockedByDialog()) {
            return false;
        }
        int priority = dialogInfo.config.getPriority();
        LinkedList<PendingDialogState> samePriorityDialogs = pendingDialogMap.get(priority);
        if (samePriorityDialogs == null || !samePriorityDialogs.contains(dialogInfo)) {
            return false;
        }
        String uuid = dialogInfo.config.getUuid();
        String hostUuid = dialogInfo.config.getHostUuid();
        DialogHost dialogHost = getDialogHost(hostUuid);
        if (dialogHost != null) {
            PriorityDialog dialog = cachePendingDialogMap.get(uuid);
            if (dialog == null) {
                FragmentManager fragmentManager = dialogHost.getPriorityDialogHostDelegate().childFragmentManager;
                dialog = (PriorityDialog) FragmentUtil.restoreFragment(dialogInfo.fragmentStateData, fragmentManager);
            }
            if (dialog != null) {
                dialog.getPriorityConfig().copyFrom(dialogInfo.config);
                if (isReady(hostUuid) && canPendingDialogShow(dialog)) {
                    dialog.getPriorityDialogDelegate().setInPendingQueue(true);
                    if (dialogHost.showPriorityDialog(dialog, dialog.getPriorityConfig().isAllowStateLoss())) {
                        cachePendingDialogMap.remove(uuid);
                        samePriorityDialogs.remove(dialogInfo);
                        if (samePriorityDialogs.isEmpty()) {
                            pendingDialogMap.remove(priority);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canPendingDialogShow(@NonNull PriorityDialog newDialog) {
        PriorityDialog currentDialog = getCurrentDialog();
        if (currentDialog != null) {
            if (getPriorityStrategy().canNewShow(currentDialog, newDialog)) {
                return true;
            }
        }
        return true;
    }

    boolean isReady(String hostUuid) {
        DialogHost dialogHost = getDialogHost(hostUuid);
        if (dialogHost instanceof Fragment) {
            Fragment fragment = (Fragment) dialogHost;
            if (fragment.isDetached() || fragment.isRemoving() || !fragment.isAdded()) {
                return false;
            }
            if (fragment.getHost() == null) {
                return false;
            }
        }
        return !activity.isFinishing();
    }

    void savePendingDialogs(Bundle outState) {
        ArrayList<PendingDialogState> list = new ArrayList<>();
        for (LinkedList<PendingDialogState> value : pendingDialogMap.values()) {
            list.addAll(value);
        }
        outState.putParcelableArrayList(PENDING_DIALOGS, list);
    }

    void restorePendingDialogs(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            ArrayList<PendingDialogState> list = savedInstanceState.getParcelableArrayList(PENDING_DIALOGS);
            if (list != null) {
                for (PendingDialogState pendingDialogState : list) {
                    LinkedList<PendingDialogState> linkedList = pendingDialogMap.get(pendingDialogState.config.getPriority());
                    if (linkedList == null) {
                        linkedList = new LinkedList<>();
                        pendingDialogMap.put(pendingDialogState.config.getPriority(), linkedList);
                    }
                    linkedList.addLast(pendingDialogState);
                }
            }
        }
    }

    void savePendingActivityActions(Bundle outState) {
        Bundle activityActions = new Bundle();
        for (Map.Entry<String, ArrayDeque<ActivityAction>> entry : pendingActivityActionMap.entrySet()) {
            ArrayList<ActivityAction> list = new ArrayList<>(entry.getValue());
            activityActions.putParcelableArrayList(entry.getKey(), list);
        }
        outState.putBundle(PENDING_ACTIVITY_ACTIONS, activityActions);
    }

    void restorePendingActivityActions(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Bundle activityActions = savedInstanceState.getBundle(PENDING_ACTIVITY_ACTIONS);
            if (activityActions != null) {
                for (String key : activityActions.keySet()) {
                    ArrayList<ActivityAction> list = activityActions.getParcelableArrayList(key);
                    pendingActivityActionMap.put(key, new ArrayDeque<>(list));
                }
            }
        }
    }

    void savePendingFragmentActions(Bundle outState) {
        Bundle fragmentActions = new Bundle();
        for (Map.Entry<String, ArrayDeque<FragmentAction>> entry : pendingFragmentActionMap.entrySet()) {
            ArrayList<FragmentAction> list = new ArrayList<>(entry.getValue());
            for (FragmentAction fragmentAction : list) {
                fragmentAction.cachedTransaction = null;//the cached transaction not use after recreate
            }
            fragmentActions.putParcelableArrayList(entry.getKey(), list);
        }
        outState.putBundle(PENDING_FRAGMENT_ACTIONS, fragmentActions);
    }

    void restorePendingFragmentActions(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Bundle fragmentActions = savedInstanceState.getBundle(PENDING_FRAGMENT_ACTIONS);
            if (fragmentActions != null) {
                for (String key : fragmentActions.keySet()) {
                    ArrayList<FragmentAction> list = fragmentActions.getParcelableArrayList(key);
                    pendingFragmentActionMap.put(key, new ArrayDeque<>(list));
                }
            }
        }
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
        FragmentManager fragmentManager = fragment.getFragmentManager();
        if (fragmentManager == null || fragmentManager.isDestroyed() || fragment.getHost() == null) {
            return;
        }
        //child first
        for (Fragment child : fragment.getChildFragmentManager().getFragments()) {
            tryPendingFragmentActionsForFragment(child);
        }
        if (fragment instanceof DialogHost) {
            tryPendingFragmentActions((DialogHost) fragment);
        }
    }

    private void tryPendingFragmentActions(DialogHost dialogHost) {
        ArrayDeque<FragmentAction> fragmentActions = pendingFragmentActionMap.get(dialogHost.getUuid());
        if (fragmentActions != null) {
            FragmentAction fragmentAction = fragmentActions.peekFirst();
            while (fragmentAction != null) {
                WarpFragmentManager fragmentManager;
                if (fragmentAction.isChildFragmentManager) {
                    fragmentManager = dialogHost.getWarpChildFragmentManager();
                } else {
                    fragmentManager = dialogHost.getWarpParentFragmentManager();
                }
                switch (fragmentAction.type) {
                    case FragmentAction.TYPE_TRANSACTION:
                        FragmentTransaction transaction = fragmentAction.cachedTransaction;
                        if (transaction == null) {
                            transaction = FragmentUtil.restorePendingTransaction(fragmentAction.transactionState, fragmentManager.fragmentManager);
                        }
                        if (transaction != null) {
                            switch (fragmentAction.transactionType) {
                                case FragmentAction.TRANSACTION_TYPE_COMMIT:
                                    transaction.commit();
                                    break;
                                case FragmentAction.TRANSACTION_TYPE_COMMIT_ALLOWING_STATE_LOSS:
                                    transaction.commitAllowingStateLoss();
                                    break;
                                case FragmentAction.TRANSACTION_TYPE_COMMIT_NOW:
                                    transaction.commitNow();
                                    break;
                                case FragmentAction.TRANSACTION_TYPE_COMMIT_NOW_ALLOWING_STATE_LOSS:
                                    transaction.commitNowAllowingStateLoss();
                                    break;
                            }
                            fragmentActions.removeFirst();
                        }
                        break;
                    case FragmentAction.TYPE_POP_BACKSTACK:
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

    static void removeDialogListeners(String uuid) {
        onCancelListenerMap.remove(uuid);
        onDismissListenerMap.remove(uuid);
        onDialogEventListenerMap.remove(uuid);
    }

    boolean isWindowLockedByDialog() {
        if (getCurrentDialog() != null) {
            return getCurrentDialog().getPriorityConfig().isLockWindow();
        }
        return false;
    }

    void dispatchDialogEvent(@NonNull PriorityDialog dialog, @NonNull Object event) {
        DialogHost dialogHost = dialog.getDialogHost();
        if (dialogHost != null) {
            dialogHost.onDialogEvent(dialog, event);
        }
        OnDialogEventListener listener = dialog.getPriorityDialogDelegate().getOnDialogEventListener();
        if (listener == null) {
            listener = onDialogEventListenerMap.get(dialog.getPriorityConfig().getUuid());
        }
        if (listener != null) {
            listener.onDialogEvent(dialog, event);
        }
    }

    void dispatchDialogCancel(@NonNull PriorityDialog dialog) {
        DialogHost dialogHost = dialog.getDialogHost();
        if (dialogHost != null) {
            dialogHost.onCancel(dialog);
        }
        OnCancelListener listener = dialog.getPriorityDialogDelegate().getOnCancelListener();
        if (listener == null) {
            listener = onCancelListenerMap.get(dialog.getPriorityConfig().getUuid());
        }
        if (listener != null) {
            listener.onCancel(dialog);
        }
    }

    void dispatchDialogDismiss(@NonNull PriorityDialog dialog) {
        DialogHost dialogHost = dialog.getDialogHost();
        if (dialogHost != null) {
            dialogHost.onDismiss(dialog);
        }
        OnDismissListener listener = dialog.getPriorityDialogDelegate().getOnDismissListener();
        if (listener == null) {
            listener = onDismissListenerMap.get(dialog.getPriorityConfig().getUuid());
        }
        if (listener != null) {
            listener.onDismiss(dialog);
        }
        release(dialog);
    }

    void release(@NonNull PriorityDialog dialog) {
        removeDialogListeners(dialog.getPriorityConfig().getUuid());
        dialog.onReallyDismiss();
    }
}
