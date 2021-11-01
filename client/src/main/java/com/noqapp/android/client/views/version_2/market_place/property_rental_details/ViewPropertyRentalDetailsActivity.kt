package com.noqapp.android.client.views.version_2.market_place.property_rental_details

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.noqapp.android.client.databinding.ActivityViewPropertyRentalDetailsBinding
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.views.activities.BaseActivity
import com.noqapp.android.client.views.version_2.market_place.PostPropertyRentalViewModel
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic

class ViewPropertyRentalDetailsActivity : BaseActivity() {
    private lateinit var activityViewPropertyRentalDetailsBinding: ActivityViewPropertyRentalDetailsBinding
    private lateinit var propertyRentalViewModel: PostPropertyRentalViewModel
    private lateinit var marketPlaceElastic: MarketplaceElastic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityViewPropertyRentalDetailsBinding =
            ActivityViewPropertyRentalDetailsBinding.inflate(LayoutInflater.from(this))
        setContentView(activityViewPropertyRentalDetailsBinding.root)

        propertyRentalViewModel = ViewModelProvider(this)[PostPropertyRentalViewModel::class.java]

        intent?.let {
            marketPlaceElastic =
                it.getSerializableExtra(Constants.POST_PROPERTY_RENTAL) as MarketplaceElastic
            setData(marketPlaceElastic)
        }

        setListeners()

    }

    private fun setListeners() {
        activityViewPropertyRentalDetailsBinding.toolbar.setNavigationOnClickListener {
            finish()
        }

        activityViewPropertyRentalDetailsBinding.cvInterested.setOnClickListener {
            propertyRentalViewModel.initiateContact(marketPlaceElastic.id)
        }
    }

    private fun setData(marketPlaceElastic: MarketplaceElastic) {
        activityViewPropertyRentalDetailsBinding.tvPrice.text = marketPlaceElastic.productPrice
        activityViewPropertyRentalDetailsBinding.tvDescription.text = marketPlaceElastic.description
        activityViewPropertyRentalDetailsBinding.toolbar.title = marketPlaceElastic.title
        // activityViewPropertyRentalDetailsBinding.tvCarpetArea.text = String.format("%s: ", marketPlaceElastic.are)
        // activityViewPropertyRentalDetailsBinding.tvBathrooms = marketPlaceElastic.b
        // activityViewPropertyRentalDetailsBinding.tvBathrooms.text
        activityViewPropertyRentalDetailsBinding.tvAddress.text = marketPlaceElastic.city
        activityViewPropertyRentalDetailsBinding.tvRating.text = marketPlaceElastic.computeRating().toString()
        activityViewPropertyRentalDetailsBinding.tvPropertyViews.text =
            marketPlaceElastic.viewCount.toString() + " Views"

        setUpViewPager()
    }

    private fun setUpViewPager() {
        val imagesAdapter = ImagesAdapter(marketPlaceElastic)
        activityViewPropertyRentalDetailsBinding.viewPager.adapter = imagesAdapter
        TabLayoutMediator(
            activityViewPropertyRentalDetailsBinding.tabLayout,
            activityViewPropertyRentalDetailsBinding.viewPager
        ) { tab, position ->
            //Some implementation
        }.attach()
    }

}