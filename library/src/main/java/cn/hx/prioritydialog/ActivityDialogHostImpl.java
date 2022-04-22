package cn.hx.prioritydialog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivityDialogHostImpl extends AbsDialogHostImpl implements ActivityDialogHost {

    private static final Map<String, ArrayDeque<ActivityAction>> pendingActivityActionMap = new HashMap<>();

    FragmentActivity activity;

    @NonNull
    private ArrayDeque<ActivityAction> getPendingActions() {
        ArrayDeque<ActivityAction> activityActions = pendingActivityActionMap.get(uuid);
        if (activityActions != null) {
            return activityActions;
        }
        activityActions = new ArrayDeque<>();
        pendingActivityActionMap.put(uuid, activityActions);
        return activityActions;
    }

    @Override
    public void initAsDialogHost(@NonNull FragmentActivity activity) {
        if (!(activity instanceof DialogManager)) {
            throw new IllegalArgumentException("activity must implements DialogManager");
        }
        this.activity = activity;
        Bundle savedState = activity.getSavedStateRegistry().consumeRestoredStateForKey(KEY_DIALOG_HOST_STATE);
        if (savedState != null) {
            uuid = savedState.getString(BASE_DIALOG_HOST_UUID);
        }
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        init((DialogManager) activity, uuid, activity.getSupportFragmentManager(), activity.getSupportFragmentManager());
        mDialogManager.registerDialogHost(uuid, this);
        activity.getSavedStateRegistry().registerSavedStateProvider(KEY_DIALOG_HOST_STATE, () -> {
            Bundle bundle = new Bundle();
            bundle.putString(BASE_DIALOG_HOST_UUID, uuid);
            return bundle;
        });
        activity.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_START) {
                tryWarpPendingTransaction();
            }
            if (event == Lifecycle.Event.ON_DESTROY) {
                if (activity.isFinishing()) {
                    mDialogManager.unregisterDialogHost(uuid);
                    cleanAllPendingAction();
                }
            }
        });
    }

    @Override
    public boolean isReady() {
        if (!super.isReady()) {
            return false;
        }
        return !activity.isFinishing();
    }

    @Override
    public void tryPendingAction() {
        super.tryPendingAction();
        if (mDialogManager.isWindowLockedByDialog()) {
            return;
        }
        ArrayDeque<ActivityAction> activityActions = pendingActivityActionMap.remove(uuid);
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

    @Override
    public boolean warpStartActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
        if (mDialogManager.isWindowLockedByDialog()) {
            getPendingActions().addLast(new ActivityAction(intent, requestCode, options));
            return true;
        }
        return false;
    }

    @Override
    public boolean warpFinish() {
        if (mDialogManager.isWindowLockedByDialog()) {
            getPendingActions().add(new ActivityAction(ActivityAction.TYPE_FINISH_ACTIVITY));
            return true;
        }
        return false;
    }

    @Override
    protected void cleanAllPendingAction() {
        super.cleanAllPendingAction();
        pendingActivityActionMap.remove(uuid);
    }
}
