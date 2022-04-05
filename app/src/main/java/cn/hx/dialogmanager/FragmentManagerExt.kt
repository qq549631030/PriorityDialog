package cn.hx.dialogmanager

import androidx.fragment.app.FragmentManager
import java.util.*

//等待显示对话框
val FragmentManager.pendingDialogs: TreeMap<Int, Stack<BaseDialog>> by lazy { TreeMap() }

//加入等待队列
fun FragmentManager.addPendingDialog(baseDialog: BaseDialog) {
    val stack = pendingDialogs[baseDialog.priority] ?: run {
        val stack = Stack<BaseDialog>()
        pendingDialogs[baseDialog.priority] = stack
        stack
    }
    stack.push(baseDialog)
}

//弹出最高优先级对话框
fun FragmentManager.popPendingDialog(): BaseDialog? {
    var baseDialog: BaseDialog? = null
    return pendingDialogs.lastEntry()?.let {
        if (it.value.isNotEmpty()) {
            baseDialog = it.value.pop()
        }
        if (it.value.isEmpty()) {
            pendingDialogs.remove(it.key)
        }
        baseDialog
    }
}

val FragmentManager.currentDialog: BaseDialog?
    get() {
        val baseDialog = findFragmentByTag(BaseDialog.BASE_DIALOG_TAG) as? BaseDialog
        if (baseDialog?.isShowing() == true) {
            return baseDialog
        }
        return null
    }