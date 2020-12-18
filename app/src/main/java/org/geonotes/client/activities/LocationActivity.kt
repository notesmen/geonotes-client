package org.geonotes.client.activities

import android.app.ActivityOptions
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapview.MapImage
import com.here.sdk.mapview.MapImageFactory
import com.here.sdk.mapview.MapView
import org.geonotes.client.R
import org.geonotes.client.geoapi.InteractiveMap

class LocationActivity : AppCompatActivity() {

    private lateinit var saveNoteLocationBtn: Button
    private lateinit var map: InteractiveMap
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)

        val loc: Location = intent.extras?.get("EXTRA_LOCATION") as Location
        val geo: GeoCoordinates = GeoCoordinates(loc.latitude, loc.longitude)

        map = InteractiveMap(this, mapView, geo)
        map.loadMapScene()
        val mapImage: MapImage = MapImageFactory.fromResource(this.resources, R.drawable.ic_loc)
        map.setMapImage(mapImage)

        saveNoteLocationBtn = findViewById(R.id.saveLocationButton)
        saveNoteLocationBtn.setOnClickListener {
            val coordinates: GeoCoordinates = map.getCoordinates()
            val intent: Intent = Intent(this, EditNoteActivity::class.java)
            intent.putExtra("EXTRA_LOCATION_STRING", coordinates.latitude.toString() + " " + coordinates.longitude.toString())
            intent.putExtra("EXTRA_ADDRESS_STRING", map.getAddress().getAddressLine(0))
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}