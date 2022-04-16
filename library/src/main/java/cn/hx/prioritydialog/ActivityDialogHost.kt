package cn.hx.prioritydialog

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

interface ActivityDialogHost : DialogHost {
    fun initDialogHost(activity: FragmentActivity)

    fun warpStartActivityForResult(intent: Intent, requestCode: Int, options: Bundle?): Boolean

    fun warpFinish(): Boolean
}