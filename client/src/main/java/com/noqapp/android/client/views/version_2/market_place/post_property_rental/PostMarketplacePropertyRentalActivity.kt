package com.noqapp.android.client.views.version_2.market_place.post_property_rental

import android.os.Bundle
import android.view.LayoutInflater
import com.noqapp.android.client.databinding.ActivityPostMarketPlaceBinding
import com.noqapp.android.client.views.activities.LocationBaseActivity

class PostMarketplacePropertyRentalActivity : LocationBaseActivity() {

    override fun displayAddressOutput(
        addressOutput: String?,
        countryShortName: String?,
        area: String?,
        town: String?,
        district: String?,
        state: String?,
        stateShortName: String?,
        latitude: Double?,
        longitude: Double?
    ) {
    }

    override fun locationPermissionRequired() {

    }

    override fun locationPermissionGranted() {

    }

    private lateinit var activityPostMarketPlaceBinding: ActivityPostMarketPlaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPostMarketPlaceBinding =
            ActivityPostMarketPlaceBinding.inflate(LayoutInflater.from(this))
        setContentView(activityPostMarketPlaceBinding.root)
    }

}