package org.geonotes.client.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import org.geonotes.client.R
import org.geonotes.client.enums.Action
import org.geonotes.client.helpers.NoteActionManager
import org.geonotes.client.screens.EditNoteActivity.Companion.IS_EDIT

class NoteActivity : AppCompatActivity() {
    private val noteActionManager = NoteActionManager.getInstance()
    private val note = noteActionManager.current()

    /**
     * Gets Note contents from intent extras
     * And updates views according to those contents
     * @see AppCompatActivity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        window.allowEnterTransitionOverlap = true
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        findViewById<TextView>(R.id.title_view).text = note.getTitle()
        findViewById<TextView>(R.id.value_view).text = note.getValue()
        findViewById<MaterialCardView>(R.id.note_cardview).setCardBackgroundColor(note.getColor())
    }

    /**
     * Called by edit button.
     * Starts EditNoteActivity with IS_EDIT set to true
     * And passes all contents of the Note object
     * @param view Button's view
     */
    fun edit(@Suppress("UNUSED_PARAMETER") view: View) {
        startActivity(Intent(this, EditNoteActivity::class.java).apply {
            putExtra(IS_EDIT, true)
        })
        finish()
    }

    /**
     * Called by delete button.
     * Deletes note using NOTE_TIME intent extra
     * @param view Button's view
     */
    fun delete(@Suppress("UNUSED_PARAMETER") view: View) {
        noteActionManager.callOnCurrent(Action.DELETE)
        super.onBackPressed()
    }

    override fun onRestart() {
        super.onRestart()
        finish()
    }

    /**
     * Called by back floating action button
     * Finishes NoteActivity lifecycle
     * @param view View where button lays
     */
    fun back(@Suppress("UNUSED_PARAMETER") view: View) = super.onBackPressed()
}