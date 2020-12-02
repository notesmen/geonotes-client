package org.geonotes.client.model.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.*
import androidx.room.Query
import androidx.room.Transaction

import org.geonotes.client.model.entity.NoteBase
import org.geonotes.client.model.entity.NoteDescription


@Dao
interface NoteBaseDao {
    @Insert(onConflict = REPLACE)
    suspend fun save(noteBase: NoteBase)

    @Query("SELECT noteId, lastChangeTime FROM NoteBase")
    fun loadNoteDescriptions(): LiveData<List<NoteDescription>>
}