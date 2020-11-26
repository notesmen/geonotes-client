package org.geonotes.client.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import org.geonotes.client.utils.DateConverter


@TypeConverters(DateConverter::class)
@Database(entities = [NoteBase::class, Tag::class, NoteBaseTagCrossRef::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
