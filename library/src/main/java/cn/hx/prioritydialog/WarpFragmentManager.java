package cn.hx.prioritydialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

public class WarpFragmentManager extends FragmentManager {

    public static final String POP_BACK_STACK_TYPE = "pop_back_stack_type";
    public static final String POP_BACK_STACK_NAME = "pop_back_stack_name";
    public static final String POP_BACK_STACK_ID = "pop_back_stack_id";
    public static final String POP_BACK_STACK_FLAG = "pop_back_stack_flag";

    public static final int TYPE_POP = 1;
    public static final int TYPE_POP_WITH_NAME = 2;
    public static final int TYPE_POP_WITH_ID = 3;
    public static final int TYPE_POP_IMMEDIATE = 4;
    public static final int TYPE_POP_IMMEDIATE_WITH_NAME = 5;
    public static final int TYPE_POP_IMMEDIATE_WITH_ID = 6;

    final FragmentManager fragmentManager;
    final AbsDialogHostImpl dialogHost;

    public WarpFragmentManager(FragmentManager fragmentManager, AbsDialogHostImpl dialogHost) {
        this.fragmentManager = fragmentManager;
        this.dialogHost = dialogHost;
    }

    @NonNull
    @Override
    public FragmentTransaction beginTransaction() {
        return new WarpBackStackRecord(fragmentManager.beginTransaction(), dialogHost);
    }

    @Override
    public boolean executePendingTransactions() {
        return fragmentManager.executePendingTransactions();
    }

    @Nullable
    @Override
    public Fragment findFragmentById(int id) {
        return fragmentManager.findFragmentById(id);
    }

    @Nullable
    @Override
    public Fragment findFragmentByTag(@Nullable String tag) {
        return fragmentManager.findFragmentByTag(tag);
    }

    @Override
    public void popBackStack() {
        if (dialogHost.isWindowLockedByDialog()) {
            Bundle bundle = new Bundle();
            bundle.putInt(POP_BACK_STACK_TYPE, TYPE_POP);
            dialogHost.setPendingPopBackStack(bundle);
            return;
        }
        fragmentManager.popBackStack();
    }

    @Override
    public boolean popBackStackImmediate() {
        if (dialogHost.isWindowLockedByDialog()) {
            Bundle bundle = new Bundle();
            bundle.putInt(POP_BACK_STACK_TYPE, TYPE_POP_IMMEDIATE);
            dialogHost.setPendingPopBackStack(bundle);
            return false;
        }
        return false;
    }

    @Override
    public void popBackStack(@Nullable String name, int flags) {
        if (dialogHost.isWindowLockedByDialog()) {
            Bundle bundle = new Bundle();
            bundle.putInt(POP_BACK_STACK_TYPE, TYPE_POP_WITH_NAME);
            bundle.putString(POP_BACK_STACK_NAME, name);
            bundle.putInt(POP_BACK_STACK_FLAG, flags);
            dialogHost.setPendingPopBackStack(bundle);
            return;
        }
        fragmentManager.popBackStack(name, flags);
    }

    @Override
    public boolean popBackStackImmediate(@Nullable String name, int flags) {
        if (dialogHost.isWindowLockedByDialog()) {
            Bundle bundle = new Bundle();
            bundle.putInt(POP_BACK_STACK_TYPE, TYPE_POP_IMMEDIATE_WITH_NAME);
            bundle.putString(POP_BACK_STACK_NAME, name);
            bundle.putInt(POP_BACK_STACK_FLAG, flags);
            dialogHost.setPendingPopBackStack(bundle);
            return false;
        }
        return fragmentManager.popBackStackImmediate(name, flags);
    }

    @Override
    public void popBackStack(int id, int flags) {
        if (dialogHost.isWindowLockedByDialog()) {
            Bundle bundle = new Bundle();
            bundle.putInt(POP_BACK_STACK_TYPE, TYPE_POP_WITH_ID);
            bundle.putInt(POP_BACK_STACK_ID, id);
            bundle.putInt(POP_BACK_STACK_FLAG, flags);
            dialogHost.setPendingPopBackStack(bundle);
            return;
        }
        fragmentManager.popBackStack(id, flags);
    }

    @Override
    public boolean popBackStackImmediate(int id, int flags) {
        if (dialogHost.isWindowLockedByDialog()) {
            Bundle bundle = new Bundle();
            bundle.putInt(POP_BACK_STACK_TYPE, TYPE_POP_IMMEDIATE_WITH_ID);
            bundle.putInt(POP_BACK_STACK_ID, id);
            bundle.putInt(POP_BACK_STACK_FLAG, flags);
            dialogHost.setPendingPopBackStack(bundle);
            return false;
        }
        return fragmentManager.popBackStackImmediate(id, flags);
    }

    @Override
    public int getBackStackEntryCount() {
        return fragmentManager.getBackStackEntryCount();
    }

    @NonNull
    @Override
    public BackStackEntry getBackStackEntryAt(int index) {
        return fragmentManager.getBackStackEntryAt(index);
    }

    @Override
    public void addOnBackStackChangedListener(@NonNull OnBackStackChangedListener listener) {
        fragmentManager.addOnBackStackChangedListener(listener);
    }

    @Override
    public void removeOnBackStackChangedListener(@NonNull OnBackStackChangedListener listener) {
        fragmentManager.removeOnBackStackChangedListener(listener);
    }

    @Override
    public void putFragment(@NonNull Bundle bundle, @NonNull String key, @NonNull Fragment fragment) {
        fragmentManager.putFragment(bundle, key, fragment);
    }

    @Nullable
    @Override
    public Fragment getFragment(@NonNull Bundle bundle, @NonNull String key) {
        return fragmentManager.getFragment(bundle, key);
    }

    @NonNull
    @Override
    public List<Fragment> getFragments() {
        return fragmentManager.getFragments();
    }

    @Nullable
    @Override
    public Fragment.SavedState saveFragmentInstanceState(@NonNull Fragment f) {
        return fragmentManager.saveFragmentInstanceState(f);
    }

    @Override
    public boolean isDestroyed() {
        return fragmentManager.isDestroyed();
    }

    @Override
    public void registerFragmentLifecycleCallbacks(@NonNull FragmentLifecycleCallbacks cb, boolean recursive) {
        fragmentManager.registerFragmentLifecycleCallbacks(cb, recursive);
    }

    @Override
    public void unregisterFragmentLifecycleCallbacks(@NonNull FragmentLifecycleCallbacks cb) {
        fragmentManager.unregisterFragmentLifecycleCallbacks(cb);
    }

    @Nullable
    @Override
    public Fragment getPrimaryNavigationFragment() {
        return fragmentManager.getPrimaryNavigationFragment();
    }

    @Override
    public void dump(@NonNull String prefix, @Nullable FileDescriptor fd, @NonNull PrintWriter writer, @Nullable String[] args) {
        fragmentManager.dump(prefix, fd, writer, args);
    }

    @Override
    public boolean isStateSaved() {
        return fragmentManager.isStateSaved();
    }

    public boolean tryPendingAction(@NonNull Bundle pendingPopBackStack) {
        if (dialogHost.isWindowLockedByDialog()) {
            return false;
        }
        int type = pendingPopBackStack.getInt(POP_BACK_STACK_TYPE);
        String name = pendingPopBackStack.getString(POP_BACK_STACK_NAME);
        int id = pendingPopBackStack.getInt(POP_BACK_STACK_ID);
        int flags = pendingPopBackStack.getInt(POP_BACK_STACK_FLAG);
        switch (type) {
            case TYPE_POP:
                popBackStack();
                break;
            case TYPE_POP_IMMEDIATE:
                popBackStackImmediate();
                break;
            case TYPE_POP_WITH_NAME:
                popBackStack(name, flags);
                break;
            case TYPE_POP_IMMEDIATE_WITH_NAME:
                popBackStackImmediate(name, flags);
                break;
            case TYPE_POP_WITH_ID:
                popBackStack(id, flags);
                break;
            case TYPE_POP_IMMEDIATE_WITH_ID:
                popBackStackImmediate(id, flags);
                break;
        }
        return true;
    }
}
