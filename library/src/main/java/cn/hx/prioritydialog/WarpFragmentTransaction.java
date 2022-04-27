package cn.hx.prioritydialog;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

public class WarpFragmentTransaction extends FragmentTransaction {

    public int type = FragmentAction.TRANSACTION_TYPE_COMMIT;

    final FragmentTransaction transaction;
    final PriorityDialogManager dialogManager;
    final String hostUuid;
    final boolean isChildFragmentManager;

    WarpFragmentTransaction(FragmentTransaction transaction, PriorityDialogManager dialogManager, String hostUuid, boolean isChildFragmentManager) {
        this.transaction = transaction;
        this.dialogManager = dialogManager;
        this.hostUuid = hostUuid;
        this.isChildFragmentManager = isChildFragmentManager;
    }

    @Override
    public int commit() {
        if (dialogManager.isWindowLockedByDialog()) {
            type = FragmentAction.TRANSACTION_TYPE_COMMIT;
            // add to pending, this will lose the returned backStackIndex
            dialogManager.setPendingFragmentAction(new FragmentAction(type, transaction, hostUuid, isChildFragmentManager));
            return -1;
        }
        return transaction.commit();
    }

    @Override
    public int commitAllowingStateLoss() {
        if (dialogManager.isWindowLockedByDialog()) {
            type = FragmentAction.TRANSACTION_TYPE_COMMIT_ALLOWING_STATE_LOSS;
            // add to pending, this will lose the returned backStackIndex
            dialogManager.setPendingFragmentAction(new FragmentAction(type, transaction, hostUuid, isChildFragmentManager));
            return -1;
        }
        return transaction.commitAllowingStateLoss();
    }

    @Override
    public void commitNow() {
        if (dialogManager.isWindowLockedByDialog()) {
            type = FragmentAction.TRANSACTION_TYPE_COMMIT_NOW;
            // add to pending
            dialogManager.setPendingFragmentAction(new FragmentAction(type, transaction, hostUuid, isChildFragmentManager));
            return;
        }
        transaction.commitNow();
    }

    @Override
    public void commitNowAllowingStateLoss() {
        if (dialogManager.isWindowLockedByDialog()) {
            type = FragmentAction.TRANSACTION_TYPE_COMMIT_NOW_ALLOWING_STATE_LOSS;
            // add to pending
            dialogManager.setPendingFragmentAction(new FragmentAction(type, transaction, hostUuid, isChildFragmentManager));
            return;
        }
        transaction.commitNowAllowingStateLoss();
    }

    @NonNull
    @Override
    public FragmentTransaction add(@NonNull Fragment fragment, @Nullable String tag) {
        transaction.add(fragment, tag);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction add(int containerViewId, @NonNull Fragment fragment) {
        transaction.add(containerViewId, fragment);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction add(int containerViewId, @NonNull Fragment fragment, @Nullable String tag) {
        transaction.add(containerViewId, fragment, tag);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction replace(int containerViewId, @NonNull Fragment fragment) {
        transaction.replace(containerViewId, fragment);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction replace(int containerViewId, @NonNull Fragment fragment, @Nullable String tag) {
        transaction.replace(containerViewId, fragment, tag);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction remove(@NonNull Fragment fragment) {
        transaction.remove(fragment);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction hide(@NonNull Fragment fragment) {
        transaction.hide(fragment);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction show(@NonNull Fragment fragment) {
        transaction.show(fragment);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction detach(@NonNull Fragment fragment) {
        transaction.detach(fragment);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction attach(@NonNull Fragment fragment) {
        transaction.attach(fragment);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction setPrimaryNavigationFragment(@Nullable Fragment fragment) {
        transaction.setPrimaryNavigationFragment(fragment);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction setMaxLifecycle(@NonNull Fragment fragment, @NonNull Lifecycle.State state) {
        transaction.setMaxLifecycle(fragment, state);
        return this;
    }

    @Override
    public boolean isEmpty() {
        return transaction.isEmpty();
    }

    @NonNull
    @Override
    public FragmentTransaction setCustomAnimations(int enter, int exit) {
        transaction.setCustomAnimations(enter, exit);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction setCustomAnimations(int enter, int exit, int popEnter, int popExit) {
        transaction.setCustomAnimations(enter, exit, popEnter, popExit);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction addSharedElement(@NonNull View sharedElement, @NonNull String name) {
        transaction.addSharedElement(sharedElement, name);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction setTransition(int transition) {
        transaction.setTransition(transition);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction setTransitionStyle(int styleRes) {
        transaction.setTransitionStyle(styleRes);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction addToBackStack(@Nullable String name) {
        transaction.addToBackStack(name);
        return this;
    }

    @Override
    public boolean isAddToBackStackAllowed() {
        return transaction.isAddToBackStackAllowed();
    }

    @NonNull
    @Override
    public FragmentTransaction disallowAddToBackStack() {
        transaction.disallowAddToBackStack();
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction setBreadCrumbTitle(int res) {
        transaction.setBreadCrumbTitle(res);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction setBreadCrumbTitle(@Nullable CharSequence text) {
        transaction.setBreadCrumbTitle(text);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction setBreadCrumbShortTitle(int res) {
        transaction.setBreadCrumbShortTitle(res);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction setBreadCrumbShortTitle(@Nullable CharSequence text) {
        transaction.setBreadCrumbShortTitle(text);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction setReorderingAllowed(boolean reorderingAllowed) {
        transaction.setReorderingAllowed(reorderingAllowed);
        return this;
    }

    @SuppressWarnings("deprecation")
    @NonNull
    @Override
    public FragmentTransaction setAllowOptimization(boolean allowOptimization) {
        transaction.setAllowOptimization(allowOptimization);
        return this;
    }

    @NonNull
    @Override
    public FragmentTransaction runOnCommit(@NonNull Runnable runnable) {
        transaction.runOnCommit(runnable);
        return this;
    }
}
