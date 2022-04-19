package cn.hx.prioritydialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class DialogManagerImpl implements DialogManager {
    private static final String BASE_DIALOG_MANAGER_STATE = "cn.hx.base.dialogManager.state";
    private static final String BASE_DIALOG_MANAGER_UUID = "cn.hx.base.dialogManager.uuid";
    private static final Map<String, DialogHost> dialogHostMap = new HashMap();
    private static final Map<String, TreeMap<Integer, LinkedList<PriorityDialog>>> pendingDialogMap = new HashMap();

    private String managerUuid;
    FragmentActivity activity;

    PriorityDialog mCurrentDialog;

    @Override
    public void initAsDialogManager(@NonNull FragmentActivity activity) {
        this.activity = activity;
        Bundle savedState = activity.getSavedStateRegistry().consumeRestoredStateForKey(BASE_DIALOG_MANAGER_STATE);
        if (savedState != null) {
            managerUuid = savedState.getString(BASE_DIALOG_MANAGER_UUID);
        }
        if (managerUuid == null) {
            managerUuid = UUID.randomUUID().toString();
        }
        activity.getSavedStateRegistry().registerSavedStateProvider(BASE_DIALOG_MANAGER_STATE, () -> {
            Bundle bundle = new Bundle();
            bundle.putString(BASE_DIALOG_MANAGER_UUID, managerUuid);
            return bundle;
        });
    }

    @Override
    public void registerDialogHost(@NonNull String uuid, @NonNull DialogHost dialogHost) {
        dialogHostMap.put(uuid, dialogHost);
    }

    @Override
    public void unregisterDialogHost(@NonNull String uuid) {
        dialogHostMap.remove(uuid);
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

    @Nullable
    @Override
    public PriorityDialog getCurrentDialog() {
        return mCurrentDialog;
    }

    @Override
    public void setCurrentDialog(@Nullable PriorityDialog priorityDialog) {
        mCurrentDialog = priorityDialog;
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

    @Override
    public void addToPendingDialog(@NonNull PriorityDialog priorityDialog) {
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

    @Override
    public void tryShowNextPendingDialog() {
        if (isWindowLockedByDialog()) {
            return;
        }
        TreeMap<Integer, LinkedList<PriorityDialog>> treeMap = getPendingDialog();
        Map.Entry<Integer, LinkedList<PriorityDialog>> lastEntry = treeMap.lastEntry();
        while (lastEntry != null) {
            LinkedList<PriorityDialog> linkedList = lastEntry.getValue();
            if (!linkedList.isEmpty()) {
                ListIterator<PriorityDialog> listIterator = linkedList.listIterator(linkedList.size());
                while (listIterator.hasPrevious()) {
                    PriorityDialog priorityDialog = listIterator.previous();
                    String hostUuid = priorityDialog.getHostUuid();
                    if (hostUuid != null) {
                        DialogHost dialogHost = dialogHostMap.get(hostUuid);
                        if (dialogHost != null) {
                            if (dialogHost.isReady()) {
                                if (dialogHost.showPriorityDialog(priorityDialog)) {
                                    listIterator.remove();
                                    if (linkedList.isEmpty()) {
                                        treeMap.remove(lastEntry.getKey());
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            if (!linkedList.isEmpty()) {
                lastEntry = treeMap.lowerEntry(lastEntry.getKey());
            }
        }
    }

    @Override
    public void tryAllPendingAction() {
        if (isWindowLockedByDialog()) {
            return;
        }
        if (activity instanceof ActivityDialogHost) {
            ((ActivityDialogHost) activity).tryPendingAction();
        } else {
            for (Fragment fragment : activity.getSupportFragmentManager().getFragments()) {
                if (fragment instanceof DialogHost) {
                    ((DialogHost) fragment).tryPendingAction();
                }
            }
        }
    }

    @Override
    public boolean isWindowLockedByDialog() {
        if (mCurrentDialog != null) {
            return mCurrentDialog.getLockWindow();
        }
        return false;
    }
}
