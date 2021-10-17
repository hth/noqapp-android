package com.noqapp.android.client.views.version_2.market_place.post_property_rental

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.kofigyan.stateprogressbar.StateProgressBar
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityPostMarketPlaceBinding
import com.noqapp.android.client.views.activities.LocationBaseActivity

class PostMarketplacePropertyRentalActivity : LocationBaseActivity(),
    PostPropertyRentalTitleFragmentInteractionListener,
    PostPropertyRentalDetailsFragmentInteractionListener,
    PostPropertyRentalImageFragmentInteractionListener {

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

        setListeners()
    }

    private fun setListeners() {
        activityPostMarketPlaceBinding.stateProgressBar.setOnStateItemClickListener { _, _, stateNumber, _ ->
            when (stateNumber) {
                1 -> {
                    activityPostMarketPlaceBinding.stateProgressBar.setCurrentStateNumber(
                        StateProgressBar.StateNumber.ONE
                    )
                    navController.popBackStack(R.id.fragment_post_property_rental_title_desc, true)
                }
                2 -> {
                    goToPostPropertyRentalDetails()
                }
                3 -> {
                    goToPostPropertyRentalImageUploadFragment()
                }
                4 -> {
                    goToPostPropertyRentalReviewFragment()
                }
            }
        }
    }

    private fun setUpNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.post_property_rental_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun goToPostPropertyRentalDetails() {
        activityPostMarketPlaceBinding.stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO)
        navController.navigate(R.id.fragment_post_property_rental_details)
    }

    override fun goToPostPropertyRentalImageUploadFragment() {
        activityPostMarketPlaceBinding.stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE)
        navController.navigate(R.id.fragment_post_property_rental_image)
    }

    override fun goToPostPropertyRentalReviewFragment() {
        activityPostMarketPlaceBinding.stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR)
        navController.navigate(R.id.fragment_post_property_rental_review)
    }

}