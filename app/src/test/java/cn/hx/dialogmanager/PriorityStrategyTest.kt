package cn.hx.dialogmanager

import android.R
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import cn.hx.prioritydialog.DefaultPriorityStrategy
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PriorityStrategyTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(BaseActivity::class.java)

    @Test
    fun showPendingAfterPreDismiss() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            it.showAlertDialog(message = "first dialog", priority = 1)
        }
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            it.showAlertDialog(message = "second dialog", priority = 2)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("second dialog")))
            Espresso.onView(ViewMatchers.withId(R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            Espresso.onView(ViewMatchers.withId(R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
            assert(it.currentPriorityDialog == null)
        }
    }

    @Test
    fun notShowPendingAfterPreDismiss() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            it.setCurrentPriorityStrategy(object : DefaultPriorityStrategy() {
                override fun showNextPendingImmediateAfterPreDismiss(): Boolean {
                    return false
                }
            })
            it.showAlertDialog(message = "first dialog", priority = 1)
        }
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            it.showAlertDialog(message = "second dialog", priority = 2)
        }
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("second dialog")))
            it.showAlertDialog(message = "third dialog", priority = 3)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("third dialog")))
            Espresso.onView(ViewMatchers.withId(R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(it.currentPriorityDialog == null)
            assert(it.allPendingDialog.size == 2)

            it.tryShowPendingDialog(it.allPendingDialog.last())

            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            Espresso.onView(ViewMatchers.withId(R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(it.currentPriorityDialog == null)
            assert(it.allPendingDialog.size == 1)

            it.tryShowNextPendingDialog()

            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("second dialog")))
        }
    }
}