package org.geonotes.client.model

import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData

import org.geonotes.client.model.dao.NoteBaseDao
import org.geonotes.client.model.dao.NoteTagRefDao
import org.geonotes.client.model.dao.TagDao
import org.geonotes.client.model.entity.*


class NoteRepository constructor(
    private val noteBaseDao: NoteBaseDao,
    private val tagDao: TagDao,
    private val noteTagRefDao: NoteTagRefDao
) {
    constructor(database: NoteDatabase): this(database.noteDao(), database.tagDao(), database.noteTagRefDao())

    val notes by lazy { loadNotes() }
    val availableTags by lazy { loadTags() }
    val noteDescriptions: List<NoteDescription>
        get() = noteBaseDao.loadNoteDescriptions()

    fun getNotesByIds(ids: LongArray): List<Note>
        = noteTagRefDao.loadNotesByIds(ids)

    suspend fun addNote(note: Note) = withContext(Dispatchers.IO) {
        insertNoteBase(note.noteBase)
        for (tag in note.tags) {
            insertTag(tag)
            Log.d(TAG, "Add cross ref between ${note.noteBase.noteId} and ${tag.tagId}")
            noteTagRefDao.save(NoteBaseTagCrossRef(note.noteBase.noteId, tag.tagId))
        }
    }

    private suspend fun insertNoteBase(noteBase: NoteBase) {
        val noteId = noteBaseDao.save(noteBase)
        if (noteId != -1L) {
            noteBase.noteId = noteId
        }
    }

    private suspend fun insertTag(tag: Tag) {
        Log.d(TAG, "Add new tag $tag")
        val tagId = tagDao.save(tag)
        if (tagId != -1L) {
            tag.tagId = tagId
        }
    }

    private fun loadNotes(): LiveData<PagedList<Note>> =
        noteTagRefDao.loadNotes().toLiveData(pageSize = 30)

    private fun loadTags(): LiveData<List<Tag>> = tagDao.loadTags()

    companion object {
        private val TAG = this::class.simpleName
    }
}
