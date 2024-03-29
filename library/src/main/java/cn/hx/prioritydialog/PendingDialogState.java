package cn.hx.prioritydialog;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class PendingDialogState implements Parcelable {
    @NonNull
    public final PriorityDialogConfig config;
    @NonNull
    public final FragmentStateData fragmentStateData;
    @Nullable
    public Bundle arguments;

    public PendingDialogState(@NonNull PriorityDialogConfig config, @NonNull FragmentStateData fragmentStateData) {
        this.config = config;
        this.fragmentStateData = fragmentStateData;
        arguments = FragmentUtil.getArgumentsFromFragmentStateData(fragmentStateData);
    }

    protected PendingDialogState(Parcel in) {
        config = Objects.requireNonNull(in.readParcelable(PriorityDialogConfig.class.getClassLoader()));
        fragmentStateData = Objects.requireNonNull(in.readParcelable(PriorityDialogConfig.class.getClassLoader()));
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
        parcel.writeParcelable(fragmentStateData, i);
    }
}
