package com.noqapp.android.client.location

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import com.noqapp.android.client.utils.Constants

/**
 * Address Receiver class for data sent from FetchAddressIntentService.
 * Created by Vivek Jha on 23/02/2021
 */
class AddressResultReceiver constructor(
        val address: (String?, String?, Double, Double) -> Unit
) : ResultReceiver(Handler(Looper.getMainLooper())) {

    /**
     * Receives data sent from FetchAddressIntentService
     */
    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        val addressOutput = resultData.getString(Constants.LocationConstants.RESULT_DATA_KEY)
        val latitude = resultData.getDouble(Constants.LocationConstants.LOCATION_LAT_DATA_EXTRA)
        val longitude = resultData.getDouble(Constants.LocationConstants.LOCATION_LNG_DATA_EXTRA)
        val cityName = resultData.getString(Constants.LocationConstants.PLACE)

        if (resultCode == Constants.LocationConstants.SUCCESS_RESULT) {
            address(addressOutput, cityName, latitude, longitude)
        } else {
            address(addressOutput, cityName, 0.0, 0.0)
        }
    }
}
