package org.geonotes.client.model.entity

import androidx.room.*


@Entity
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val tagId: Long,
    val name: String
)
