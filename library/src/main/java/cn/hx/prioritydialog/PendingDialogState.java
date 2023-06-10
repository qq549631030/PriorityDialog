package cn.hx.prioritydialog;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class PendingDialogState implements Parcelable {
    public final PriorityDialogConfig config;
    public final FragmentStateData fragmentStateData;

    public PendingDialogState(@NonNull PriorityDialogConfig config, @NonNull FragmentStateData fragmentStateData) {
        this.config = config;
        this.fragmentStateData = fragmentStateData;
    }

    protected PendingDialogState(Parcel in) {
        config = in.readParcelable(PriorityDialogConfig.class.getClassLoader());
        fragmentStateData = in.readParcelable(PriorityDialogConfig.class.getClassLoader());
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
