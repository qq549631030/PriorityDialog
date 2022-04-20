package cn.hx.prioritydialog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ActivityAction {
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
}
