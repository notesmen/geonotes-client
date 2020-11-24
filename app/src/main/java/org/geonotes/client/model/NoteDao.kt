package org.geonotes.client.model

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.*
import androidx.room.Query
import androidx.room.Transaction


@Dao
interface NoteDao {
    @Insert(onConflict = REPLACE)
    suspend fun save(note: NoteBase)

    @Transaction
    @Query("SELECT * FROM NoteBase")
    suspend fun loadNotes(): PagingSource<Int, Note>

    @Query("SELECT noteId, lastChangeTime FROM NoteBase")
    suspend fun loadNoteDescriptions(): LiveData<List<NoteDescription>>
}