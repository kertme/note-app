package com.noteapp.datasource.local.db

import androidx.paging.PagingSource
import androidx.room.*

/**
 * @author: R. Cemre Ünal,
 * created on 9/21/2022
 */

@Dao
interface NoteDao {

    @Upsert
    fun upsertNote(noteEntity: NoteEntity)

    @Query("DELETE FROM noteentity")
    fun clearNotes()

    @Delete
    fun deleteNote(model: NoteEntity)

    @Query(
        """
        SELECT *
        FROM noteentity
        WHERE LOWER(content) LIKE '%' || LOWER(:query) || '%' OR 
            LOWER(title) LIKE '%' || LOWER(:query)
        """
    )
    fun searchNote(query: String): PagingSource<Int, NoteEntity>

    @Query("SELECT * FROM noteentity")
    fun getAllNotes(): PagingSource<Int, NoteEntity>
}