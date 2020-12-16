package org.geonotes.client

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapview.MapImage
import com.here.sdk.mapview.MapImageFactory
import com.here.sdk.mapview.MapView

import org.geonotes.client.geoapi.InteractiveMap

class AddNoteActivity : AppCompatActivity() {

    private lateinit var saveNoteBtn: Button
    private lateinit var map: InteractiveMap
    private lateinit var addressLabel: TextView
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)

        addressLabel = findViewById(R.id.addressTextView)
        val loc: Location = intent.extras?.get("EXTRA_LOCATION") as Location
        val geo: GeoCoordinates = GeoCoordinates(loc.latitude, loc.longitude)

        map = InteractiveMap(this, mapView, geo, addressLabel)
        map.loadMapScene()
        addressLabel.text = map.getAddress().getAddressLine(0)
        val mapImage: MapImage = MapImageFactory.fromResource(this.resources, R.drawable.ic_loc)
        map.setMapImage(mapImage)

        saveNoteBtn = findViewById(R.id.saveNote)
        saveNoteBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
