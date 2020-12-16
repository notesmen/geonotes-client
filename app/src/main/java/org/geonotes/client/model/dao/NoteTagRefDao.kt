package org.geonotes.client.model.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.*
import androidx.room.Query
import androidx.room.Transaction

import org.geonotes.client.model.entity.Note
import org.geonotes.client.model.entity.NoteBaseTagCrossRef

@Dao
interface NoteTagRefDao {
    @Insert(onConflict = IGNORE)
    suspend fun save(noteBaseTagCrossRef: NoteBaseTagCrossRef)

    @Transaction
    @Query("SELECT * FROM NoteBase ORDER BY lastChangeTime DESC")
    fun loadNotes(): DataSource.Factory<Int, Note>

    @Transaction
    @Query("SELECT * FROM NoteBase WHERE noteId IN(:ids)")
    fun loadNotesByIds(ids: LongArray): List<Note>
}
