package org.geonotes.client.geoapi

import com.here.sdk.core.Anchor2D
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.gestures.TapListener
import com.here.sdk.mapview.MapImage
import com.here.sdk.mapview.MapMarker
import com.here.sdk.mapview.MapScheme
import com.here.sdk.mapview.MapView
import com.here.sdk.search.SearchEngine


class InteractiveMap(map: MapView, coordinates: GeoCoordinates) {
    private var mapView: MapView = map
    private lateinit var mapImage: MapImage
    private var currentCoordinates: GeoCoordinates = coordinates
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

    fun setCameraPosition(coordinates: GeoCoordinates) {
        mapView.camera.lookAt(coordinates)
    }

    fun setMarker(coordinates: GeoCoordinates) {
        val anchor2D = Anchor2D(0.5, 1.0)
        val mapMarker = MapMarker(currentCoordinates, mapImage, anchor2D)
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
                val distanceInMeters = (100 * 1).toDouble()
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