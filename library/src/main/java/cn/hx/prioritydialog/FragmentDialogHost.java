package cn.hx.prioritydialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public interface FragmentDialogHost extends DialogHost {

    void initAsDialogHost(@NonNull Fragment fragment, @Nullable Bundle savedInstanceState);
}