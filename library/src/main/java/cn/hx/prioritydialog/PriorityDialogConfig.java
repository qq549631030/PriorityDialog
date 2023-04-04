package cn.hx.prioritydialog;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

public class PriorityDialogConfig implements Parcelable {

    /**
     * 对话框唯一标识
     */
    private String mUuid;
    /**
     * 对话框宿主唯一标识
     */
    private String mHostUuid;
    /**
     * 优先级，值越大优先级越高
     */
    private int mPriority = 0;
    /**
     * 对话框显示时是否锁定窗口，若为true则弹框显示时只能停留在当前页面，无法关闭无法跳走，默认false
     */
    private boolean mLockWindow = false;
    /**
     * 否支持Activity重建后对话框也重新显示，默认true
     */
    private boolean mSupportRecreate = true;
    /**
     * 是否 allowStateLoss 默认false
     */
    private boolean mAllowStateLoss = false;
    /**
     * 当不能显示的时候是否要加入等待队列，默认true
     */
    private boolean mAddToPendingWhenCanNotShow = true;
    /**
     * 当被其它对话框顶下去的时候是否要加入等待队列，默认true
     */
    private boolean mAddToPendingWhenReplaceByOther = true;
    /**
     * 是否在上一个对话框关闭后立即弹出，默认true
     */
    private boolean mShowImmediateAfterPreDismiss = true;
    /**
     * 是否在上一个对话框无法弹出（和等待队列比）后立即弹出，true
     */
    private boolean mShowImmediateAfterPreCanNotShowCasePending = true;
    /**
     * 是否需要考虑等待队列中的弹窗，默认false
     */
    private boolean mCasePending = false;

    public PriorityDialogConfig() {
    }

    protected PriorityDialogConfig(Parcel in) {
        mUuid = in.readString();
        mHostUuid = in.readString();
        mPriority = in.readInt();
        mLockWindow = in.readByte() != 0;
        mSupportRecreate = in.readByte() != 0;
        mAllowStateLoss = in.readByte() != 0;
        mAddToPendingWhenCanNotShow = in.readByte() != 0;
        mAddToPendingWhenReplaceByOther = in.readByte() != 0;
        mShowImmediateAfterPreDismiss = in.readByte() != 0;
        mShowImmediateAfterPreCanNotShowCasePending = in.readByte() != 0;
        mCasePending = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUuid);
        dest.writeString(mHostUuid);
        dest.writeInt(mPriority);
        dest.writeByte((byte) (mLockWindow ? 1 : 0));
        dest.writeByte((byte) (mSupportRecreate ? 1 : 0));
        dest.writeByte((byte) (mAllowStateLoss ? 1 : 0));
        dest.writeByte((byte) (mAddToPendingWhenCanNotShow ? 1 : 0));
        dest.writeByte((byte) (mAddToPendingWhenReplaceByOther ? 1 : 0));
        dest.writeByte((byte) (mShowImmediateAfterPreDismiss ? 1 : 0));
        dest.writeByte((byte) (mShowImmediateAfterPreCanNotShowCasePending ? 1 : 0));
        dest.writeByte((byte) (mCasePending ? 1 : 0));
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

    public void setPriority(int priority) {
        this.mPriority = priority;
    }

    public boolean isLockWindow() {
        return mLockWindow;
    }

    public void setLockWindow(boolean lockWindow) {
        this.mLockWindow = lockWindow;
    }

    public boolean isSupportRecreate() {
        return mSupportRecreate;
    }

    public void setSupportRecreate(boolean supportRecreate) {
        this.mSupportRecreate = supportRecreate;
    }

    public boolean isAllowStateLoss() {
        return mAllowStateLoss;
    }

    public void setAllowStateLoss(boolean allowStateLoss) {
        this.mAllowStateLoss = allowStateLoss;
    }

    public boolean isAddToPendingWhenCanNotShow() {
        return mAddToPendingWhenCanNotShow;
    }

    public void setAddToPendingWhenCanNotShow(boolean addToPendingWhenCanNotShow) {
        this.mAddToPendingWhenCanNotShow = addToPendingWhenCanNotShow;
    }

    public boolean isAddToPendingWhenReplaceByOther() {
        return mAddToPendingWhenReplaceByOther;
    }

    public void setAddToPendingWhenReplaceByOther(boolean addToPendingWhenReplaceByOther) {
        this.mAddToPendingWhenReplaceByOther = addToPendingWhenReplaceByOther;
    }

    public boolean isShowImmediateAfterPreDismiss() {
        return mShowImmediateAfterPreDismiss;
    }

    public void setShowImmediateAfterPreDismiss(boolean showImmediateAfterPreDismiss) {
        this.mShowImmediateAfterPreDismiss = showImmediateAfterPreDismiss;
    }

    public boolean isShowImmediateAfterPreCanNotShowCasePending() {
        return mShowImmediateAfterPreCanNotShowCasePending;
    }

    public void setShowImmediateAfterPreCanNotShowCasePending(boolean showImmediateAfterPreCanNotShowCasePending) {
        this.mShowImmediateAfterPreCanNotShowCasePending = showImmediateAfterPreCanNotShowCasePending;
    }

    public boolean isCasePending() {
        return mCasePending;
    }

    public void setCasePending(boolean casePending) {
        this.mCasePending = casePending;
    }

    void copyFrom(@NonNull PriorityDialogConfig other) {
        this.mUuid = other.getUuid();
        this.mHostUuid = other.getHostUuid();
        this.mPriority = other.getPriority();
        this.mLockWindow = other.isLockWindow();
        this.mSupportRecreate = other.isSupportRecreate();
        this.mAllowStateLoss = other.isAllowStateLoss();
        this.mAddToPendingWhenCanNotShow = other.isAddToPendingWhenCanNotShow();
        this.mAddToPendingWhenReplaceByOther = other.isAddToPendingWhenReplaceByOther();
        this.mShowImmediateAfterPreDismiss = other.isShowImmediateAfterPreDismiss();
        this.mShowImmediateAfterPreCanNotShowCasePending = other.isShowImmediateAfterPreDismiss();
        this.mCasePending = other.isCasePending();
    }
}
