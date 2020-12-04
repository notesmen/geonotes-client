package org.geonotes.client.model.entity

import androidx.room.*


@Entity
data class Tag(
    @PrimaryKey(autoGenerate = true)
    var tagId: Long,
    val name: String
) {
    constructor(name: String): this(0, name)
}