package cn.hx.prioritydialog

import androidx.fragment.app.Fragment

interface FragmentDialogHost : DialogHost {

    fun initAsDialogHost(fragment: Fragment)
}