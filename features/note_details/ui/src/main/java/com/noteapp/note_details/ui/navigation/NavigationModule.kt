package com.noteapp.note_details.ui.navigation

import com.noteapp.core.ui.navigation.NavigationNode
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {
    @IntoSet
    @Binds
    fun bindNavigationNode(noteDetailsNavigationNode: NoteDetailsNavigationNode): NavigationNode
}