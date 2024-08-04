package com.rcunal.supernotes.matcher

import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

/**
 * @author: R. Cemre Ünal,
 * created on 9/23/2022
 */
class DrawableMatcher(private val expectedId: Int) : TypeSafeMatcher<View>() {

    override fun matchesSafely(target: View): Boolean {
        if (target !is ImageView || target.visibility == View.GONE) {
            return false
        }
        if (expectedId < 0) {
            return target.drawable == null
        }
        val context = target.context
        val expectedBitmap = context.getDrawable(expectedId)?.toBitmap()

        return target.drawable.toBitmap().sameAs(expectedBitmap)
    }

    override fun describeTo(description: Description?) {
        description?.appendText("isMatchingDrawableResource")
    }
}