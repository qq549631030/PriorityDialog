package cn.hx.dialogmanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import cn.hx.prioritydialog.FragmentDialogHost
import cn.hx.prioritydialog.FragmentDialogHostImpl

open class BaseFragment : Fragment(), FragmentDialogHost by FragmentDialogHostImpl() {

    val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDialogHost(this)
    }
}