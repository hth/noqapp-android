package com.noqapp.android.client.location

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.ResultReceiver
import android.text.TextUtils
import android.util.Log
import androidx.core.app.JobIntentService
import com.noqapp.android.client.R
import com.noqapp.android.client.utils.Constants
import java.io.IOException
import java.util.*

/**
 * Intent service job class to get address from latitude/longitude in background
 * Created by Vivek Jha on 23/02/2021
 */
class FetchAddressIntentService : JobIntentService() {
    private val TAG = FetchAddressIntentService::class.java.simpleName
    private var receiver: ResultReceiver? = null
    private var lastKnownLatitude = 0.0
    private var lastKnownLongitude = 0.0

    companion object {
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, FetchAddressIntentService::class.java, Constants.LocationConstants.FETCH_LOCATION_JOB_ID, work)
        }
    }

    override fun onHandleWork(intent: Intent) {
        var errorMessage = ""

        receiver = intent.getParcelableExtra(Constants.LocationConstants.RECEIVER)
        if (receiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.")
            return
        }

        val latitude = intent.getDoubleExtra(Constants.LocationConstants.LOCATION_LAT_DATA_EXTRA, 0.0)
        val longitude = intent.getDoubleExtra(Constants.LocationConstants.LOCATION_LNG_DATA_EXTRA, 0.0)
        if (latitude == 0.0 || longitude == 0.0) {
            errorMessage = getString(R.string.no_location_data_provided)
            Log.wtf(TAG, errorMessage)
            deliverResultToReceiver(Constants.LocationConstants.FAILURE_RESULT, errorMessage, latitude, longitude)
            return
        }

        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address> = emptyList()

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
        } catch (ioException: IOException) {
            errorMessage = getString(R.string.service_not_available)
            Log.e(TAG, errorMessage, ioException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            errorMessage = getString(R.string.invalid_lat_long_used)
            Log.e(TAG, "$errorMessage, Latitude=$latitude, Longitude=$longitude", illegalArgumentException)
        }

        if (addresses.isEmpty()) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found)
                Log.e(TAG, errorMessage)
            }
            deliverResultToReceiver(Constants.LocationConstants.FAILURE_RESULT, errorMessage, latitude, longitude)
        } else {
            val address = addresses[0]
            Log.i(TAG, getString(R.string.address_found))
            //TODO(hth) may be subAdminArea would not be needed
//            val addressStr = address.getAddressLine(0) + ", " + address.subAdminArea
            val addressStr = address.getAddressLine(0)
            var countryShortName = ""
            var area = ""
            var town = ""
            var district = ""
            var state = ""
            var stateShortName = ""
            if (!TextUtils.isEmpty(address.countryCode)) {
                countryShortName = address.countryCode
            }
            if (!TextUtils.isEmpty(address.subLocality)) {
                area = address.subLocality;
            } else if (!TextUtils.isEmpty(address.locality)) {
                town = address.locality;
            }
            if (!TextUtils.isEmpty(address.subAdminArea)) {
                district = address.subAdminArea
            }
            if (!TextUtils.isEmpty(address.adminArea)) {
                state = address.adminArea
            }

            val results = FloatArray(1)
            Location.distanceBetween(
                lastKnownLatitude, lastKnownLongitude,
                latitude, longitude, results
            )

            if(results[0] <= 1000){
                Log.i(TAG, "new Address found less than 1 km ")
            }else{
                Log.i(TAG, "new Address found and assigned")
                deliverResultToReceiver(Constants.LocationConstants.SUCCESS_RESULT, addressStr, countryShortName, area, town, district, state, stateShortName, latitude, longitude)
            }
            lastKnownLatitude = latitude
            lastKnownLongitude = longitude
        }
    }

    /** Sends a resultCode and message to the receiver. */
    private fun deliverResultToReceiver(resultCode: Int, addressOutput: String, countryShortName: String, area: String, town: String, district: String, state: String, stateShortName: String, latitude: Double, longitude: Double) {
        val bundle = Bundle().apply {
            putString(Constants.LocationConstants.COUNTRY_SHORT_NAME, countryShortName)
            putString(Constants.LocationConstants.AREA, area)
            putString(Constants.LocationConstants.TOWN, town)
            putString(Constants.LocationConstants.DISTRICT, district)
            putString(Constants.LocationConstants.STATE, state)
            putString(Constants.LocationConstants.STATE_SHORT_NAME, stateShortName)
            putString(Constants.LocationConstants.RESULT_DATA_KEY, addressOutput)
            putDouble(Constants.LocationConstants.LOCATION_LAT_DATA_EXTRA, latitude)
            putDouble(Constants.LocationConstants.LOCATION_LNG_DATA_EXTRA, longitude)
        }
        receiver?.send(resultCode, bundle)
    }

    /** Sends a resultCode and message to the receiver. */
    private fun deliverResultToReceiver(resultCode: Int, errorMessage: String, latitude: Double, longitude: Double) {
        val bundle = Bundle().apply {
            putString(Constants.LocationConstants.RESULT_DATA_KEY, errorMessage)
            putDouble(Constants.LocationConstants.LOCATION_LAT_DATA_EXTRA, latitude)
            putDouble(Constants.LocationConstants.LOCATION_LNG_DATA_EXTRA, longitude)
        }
        receiver?.send(resultCode, bundle)
    }
}
