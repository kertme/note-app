package com.task.noteapp.core.di

import android.app.Application
import androidx.room.Room
import com.task.noteapp.features.add_note_view_note.common.db.NoteDatabase
import com.task.noteapp.core.utils.Constant.NOTE_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

/**
 * @author: R. Cemre Ünal,
 * created on 9/21/2022
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application) : NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            NOTE_DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideDispatcher(): Dispatchers {
        return Dispatchers
    }
}