package org.geonotes.client.observers

import org.geonotes.client.entities.Note

/**
 * Standard observer interface for Notes
 */
interface NoteObserver {
    /**
     * Called on notes update
     * @param mNotes Array of notes
     */
    fun onNotesChanged(mNotes: Array<Note>?)
}