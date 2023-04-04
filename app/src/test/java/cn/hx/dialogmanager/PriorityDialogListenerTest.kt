package cn.hx.dialogmanager

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import cn.hx.prioritydialog.PriorityDialog
import cn.hx.prioritydialog.PriorityDialogListener
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PriorityDialogListenerTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(BaseActivity::class.java)

    @Test
    fun dialogListener() {
        var showDialog: PriorityDialog? = null
        var dismissDialog: PriorityDialog? = null
        var addToPendingDialog: PriorityDialog? = null
        val listener = object : PriorityDialogListener {
            override fun onDialogShow(dialog: PriorityDialog) {
                showDialog = dialog
            }

            override fun onDialogDismiss(dialog: PriorityDialog) {
                dismissDialog = dialog
            }

            override fun onDialogAddToPending(dialog: PriorityDialog) {
                addToPendingDialog = dialog
            }
        }
        activityRule.scenario.onActivity {
            it.registerPriorityDialogListener(listener)
            it.showAlertDialog(message = "first dialog", priority = 1)
        }
        activityRule.scenario.onActivity {
            assert(showDialog != null)
            assert(dismissDialog == null)
            assert(addToPendingDialog == null)

            it.showAlertDialog(message = "second dialog", priority = 2)
        }
        activityRule.scenario.onActivity {
            assert(showDialog != null)
            assert(dismissDialog == null)
            assert(addToPendingDialog != null)

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            assert(showDialog != null)
            assert(dismissDialog != null)
            assert(addToPendingDialog != null)

            it.unregisterPriorityDialogListener(listener)
            showDialog = null
            dismissDialog = null
            addToPendingDialog = null

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
            assert(showDialog == null)
            assert(dismissDialog == null)
            assert(addToPendingDialog == null)
        }
    }
}