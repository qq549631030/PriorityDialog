package cn.hx.prioritydialog;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class FragmentAction implements Parcelable {
    public static final int TYPE_TRANSACTION = 1;
    public static final int TYPE_POP_BACKSTACK = 2;
    public final String hostUuid;
    public final int type;
    public final boolean isChildFragmentManager;
    public PendingTransactionState transaction;
    public Bundle popBackStack;

    public FragmentAction(@NonNull PendingTransactionState transaction, String hostUuid, boolean isChildFragmentManager) {
        this.isChildFragmentManager = isChildFragmentManager;
        this.type = TYPE_TRANSACTION;
        this.transaction = transaction;
        this.hostUuid = hostUuid;
    }

    public FragmentAction(@NonNull Bundle popBackStack, String hostUuid, boolean isChildFragmentManager) {
        this.isChildFragmentManager = isChildFragmentManager;
        this.type = TYPE_POP_BACKSTACK;
        this.popBackStack = popBackStack;
        this.hostUuid = hostUuid;
    }

    protected FragmentAction(Parcel in) {
        hostUuid = in.readString();
        type = in.readInt();
        isChildFragmentManager = in.readByte() != 0;
        transaction = in.readParcelable(PendingTransactionState.class.getClassLoader());
        popBackStack = in.readBundle(PendingTransactionState.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hostUuid);
        dest.writeInt(type);
        dest.writeByte((byte) (isChildFragmentManager ? 1 : 0));
        dest.writeParcelable(transaction, flags);
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
