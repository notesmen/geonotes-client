package org.geonotes.client.model.entity

import androidx.room.*

data class Note(
    @Embedded
    val noteBase: NoteBase,

    @Relation(
        parentColumn = "noteId",
        entityColumn = "tagId",
        associateBy = Junction(NoteBaseTagCrossRef::class)
    )
    var geoTags: List<GeoTag>
)
