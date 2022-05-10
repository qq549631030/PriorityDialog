package cn.hx.dialogmanager

import android.content.Intent
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
import org.robolectric.Shadows

@RunWith(AndroidJUnit4::class)
class LockWindowStartActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(FragmentTestActivity::class.java)

    private fun getCurrentFragment(activity: FragmentTestActivity): BaseFragment? {
        return activity.supportFragmentManager.findFragmentById(R.id.container) as? BaseFragment
    }

    @Test
    fun activity_startActivity() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            it.showAlertDialog(message = "first dialog", lockWindow = true)
        }
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            it.startActivity(Intent(it, SecondActivity::class.java))
            assert(Shadows.shadowOf(it).nextStartedActivity == null)

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(Shadows.shadowOf(it).nextStartedActivity != null)
        }
    }

    @Test
    fun activity_startActivity_multi() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            it.showAlertDialog(message = "first dialog", lockWindow = true)
        }
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            it.startActivity(Intent(it, SecondActivity::class.java))
            it.startActivity(Intent(it, BaseActivity::class.java))
            assert(Shadows.shadowOf(it).nextStartedActivity == null)

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(Shadows.shadowOf(it).nextStartedActivity?.component?.className == BaseActivity::class.qualifiedName)
        }
    }

    @Test
    fun activity_startActivity_recreate() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            it.showAlertDialog(message = "first dialog", lockWindow = true)
        }
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            it.startActivity(Intent(it, SecondActivity::class.java))
            assert(Shadows.shadowOf(it).nextStartedActivity == null)
        }

        activityRule.scenario.recreate()

        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            assert(Shadows.shadowOf(it).nextStartedActivity == null)

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(Shadows.shadowOf(it).nextStartedActivity != null)
        }
    }

    @Test
    fun fragment_startActivity() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            getCurrentFragment(it)?.run {
                showAlertDialog(message = "first dialog", lockWindow = true)
            }
        }
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog != null)
            getCurrentFragment(it)?.run {
                Espresso.onView(ViewMatchers.withId(android.R.id.message))
                        .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                        .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

                startActivity(Intent(it, SecondActivity::class.java))
                assert(Shadows.shadowOf(it).nextStartedActivity == null)

                Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                        .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                        .perform(ViewActions.click())

                assert(Shadows.shadowOf(it).nextStartedActivity != null)
            }
        }
    }

    @Test
    fun fragment_startActivity_recreate() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            getCurrentFragment(it)?.run {
                showAlertDialog(message = "first dialog", lockWindow = true)
            }
        }
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog != null)
            getCurrentFragment(it)?.run {
                Espresso.onView(ViewMatchers.withId(android.R.id.message))
                        .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                        .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

                startActivity(Intent(it, SecondActivity::class.java))
                assert(Shadows.shadowOf(it).nextStartedActivity == null)
            }
        }
        activityRule.scenario.recreate()
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog != null)
            getCurrentFragment(it)?.run {
                Espresso.onView(ViewMatchers.withId(android.R.id.message))
                        .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                        .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

                assert(Shadows.shadowOf(it).nextStartedActivity == null)

                Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                        .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                        .perform(ViewActions.click())

                assert(Shadows.shadowOf(it).nextStartedActivity != null)
            }
        }
    }
}