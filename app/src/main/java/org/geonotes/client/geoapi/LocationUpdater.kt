package org.geonotes.client.geoapi

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat

class LocationUpdater(private val context: Context) : LocationListener {
    private val LOCATION_UPDATE_INTERVAL_IN_MS: Long = 100
    private lateinit var locationManager: LocationManager
    private var platformLocationListener: PlatformLocationListener? = null

    interface PlatformLocationListener {
        fun onLocationUpdated(location: Location?)
    }

    override fun onLocationChanged(location: Location) {
        platformLocationListener?.onLocationUpdated(location)
    }

    fun startLocating(locationCallback: PlatformLocationListener) {
        if (this.platformLocationListener != null) {
            throw RuntimeException("Please stop locating before starting again.");
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }

        platformLocationListener = locationCallback
        locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_UPDATE_INTERVAL_IN_MS,
                0.1f,
                this
            );
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                LOCATION_UPDATE_INTERVAL_IN_MS,
                0.1f,
                this
            );
        } else {
            stopLocating();
        }
    }

    private fun stopLocating() {
        locationManager.removeUpdates(this);
        platformLocationListener = null;
    }

    fun getLastKnownLocation(): Location? {
        if (this.platformLocationListener == null) {
            throw RuntimeException("Please start locating");
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        return null
    }
}