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
import org.geonotes.client.model.entity.Note
import org.geonotes.client.model.entity.NoteBase
import org.geonotes.client.model.entity.NoteBaseTagCrossRef
import org.geonotes.client.model.entity.GeoTag


class NoteRepository constructor(
    private val noteBaseDao: NoteBaseDao,
    private val tagDao: TagDao,
    private val noteTagRefDao: NoteTagRefDao
) {
    val notes = loadNotes()
    val availableTags = loadTags()

    suspend fun addNote(note: Note) = withContext(Dispatchers.IO) {
        insertNoteBase(note.noteBase)
        for (tag in note.geoTags) {
            insertTag(tag)
            Log.d(TAG, "Add cross ref between ${note.noteBase.noteId} and ${tag.tagId}")
            noteTagRefDao.save(NoteBaseTagCrossRef(note.noteBase.noteId, tag.tagId))
        }
    }

    suspend fun updateNote(note: Note) = withContext(Dispatchers.IO) {
        noteTagRefDao.deleteNoteTagRefs(note.noteBase.noteId)
        noteBaseDao.update(note.noteBase)
        for (tag in note.geoTags) {
            insertTag(tag)
            noteTagRefDao.save(NoteBaseTagCrossRef(note.noteBase.noteId, tag.tagId))
        }
    }

    suspend fun deleteNote(note: Note) = withContext(Dispatchers.IO) {
        noteTagRefDao.deleteNoteTagRefs(note.noteBase.noteId)
        noteBaseDao.delete(note.noteBase)
    }

    private suspend fun insertNoteBase(noteBase: NoteBase) {
        val noteId = noteBaseDao.save(noteBase)
        if (noteId != -1L) {
            noteBase.noteId = noteId
        }
    }

    private suspend fun insertTag(geoTag: GeoTag) {
        Log.d(TAG, "Add new tag $geoTag")
        val tagId = tagDao.save(geoTag)
        if (tagId != -1L) {
            geoTag.tagId = tagId
        }
    }

    private fun loadNotes(): LiveData<PagedList<Note>> =
        noteTagRefDao.loadNotes().toLiveData(pageSize = 30)

    private fun loadTags(): LiveData<List<GeoTag>> = tagDao.loadTags()

    companion object {
        private val TAG = this::class.simpleName
    }
}
