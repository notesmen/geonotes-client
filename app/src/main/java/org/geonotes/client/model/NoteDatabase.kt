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
import org.geonotes.client.model.entity.GeoTag


@Database(entities = [NoteBase::class, GeoTag::class, NoteBaseTagCrossRef::class], version = 2)
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
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
