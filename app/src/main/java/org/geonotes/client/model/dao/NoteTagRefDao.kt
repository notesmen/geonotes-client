package org.geonotes.client.model.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.*
import androidx.room.Query
import androidx.room.Transaction

import org.geonotes.client.model.entity.Note
import org.geonotes.client.model.entity.NoteBaseTagCrossRef
import org.geonotes.client.model.entity.Tag

@Dao
interface NoteTagRefDao {
    @Insert(onConflict = IGNORE)
    suspend fun save(noteBaseTagCrossRef: NoteBaseTagCrossRef)

    @Transaction
    @Query("SELECT * FROM NoteBase ORDER BY lastChangeTime DESC")
    fun loadNotes(): PagingSource<Int, Note>
}