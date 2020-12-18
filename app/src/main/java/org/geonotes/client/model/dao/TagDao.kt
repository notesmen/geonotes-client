package org.geonotes.client.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.*
import androidx.room.Query


import org.geonotes.client.model.entity.GeoTag

@Dao
interface TagDao {
    @Insert(onConflict = IGNORE)
    suspend fun save(geoTag: GeoTag): Long

    @Query("SELECT * from GeoTag")
    fun loadTags(): LiveData<List<GeoTag>>
}
