package cn.hx.prioritydialog;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class PendingTransactionState implements Parcelable {

    boolean addToBackStack;
    Parcelable backStackState;
    Bundle fragmentStates;

    public PendingTransactionState(boolean addToBackStack, Parcelable backStackState, Bundle fragmentStates) {
        this.addToBackStack = addToBackStack;
        this.backStackState = backStackState;
        this.fragmentStates = fragmentStates;
    }

    protected PendingTransactionState(Parcel in) {
        addToBackStack = in.readInt() != 0;
        backStackState = in.readParcelable(Parcelable.class.getClassLoader());
        fragmentStates = in.readBundle(Bundle.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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
