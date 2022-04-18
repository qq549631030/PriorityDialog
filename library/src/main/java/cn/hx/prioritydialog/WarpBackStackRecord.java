package cn.hx.prioritydialog;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WarpBackStackRecord extends FragmentTransaction {

    public static final int TYPE_COMMIT = 1;
    public static final int TYPE_COMMIT_ALLOWING_STATE_LOSS = 2;
    public static final int TYPE_COMMIT_NOW = 3;
    public static final int TYPE_COMMIT_NOW_ALLOWING_STATE_LOSS = 4;

    public int type = TYPE_COMMIT;

    final FragmentTransaction transaction;
    final AbsDialogHostImpl dialogHost;

    WarpBackStackRecord(FragmentTransaction transaction, AbsDialogHostImpl dialogHost) {
        this.transaction = transaction;
        this.dialogHost = dialogHost;
    }

    @Override
    public int commit() {
        if (dialogHost.isWindowLockedByDialog()) {
            type = TYPE_COMMIT;
            // add to pending, this will lose the returned backStackIndex
            dialogHost.setPendingTransaction(this);
            return -1;
        }
        return transaction.commit();
    }

    @Override
    public int commitAllowingStateLoss() {
        if (dialogHost.isWindowLockedByDialog()) {
            type = TYPE_COMMIT_ALLOWING_STATE_LOSS;
            // add to pending, this will lose the returned backStackIndex
            dialogHost.setPendingTransaction(this);
            return -1;
        }
        return transaction.commitAllowingStateLoss();
    }

    @Override
    public void commitNow() {
        if (dialogHost.isWindowLockedByDialog()) {
            type = TYPE_COMMIT_NOW;
            // add to pending
            dialogHost.setPendingTransaction(this);
            return;
        }
        transaction.commitNow();
    }

    @Override
    public void commitNowAllowingStateLoss() {
        if (dialogHost.isWindowLockedByDialog()) {
            type = TYPE_COMMIT_NOW_ALLOWING_STATE_LOSS;
            // add to pending
            dialogHost.setPendingTransaction(this);
            return;
        }
        transaction.commitNowAllowingStateLoss();
    }

    public boolean tryPendingAction() {
        if (dialogHost.isWindowLockedByDialog()) {
            return false;
        }
        switch (type) {
            case TYPE_COMMIT:
                commit();
                break;
            case TYPE_COMMIT_ALLOWING_STATE_LOSS:
                commitAllowingStateLoss();
                break;
            case TYPE_COMMIT_NOW:
                commitNow();
                break;
            case TYPE_COMMIT_NOW_ALLOWING_STATE_LOSS:
                commitNowAllowingStateLoss();
                break;
        }
        return true;
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

    @SuppressWarnings({"JavaReflectionMemberAccess", "JavaReflectionInvocation"})
    @Nullable
    public WarpBackStackRecord warpWithNewFragmentManager(FragmentManager fragmentManager, AbsDialogHostImpl dialogHost) {

        try {
            Class<?> fragmentTransactionClass = Class.forName("androidx.fragment.app.FragmentTransaction");
            Field mAddToBackStackField = fragmentTransactionClass.getDeclaredField("mAddToBackStack");
            Field mOpsField = fragmentTransactionClass.getDeclaredField("mOps");
            mAddToBackStackField.setAccessible(true);
            mOpsField.setAccessible(true);
            boolean mAddToBackStack = mAddToBackStackField.getBoolean(transaction);
            if (!mAddToBackStack) {
                mAddToBackStackField.setBoolean(transaction, true);
            }
            Object mOps = mOpsField.get(transaction);
            Class<?> backStackStateClass = Class.forName("androidx.fragment.app.BackStackState");
            Class<?> backStackRecordClass = Class.forName("androidx.fragment.app.BackStackRecord");
            Constructor<?> constructor = backStackStateClass.getDeclaredConstructor(backStackRecordClass);
            constructor.setAccessible(true);
            Object backStackState = constructor.newInstance(transaction);
            Method instantiateMethod = backStackStateClass.getDeclaredMethod("instantiate", FragmentManager.class);
            instantiateMethod.setAccessible(true);
            Object newBackStackRecord = instantiateMethod.invoke(backStackState, fragmentManager);
            if (!mAddToBackStack) {
                mAddToBackStackField.setBoolean(newBackStackRecord, false);
            }
            mOpsField.set(newBackStackRecord, mOps);
            WarpBackStackRecord newWarpBackStackRecord = new WarpBackStackRecord((FragmentTransaction) newBackStackRecord, dialogHost);
            newWarpBackStackRecord.type = type;
            return newWarpBackStackRecord;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
