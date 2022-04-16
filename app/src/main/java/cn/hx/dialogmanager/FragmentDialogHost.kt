package cn.hx.dialogmanager

import androidx.fragment.app.Fragment

interface FragmentDialogHost : DialogHost {

    fun initDialogHost(fragment: Fragment)
}