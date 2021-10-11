package com.noqapp.android.client.views.version_2.market_place.post_property_rental

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.noqapp.android.client.R
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
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPostMarketPlaceBinding =
            ActivityPostMarketPlaceBinding.inflate(LayoutInflater.from(this))
        setContentView(activityPostMarketPlaceBinding.root)

        setUpNavigation()
    }

    private fun setUpNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.post_property_rental_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
//        NavigationUI.setupActionBarWithNavController(this, navController)
    }

}