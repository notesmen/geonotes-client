package org.geonotes.client

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList

import com.google.android.material.floatingactionbutton.FloatingActionButton

import org.geonotes.client.adapters.NotesAdapter
import org.geonotes.client.model.entity.Note
import org.geonotes.client.screens.EditNoteActivity
import org.geonotes.client.viewmodel.NoteViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var nothingTextView: TextView
    private lateinit var notesView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        window.allowEnterTransitionOverlap = true
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nothingTextView = findViewById(R.id.nothingTextView)
        notesView = findViewById(R.id.notesView)
        fab = findViewById(R.id.addNote)
        fab.setOnClickListener {
            val intent = Intent(this, EditNoteActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        noteViewModel.getNotes().observe(this, this::onNotesChanged)
    }

    private fun onNotesChanged(mNotes: PagedList<Note>) {
        nothingTextView.visibility = View.GONE
        notesView.visibility = View.GONE
        if (mNotes.isEmpty()) {
            nothingTextView.visibility = View.VISIBLE
        } else {
            notesView.visibility = View.VISIBLE
            findViewById<RecyclerView>(R.id.notesView).apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = NotesAdapter(mNotes.toTypedArray(), this@MainActivity)
            }
        }
    }
}
