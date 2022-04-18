package cn.hx.prioritydialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public interface FragmentDialogHost extends DialogHost {

    void initAsDialogHost(@NonNull Fragment fragment);
}