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
            it.showPriorityDialog(it.createAlertDialog(message = "first dialog").apply {
                priorityConfig.priority = 1
                priorityConfig.isShowImmediateAfterPreDismiss = false
            })
        }
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            it.showPriorityDialog(it.createAlertDialog(message = "second dialog").apply {
                priorityConfig.priority = 2
                priorityConfig.isShowImmediateAfterPreDismiss = false
            })
        }
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("second dialog")))
            it.showPriorityDialog(it.createAlertDialog(message = "third dialog").apply {
                priorityConfig.priority = 3
                priorityConfig.isShowImmediateAfterPreDismiss = false
            })
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

    @Test
    fun showNewDialogIgnorePending() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            it.showPriorityDialog(it.createAlertDialog(message = "first dialog").apply {
                priorityConfig.priority = 2
                priorityConfig.isShowImmediateAfterPreDismiss = false
            })
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            it.showPriorityDialog(it.createAlertDialog(message = "second dialog").apply {
                priorityConfig.priority = 3
                priorityConfig.isShowImmediateAfterPreDismiss = false
            })
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("second dialog")))

            Espresso.onView(ViewMatchers.withId(R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(it.currentPriorityDialog == null)
            assert(it.allPendingDialog.size == 1)

            it.showPriorityDialog(it.createAlertDialog(message = "fourth dialog").apply {
                priorityConfig.priority = 1
                priorityConfig.isShowImmediateAfterPreDismiss = false
            })
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("fourth dialog")))
        }
    }

    @Test
    fun showNewDialogCasePending_addToPending_showNextPending() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)

            it.showPriorityDialog(it.createAlertDialog(message = "first dialog").apply {
                priorityConfig.priority = 2
                priorityConfig.isShowImmediateAfterPreDismiss = false
                priorityConfig.isCasePending = true
            })
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            it.showPriorityDialog(it.createAlertDialog(message = "second dialog").apply {
                priorityConfig.priority = 3
                priorityConfig.isShowImmediateAfterPreDismiss = false
                priorityConfig.isCasePending = true
            })
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("second dialog")))

            Espresso.onView(ViewMatchers.withId(R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(it.currentPriorityDialog == null)
            assert(it.allPendingDialog.size == 1)

            it.showPriorityDialog(it.createAlertDialog(message = "third dialog").apply {
                priorityConfig.priority = 1
                priorityConfig.isShowImmediateAfterPreDismiss = false
                priorityConfig.isCasePending = true
            })
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            assert(it.allPendingDialog.size == 1)
        }
    }

    @Test
    fun showNewDialogCasePending_addToPending_notShowNextPending() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            it.showPriorityDialog(it.createAlertDialog(message = "first dialog").apply {
                priorityConfig.priority = 2
                priorityConfig.isShowImmediateAfterPreDismiss = false
                priorityConfig.isCasePending = true
                priorityConfig.isShowImmediateAfterPreCanNotShowCasePending = false
            })
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            it.showPriorityDialog(it.createAlertDialog(message = "second dialog").apply {
                priorityConfig.priority = 3
                priorityConfig.isShowImmediateAfterPreDismiss = false
                priorityConfig.isCasePending = true
                priorityConfig.isShowImmediateAfterPreCanNotShowCasePending = false
            })
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("second dialog")))

            Espresso.onView(ViewMatchers.withId(R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(it.currentPriorityDialog == null)
            assert(it.allPendingDialog.size == 1)

            it.showPriorityDialog(it.createAlertDialog(message = "third dialog").apply {
                priorityConfig.priority = 1
                priorityConfig.isShowImmediateAfterPreDismiss = false
                priorityConfig.isCasePending = true
                priorityConfig.isShowImmediateAfterPreCanNotShowCasePending = false
            })
            assert(it.currentPriorityDialog == null)
            assert(it.allPendingDialog.size == 2)
        }
    }

    @Test
    fun showNewDialogCasePending_notAddToPending_showNextPending() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            it.showPriorityDialog(it.createAlertDialog(message = "first dialog").apply {
                priorityConfig.priority = 2
                priorityConfig.isShowImmediateAfterPreDismiss = false
                priorityConfig.isCasePending = true
                priorityConfig.isAddToPendingWhenCanNotShow = false
            })
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            it.showPriorityDialog(it.createAlertDialog(message = "second dialog").apply {
                priorityConfig.priority = 3
                priorityConfig.isShowImmediateAfterPreDismiss = false
                priorityConfig.isCasePending = true
                priorityConfig.isAddToPendingWhenCanNotShow = false
            })
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("second dialog")))

            Espresso.onView(ViewMatchers.withId(R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(it.currentPriorityDialog == null)
            assert(it.allPendingDialog.size == 1)
            it.showPriorityDialog(it.createAlertDialog(message = "third dialog").apply {
                priorityConfig.priority = 1
                priorityConfig.isShowImmediateAfterPreDismiss = false
                priorityConfig.isCasePending = true
                priorityConfig.isAddToPendingWhenCanNotShow = false
            })
            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            assert(it.allPendingDialog.size == 0)
        }
    }

    @Test
    fun showNewDialogCasePending_notAddToPending_notShowNextPending() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)

            it.showPriorityDialog(it.createAlertDialog(message = "first dialog").apply {
                priorityConfig.priority = 2
                priorityConfig.isShowImmediateAfterPreDismiss = false
                priorityConfig.isCasePending = true
                priorityConfig.isAddToPendingWhenCanNotShow = false
                priorityConfig.isShowImmediateAfterPreCanNotShowCasePending = false
            })

            assert(it.currentPriorityDialog != null)
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            it.showPriorityDialog(it.createAlertDialog(message = "second dialog").apply {
                priorityConfig.priority = 3
                priorityConfig.isShowImmediateAfterPreDismiss = false
                priorityConfig.isCasePending = true
                priorityConfig.isAddToPendingWhenCanNotShow = false
                priorityConfig.isShowImmediateAfterPreCanNotShowCasePending = false
            })
            Espresso.onView(ViewMatchers.withId(R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("second dialog")))

            Espresso.onView(ViewMatchers.withId(R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(it.currentPriorityDialog == null)
            assert(it.allPendingDialog.size == 1)

            it.showPriorityDialog(it.createAlertDialog(message = "third dialog").apply {
                priorityConfig.priority = 1
                priorityConfig.isShowImmediateAfterPreDismiss = false
                priorityConfig.isCasePending = true
                priorityConfig.isAddToPendingWhenCanNotShow = false
                priorityConfig.isShowImmediateAfterPreCanNotShowCasePending = false
            })
            assert(it.currentPriorityDialog == null)
            assert(it.allPendingDialog.size == 1)
        }
    }

}