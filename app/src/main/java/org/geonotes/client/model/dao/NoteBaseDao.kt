package org.geonotes.client.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.*

import org.geonotes.client.model.entity.NoteBase
import org.geonotes.client.model.entity.NoteDescription


@Dao
interface NoteBaseDao {
    @Insert(onConflict = REPLACE)
    suspend fun save(noteBase: NoteBase): Long

    @Update
    suspend fun update(noteBase: NoteBase)

    @Delete
    suspend fun delete(noteBase: NoteBase)

    @Query("DELETE FROM NoteBase WHERE noteId = :noteId")
    suspend fun deleteById(noteId: Long)

    @Query("SELECT noteId, lastChangeTime FROM NoteBase")
    fun loadNoteDescriptions(): LiveData<List<NoteDescription>>
}
