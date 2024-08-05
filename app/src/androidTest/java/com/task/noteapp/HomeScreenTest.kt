package com.rcunal.supernotes

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author: R. Cemre Ünal,
 * created on 9/23/2022
 */

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun checkIfHomeTitleVisible_expectVisible() {
        onView(
            allOf(
                withId(R.id.tv_title),
                withText(R.string.notes)
            )
        ).check(matches(isDisplayed()))
    }


    @Test
    fun checkIfFloatingActionButtonVisible_expectVisible() {
        onView(withId(R.id.floating_action_button)).check(matches(isDisplayed()))
    }

    @Test
    fun testClickFabAndNavigateToAddNoteFragment() {
        onView(withId(R.id.floating_action_button)).perform(click())
        onView(allOf(withId(R.id.btn_save), withText(R.string.save))).check(matches(isDisplayed()))
    }
}