package com.noqapp.android.client.location

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import androidx.core.app.JobIntentService
import com.noqapp.android.client.R
import com.noqapp.android.client.utils.Constants
import java.io.IOException
import java.util.*

class FetchAddressIntentService : JobIntentService() {

    private val TAG = "FetchAddressService"
    private var receiver: ResultReceiver? = null

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
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1)
        } catch (ioException: IOException) {
            errorMessage = getString(R.string.service_not_available)
            Log.e(TAG, errorMessage, ioException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            errorMessage = getString(R.string.invalid_lat_long_used)
            Log.e(TAG, "$errorMessage. Latitude = $latitude , " +
                    "Longitude = $longitude", illegalArgumentException)
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

            deliverResultToReceiver(Constants.LocationConstants.SUCCESS_RESULT,
                    address.subLocality + ", " + address.locality, latitude, longitude)
        }
    }

    /**
     * Sends a resultCode and message to the receiver.
     */
    private fun deliverResultToReceiver(resultCode: Int, message: String, latitude: Double, longitude: Double) {
        val bundle = Bundle().apply {
            putString(Constants.LocationConstants.RESULT_DATA_KEY, message)
            putDouble(Constants.LocationConstants.LOCATION_LAT_DATA_EXTRA, latitude)
            putDouble(Constants.LocationConstants.LOCATION_LNG_DATA_EXTRA, longitude)
        }
        receiver?.send(resultCode, bundle)
    }

}