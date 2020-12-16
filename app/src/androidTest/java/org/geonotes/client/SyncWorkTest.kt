package org.geonotes.client

import java.io.IOException
import java.lang.RuntimeException
import kotlin.jvm.Throws
import kotlinx.coroutines.*

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers
import org.mockito.Mockito
import org.mockito.internal.util.reflection.FieldSetter
import com.android.volley.Request
import com.android.volley.toolbox.BaseHttpStack
import com.android.volley.toolbox.HttpResponse
import com.google.gson.Gson

import org.geonotes.client.model.NoteDatabase
import org.geonotes.client.model.NoteRepository
import org.geonotes.client.model.entity.Note
import org.geonotes.client.model.entity.NoteBase
import org.geonotes.client.model.entity.NoteDescription
import org.geonotes.client.model.entity.Tag
import org.geonotes.client.services.sync.SyncWorker
import org.geonotes.client.utils.rest.HttpRequestQueue


open class MockHttpStack(
    private val remoteNotes: Array<Note>
) : BaseHttpStack() {
    private val gson = Gson()
    lateinit var uploadedContents: Array<Note>
    lateinit var downloadedContents: LongArray
    private var requestBody: ByteArray? = null

    override fun executeRequest(
        request: Request<*>?,
        additionalHeaders: MutableMap<String, String>?
    ): HttpResponse {
        requestBody = request?.body
        if (request?.url?.endsWith("/api/notes/list") == true) {
            return getNotesList()
        }
        if (request?.url?.endsWith("/api/notes/upload") == true) {
            return uploadNotes()
        }
        if (request?.url?.endsWith("/api/notes/download") == true) {
            return downloadNotes()
        }
        throw RuntimeException(String.format("Unexpected rest api endpoint: %s", request?.url))
    }

    fun getNotesList(): HttpResponse {
        val noteDescriptions: Array<NoteDescription> = remoteNotes.map {
            NoteDescription(it.noteBase.noteId, it.noteBase.lastChangeTime)
        }.toTypedArray()
        val responseBody: String = gson.toJson(noteDescriptions)
        return HttpResponse(200, listOf(), responseBody.length, responseBody.byteInputStream())
    }

    fun uploadNotes(): HttpResponse {
        uploadedContents =
            gson.fromJson(requestBody!!.decodeToString(), Array<Note>::class.java)
        val response = "Created"
        return HttpResponse(200, listOf(), response.length, response.byteInputStream())
    }

    fun downloadNotes(): HttpResponse {
        val contents = gson.fromJson(requestBody!!.decodeToString(), LongArray::class.java)
        downloadedContents = contents

        val response: String = gson.toJson(remoteNotes)
        return HttpResponse(200, listOf(), response.length, response.byteInputStream())
    }
}


@RunWith(AndroidJUnit4::class)
class SyncWorkTest {
    private lateinit var database: NoteDatabase
    private lateinit var appContext: Context
    private lateinit var remoteNotes: Array<Note>
    private lateinit var localNotes: Array<Note>
    private lateinit var noteRepository: NoteRepository
    private lateinit var mockHttpStack: MockHttpStack

    @Before
    fun createDb() {
        appContext = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(
            appContext, NoteDatabase::class.java
        ).build()

        FieldSetter.setField(
            NoteDatabase.Companion,
            NoteDatabase::class.java.getDeclaredField("INSTANCE"),
            database
        )

        noteRepository = NoteRepository(NoteDatabase.getDatabase(appContext))

        val sharedTag = Tag(100, "shared-tag")
        val remoteTag = Tag(101, "remote-tag")
        remoteNotes = arrayOf(
            Note(NoteBase(100, "remote", "remote note text", 100), listOf(remoteTag)),
            Note(NoteBase(101, "shared", "remote note text", 99), listOf(sharedTag, remoteTag))
        )

        val localTag = Tag(1, "local-tag")
        localNotes = arrayOf(
            Note(NoteBase(1, "local", "local note text", 10), listOf(localTag)),
            Note(NoteBase(101, "shared", "local note text", 98), listOf(localTag, sharedTag))
        )

        runBlocking {
            for (note in localNotes) {
                noteRepository.addNote(note)
            }
            delay(1000L)
        }

        mockHttpStack = MockHttpStack(remoteNotes)
        HttpRequestQueue.getInstance(appContext, mockHttpStack)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun syncTest() {
        val syncWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
        WorkManager.getInstance(appContext).enqueue(syncWorkRequest)

        //! wait for performing all the operations
        runBlocking { delay(2000L) }

//        Mockito.verify(mockHttpStack).getNotesList()
//        Mockito.verify(mockHttpStack).uploadNotes()
//        Mockito.verify(mockHttpStack).downloadNotes()

        assertThat(mockHttpStack.uploadedContents, Matchers.arrayWithSize(1))
        assertThat(mockHttpStack.uploadedContents, Matchers.arrayContaining(localNotes[0]))

        assertThat(
            mockHttpStack.downloadedContents.asSequence().toList().toTypedArray(),
            Matchers.arrayWithSize(2)
        )
        assertThat(
            mockHttpStack.downloadedContents.asSequence().toList().toTypedArray(),
            Matchers.arrayContainingInAnyOrder(
                remoteNotes[0].noteBase.noteId,
                remoteNotes[1].noteBase.noteId
            )
        )
    }
}
