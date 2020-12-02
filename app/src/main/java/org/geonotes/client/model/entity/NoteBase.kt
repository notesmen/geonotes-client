package org.geonotes.client.model.entity

import java.util.Date

import androidx.room.*


@Entity(indices = [(Index(value = ["noteId"], unique = true))])
data class NoteBase(
    @PrimaryKey(autoGenerate = true)
    val noteId: Long,
    val title: String,
    val text: String,
    val lastChangeTime: Date
)