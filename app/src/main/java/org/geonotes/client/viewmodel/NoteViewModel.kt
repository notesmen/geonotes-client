package org.geonotes.client.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

import org.geonotes.client.App
import org.geonotes.client.model.NoteDatabase
import org.geonotes.client.model.NoteRepository
import org.geonotes.client.model.entity.Note

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val noteRepository: NoteRepository
    private val database: NoteDatabase

    init {
        application as App
        database = NoteDatabase.getDatabase(application)
        noteRepository = NoteRepository(
            database.noteDao(),
            database.tagDao(),
            database.noteTagRefDao()
        )
    }

    fun addNote(note: Note) = viewModelScope.launch {
        noteRepository.addNote(note)
    }

    fun getNotes() = noteRepository.notes
}