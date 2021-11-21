package com.noqapp.android.client.views.version_2.market_place.householdItem.post_household_item

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.kofigyan.stateprogressbar.StateProgressBar
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityPostHouseHoldItemBinding
import com.noqapp.android.client.views.activities.BaseActivity

class PostHouseholdItemActivity : BaseActivity(),
    PostHouseHoldTitleFragmentInteractionListener,
    PostHouseHoldItemDetailsFragmentInteractionListener,
    PostHouseHoldItemImageFragmentInteractionListener {

    private lateinit var activityPostHouseHoldItemBinding: ActivityPostHouseHoldItemBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPostHouseHoldItemBinding =
            ActivityPostHouseHoldItemBinding.inflate(LayoutInflater.from(this))
        setContentView(activityPostHouseHoldItemBinding.root)

        setSupportActionBar(activityPostHouseHoldItemBinding.toolbar)

        activityPostHouseHoldItemBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        setUpNavigation()

        setListeners()
    }

    private fun setListeners() {
        activityPostHouseHoldItemBinding.stateProgressBar.setOnStateItemClickListener { _, _, stateNumber, _ ->
            when (stateNumber) {
                1 -> {
                    activityPostHouseHoldItemBinding.stateProgressBar.setCurrentStateNumber(
                        StateProgressBar.StateNumber.ONE
                    )
                    navController.popBackStack(R.id.fragment_post_house_hold_item_title_desc, true)
                }
                2 -> {
                    goToPostHouseHoldItemDetails()
                }
                3 -> {
                    goToHouseHoldItemImageUploadFragment()
                }
                4 -> {
                    goToPostHouseHoldItemReviewFragment()
                }
            }
        }
    }

    private fun setUpNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.post_house_hold_item_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun goToPostHouseHoldItemDetails() {
        activityPostHouseHoldItemBinding.stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO)
        navController.navigate(R.id.fragment_post_house_hold_item_details)
    }

    override fun goToHouseHoldItemImageUploadFragment() {
        activityPostHouseHoldItemBinding.stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE)
        navController.navigate(R.id.fragment_post_house_hold_item_image)
    }

    override fun goToPostHouseHoldItemReviewFragment() {
        activityPostHouseHoldItemBinding.stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR)
        navController.navigate(R.id.fragment_post_house_hold_item_review)
    }

}