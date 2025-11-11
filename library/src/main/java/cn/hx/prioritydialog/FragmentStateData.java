package cn.hx.prioritydialog;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FragmentStateData implements Parcelable {

    public boolean isAttached;
    public Parcelable fragmentState;
    public Bundle savedFragmentState;
    public String className;
    public Bundle arguments;


    public FragmentStateData(boolean isAttached, @NonNull Parcelable fragmentState, @Nullable Bundle savedFragmentState) {
        this.isAttached = isAttached;
        this.fragmentState = fragmentState;
        this.savedFragmentState = savedFragmentState;
    }

    public FragmentStateData(@NonNull String className, @Nullable Bundle arguments) {
        isAttached = false;
        this.className = className;
        this.arguments = arguments;
    }

    protected FragmentStateData(Parcel in) {
        isAttached = in.readByte() != 0;
        fragmentState = in.readParcelable(getClass().getClassLoader());
        savedFragmentState = in.readBundle(getClass().getClassLoader());
        className = in.readString();
        arguments = in.readBundle(getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isAttached ? 1 : 0));
        dest.writeParcelable(fragmentState, flags);
        dest.writeBundle(savedFragmentState);
        dest.writeString(className);
        dest.writeBundle(arguments);
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
