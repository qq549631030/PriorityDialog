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
class OnlyDismissByUserTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(BaseActivity::class.java)

    @Test
    fun onlyDismissByUser_true() {
        activityRule.scenario.onActivity {
            assert(it.currentDialog == null)
            it.showAlertDialog(message = "first dialog", priority = 1)
        }
        activityRule.scenario.onActivity {
            assert(it.currentDialog != null)
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
            assert(it.currentDialog == null)
        }
    }

    @Test
    fun onlyDismissByUser_false() {
        activityRule.scenario.onActivity {
            assert(it.currentDialog == null)
            it.showAlertDialog(message = "first dialog", priority = 1, onlyDismissByUser = false)
        }
        activityRule.scenario.onActivity {
            assert(it.currentDialog != null)

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

            assert(it.currentDialog == null)
        }
    }
}