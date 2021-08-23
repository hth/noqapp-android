package com.noqapp.android.client.views.version_2.market_place.search_market_place

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.noqapp.android.client.databinding.ActivityMarketPlaceBinding
import com.noqapp.android.client.views.activities.LocationBaseActivity
import com.noqapp.android.client.views.version_2.market_place.MarketPlaceListActivity
import com.noqapp.android.client.views.version_2.market_place.post_market_place.PostMarketPlaceActivity

class MarketPlaceActivity : LocationBaseActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMarketPlaceBinding = ActivityMarketPlaceBinding.inflate(LayoutInflater.from(this))
        setContentView(activityMarketPlaceBinding.root)

        activityMarketPlaceBinding.btnSearch.setOnClickListener {
            startActivity(Intent(this, MarketPlaceListActivity::class.java))
        }

        activityMarketPlaceBinding.cvPostAProperty.setOnClickListener {
            startActivity(Intent(this, PostMarketPlaceActivity::class.java))
        }

        activityMarketPlaceBinding.ivClose.setOnClickListener {
            finish()
        }
    }
}