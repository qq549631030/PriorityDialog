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
class PendingDialogTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(TestActivity::class.java)

    private fun getCurrentFragment(activity: TestActivity): BaseFragment? {
        return activity.supportFragmentManager.findFragmentById(R.id.container) as? BaseFragment
    }

    @Test
    fun multiPending() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            it.showAlertDialog(message = "dialog1_1", priority = 1)
            it.showAlertDialog(message = "dialog1_2", priority = 1)
            it.showAlertDialog(message = "dialog1_3", priority = 1)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog1_3")))
            it.showAlertDialog(message = "dialog3_1", priority = 3)
            it.showAlertDialog(message = "dialog3_2", priority = 3)
            it.showAlertDialog(message = "dialog2_1", priority = 2)
            it.showAlertDialog(message = "dialog2_2", priority = 2)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog3_2")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog3_1")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog2_2")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog2_1")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog1_3")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog1_2")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog1_1")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
        }
    }

    @Test
    fun multiPending_recreate() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            it.showAlertDialog(message = "dialog1_1", priority = 1)
            it.showAlertDialog(message = "dialog1_2", priority = 1)
            it.showAlertDialog(message = "dialog1_3", priority = 1)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog1_3")))
            it.showAlertDialog(message = "dialog3_1", priority = 3)
            it.showAlertDialog(message = "dialog3_2", priority = 3)
            it.showAlertDialog(message = "dialog2_1", priority = 2)
            it.showAlertDialog(message = "dialog2_2", priority = 2)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog3_2")))
        }
        activityRule.scenario.recreate()
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog3_2")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog3_1")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog2_2")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog2_1")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog1_3")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog1_2")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog1_1")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
        }
    }

    @Test
    fun dropPendingDialogWhenDestroy() {
        activityRule.scenario.onActivity {
            it.showAlertDialog(message = "dialog1_1", priority = 1)
            it.showAlertDialog(message = "dialog2_1", priority = 2)
            it.showAlertDialog(message = "dialog3_1", priority = 3)
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog3_1")))
            it.supportFragmentManager.beginTransaction().add(R.id.container, TestFragment()).commit()
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog3_1")))
            it.supportFragmentManager.beginTransaction().add(R.id.container, SecondFragment()).addToBackStack(null).commit()
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == SecondFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog3_1")))
            getCurrentFragment(it)?.run {
                showAlertDialog(message = "dialog2_1", priority = 1)
                showAlertDialog(message = "dialog2_2", priority = 2)
                showAlertDialog(message = "dialog2_3", priority = 3)
            }
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog2_3")))
            it.supportFragmentManager.popBackStack()
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog3_1")))
            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog2_1")))
            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog1_1")))
            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
            assert(it.currentPriorityDialog == null)
        }
    }

    @Test
    fun removePendingDialogByUuid() {
        activityRule.scenario.onActivity {
            it.showAlertDialog(message = "dialog2_1", priority = 2, uuid = "dialog2_1")
            it.showAlertDialog(message = "dialog1_1", priority = 1, uuid = "dialog1_1")
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog2_1")))
            it.removePendingDialogByUuid("dialog1_1")

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
        }
    }

    @Test
    fun removeAllPendingDialogs() {
        activityRule.scenario.onActivity {
            it.showAlertDialog(message = "dialog3_1", priority = 3, uuid = "dialog3_1")
            it.showAlertDialog(message = "dialog2_1", priority = 2, uuid = "dialog2_1")
            it.showAlertDialog(message = "dialog1_1", priority = 1, uuid = "dialog1_1")
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("dialog3_1")))
            it.removeAllPendingDialogs()

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
        }
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
        }
    }
}