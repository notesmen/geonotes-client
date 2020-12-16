package org.geonotes.client

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.geonotes.client.geoapi.LocationUpdater
import org.geonotes.client.geoapi.PermissionDeniedException


class MainActivity : AppCompatActivity() {

    private lateinit var addNotesBtn: FloatingActionButton
    private lateinit var locationListener: LocationUpdater
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null
    private val PERMISSION_REQUEST_CODE: Int = 1

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        }

        addNotesBtn = findViewById(R.id.addNotes)
        addNotesBtn.setOnClickListener {
            val intent: Intent = Intent(this, AddNoteActivity::class.java)
            if (currentLocation == null) {
                currentLocation = locationListener.getLastKnownLocation()
            }
            intent.putExtra("EXTRA_LOCATION", currentLocation)
            startActivity(intent)
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