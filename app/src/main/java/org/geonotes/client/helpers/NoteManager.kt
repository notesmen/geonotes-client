package org.geonotes.client.helpers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import android.provider.BaseColumns
import org.geonotes.client.entities.Note
import org.geonotes.client.enums.Action
import org.geonotes.client.interfaces.NoteObserverManager
import org.geonotes.client.observers.NoteActionObserver
import org.geonotes.client.observers.NoteObserver

/**
 * Manages Notes and NoteObservers
 * @constructor Creates empty NoteManager object, updates notes and notifies observers
 */
class NoteManager(context: Context) : NoteObserverManager, NoteActionObserver {
    private val dbHelper = DatabaseHelper(context)
    private val mObservers = ArrayList<NoteObserver>()
    private var mNotes: Array<Note>? = null

    companion object {
        // Static instance of NoteManager
        private var INSTANCE: NoteManager? = null

        /**
         * Creates one and only instance of NoteManager
         * @param context Application context. Can be null if not called from org.geonotes.client.App
         */
        fun getInstance(context: Context?): NoteManager {
            if (INSTANCE == null) {
                if (context != null)
                    INSTANCE = NoteManager(context)
                else
                    throw NullPointerException("Should pass actual context!")
            }
            return INSTANCE!!
        }
    }

    init {
        NoteActionManager.getInstance().registerObserver(this)
        getNotes()
    }

    /**
     * Adds note into database, updates notes and notifies observers
     * @param note Note that should be registered
     * @return If action is successful, return true otherwise false
     */
    private fun putNote(note: Note): Boolean {
        return try {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(Note.Companion.Entry.COLUMN_NAME_TITLE, note.getTitle())
                put(Note.Companion.Entry.COLUMN_NAME_VALUE, note.getValue())
                put(Note.Companion.Entry.COLUMN_NAME_COLOR, note.getColor())
                put(Note.Companion.Entry.COLUMN_NAME_TIME, note.getTime())
            }
            db?.insert(Note.Companion.Entry.TABLE_NAME, null, values)
            db.close()
            getNotes()
            true
        } catch (e: SQLiteException) {
            false
        }
    }

    /**
     * Retrieves all notes from database
     * Creates array of Note types based on title, value, color and creation time
     * If current notes array doesn't match new one, update current and notify observers
     */
    fun getNotes() {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
                Note.Companion.Entry.TABLE_NAME,
                arrayOf(
                        BaseColumns._ID,
                        Note.Companion.Entry.COLUMN_NAME_TITLE,
                        Note.Companion.Entry.COLUMN_NAME_VALUE,
                        Note.Companion.Entry.COLUMN_NAME_COLOR,
                        Note.Companion.Entry.COLUMN_NAME_TIME
                ),
                null,
                null,
                null,
                null,
                "${Note.Companion.Entry.COLUMN_NAME_TIME} DESC"
        )
        val notes = mutableListOf<Note>()
        with(cursor) {
            while (moveToNext()) {
                notes.add(
                        Note(
                                getString(getColumnIndexOrThrow(Note.Companion.Entry.COLUMN_NAME_TITLE)),
                                getString(getColumnIndexOrThrow(Note.Companion.Entry.COLUMN_NAME_VALUE)),
                                getString(getColumnIndexOrThrow(Note.Companion.Entry.COLUMN_NAME_COLOR)).toInt(),
                                getString(getColumnIndexOrThrow(Note.Companion.Entry.COLUMN_NAME_TIME)).toLong()
                        )
                )
            }
        }
        cursor.close()
        db.close()
        val newNotes = notes.toTypedArray()
        if (newNotes != mNotes) {
            mNotes = newNotes
            notifyObserver()
        }
    }

    /**
     * Deletes note by given time
     * @param note Note that should be deleted
     * @return If action is successful, return true otherwise false
     */
    private fun deleteNote(note: Note): Boolean {
        return try {
            val db = dbHelper.writableDatabase
            db.delete(Note.Companion.Entry.TABLE_NAME, "${Note.Companion.Entry.COLUMN_NAME_TIME} LIKE ?", arrayOf(note.getTime().toString()))
            db.close()
            getNotes()
            true
        } catch (e: SQLiteException) {
            false
        }
    }

    override fun onAction(note: Note, action: Action) {
        when (action) {
            Action.ADD, Action.UNDO_DELETE -> {
                putNote(note)
            }
            Action.DELETE -> {
                deleteNote(note)
            }
            else -> return
        }
    }

    /**
     * @see NoteObserverManager.registerObserver
     */
    override fun registerObserver(observer: NoteObserver) {
        if (!mObservers.contains(observer)) {
            mObservers.add(observer)
            notifyObserver()
        }
    }

    /**
     * @see NoteObserverManager.removeObserver
     */
    override fun removeObserver(observer: NoteObserver) {
        if (mObservers.contains(observer)) {
            mObservers.remove(observer)
        }
    }

    /**
     * @see NoteObserverManager.notifyObserver
     */
    override fun notifyObserver() {
        for (observer in mObservers) {
            observer.onNotesChanged(mNotes)
        }
    }
}