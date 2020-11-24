package org.geonotes.client.model

import java.util.*

import androidx.room.*


@Entity
data class NoteBase(
    @PrimaryKey(autoGenerate = true)
    val noteId: String,
    val title: String,
    val text: String,
    val lastChangeTime: Date
)

@Entity
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val tagId: String,
    val name: String
)

@Entity(primaryKeys = ["noteId", "tagId"])
data class NoteBaseTagCrossRef(
    val noteId: String,
    val tagId: String
)

@Entity
data class Note(
    @Embedded
    val noteBase: NoteBase,

    @Relation(
        parentColumn = "noteId",
        entityColumn = "tagId",
        associateBy = Junction(NoteBaseTagCrossRef::class)
    )
    val tags: List<Tag>
)

@Entity
data class NoteDescription(
    val noteId: String,
    val lastChangeTime: Date
)