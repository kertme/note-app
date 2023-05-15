package com.task.noteapp.features.add_note_view_note.common.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * @author: R. Cemre Ünal,
 * created on 9/21/2022
 */
@Database(
    entities = [NoteEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract val noteDao: NoteDao
}