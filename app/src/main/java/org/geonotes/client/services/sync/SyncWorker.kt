package org.geonotes.client.services.sync

import kotlinx.coroutines.*

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

import org.geonotes.client.App
import org.geonotes.client.model.NoteDatabase
import org.geonotes.client.model.NoteRepository
import org.geonotes.client.model.entity.Note
import org.geonotes.client.model.entity.NoteDescription
import org.geonotes.client.utils.rest.GsonGetRequest
import org.geonotes.client.utils.rest.GsonPostRequest
import org.geonotes.client.utils.rest.HttpRequestQueue


class SyncWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams), CoroutineScope {

    private val prefs: SharedPreferences =
        appContext.getSharedPreferences("user-data", MODE_PRIVATE)
    private val scope: CoroutineScope = (appContext as App).applicationScope
    private val noteRepository: NoteRepository =
        NoteRepository(NoteDatabase.getDatabase(appContext))

    override suspend fun doWork(): Result {

        Log.i(TAG, "Sync worker RUN!")
        val authToken = prefs.getString("authentication-token", null)
            ?: return Result.failure(workDataOf("error" to "Authentication token not found"))

        val request = makeNoteListRequest(authToken)
        HttpRequestQueue.getInstance(applicationContext).addRequest(request)

        return Result.success()
    }

    private fun doSync(noteList: Array<NoteDescription>) = scope.launch(Dispatchers.Default) {
        val localNoteList: Map<Long, Long> =
            noteRepository.noteDescriptions.map { it.noteId to it.lastChangeTime }.toMap()

        val remoteNoteList: Map<Long, Long> =
            noteList.map { it.noteId to it.lastChangeTime }.toMap()

        val notesToDownload: MutableList<Long> = mutableListOf()
        for ((noteId, lastChangeTime) in remoteNoteList) {
            if (localNoteList.getOrDefault(noteId, 0) < lastChangeTime) {
                notesToDownload.add(noteId)
            }
        }
        launch { downloadNotes(notesToDownload) }

        val notesToUpload: MutableList<Long> = mutableListOf()
        for ((noteId, lastChangeTime) in localNoteList) {
            if (remoteNoteList.getOrDefault(noteId, 0) < lastChangeTime) {
                notesToUpload.add(noteId)
            }
        }
        launch { uploadNotes(notesToUpload) }
    }

    private fun downloadNotes(noteIds: List<Long>) {
        val authToken: String? = prefs.getString("authentication-token", null)
        if (authToken == null) {
            Log.w(TAG, "Authentication token not found at 'downloadNotes' method")
            return
        }

        noteIds.chunked(MAX_NOTES_IN_QUERY).forEach {
            HttpRequestQueue.getInstance(applicationContext).addRequest(
                makeDownloadRequest(authToken, it.toLongArray())
            )
        }
    }

    private fun uploadNotes(noteIds: List<Long>) {
        val authToken: String? = prefs.getString("authentication-token", null)
        if (authToken == null) {
            Log.w(TAG, "Authentication token not found at 'downloadNotes' method")
            return
        }

        noteIds.chunked(MAX_NOTES_IN_QUERY).forEach {
            HttpRequestQueue.getInstance(applicationContext).addRequest(
                makeUploadRequest(
                    authToken,
                    noteRepository.getNotesByIds(it.toLongArray()).toTypedArray()
                )
            )
        }
    }

    private fun downloadNotesCallback(notes: Array<Note>) = scope.launch(Dispatchers.IO) {
        for (note in notes) {
            noteRepository.addNote(note)
        }
    }

    private fun makeNoteListRequest(authToken: String): GsonGetRequest<Array<NoteDescription>> =
        GsonGetRequest(
            BASE_URL + "api/notes/list",
            Array<NoteDescription>::class.java,
            mutableMapOf("Authorization" to "Bearer $authToken"),
            this::doSync
        ) { error -> Log.d(TAG, "Note list request failed: ${error.networkResponse}") }

    private fun makeDownloadRequest(
        authToken: String,
        noteIds: LongArray
    ): GsonPostRequest<Array<Note>, LongArray> =
        GsonPostRequest(
            BASE_URL + "api/notes",
            noteIds,
            Array<Note>::class.java,
            mutableMapOf("Authorization" to "Bearer $authToken"),
            this::downloadNotesCallback,
        ) { error -> Log.d(TAG, "Note downloading failed: ${error.networkResponse}") }

    private fun makeUploadRequest(
        authToken: String,
        notes: Array<Note>
    ): GsonPostRequest<Unit, Array<Note>> =
        GsonPostRequest(
            BASE_URL + "api/notes",
            notes,
            Unit::class.java,
            mutableMapOf("Authorization" to "Bearer $authToken"),
            {},
        ) { error -> Log.d(TAG, "Note uploading failed: ${error.networkResponse}") }

    companion object {
        private const val MAX_NOTES_IN_QUERY = 50
        private const val BASE_URL = "http://localhost:8080/"
        private val TAG = this::class.simpleName
    }
}
