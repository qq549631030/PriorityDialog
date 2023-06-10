package cn.hx.prioritydialog;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class FragmentStateData implements Parcelable {

    @NonNull
    final Parcelable fragmentState;
    @NonNull
    final Bundle savedFragmentState;

    public FragmentStateData(@NonNull Parcelable fragmentState, @NonNull Bundle savedFragmentState) {
        this.fragmentState = fragmentState;
        this.savedFragmentState = savedFragmentState;
    }

    protected FragmentStateData(Parcel in) {
        fragmentState = in.readParcelable(getClass().getClassLoader());
        savedFragmentState = in.readBundle(getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(fragmentState, flags);
        dest.writeBundle(savedFragmentState);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FragmentStateData> CREATOR = new Creator<FragmentStateData>() {
        @Override
        public FragmentStateData createFromParcel(Parcel in) {
            return new FragmentStateData(in);
        }

        @Override
        public FragmentStateData[] newArray(int size) {
            return new FragmentStateData[size];
        }
    };
}
