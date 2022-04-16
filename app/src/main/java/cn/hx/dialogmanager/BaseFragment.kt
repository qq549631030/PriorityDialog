package cn.hx.dialogmanager

import android.os.Bundle
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment(), FragmentDialogHost by FragmentDialogHostImpl() {

    val TAG = this::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDialogHost(this)
    }
}