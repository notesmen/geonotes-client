package com.example.geonotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

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