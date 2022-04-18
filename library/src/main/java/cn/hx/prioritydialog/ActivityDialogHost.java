package cn.hx.prioritydialog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public interface ActivityDialogHost extends DialogHost {

    void initAsDialogHost(@NonNull FragmentActivity activity);

    boolean warpStartActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options);

    boolean warpFinish();
}