package cn.hx.prioritydialog;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class PendingTransactionState implements Parcelable {
    public static final int TYPE_COMMIT = 1;
    public static final int TYPE_COMMIT_ALLOWING_STATE_LOSS = 2;
    public static final int TYPE_COMMIT_NOW = 3;
    public static final int TYPE_COMMIT_NOW_ALLOWING_STATE_LOSS = 4;

    int type;
    boolean addToBackStack;
    Parcelable backStackState;
    Bundle fragmentStates;

    public PendingTransactionState(int type, boolean addToBackStack, Parcelable backStackState, Bundle fragmentStates) {
        this.type = type;
        this.addToBackStack = addToBackStack;
        this.backStackState = backStackState;
        this.fragmentStates = fragmentStates;
    }

    protected PendingTransactionState(Parcel in) {
        type = in.readInt();
        addToBackStack = in.readInt() != 0;
        backStackState = in.readParcelable(PendingTransactionState.class.getClassLoader());
        fragmentStates = in.readBundle(PendingTransactionState.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeInt(addToBackStack ? 1 : 0);
        dest.writeParcelable(backStackState, flags);
        dest.writeBundle(fragmentStates);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PendingTransactionState> CREATOR = new Creator<PendingTransactionState>() {
        @Override
        public PendingTransactionState createFromParcel(Parcel in) {
            return new PendingTransactionState(in);
        }

        @Override
        public PendingTransactionState[] newArray(int size) {
            return new PendingTransactionState[size];
        }
    };
}
