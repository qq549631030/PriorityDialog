package cn.hx.dialogmanager

import android.os.Bundle
import android.widget.Toast
import cn.hx.prioritydialog.PriorityDialog

class TestActivity : BaseActivity() {

    var dismissAfterSavedState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        savedInstanceState ?: supportFragmentManager.beginTransaction()
                .add(R.id.container, TestFragment()).commit()
    }

    override fun onCancel(priorityDialog: PriorityDialog) {
        if (priorityDialog.priorityConfig.uuid == "onCancel_test_uuid") {
            Toast.makeText(this, "onCancel called", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDismiss(priorityDialog: PriorityDialog) {
        if (priorityDialog.priorityConfig.uuid == "onDismiss_test_uuid") {
            Toast.makeText(this, "onDismiss called", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDialogEvent(priorityDialog: PriorityDialog, event: Any) {
        if (priorityDialog.priorityConfig.uuid == "onDialogEvent_test_uuid") {
            when (event) {
                is BaseAlertDialog.AlertDialogClickEvent -> {
                    Toast.makeText(this, "button ${event.which} clicked", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (dismissAfterSavedState) {
            dismissCurrentDialog(true)
        }
    }
}