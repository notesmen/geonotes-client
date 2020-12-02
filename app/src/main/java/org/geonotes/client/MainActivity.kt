package org.geonotes.client

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.floatingactionbutton.FloatingActionButton

import org.geonotes.client.viewmodel.NoteViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val noteViewModel: NoteViewModel by viewModels()
    private lateinit var addNotesBtn: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addNotesBtn = findViewById(R.id.addNotes)
        addNotesBtn.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
            finish()
        }
    }
}