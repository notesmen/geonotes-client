package org.geonotes.client.model.entity

import android.graphics.Color
import androidx.room.*


@Entity(indices = [(Index(value = ["noteId"], unique = true))])
data class NoteBase(
    @PrimaryKey(autoGenerate = true)
    var noteId: Long,
    val title: String,
    val text: String,
    val color: Int,
    val lastChangeTime: Long
) {
    constructor(title: String, text: String, color: Int)
            : this(0, title, text, color, System.nanoTime())
}
