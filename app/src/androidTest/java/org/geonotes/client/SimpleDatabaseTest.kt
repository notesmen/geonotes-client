package org.geonotes.client

import java.io.IOException
import kotlin.jvm.Throws
import kotlinx.coroutines.*

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.geonotes.client.old.viewmodel.model.NoteDatabase
import org.geonotes.client.old.viewmodel.model.NoteRepository
import org.geonotes.client.old.viewmodel.model.entity.Note
import org.geonotes.client.old.viewmodel.model.entity.NoteBase
import org.geonotes.client.old.viewmodel.model.entity.Tag
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
        runBlocking {
            withContext(Dispatchers.Main) {
                noteRepository.availableTags.observeForever { }
                noteRepository.notes.observeForever { }
            }
        }
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

        GlobalScope.launch {
            noteRepository.addNote(sampleNote)
            noteRepository.addNote(anotherNote)
        }

        // wait for live data update
        runBlocking {
            delay(1000L);
        }

        // ensure tagId and noteId were generated
        assertThat(sampleTag.tagId, Matchers.greaterThan(0L))
        assertThat(anotherTag.tagId, Matchers.greaterThan(0L))
        assertThat(sampleNote.noteBase.noteId, Matchers.greaterThan(0L))
        assertThat(anotherNote.noteBase.noteId, Matchers.greaterThan(0L))

        // ensure tags and notes were inserted and delivered to LiveData
        assertThat(
            noteRepository.availableTags.value,
            Matchers.containsInAnyOrder(sampleTag, anotherTag)
        )
        assertThat(noteRepository.notes.value, Matchers.contains(anotherNote, sampleNote))
    }
}
