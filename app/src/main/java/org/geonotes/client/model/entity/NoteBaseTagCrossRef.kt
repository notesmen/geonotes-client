package org.geonotes.client.model.entity

import androidx.room.*


@Entity(
    primaryKeys = ["noteId", "tagId"],
    indices = [
        Index(value = ["noteId"], unique = true),
        Index(value = ["tagId"], unique = true)
    ]
)
data class NoteBaseTagCrossRef(
    val noteId: Long,
    val tagId: Long
)