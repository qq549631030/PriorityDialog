package cn.hx.prioritydialog;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

public class PriorityDialogConfig implements Parcelable {

    private String mUuid;
    private String mHostUuid;
    private int mPriority = 0;
    private boolean mOnlyDismissByUser;
    private boolean mLockWindow = false;
    private boolean mSupportRecreate = true;
    private boolean allowStateLoss = false;

    public PriorityDialogConfig() {
    }

    protected PriorityDialogConfig(Parcel in) {
        mUuid = in.readString();
        mHostUuid = in.readString();
        mPriority = in.readInt();
        mOnlyDismissByUser = in.readByte() != 0;
        mLockWindow = in.readByte() != 0;
        mSupportRecreate = in.readByte() != 0;
        allowStateLoss = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUuid);
        dest.writeString(mHostUuid);
        dest.writeInt(mPriority);
        dest.writeByte((byte) (mOnlyDismissByUser ? 1 : 0));
        dest.writeByte((byte) (mLockWindow ? 1 : 0));
        dest.writeByte((byte) (mSupportRecreate ? 1 : 0));
        dest.writeByte((byte) (allowStateLoss ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PriorityDialogConfig> CREATOR = new Creator<PriorityDialogConfig>() {
        @Override
        public PriorityDialogConfig createFromParcel(Parcel in) {
            return new PriorityDialogConfig(in);
        }

        @Override
        public PriorityDialogConfig[] newArray(int size) {
            return new PriorityDialogConfig[size];
        }
    };

    @NonNull
    public String getUuid() {
        if (mUuid == null) {
            mUuid = UUID.randomUUID().toString();
        }
        return mUuid;
    }

    public void setUuid(@NonNull String uuid) {
        this.mUuid = uuid;
    }

    @Nullable
    public String getHostUuid() {
        return mHostUuid;
    }

    public void setHostUuid(@Nullable String hostUuid) {
        this.mHostUuid = hostUuid;
    }

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int mPriority) {
        this.mPriority = mPriority;
    }

    public boolean isOnlyDismissByUser() {
        return mOnlyDismissByUser;
    }

    public void setOnlyDismissByUser(boolean mOnlyDismissByUser) {
        this.mOnlyDismissByUser = mOnlyDismissByUser;
    }

    public boolean isLockWindow() {
        return mLockWindow;
    }

    public void setLockWindow(boolean mLockWindow) {
        this.mLockWindow = mLockWindow;
    }

    public boolean isSupportRecreate() {
        return mSupportRecreate;
    }

    public void setSupportRecreate(boolean mSupportRecreate) {
        this.mSupportRecreate = mSupportRecreate;
    }

    public boolean isAllowStateLoss() {
        return allowStateLoss;
    }

    public void setAllowStateLoss(boolean allowStateLoss) {
        this.allowStateLoss = allowStateLoss;
    }

    public void copyFrom(@NonNull PriorityDialogConfig other) {
        this.mUuid = other.getUuid();
        this.mHostUuid = other.getHostUuid();
        this.mPriority = other.getPriority();
        this.mOnlyDismissByUser = other.isOnlyDismissByUser();
        this.mLockWindow = other.isLockWindow();
        this.mSupportRecreate = other.isSupportRecreate();
        this.allowStateLoss = other.isAllowStateLoss();
    }
}
