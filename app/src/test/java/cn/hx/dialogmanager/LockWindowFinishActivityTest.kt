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
class LockWindowFinishActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(FragmentTestActivity::class.java)

    private fun getCurrentFragment(activity: FragmentTestActivity): BaseFragment? {
        return activity.supportFragmentManager.findFragmentById(R.id.container) as? BaseFragment
    }

    @Test
    fun activity_finishActivity() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            it.showAlertDialog(message = "first dialog", lockWindow = true)
        }
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            it.finish()
            assert(!it.isFinishing)

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(it.isFinishing)
        }
    }

    @Test
    fun activity_finishActivity_recreate() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            it.showAlertDialog(message = "first dialog", lockWindow = true)
        }
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            it.finish()
            assert(!it.isFinishing)
        }
        activityRule.scenario.recreate()
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            assert(!it.isFinishing)

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(it.isFinishing)
        }
    }


    @Test
    fun fragment_finishActivity() {
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

                requireActivity().finish()
                assert(!it.isFinishing)

                Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                        .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                        .perform(ViewActions.click())

                assert(it.isFinishing)
            }
        }
    }

    @Test
    fun fragment_finishActivity_recreate() {
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

                requireActivity().finish()
                assert(!it.isFinishing)
            }
        }
        activityRule.scenario.recreate()
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog != null)
            getCurrentFragment(it)?.run {
                Espresso.onView(ViewMatchers.withId(android.R.id.message))
                        .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                        .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

                assert(!it.isFinishing)

                Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                        .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                        .perform(ViewActions.click())

                assert(it.isFinishing)
            }
        }
    }
}