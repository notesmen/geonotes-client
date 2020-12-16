package org.geonotes.client.model.dao

import androidx.paging.DataSource
import androidx.room.*
import androidx.room.OnConflictStrategy.*

import org.geonotes.client.model.entity.Note
import org.geonotes.client.model.entity.NoteBaseTagCrossRef

@Dao
interface NoteTagRefDao {
    @Insert(onConflict = IGNORE)
    suspend fun save(noteBaseTagCrossRef: NoteBaseTagCrossRef)

    @Query("DELETE FROM NoteBaseTagCrossRef WHERE noteId = :noteId")
    suspend fun deleteNoteTagRefs(noteId: Long)

    @Transaction
    @Query("SELECT * FROM NoteBase ORDER BY lastChangeTime DESC")
    fun loadNotes(): DataSource.Factory<Int, Note>
}
