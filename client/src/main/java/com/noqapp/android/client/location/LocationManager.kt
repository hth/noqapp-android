package com.noqapp.android.client.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.ResultReceiver
import android.util.Log
import com.google.android.gms.location.*
import com.noqapp.android.client.utils.Constants

internal object LocationManager {
    private var lastLocation: Location? = null

    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            lastLocation = locationResult.lastLocation
        }
    }

    private val locationRequest = LocationRequest.create().apply {
        fastestInterval = 60000
        smallestDisplacement = 10f
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    @Synchronized
    @SuppressLint("MissingPermission")
    fun startLocationUpdate(context: Context) {
        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest, locationCallback, null).addOnFailureListener { e: Exception? -> Log.e(LocationManager::class.java.simpleName, "getLastLocation:onFailure") }
    }

    @Synchronized
    fun stopLocationUpdate(context: Context) {
        LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(locationCallback)
    }

    fun fetchLocationAddress(latitude: Double, longitude: Double, context: Context, complete: (String, Double, Double) -> Unit) {
        val addressResultReceiver = AddressResultReceiver { address, latitude, longitude ->
            address?.let {
                complete(address, latitude, longitude)
            }
        }
        startFetchLocationService(context, latitude, longitude, addressResultReceiver)
    }

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocationAddress(context: Context, complete: (String, Double, Double) -> Unit) {
        val addressResultReceiver = AddressResultReceiver { address, latitude, longitude ->
            address?.let {
                complete(address, latitude, longitude)
            }
        }

        LocationServices.getFusedLocationProviderClient(context).lastLocation.addOnSuccessListener {
            it?.let { location ->
                lastLocation = location
                startFetchLocationService(context, location.latitude, location.longitude, addressResultReceiver)
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Synchronized
    fun getLastKnownLocation(context: Context, complete: (Location) -> Unit){
        LocationServices.getFusedLocationProviderClient(context).lastLocation.addOnSuccessListener {
            it?.let { location ->
                lastLocation = location
                complete(location)
            }
        }
    }

    private fun startFetchLocationService(context: Context, latitude: Double, longitude: Double, resultReceiver: ResultReceiver){
        val intent = Intent()
        intent.putExtra(Constants.LocationConstants.RECEIVER, resultReceiver)
        intent.putExtra(Constants.LocationConstants.LOCATION_LAT_DATA_EXTRA, latitude)
        intent.putExtra(Constants.LocationConstants.LOCATION_LNG_DATA_EXTRA, longitude)
        FetchAddressIntentService.enqueueWork(context, intent)
    }
}
