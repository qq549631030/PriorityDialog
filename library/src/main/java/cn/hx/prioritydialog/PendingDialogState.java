package cn.hx.prioritydialog;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PendingDialogState implements Parcelable {
    final PriorityDialogConfig config;
    final Parcelable fragmentState;

    public PendingDialogState(@NonNull PriorityDialogConfig config, @NonNull Parcelable fragmentState) {
        this.config = config;
        this.fragmentState = fragmentState;
    }

    protected PendingDialogState(Parcel in) {
        config = in.readParcelable(PriorityDialogConfig.class.getClassLoader());
        fragmentState = in.readParcelable(PriorityDialogConfig.class.getClassLoader());
    }

    @Nullable
    public Bundle getArguments() {
        return FragmentUtil.getArgumentsFromFragmentState(fragmentState);
    }

    public static final Creator<PendingDialogState> CREATOR = new Creator<PendingDialogState>() {
        @Override
        public PendingDialogState createFromParcel(Parcel in) {
            return new PendingDialogState(in);
        }

        @Override
        public PendingDialogState[] newArray(int size) {
            return new PendingDialogState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(config, i);
        parcel.writeParcelable(fragmentState, i);
    }
}
