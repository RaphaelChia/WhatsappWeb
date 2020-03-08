package edu.singaporetech.codedispensary

import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by chek on 7 Mar 2020.
 *
 * Tests that your view ids and button labels are all in place.
 */
@RunWith(AndroidJUnit4::class)
class ViewInstrumentedTest {
    companion object {
        private const val TAG = "ViewInstrumentedTest"

        // test data
        private const val TIMEOUT = 1500L
        private const val PRINT_BUTTON_TEXT = "PRINT COD3"
        private const val SHUTDOWN_BUTTON_TEXT = "SHUTDOWN"
    }

    @get:Rule
    var activityRule = activityScenarioRule<CodeDispensaryView>()

    @Test
    fun onLaunch_containsRequiredUI() {
        Log.i(TAG, """
            ### 0. UI elements all exist
            - wait a teeny bit
            - check text element textViewResult exist
            - check buttons "$PRINT_BUTTON_TEXT" and "$SHUTDOWN_BUTTON_TEXT" exist 
            """.trimIndent())

        Thread.sleep(1500)

        onView(withId(R.id.textViewPrint))
            .check(matches(isDisplayed()))

        onView(allOf(withClassName(endsWith("Button")), withText(PRINT_BUTTON_TEXT)))
            .check(matches(isDisplayed()))

        onView(allOf(withClassName(endsWith("Button")), withText(SHUTDOWN_BUTTON_TEXT)))
            .check(matches(isDisplayed()))
    }
}