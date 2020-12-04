package org.geonotes.client.model.entity

import androidx.room.*


@Entity(
    primaryKeys = ["noteId", "tagId"],
    indices = [
        Index(value = ["noteId"]),
        Index(value = ["tagId"])
    ]
)
data class NoteBaseTagCrossRef(
    var noteId: Long,
    var tagId: Long
)