package org.geonotes.client

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.geonotes.client.geoapi.LocationUpdater


class MainActivity : AppCompatActivity() {

    private lateinit var addNotesBtn: FloatingActionButton
    private lateinit var locationListener: LocationUpdater
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationListener = LocationUpdater(this)
        locationListener.startLocating(object : LocationUpdater.PlatformLocationListener {
            override fun onLocationUpdated(location: Location?) {
                if (location != null) {
                    Log.d("LOC-123", location.latitude.toString())
                }
                currentLocation = location
            }
        })

        addNotesBtn = findViewById(R.id.addNotes)
        addNotesBtn.setOnClickListener {
            val intent: Intent = Intent(this, AddNoteActivity::class.java)
            if (currentLocation == null) {
                val loc: Location? = locationListener.getLastKnownLocation()
                intent.putExtra("EXTRA_LOCATION", loc)
            } else {
                intent.putExtra("EXTRA_LOCATION", currentLocation)
            }

            startActivity(intent)
        }
    }
}