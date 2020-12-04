package org.geonotes.client.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.*
import androidx.room.Query


import org.geonotes.client.model.entity.Tag

@Dao
interface TagDao {
    @Insert(onConflict = IGNORE)
    suspend fun save(tag: Tag): Long

    @Query("SELECT * from Tag")
    fun loadTags(): LiveData<List<Tag>>
}