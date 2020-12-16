package org.geonotes.client.geoapi

import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class LocationUpdater(private val context: Context) {
    private val DEFAULT_UPDATE_INTERVAL_IN_S: Long = 20
    private val FAST_UPDATE_INTERVAL_IN_S: Long = 5
    private var isActive: Boolean = false
    private lateinit var locationProvider: FusedLocationProviderClient;
    private lateinit var locationRequest: LocationRequest;

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 1000 * DEFAULT_UPDATE_INTERVAL_IN_S
        locationRequest.fastestInterval = 1000 * FAST_UPDATE_INTERVAL_IN_S
        setBalancedPowerAccuracy()
    }

    fun startLocationUpdates(callback: LocationCallback) {
        checkPermission()
        createLocationRequest()
        isActive = true
        locationProvider = LocationServices.getFusedLocationProviderClient(context)
        locationProvider.requestLocationUpdates(locationRequest, callback, null)
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw PermissionDeniedException("Permission Denied")
        }
    }

    fun getLastKnownLocation(): Location? {
        if (isActive && locationProvider.locationAvailability.isSuccessful) {
            checkPermission()
            return locationProvider.lastLocation.result
        }
        val fakeLocation: Location = Location(FusedLocationProviderClient.KEY_MOCK_LOCATION)
        fakeLocation.latitude = 53.0
        fakeLocation.longitude = 27.0
        return fakeLocation
    }

    fun setHighAccuracy() {
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    fun setBalancedPowerAccuracy() {
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }
}