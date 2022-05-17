package cn.hx.dialogmanager

import androidx.test.espresso.Espresso
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
class DismissDialogTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(FragmentTestActivity::class.java)

    @Test
    fun dismissCurrentDialog() {
        activityRule.scenario.onActivity {
            it.showAlertDialog(message = "first dialog")
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            it.dismissCurrentDialog(true)
        }
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
        }
    }

    @Test
    fun dismissCurrentDialog_AfterSavedState() {
        activityRule.scenario.onActivity {
            it.showAlertDialog(message = "first dialog")
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            it.dismissAfterSavedState = true
        }
        activityRule.scenario.recreate()
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
        }
    }
}