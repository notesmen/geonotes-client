package org.geonotes.client.geoapi

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.widget.TextView
import com.here.sdk.core.Anchor2D
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.gestures.TapListener
import com.here.sdk.mapview.MapImage
import com.here.sdk.mapview.MapMarker
import com.here.sdk.mapview.MapScheme
import com.here.sdk.mapview.MapView
import com.here.sdk.search.SearchEngine


class InteractiveMap(
    private val context: Context,
    private var mapView: MapView,
    private var currentCoordinates: GeoCoordinates
) {
    private lateinit var mapImage: MapImage
    private var currentMarker: MapMarker? = null

    private fun setTapHandler() {
        mapView.gestures.tapListener = TapListener { touchPoint ->
            currentCoordinates = mapView.viewToGeoCoordinates(touchPoint)!!

            if (currentMarker != null) {
                mapView.mapScene.removeMapMarker(currentMarker!!)
            }
            setMarker(currentCoordinates)
            setCameraPosition(currentCoordinates)
        }
    }

    fun getAddress(): Address {
        val geocoder: Geocoder = Geocoder(context)
        val addresses: List<Address> =
            geocoder.getFromLocation(currentCoordinates.latitude, currentCoordinates.longitude, 1)
        return addresses[0]
    }

    fun setCameraPosition(coordinates: GeoCoordinates) {
        mapView.camera.lookAt(coordinates)
    }

    fun setMarker(coordinates: GeoCoordinates) {
        val anchor2D = Anchor2D(0.5, 1.0)
        val mapMarker = MapMarker(coordinates, mapImage, anchor2D)
        mapView.mapScene.addMapMarker(mapMarker)
        currentMarker = mapMarker
    }

    fun setMapImage(image: MapImage) {
        mapImage = image
    }

    fun loadMapScene() {
        mapView.mapScene.loadScene(
            MapScheme.NORMAL_DAY
        ) { errorCode ->
            if (errorCode == null) {
                val distanceInMeters = (1e7).toDouble()
                mapView.camera.lookAt(
                    currentCoordinates, distanceInMeters
                )
                setMarker(currentCoordinates)
            }
        }
        setTapHandler()
    }

    fun getCoordinates(): GeoCoordinates {
        return currentCoordinates
    }
}
