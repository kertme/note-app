package com.rcunal.supernotes.extension

import android.view.View
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher
import java.util.concurrent.TimeoutException

/**
 * @author: R. Cemre Ünal,
 * created on 9/23/2022
 */

fun ViewInteraction.waitUntilReady(
    timeout: Long = 30_000,
    matcher: () -> Matcher<View> = { ViewMatchers.isDisplayed() }
): ViewInteraction {
    val startTime = System.currentTimeMillis()
    val endTime = startTime + timeout

    do {
        try {
            check(ViewAssertions.matches(matcher()))
            return this
        } catch (e: Throwable) {
            Thread.sleep(50)
        }
    } while (System.currentTimeMillis() < endTime)

    throw TimeoutException()
}