package org.geonotes.client

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapviewlite.MapStyle
import com.here.sdk.mapviewlite.MapViewLite


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