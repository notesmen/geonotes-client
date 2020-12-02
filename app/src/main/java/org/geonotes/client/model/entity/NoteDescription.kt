package org.geonotes.client.model.entity

import androidx.room.Entity


@Entity
data class NoteDescription(
    val noteId: Long,
    val lastChangeTime: Long
)