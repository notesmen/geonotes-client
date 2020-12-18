package org.geonotes.client.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.here.sdk.core.GeoCoordinates

import org.geonotes.client.R
import org.geonotes.client.model.entity.GeoTag
import org.geonotes.client.model.entity.Note
import org.geonotes.client.model.entity.NoteBase
import org.geonotes.client.viewmodel.NoteViewModel


class EditNoteActivity : AppCompatActivity() {
    private var color: Int? = null
    private lateinit var titleInput: TextInputEditText
    private lateinit var valueInput: TextInputEditText
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var saveNoteFab: FloatingActionButton
    private lateinit var addLocation: FloatingActionButton
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var currentCoordinates: GeoCoordinates
    private var currentAddress: String = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        window.allowEnterTransitionOverlap = true
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)

        titleInput = findViewById(R.id.title_input)
        valueInput = findViewById(R.id.value_input)
        textInputLayout = findViewById(R.id.textInputLayout)
        saveNoteFab = findViewById(R.id.saveNote)
        addLocation = findViewById(R.id.addLocation)
        color = getColor(R.color.colorNote0)
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)


        val loc: Location = intent.extras?.get("EXTRA_LOCATION") as Location
        currentCoordinates = GeoCoordinates(loc.latitude, loc.longitude)
        addLocation.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            intent.putExtra("EXTRA_LOCATION", loc)
            startActivityForResult(intent, 1)
        }

        if (!intent.getBooleanExtra("EXTRA_IS_EDIT", false)) {
            saveNoteFab.setOnClickListener {
                if (!validateTitle()) return@setOnClickListener
                save(titleInput.text.toString(), valueInput.text.toString(), color!!)
            }
        } else {
            val note = Gson().fromJson(intent.getStringExtra("EXTRA_TARGET_NOTE"), Note::class.java)
            color = note.noteBase.color
            findViewById<TextView>(R.id.new_note).setText(R.string.edit_note)
            titleInput.setText(note.noteBase.title)
            valueInput.setText(note.noteBase.text)
            saveNoteFab.setOnClickListener {
                val tag: GeoTag =
                    GeoTag(
                        currentAddress,
                        currentCoordinates.latitude,
                        currentCoordinates.longitude
                    )
                note.geoTags = listOf(tag)
                if (!validateTitle()) return@setOnClickListener
                val newNote = Note(
                    NoteBase(
                        note.noteBase.noteId,
                        titleInput.text.toString(),
                        valueInput.text.toString(),
                        color!!,
                        System.nanoTime()
                    ), note.geoTags
                )
                update(newNote)
            }
        }
    }


    private fun save(title: String, text: String, color: Int) {
        val tag: GeoTag =
            GeoTag(currentAddress, currentCoordinates.latitude, currentCoordinates.longitude)
        val note = Note(NoteBase(title, text, color), listOf(tag))
        noteViewModel.addNote(note)
        super.onBackPressed()
    }

    private fun update(note: Note) {
        noteViewModel.updateNote(note)
        super.onBackPressed()
    }

    private fun validateTitle(): Boolean {
        val titleText: String = titleInput.text.toString()
        if (titleText.isEmpty()) {
            textInputLayout.error = "Title can't be empty!"
            return false
        } else if (titleText.length > 20) {
            textInputLayout.error = "Title length must be less than 20 characters"
            return false
        }
        return true
    }

    private fun delete(note: Note) = noteViewModel.deleteNote(note)

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setColor(view: View) {
        resetImages()
        view as FloatingActionButton
        color = view.backgroundTintList!!.defaultColor
        view.setImageDrawable(getDrawable(R.drawable.ic_outline_done))
    }

    private fun resetImages() {
        findViewById<FloatingActionButton>(R.id.color1).setImageDrawable(null)
        findViewById<FloatingActionButton>(R.id.color2).setImageDrawable(null)
        findViewById<FloatingActionButton>(R.id.color3).setImageDrawable(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val coordinatesString: String? = data?.extras?.getString("EXTRA_LOCATION_STRING", null)
        val address: String? = data?.extras?.getString("EXTRA_ADDRESS_STRING", " ")
        val coordinates: List<String> = coordinatesString?.split(' ') ?: listOf("10.0, 10.0")
        if (address != null) {
            currentAddress = address
        }
        currentCoordinates = GeoCoordinates(coordinates[0].toDouble(), coordinates[1].toDouble())
    }

    fun back(@Suppress("UNUSED_PARAMETER") view: View) = super.onBackPressed()
}
