package org.geonotes.client.model.entity

import androidx.room.*


@Entity
data class GeoTag(
    @PrimaryKey(autoGenerate = true)
    var tagId: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double
) {
    constructor(name: String, latitude: Double, longitude: Double): this(0, name, latitude, longitude)
}
