package org.geonotes.client

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import org.geonotes.client.adapters.NotesAdapter
import org.geonotes.client.entities.Note
import org.geonotes.client.enums.Action
import org.geonotes.client.helpers.NoteActionManager
import org.geonotes.client.helpers.NoteManager
import org.geonotes.client.observers.NoteObserver
import org.geonotes.client.screens.EditNoteActivity


/**
 * MainActivity. Shows previews for already saved notes
 */
class MainActivity : AppCompatActivity(), NoteObserver {
    private lateinit var nothingTextView: TextView
    private lateinit var notesView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private val noteManager = NoteManager.getInstance(null)
    private val noteActionManager = NoteActionManager.getInstance()

    /**
     * Creates Notes view and defines addNote fab action
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        window.allowEnterTransitionOverlap = true
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nothingTextView = findViewById(R.id.nothingTextView)
        notesView = findViewById(R.id.notesView)
        fab = findViewById(R.id.addNote)
        noteManager.registerObserver(this)
        fab.setOnClickListener {
            startActivity(Intent(this, EditNoteActivity::class.java),
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }

    /**
     * Called when mNotes of NoteManager was changed
     * Creates and updates RecyclerView
     * @see NoteObserver
     */
    override fun onNotesChanged(mNotes: Array<Note>?) {
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (NoteActionManager.getInstance().currentAction()) {
            Action.ADD -> Snackbar.make(findViewById(R.id.main_layout),
                    getString(R.string.snackbar_add), Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getColor(R.color.snackBarBackground))
                    .setTextColor(getColor(R.color.foreground))
                    .setAnchorView(fab)
                    .show()
            Action.UNDO_DELETE -> Snackbar.make(findViewById(R.id.main_layout),
                    getString(R.string.snackbar_restore), Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getColor(R.color.snackBarBackground))
                    .setTextColor(getColor(R.color.foreground))
                    .setAnchorView(fab)
                    .show()
            Action.DELETE -> Snackbar.make(findViewById(R.id.main_layout),
                    getString(R.string.snackbar_delete), Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getColor(R.color.snackBarBackground))
                    .setTextColor(getColor(R.color.foreground))
                    .setAnchorView(fab)
                    .setAction(getString(R.string.snackbar_undo)) {
                        noteActionManager.callOnCurrent(Action.UNDO_DELETE)
                    }.show()
        }
        nothingTextView.visibility = View.GONE
        notesView.visibility = View.GONE
        if (mNotes!!.isEmpty()) {
            nothingTextView.visibility = View.VISIBLE
        } else {
            notesView.visibility = View.VISIBLE
            findViewById<RecyclerView>(R.id.notesView).apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = NotesAdapter(mNotes, this@MainActivity)
            }
        }
    }
}