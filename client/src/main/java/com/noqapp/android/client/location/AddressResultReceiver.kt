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
        val address: (String?, String?, String?, String?, String?, String?, String?, Double, Double) -> Unit
) : ResultReceiver(Handler(Looper.getMainLooper())) {

    /**
     * Receives data sent from FetchAddressIntentService
     */
    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        val addressOutput = resultData.getString(Constants.LocationConstants.RESULT_DATA_KEY)
        val latitude = resultData.getDouble(Constants.LocationConstants.LOCATION_LAT_DATA_EXTRA)
        val longitude = resultData.getDouble(Constants.LocationConstants.LOCATION_LNG_DATA_EXTRA)
        val countryShortName = resultData.getString(Constants.LocationConstants.COUNTRY_SHORT_NAME)
        val area = resultData.getString(Constants.LocationConstants.AREA)
        val town = resultData.getString(Constants.LocationConstants.TOWN)
        val district = resultData.getString(Constants.LocationConstants.DISTRICT)
        val state = resultData.getString(Constants.LocationConstants.STATE)
        val stateShortName = resultData.getString(Constants.LocationConstants.STATE_SHORT_NAME)

        if (Constants.LocationConstants.SUCCESS_RESULT == resultCode) {
            address(addressOutput, countryShortName, area, town, district, state, stateShortName, latitude, longitude)
        } else {
            address(addressOutput, countryShortName, area, town, district, state, stateShortName, 0.0, 0.0)
        }
    }
}
