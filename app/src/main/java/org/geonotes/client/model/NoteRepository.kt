package org.geonotes.client.model

import javax.inject.Inject
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource

import org.geonotes.client.model.dao.NoteBaseDao
import org.geonotes.client.model.dao.NoteTagRefDao
import org.geonotes.client.model.dao.TagDao
import org.geonotes.client.model.entity.Note
import org.geonotes.client.model.entity.NoteBaseTagCrossRef
import org.geonotes.client.model.entity.Tag


class NoteRepository @Inject constructor(
        private val noteBaseDao: NoteBaseDao,
        private val tagDao: TagDao,
        private val noteTagRefDao: NoteTagRefDao
) {

    val notes = loadNotes()
    val availableTags = loadTags()

    suspend fun addNote(note: Note) = withContext(Dispatchers.IO) {
        noteBaseDao.save(note.noteBase)
        for (tag in note.tags) {
            tagDao.save(tag)
            noteTagRefDao.save(NoteBaseTagCrossRef(note.noteBase.noteId, tag.tagId))
        }
    }

    private fun loadNotes(): PagingSource<Int, Note> = noteTagRefDao.loadNotes()

    private fun loadTags(): LiveData<List<Tag>> = tagDao.loadTags()
}