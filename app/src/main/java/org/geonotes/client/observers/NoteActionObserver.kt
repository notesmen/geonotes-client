package org.geonotes.client.observers

import org.geonotes.client.entities.Note
import org.geonotes.client.enums.Action

/**
 * Observer interface for NoteActionManager
 */
interface NoteActionObserver {
    /**
     * Called when action request was created
     * @param note Note we are doing action on
     * @param action Actual Action type
     */
    fun onAction(note: Note, action: Action)
}