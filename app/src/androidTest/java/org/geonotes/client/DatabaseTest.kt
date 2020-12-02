package org.geonotes.client

import java.io.IOException
import kotlin.jvm.Throws

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

import org.geonotes.client.model.NoteDatabase
import org.geonotes.client.model.NoteRepository
import org.geonotes.client.model.entity.Note
import org.geonotes.client.model.entity.NoteBase
import org.geonotes.client.model.entity.Tag
import org.hamcrest.Matchers


@RunWith(AndroidJUnit4::class)
class SimpleDatabaseTest {
    private lateinit var database: NoteDatabase
    private lateinit var noteRepository: NoteRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, NoteDatabase::class.java
        ).build()
        noteRepository = NoteRepository(
            database.noteDao(),
            database.tagDao(),
            database.noteTagRefDao()
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun databaseTest() {
        val sampleNoteBase = NoteBase("Example note #1", "some text here")
        val anotherNoteBase = NoteBase("Example note #2", "another text here")
        val sampleTag = Tag("sample-tag")
        val anotherTag = Tag("another-tag")

        val sampleNote = Note(sampleNoteBase, listOf(sampleTag, anotherTag))
        val anotherNote = Note(anotherNoteBase, listOf(anotherTag))

        runBlocking {
            noteRepository.addNote(sampleNote)
            noteRepository.addNote(anotherNote)
        }

        assertThat(noteRepository.availableTags.value, Matchers.containsInAnyOrder(sampleTag, anotherTag))
        assertThat(noteRepository.notes.value, Matchers.contains(anotherNote, sampleNote))
    }
}