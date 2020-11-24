package org.geonotes.client.model

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [NoteBase::class, Tag::class, NoteBaseTagCrossRef::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
