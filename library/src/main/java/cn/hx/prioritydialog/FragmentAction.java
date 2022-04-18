package cn.hx.prioritydialog;

import android.os.Bundle;

import androidx.annotation.NonNull;

public class FragmentAction {
    public static final int TYPE_TRANSACTION = 1;
    public static final int TYPE_POP_BACKSTACK = 2;
    public final int type;
    public WarpBackStackRecord transaction;
    public Bundle popBackStack;

    public FragmentAction(@NonNull WarpBackStackRecord transaction) {
        this.type = TYPE_TRANSACTION;
        this.transaction = transaction;
    }

    public FragmentAction(@NonNull Bundle popBackStack) {
        this.type = TYPE_POP_BACKSTACK;
        this.popBackStack = popBackStack;
    }
}
