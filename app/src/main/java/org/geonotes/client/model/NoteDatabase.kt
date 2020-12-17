package org.geonotes.client.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import org.geonotes.client.model.dao.NoteBaseDao
import org.geonotes.client.model.dao.NoteTagRefDao
import org.geonotes.client.model.dao.TagDao
import org.geonotes.client.model.entity.NoteBase
import org.geonotes.client.model.entity.NoteBaseTagCrossRef
import org.geonotes.client.model.entity.Tag


@Database(entities = [NoteBase::class, Tag::class, NoteBaseTagCrossRef::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteBaseDao
    abstract fun tagDao(): TagDao
    abstract fun noteTagRefDao(): NoteTagRefDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "NoteDatabase"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
