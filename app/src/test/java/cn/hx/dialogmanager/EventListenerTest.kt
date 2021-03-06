package cn.hx.dialogmanager

import android.content.DialogInterface
import android.widget.Toast
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
import org.robolectric.shadows.ShadowToast

@RunWith(AndroidJUnit4::class)
class EventListenerTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(TestActivity::class.java)

    @Test
    fun onCancel() {
        activityRule.scenario.onActivity {
            val dialog = it.createAlertDialog(message = "first dialog", uuid = "onCancel_test_uuid")
            it.showPriorityDialog(dialog)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            Espresso.pressBack()
            assert(ShadowToast.getTextOfLatestToast() == "onCancel called")
        }
    }

    @Test
    fun onCancel_recreate() {
        activityRule.scenario.onActivity {
            val dialog = it.createAlertDialog(message = "first dialog", uuid = "onCancel_test_uuid")
            it.showPriorityDialog(dialog)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            Espresso.pressBack()

            assert(ShadowToast.getTextOfLatestToast() == "onCancel called")
        }
    }


    @Test
    fun onDismiss() {
        activityRule.scenario.onActivity {
            val dialog = it.createAlertDialog(message = "first dialog", uuid = "onDismiss_test_uuid")
            it.showPriorityDialog(dialog)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            Espresso.pressBack()
            assert(ShadowToast.getTextOfLatestToast() == "onDismiss called")
        }
    }

    @Test
    fun onDismiss_recreate() {
        activityRule.scenario.onActivity {
            val dialog = it.createAlertDialog(message = "first dialog", uuid = "onDismiss_test_uuid")
            it.showPriorityDialog(dialog)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            Espresso.pressBack()
            assert(ShadowToast.getTextOfLatestToast() == "onDismiss called")
        }
    }

    @Test
    fun onDialogEvent() {
        activityRule.scenario.onActivity {
            val dialog = it.createAlertDialog(message = "first dialog", uuid = "onDialogEvent_test_uuid")
            it.showPriorityDialog(dialog)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(ShadowToast.getTextOfLatestToast() == "button ${DialogInterface.BUTTON_POSITIVE} clicked")
        }
    }

    @Test
    fun onDialogEvent_recreate() {
        activityRule.scenario.onActivity {
            val dialog = it.createAlertDialog(message = "first dialog", uuid = "onDialogEvent_test_uuid")
            it.showPriorityDialog(dialog)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            Espresso.onView(ViewMatchers.withId(android.R.id.button2))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(ShadowToast.getTextOfLatestToast() == "button ${DialogInterface.BUTTON_NEGATIVE} clicked")
        }
    }

    @Test
    fun onCancelListener() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            val dialog = it.createAlertDialog(message = "first dialog")
            dialog.setOnCancelListener { priorityDialog ->
                (priorityDialog.dialogHost as TestActivity).finish()
            }
            it.showPriorityDialog(dialog)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            Espresso.pressBack()
            assert(it.isFinishing)
        }
    }

    @Test
    fun onCancelListener_recreate() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            val dialog = it.createAlertDialog(message = "first dialog")
            dialog.setOnCancelListener { priorityDialog ->
                (priorityDialog.dialogHost as TestActivity).finish()
            }
            it.showPriorityDialog(dialog)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            assert(!it.isFinishing)
        }
        activityRule.scenario.recreate()
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            assert(!it.isFinishing)
            Espresso.pressBack()
            assert(it.isFinishing)
        }
    }

    @Test
    fun onDismissListener() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            val dialog = it.createAlertDialog(message = "first dialog")
            dialog.setOnDismissListener { priorityDialog ->
                (priorityDialog.dialogHost as TestActivity).finish()
            }
            it.showPriorityDialog(dialog)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            Espresso.pressBack()
        }
        activityRule.scenario.onActivity {
            assert(it.isFinishing)
        }
    }

    @Test
    fun onDismissListener_recreate() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            val dialog = it.createAlertDialog(message = "first dialog")
            dialog.setOnDismissListener { priorityDialog ->
                (priorityDialog.dialogHost as TestActivity).finish()
            }
            it.showPriorityDialog(dialog)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            assert(!it.isFinishing)
        }
        activityRule.scenario.recreate()
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            assert(!it.isFinishing)
            Espresso.pressBack()
            assert(it.isFinishing)
        }
    }


    @Test
    fun onDialogEventListener() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            val dialog = it.createAlertDialog(message = "first dialog")
            dialog.setOnDialogEventListener { priorityDialog, event ->
                Toast.makeText(priorityDialog.dialogHost as TestActivity, "button ${(event as? BaseAlertDialog.AlertDialogClickEvent)?.which} clicked", Toast.LENGTH_SHORT).show()
            }
            it.showPriorityDialog(dialog)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())

            assert(ShadowToast.getTextOfLatestToast() == "button ${DialogInterface.BUTTON_POSITIVE} clicked")
        }
    }

    @Test
    fun onDialogEventListener_recreate() {
        activityRule.scenario.onActivity {
            assert(it.currentPriorityDialog == null)
            val dialog = it.createAlertDialog(message = "first dialog")
            dialog.setOnDialogEventListener { priorityDialog, event ->
                Toast.makeText(priorityDialog.dialogHost as TestActivity, "button ${(event as? BaseAlertDialog.AlertDialogClickEvent)?.which} clicked", Toast.LENGTH_SHORT).show()
            }
            it.showPriorityDialog(dialog)
        }
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))
        }
        activityRule.scenario.recreate()
        activityRule.scenario.onActivity {
            Espresso.onView(ViewMatchers.withId(android.R.id.message))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .check(ViewAssertions.matches(ViewMatchers.withText("first dialog")))

            Espresso.onView(ViewMatchers.withId(android.R.id.button2))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(it.window.decorView)))
                    .perform(ViewActions.click())
            assert(ShadowToast.getTextOfLatestToast() == "button ${DialogInterface.BUTTON_NEGATIVE} clicked")
        }
    }
}