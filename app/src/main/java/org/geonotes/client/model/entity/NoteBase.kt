package org.geonotes.client.model.entity

import androidx.room.*


@Entity(indices = [(Index(value = ["noteId"], unique = true))])
data class NoteBase(
    @PrimaryKey(autoGenerate = true)
    var noteId: Long,
    val title: String,
    val text: String,
    val lastChangeTime: Long
) {
    constructor(title: String, text: String) : this(0, title, text, System.nanoTime())
}
