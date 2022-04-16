package cn.hx.prioritydialog

import androidx.fragment.app.Fragment

interface FragmentDialogHost : DialogHost {

    fun initDialogHost(fragment: Fragment)
}