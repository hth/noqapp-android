package com.noqapp.android.client.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.ResultReceiver
import android.util.Log
import com.google.android.gms.common.util.CrashUtils
import com.google.android.gms.location.*
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import com.noqapp.android.client.utils.Constants

/**
 * Location manager class to perform location related queries
 * Created by Vivek Jha on 23/02/2021
 */
internal object LocationManager {
    private var lastLocation: Location? = null

    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            lastLocation = locationResult.lastLocation
        }
    }

    private val locationRequest = LocationRequest.create().apply {
        fastestInterval = 5000
        interval = 60000
        smallestDisplacement = 500f
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    @Synchronized
    @SuppressLint("MissingPermission")
    fun startLocationUpdate(context: Context) {
        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest, locationCallback, null).addOnFailureListener { e: Exception? ->
            Log.e(LocationManager::class.java.simpleName, "getLastLocation:onFailure")
        }
    }

    @Synchronized
    fun stopLocationUpdate(context: Context) {
        LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(locationCallback)
    }

    fun fetchLocationAddress(latitude: Double, longitude: Double, context: Context, complete: (String?, String?, String?, String?, String?, String?, String?, Double, Double) -> Unit) {
        val addressResultReceiver = AddressResultReceiver { address, countryShortName, area, town, district, state, stateShortName, latitude, longitude ->
            address?.let {
                complete(address, countryShortName, area, town, district, state, stateShortName, latitude, longitude)
            }
        }
        startFetchLocationService(context, latitude, longitude, addressResultReceiver)
    }

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocationAddress(context: Context, complete: (String?, String?, String?, String?, String?, String?, String?, Double, Double) -> Unit) {
        val addressResultReceiver = AddressResultReceiver { address, countryShortName, area, town, district, state, stateShortName, latitude, longitude ->
            address?.let {
                complete(address, countryShortName, area, town, district, state, stateShortName, latitude, longitude)
            }
        }

        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                lastLocation = locationResult.lastLocation
                lastLocation?.let {
                    startFetchLocationService(context, it.latitude, it.longitude, addressResultReceiver)
                    stopLocationUpdate(context)
                }
            }
        }, null).addOnFailureListener { e: Exception? ->
            Log.e(LocationManager::class.java.simpleName, "getLastLocation:onFailure")
        }
    }

    @SuppressLint("MissingPermission")
    @Synchronized
    fun getLastKnownLocation(context: Context, complete: (Location) -> Unit) {
        LocationServices.getFusedLocationProviderClient(context).lastLocation.addOnSuccessListener {
            it?.let { location ->
                lastLocation = location
                complete(location)
            }
        }
    }

    fun getLocationAddress(context: Context, latitude: Double, longitude: Double, complete: (String?, String?, String?, String?, String?, String?, String?, Double, Double) -> Unit) {
        val addressResultReceiver = AddressResultReceiver { address, countryShortName, area, town, district, state, stateShortName, lat, lng ->
            address?.let {
                complete(address, countryShortName, area, town, district, state, stateShortName, lat, lng)
            }
        }

        startFetchLocationService(context, latitude, longitude, addressResultReceiver)
    }

    private fun startFetchLocationService(context: Context, latitude: Double, longitude: Double, resultReceiver: ResultReceiver) {
        val intent = Intent()
        intent.putExtra(Constants.LocationConstants.RECEIVER, resultReceiver)
        intent.putExtra(Constants.LocationConstants.LOCATION_LAT_DATA_EXTRA, latitude)
        intent.putExtra(Constants.LocationConstants.LOCATION_LNG_DATA_EXTRA, longitude)
        FetchAddressIntentService.enqueueWork(context, intent)
    }
}
