package cn.hx.prioritydialog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivityDialogHostImpl extends AbsDialogHostImpl implements ActivityDialogHost {

    private static final String BASE_PENDING_INTENT = "cn.hx.base.activity.pendingIntent";
    private static final String BASE_PENDING_REQUEST_CODE = "cn.hx.base.activity.pendingRequestCode";
    private static final String BASE_PENDING_OPTIONS = "cn.hx.base.activity.pendingOptions";
    private static final String BASE_PENDING_FINISH = "cn.hx.base.activity.pendingFinish";

    private static final Map<String, Bundle> pendingActionMap = new HashMap();

    FragmentActivity activity;

    @NonNull
    private Bundle getPendingActions() {
        Bundle bundle = pendingActionMap.get(uuid);
        if (bundle != null) {
            return bundle;
        }
        bundle = new Bundle();
        pendingActionMap.put(uuid, bundle);
        return bundle;
    }

    @Nullable
    private Intent getPendingIntent() {
        return getPendingActions().getParcelable(BASE_PENDING_INTENT);
    }

    private int getPendingRequestCode() {
        return getPendingActions().getInt(BASE_PENDING_REQUEST_CODE, -1);
    }

    @Nullable
    private Bundle getPendingOptions() {
        return getPendingActions().getBundle(BASE_PENDING_OPTIONS);
    }

    private boolean getPendingFinish() {
        return getPendingActions().getBoolean(BASE_PENDING_FINISH);
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
        return !activity.isFinishing();
    }

    @Override
    public void tryPendingAction() {
        super.tryPendingAction();
        if (mDialogManager.isWindowLockedByDialog()) {
            return;
        }
        if (getPendingIntent() != null) {
            activity.startActivityForResult(getPendingIntent(), getPendingRequestCode(), getPendingOptions());
        }
        if (getPendingFinish()) {
            activity.finish();
        }
        pendingActionMap.remove(uuid);
    }

    @Override
    public boolean warpStartActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
        if (mDialogManager.isWindowLockedByDialog()) {
            getPendingActions().putParcelable(BASE_PENDING_INTENT, intent);
            getPendingActions().putInt(BASE_PENDING_REQUEST_CODE, requestCode);
            getPendingActions().putBundle(BASE_PENDING_OPTIONS, options);
            return true;
        }
        return false;
    }

    @Override
    public boolean warpFinish() {
        if (mDialogManager.isWindowLockedByDialog()) {
            getPendingActions().putBoolean(BASE_PENDING_FINISH, true);
            return true;
        }
        return false;
    }

    @Override
    protected void cleanAllPendingAction() {
        super.cleanAllPendingAction();
        pendingActionMap.remove(uuid);
    }
}
