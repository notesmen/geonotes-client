package org.geonotes.client.activities

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.geonotes.client.R

import org.geonotes.client.adapters.NotesAdapter
import org.geonotes.client.geoapi.LocationUpdater
import org.geonotes.client.geoapi.PermissionDeniedException
import org.geonotes.client.model.entity.Note
import org.geonotes.client.viewmodel.NoteViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var nothingTextView: TextView
    private lateinit var notesView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var locationListener: LocationUpdater
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null
    private val PERMISSION_REQUEST_CODE: Int = 1

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        window.allowEnterTransitionOverlap = true
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationInitialisations()

        nothingTextView = findViewById(R.id.nothingTextView)
        notesView = findViewById(R.id.notesView)
        fab = findViewById(R.id.addNote)
        fab.setOnClickListener {
            val intent = Intent(this, EditNoteActivity::class.java)
            if (currentLocation == null) {
                currentLocation = locationListener.getLastKnownLocation()
            }
            intent.putExtra("EXTRA_LOCATION", currentLocation)
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun locationInitialisations() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                if (locationResult != null) {
                    currentLocation = locationResult.lastLocation
                }
            }
        }

        locationListener = LocationUpdater(this)

        try {
            locationListener.startLocationUpdates(locationCallback)
        } catch (e: PermissionDeniedException) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationListener.startLocationUpdates(locationCallback)
            } else {
                Toast.makeText(
                    this,
                    "Application can not work without locating permissions",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}
