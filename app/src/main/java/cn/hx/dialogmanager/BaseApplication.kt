package cn.hx.dialogmanager

import android.app.Application
import cn.hx.prioritydialog.PriorityDialogManager

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        PriorityDialogManager.init(this)
    }
}