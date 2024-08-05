package com.rcunal.supernotes

import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noteapp.note_details.shared.NoteDetailsCommunicator
import com.rcunal.supernotes.extension.launchFragmentInHiltContainer
import com.rcunal.supernotes.extension.waitUntilReady
import com.noteapp.note_details.shared.model.NoteDetailsType
import com.noteapp.note_details.ui.model.NoteDetailsParcelableArguments
import com.rcunal.supernotes.matcher.DrawableMatcher
import com.rcunal.supernotes.matcher.ToastMatcher
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NoteScreenTest {

    companion object {
        const val EXAMPLE_PHOTO_URL =
            "https://www.google.com/images/branding/googlelogo/1x/googlelogo_light_color_272x92dp.png"
    }

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val args = bundleOf(
        NoteDetailsCommunicator.noteDetailsNavKey to NoteDetailsParcelableArguments(noteDetailsType = NoteDetailsType.ADD)
    )

    @Test
    fun testAddNewNoteState_expectBackButtonVisibleAndDisplaysCorrectDrawable() {
        launchFragmentInHiltContainer<com.noteapp.note_details.ui.NoteFragment>(args)
        onView(withId(R.id.iv_back)).check(
            matches(
                allOf(
                    isDisplayed(),
                    DrawableMatcher(R.drawable.ic_arrow_back)
                )
            )
        )
    }
    @Test
    fun testAddNewNoteState_expectEditTextsAreEmpty() {
        launchFragmentInHiltContainer<com.noteapp.note_details.ui.NoteFragment>(args)
        onView(withId(R.id.et_title)).check(
            matches(
                allOf(
                    isDisplayed(),
                    withText("")
                )
            )
        )

        onView(withId(R.id.et_content)).check(
            matches(
                allOf(
                    isDisplayed(),
                    withText("")
                )
            )
        )
    }

    @Test
    fun testPhotoImageView_addPhotoUrlAndExpectVisible() {
        launchFragmentInHiltContainer<com.noteapp.note_details.ui.NoteFragment>(args)

        onView(allOf(withId(R.id.iv_photo), withParent(withId(R.id.cl_root)))).check(
            matches(
                not(
                    isDisplayed()
                )
            )
        )
        onView(withId(R.id.iv_add_photo)).perform(click())
        onView(withId(R.id.et_photo)).perform(click())
        onView(withId(R.id.et_photo)).perform(typeText(EXAMPLE_PHOTO_URL))
        onView(withText(R.string.ok)).perform(click())
        onView(allOf(withId(R.id.iv_photo), withParent(withId(R.id.cl_root)))).waitUntilReady {
            isDisplayed()
        }
    }

    @Test
    fun testSaveButtonWithEmptyInputs_expectToastMessageAndNotSavedNote() {
        launchFragmentInHiltContainer<com.noteapp.note_details.ui.NoteFragment>(args)
        onView(withId(R.id.btn_save)).perform(click())
        ToastMatcher.onToast((R.string.note_title_not_entered_error))

    }
}