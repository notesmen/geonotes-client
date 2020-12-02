package org.geonotes.client

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapviewlite.MapStyle
import com.here.sdk.mapviewlite.MapViewLite

class AddNoteActivity : AppCompatActivity() {

    private lateinit var saveNoteBtn : Button
    private lateinit var mapView : MapViewLite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        loadMapScene()

        saveNoteBtn = findViewById(R.id.saveNote)
        saveNoteBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun loadMapScene() {
        mapView.mapScene.loadScene(
            MapStyle.NORMAL_DAY
        ) { errorCode ->
            if (errorCode == null) {
                mapView.camera.target = GeoCoordinates(52.530932, 13.384915)
                mapView.camera.zoomLevel = 14.0
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}