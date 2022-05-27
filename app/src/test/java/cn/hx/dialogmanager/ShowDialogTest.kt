package cn.hx.dialogmanager

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShowDialogTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(TestActivity::class.java)

    @Test
    fun showDialogAfterSavedState() {
        activityRule.scenario.onActivity {
            val dialog1 = it.createAlertDialog(message = "first dialog", priority = 1)
            it.showPriorityDialog(dialog1)
            val dialog2 = it.createAlertDialog(message = "second dialog", priority = 2)
            it.showPriorityDialog(dialog2)
            val dialog3 = it.createAlertDialog(message = "third dialog", priority = 3)
            dialog3.setOnDialogEventListener { dialog, _ ->
                (dialog.dialogHost as? TestActivity)?.finish()
            }
            it.showPriorityDialog(dialog3)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("third dialog")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            assert(it.isFinishing)
        }
    }
}