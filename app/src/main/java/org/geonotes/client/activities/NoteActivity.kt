package org.geonotes.client.activities

import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationServices

import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson

import org.geonotes.client.R
import org.geonotes.client.model.entity.Note
import org.geonotes.client.viewmodel.NoteViewModel


class NoteActivity : AppCompatActivity() {
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var note: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        window.allowEnterTransitionOverlap = true
        super.onCreate(savedInstanceState)

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        note = Gson().fromJson(intent.getStringExtra("EXTRA_TARGET_NOTE"), Note::class.java)

        setContentView(R.layout.activity_note)
        findViewById<TextView>(R.id.title_view).text = note.noteBase.title
        findViewById<TextView>(R.id.value_view).text = note.noteBase.text
        findViewById<MaterialCardView>(R.id.note_cardview).setCardBackgroundColor(note.noteBase.color)
    }

    private fun getNoteLocation(): Location {
        val location: Location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = 57.0
        location.longitude = 26.0
        return location
    }

    fun edit(@Suppress("UNUSED_PARAMETER") view: View) {
        startActivity(Intent(this, EditNoteActivity::class.java).apply {
            putExtra("EXTRA_TARGET_NOTE", intent.getStringExtra("EXTRA_TARGET_NOTE"))
            putExtra("EXTRA_IS_EDIT", true)
            putExtra("EXTRA_LOCATION", getNoteLocation())
        })
        finish()
    }

    fun delete(@Suppress("UNUSED_PARAMETER") view: View) {
        noteViewModel.deleteNote(note);
        super.onBackPressed()
    }

    override fun onRestart() {
        super.onRestart()
        finish()
    }

    fun back(@Suppress("UNUSED_PARAMETER") view: View) = super.onBackPressed()
}
