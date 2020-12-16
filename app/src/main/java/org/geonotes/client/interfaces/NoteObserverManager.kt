package org.geonotes.client.interfaces

import org.geonotes.client.observers.NoteObserver

/**
 * Observer manager
 */
interface NoteObserverManager {
    /**
     * Registers observer
     * @param observer NoteObserver type to-be-registered
     */
    fun registerObserver(observer: NoteObserver)

    /**
     * Removes observer
     * @param observer NoteObserver type to-be-removed
     */
    fun removeObserver(observer: NoteObserver)

    /**
     * Notifies all observers about update
     */
    fun notifyObserver()
}