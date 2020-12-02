package org.geonotes.client.model.entity

import java.util.Date

import androidx.room.Entity


@Entity
data class NoteDescription(
    val noteId: Long,
    val lastChangeTime: Date
)