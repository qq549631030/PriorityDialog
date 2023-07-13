package cn.hx.dialogmanager

import android.R
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
class CanBeReplaceTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(TestActivity::class.java)

    @Test
    fun onlyOneDialogShowAtTheSameTime() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            it.showPriorityDialog(it.createAlertDialog(message = "first dialog").apply {
                priorityConfig.isShowImmediateAfterPreDismiss = false
            })
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            it.showPriorityDialog(it.createAlertDialog(message = "second dialog").apply {
                priorityConfig.priority = 1
            })
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("second dialog")))

            Espresso.onView(ViewMatchers.withId(R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
            assert(it.currentPriorityDialog == null)
        }
    }

    @Test
    fun showTwoDialogAtTheSameTime() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            it.showPriorityDialog(it.createAlertDialog(message = "first dialog").apply {
                priorityConfig.isShowImmediateAfterPreDismiss = false
                priorityConfig.isCanBeReplace = false
            })
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            it.showPriorityDialog(it.createAlertDialog(message = "second dialog").apply {
                priorityConfig.priority = 1
            })
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("second dialog")))

            Espresso.onView(ViewMatchers.withId(R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
        }
    }
}