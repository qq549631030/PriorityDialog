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
class LockWindowFragmentActionTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(FragmentTestActivity::class.java)

    private fun getCurrentFragment(activity: FragmentTestActivity): BaseFragment? {
        return activity.supportFragmentManager.findFragmentById(R.id.container) as? BaseFragment
    }

    @Test
    fun activity_transaction() {
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            it.showAlertDialog(message = "first dialog", lockWindow = true)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            it.warpParentFragmentManager.beginTransaction().replace(R.id.container, SecondFragment()).commit()
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(getCurrentFragment(it)?.javaClass?.simpleName == SecondFragment::class.java.simpleName)
        }
    }

    @Test
    fun activity_transaction_recreate() {
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            it.showAlertDialog(message = "first dialog", lockWindow = true)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            it.warpParentFragmentManager.beginTransaction().replace(R.id.container, SecondFragment()).commit()
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
        }
        activityRule.scenario.recreate()
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(getCurrentFragment(it)?.javaClass?.simpleName == SecondFragment::class.java.simpleName)
        }
    }

    @Test
    fun activity_popBackStack() {
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            it.warpParentFragmentManager.beginTransaction().replace(R.id.container, SecondFragment()).addToBackStack(null).commit()
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == SecondFragment::class.java.simpleName)
            it.showAlertDialog(message = "first dialog", lockWindow = true)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            it.warpParentFragmentManager.popBackStack()
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == SecondFragment::class.java.simpleName)

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
        }
    }

    @Test
    fun activity_popBackStack_recreate() {
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            it.warpParentFragmentManager.beginTransaction().replace(R.id.container, SecondFragment()).addToBackStack(null).commit()
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == SecondFragment::class.java.simpleName)
            it.showAlertDialog(message = "first dialog", lockWindow = true)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            it.warpParentFragmentManager.popBackStack()
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == SecondFragment::class.java.simpleName)

            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
        }
        activityRule.scenario.recreate()
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == SecondFragment::class.java.simpleName)

            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
        }
    }

    @Test
    fun activity_transaction_popBackStack() {
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            it.showAlertDialog(message = "first dialog", lockWindow = true)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            it.warpParentFragmentManager.beginTransaction().replace(R.id.container, SecondFragment()).addToBackStack(null).commit()
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            it.warpParentFragmentManager.popBackStack()
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)

        }
    }

    @Test
    fun activity_transaction_popBackStack_recreate() {
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            it.showAlertDialog(message = "first dialog", lockWindow = true)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            it.warpParentFragmentManager.beginTransaction().replace(R.id.container, SecondFragment()).addToBackStack(null).commit()
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            it.warpParentFragmentManager.popBackStack()
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

        }
        activityRule.scenario.recreate()
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
        }
    }

    @Test
    fun fragment_transaction() {
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            getCurrentFragment(it)?.run {
                showAlertDialog(message = "first dialog", lockWindow = true)
            }
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            getCurrentFragment(it)?.run {
                warpParentFragmentManager.beginTransaction().replace(R.id.container, SecondFragment()).commit()
            }
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(getCurrentFragment(it)?.javaClass?.simpleName == SecondFragment::class.java.simpleName)
        }
    }

    @Test
    fun fragment_transaction_recreate() {
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            getCurrentFragment(it)?.run {
                showAlertDialog(message = "first dialog", lockWindow = true)
            }
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            getCurrentFragment(it)?.run {
                warpParentFragmentManager.beginTransaction().replace(R.id.container, SecondFragment()).commit()
            }
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
        }
        activityRule.scenario.recreate()
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(getCurrentFragment(it)?.javaClass?.simpleName == SecondFragment::class.java.simpleName)
        }
    }

    @Test
    fun fragment_popBackStack() {
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            getCurrentFragment(it)?.run {
                warpParentFragmentManager.beginTransaction().replace(R.id.container, SecondFragment()).addToBackStack(null).commit()
            }
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == SecondFragment::class.java.simpleName)
            getCurrentFragment(it)?.run {
                showAlertDialog(message = "first dialog", lockWindow = true)
            }
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            getCurrentFragment(it)?.run {
                warpParentFragmentManager.popBackStack()
            }
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == SecondFragment::class.java.simpleName)

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
        }
    }

    @Test
    fun fragment_popBackStack_recreate() {
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            getCurrentFragment(it)?.run {
                warpParentFragmentManager.beginTransaction().replace(R.id.container, SecondFragment()).addToBackStack(null).commit()
            }
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == SecondFragment::class.java.simpleName)
            getCurrentFragment(it)?.run {
                showAlertDialog(message = "first dialog", lockWindow = true)
            }
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            getCurrentFragment(it)?.run {
                warpParentFragmentManager.popBackStack()
            }
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == SecondFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
        }
        activityRule.scenario.recreate()
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == SecondFragment::class.java.simpleName)

            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
        }
    }

    @Test
    fun fragment_transaction_popBackStack() {
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            getCurrentFragment(it)?.run {
                showAlertDialog(message = "first dialog", lockWindow = true)
            }
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            getCurrentFragment(it)?.run {
                warpParentFragmentManager.beginTransaction().replace(R.id.container, SecondFragment()).addToBackStack(null).commit()
            }
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            getCurrentFragment(it)?.run {
                warpParentFragmentManager.popBackStack()
            }
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
        }
    }

    @Test
    fun fragment_transaction_popBackStack_recreate() {
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            getCurrentFragment(it)?.run {
                showAlertDialog(message = "first dialog", lockWindow = true)
            }
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            getCurrentFragment(it)?.run {
                warpParentFragmentManager.beginTransaction().replace(R.id.container, SecondFragment()).addToBackStack(null).commit()
            }
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            getCurrentFragment(it)?.run {
                warpParentFragmentManager.popBackStack()
            }
        }
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
        }
        activityRule.scenario.recreate()
        activityRule.scenario.onActivity {
            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(getCurrentFragment(it)?.javaClass?.simpleName == TestFragment::class.java.simpleName)
        }
    }
}