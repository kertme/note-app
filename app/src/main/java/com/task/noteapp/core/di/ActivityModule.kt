package com.rcunal.supernotes.core.di

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.rcunal.supernotes.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {
    @Provides
    fun provideNavController(@ActivityContext activityContext: Context): NavController {
        val activity = activityContext as FragmentActivity
        val navHostFragment =
            activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }
}

