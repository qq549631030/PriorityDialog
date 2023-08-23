package cn.hx.prioritydialog;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

public class FragmentAction implements Parcelable {
    public static final int TYPE_TRANSACTION = 1;
    public static final int TYPE_POP_BACKSTACK = 2;

    public static final int TRANSACTION_TYPE_COMMIT = 1;
    public static final int TRANSACTION_TYPE_COMMIT_ALLOWING_STATE_LOSS = 2;
    public static final int TRANSACTION_TYPE_COMMIT_NOW = 3;
    public static final int TRANSACTION_TYPE_COMMIT_NOW_ALLOWING_STATE_LOSS = 4;

    @NonNull
    public final String hostUuid;
    public final int type;
    public final boolean isChildFragmentManager;
    public int transactionType;
    @Nullable
    public FragmentTransaction cachedTransaction;
    public PendingTransactionState transactionState;
    public Bundle popBackStack;

    public FragmentAction(int transactionType, @NonNull FragmentTransaction cachedTransaction, @NonNull String hostUuid, boolean isChildFragmentManager) {
        this.isChildFragmentManager = isChildFragmentManager;
        this.type = TYPE_TRANSACTION;
        this.transactionType = transactionType;
        this.cachedTransaction = cachedTransaction;
        this.transactionState = FragmentUtil.saveTransaction(cachedTransaction);
        this.hostUuid = hostUuid;
    }

    public FragmentAction(@NonNull Bundle popBackStack, @NonNull String hostUuid, boolean isChildFragmentManager) {
        this.isChildFragmentManager = isChildFragmentManager;
        this.type = TYPE_POP_BACKSTACK;
        this.popBackStack = popBackStack;
        this.hostUuid = hostUuid;
    }

    protected FragmentAction(Parcel in) {
        hostUuid = Objects.requireNonNull(in.readString());
        type = in.readInt();
        transactionType = in.readInt();
        isChildFragmentManager = in.readByte() != 0;
        transactionState = in.readParcelable(PendingTransactionState.class.getClassLoader());
        popBackStack = in.readBundle(Bundle.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hostUuid);
        dest.writeInt(type);
        dest.writeInt(transactionType);
        dest.writeByte((byte) (isChildFragmentManager ? 1 : 0));
        dest.writeParcelable(transactionState, flags);
        dest.writeBundle(popBackStack);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FragmentAction> CREATOR = new Creator<FragmentAction>() {
        @Override
        public FragmentAction createFromParcel(Parcel in) {
            return new FragmentAction(in);
        }

        @Override
        public FragmentAction[] newArray(int size) {
            return new FragmentAction[size];
        }
    };
}
