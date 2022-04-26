package cn.hx.prioritydialog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ActivityAction implements Parcelable {
    public static final int TYPE_START_ACTIVITY = 1;
    public static final int TYPE_FINISH_ACTIVITY = 2;
    public final int type;
    public Intent intent;
    public int requestCode = -1;
    public Bundle options;

    public ActivityAction(int type) {
        if (type != TYPE_FINISH_ACTIVITY) {
            throw new IllegalArgumentException("this only support finish activity Action");
        }
        this.type = type;
    }

    public ActivityAction(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
        this.type = TYPE_START_ACTIVITY;
        this.intent = intent;
        this.requestCode = requestCode;
        this.options = options;
    }

    protected ActivityAction(Parcel in) {
        type = in.readInt();
        intent = in.readParcelable(Intent.class.getClassLoader());
        requestCode = in.readInt();
        options = in.readBundle(PendingTransactionState.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeParcelable(intent, flags);
        dest.writeInt(requestCode);
        dest.writeBundle(options);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ActivityAction> CREATOR = new Creator<ActivityAction>() {
        @Override
        public ActivityAction createFromParcel(Parcel in) {
            return new ActivityAction(in);
        }

        @Override
        public ActivityAction[] newArray(int size) {
            return new ActivityAction[size];
        }
    };
}
