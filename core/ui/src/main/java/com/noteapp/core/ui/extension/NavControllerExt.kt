package com.noteapp.core.ui.extension

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.noteapp.core.ui.R

@SuppressLint("RestrictedApi")
fun NavController.navigateWithAnimation(route: String, args: Bundle?, navOptions: NavOptions? = null) {
    findDestination(route)?.id?.let { destinationId ->
        val navigationOptions = navOptions ?: NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()
        navigate(resId = destinationId, args = args, navOptions = navigationOptions)
    }
}