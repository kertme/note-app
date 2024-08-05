package com.rcunal.supernotes.features.add_note_view_note.common.db

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.HiltAndroidTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noteapp.datasource.local.db.NoteDao
import com.noteapp.datasource.local.db.NoteDatabase
import com.noteapp.datasource.local.db.NoteEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch

/**
 * @author: R. Cemre Ünal,
 * created on 9/23/2022
 */

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NoteDatabaseTest {

    private lateinit var noteDao: NoteDao
    private lateinit var db: NoteDatabase

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(
            context, NoteDatabase::class.java
        ).build()
        noteDao = db.noteDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndReadNote() = runBlocking {
        val noteEntity = NoteEntity(
            dbId = 1,
            createDate = Date(),
            modifyDate = null,
            title = "",
            content = null,
            imageUrl = null
        )
        noteDao.upsertNote(noteEntity)
        val notes = noteDao.getAllNotes().getData()
        assertThat(
            "The note that was expected to be saved to the db could not be found.",
            notes.contains(noteEntity)
        )
    }

    @Test
    fun insertAndDeleteNote() = runBlocking {
        val noteEntity = NoteEntity(
            dbId = 1,
            createDate = Date(),
            modifyDate = null,
            title = "",
            content = null,
            imageUrl = null
        )
        noteDao.upsertNote(noteEntity)
        noteDao.deleteNote(noteEntity)
        val notes = noteDao.getAllNotes().getData()
        assertThat(
            "The note that was expected to be deleted from the db is still existing.",
            notes.contains(noteEntity).not()
        )
    }

    @Test
    fun insertAndClearNotes() = runBlocking {
        val noteEntity = NoteEntity(
            dbId = 1,
            createDate = Date(),
            modifyDate = null,
            title = "",
            content = null,
            imageUrl = null
        )
        noteDao.upsertNote(noteEntity)
        noteDao.clearNotes()
        val notes = noteDao.getAllNotes().getData()
        assertThat(
            "The db is not cleared successfully",
            notes.isEmpty()
        )
    }

    private fun <PaginationKey : Any, Model : Any> PagingSource<PaginationKey, Model>.getData(): List<Model> {
        val data = mutableListOf<Model>()
        val latch = CountDownLatch(1)
        val job = CoroutineScope(Dispatchers.Main).launch {
            val loadResult: PagingSource.LoadResult<PaginationKey, Model> = this@getData.load(
                PagingSource.LoadParams.Refresh(
                    key = null, loadSize = Int.MAX_VALUE, placeholdersEnabled = false
                )
            )
            when (loadResult) {
                is PagingSource.LoadResult.Error -> throw loadResult.throwable
                is PagingSource.LoadResult.Page -> data.addAll(loadResult.data)
                is PagingSource.LoadResult.Invalid -> {}
            }
            latch.countDown()
        }
        latch.await()
        job.cancel()
        return data
    }
}