package com.noqapp.android.client.views.version_2.market_place.householdItem.household_item_details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.tabs.TabLayoutMediator
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityViewHouseholdItemDetailsBinding
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.utils.GeoHashUtils
import com.noqapp.android.client.views.activities.BaseActivity
import com.noqapp.android.client.views.version_2.market_place.householdItem.HouseholdItemViewModel
import com.noqapp.android.client.views.version_2.market_place.propertyRental.property_rental_details.ImagesAdapter
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.noqapp.android.common.model.types.category.ItemConditionEnum
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*
import com.noqapp.android.client.utils.AppUtils

class ViewHouseHoldItemDetailsActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var activityViewHouseholdItemDetailsBinding: ActivityViewHouseholdItemDetailsBinding
    private lateinit var householdItemViewModel: HouseholdItemViewModel
    private lateinit var marketPlaceElastic: MarketplaceElastic
    private var latitude = 0.0
    private var longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityViewHouseholdItemDetailsBinding =
            ActivityViewHouseholdItemDetailsBinding.inflate(LayoutInflater.from(this))
        setContentView(activityViewHouseholdItemDetailsBinding.root)

        householdItemViewModel = ViewModelProvider(this)[HouseholdItemViewModel::class.java]

        intent?.let {
            marketPlaceElastic =
                it.getSerializableExtra(Constants.POST_PROPERTY_RENTAL) as MarketplaceElastic
            setData(marketPlaceElastic)
        }

        setListeners()

    }

    private fun setListeners() {
        activityViewHouseholdItemDetailsBinding.toolbar.setNavigationOnClickListener {
            finish()
        }

        activityViewHouseholdItemDetailsBinding.cvInterested.setOnClickListener {
            householdItemViewModel.initiateContact(marketPlaceElastic.id)
        }
    }

    private fun setData(marketPlaceElastic: MarketplaceElastic) {
        val nf: NumberFormat =
            NumberFormat.getCurrencyInstance(Locale("en", marketPlaceElastic.countryShortName))
        activityViewHouseholdItemDetailsBinding.tvTitle.text = marketPlaceElastic.title
        activityViewHouseholdItemDetailsBinding.tvPrice.text =
            nf.format(BigDecimal(marketPlaceElastic.productPrice)) + "/-"
        activityViewHouseholdItemDetailsBinding.tvDescription.text = marketPlaceElastic.description
        activityViewHouseholdItemDetailsBinding.toolbar.title = marketPlaceElastic.title
        activityViewHouseholdItemDetailsBinding.tvItemCondition.text = AppUtils.halfTextBold(
            getString(R.string.txt_house_hold_item_usages) + " - " , ItemConditionEnum.valueOf(marketPlaceElastic.getValueFromTag("IC")).description)
        activityViewHouseholdItemDetailsBinding.tvItemPrice.text = AppUtils.halfTextBold(
            getString(R.string.txt_house_hold_item_price) + " - " , nf.format(BigDecimal(marketPlaceElastic.productPrice)))

        latitude = GeoHashUtils.decodeLatitude(marketPlaceElastic.geoHash)
        longitude = GeoHashUtils.decodeLongitude(marketPlaceElastic.geoHash)

        activityViewHouseholdItemDetailsBinding.tvAddress.text = marketPlaceElastic.townCity()
        activityViewHouseholdItemDetailsBinding.tvRating.text = marketPlaceElastic.rating
        activityViewHouseholdItemDetailsBinding.rbMarketPlaceRating.setStar(marketPlaceElastic.rating.toFloat())

        activityViewHouseholdItemDetailsBinding.tvPropertyViews.text = marketPlaceElastic.viewCount.toString() + " " + if (marketPlaceElastic.viewCount > 1)  { getString(R.string.txt_views) } else { getString(R.string.txt_view) }

        setUpViewPager()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun setUpViewPager() {
        val imagesAdapter = ImagesAdapter(marketPlaceElastic)
        activityViewHouseholdItemDetailsBinding.viewPager.adapter = imagesAdapter
        TabLayoutMediator(
            activityViewHouseholdItemDetailsBinding.tabLayout,
            activityViewHouseholdItemDetailsBinding.viewPager
        ) { tab, position ->
            //Some implementation
        }.attach()
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    latitude,
                    longitude
                ), 16.0f
            )
        )

        map?.setOnMapClickListener {
            launchDirections()
        }

        activityViewHouseholdItemDetailsBinding.tvAddress.setOnClickListener {
            launchDirections()
        }
    }

    private fun launchDirections() {
        val stringCoords = "$latitude,$longitude"
        val gmmIntentUri: Uri = Uri.parse(
            getString(
                R.string.google_maps_navigate_to,
                getString(R.string.google_maps_navigation_mode_driving),
                stringCoords
            )
        )
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage(getString(R.string.google_maps_package))
        startActivity(mapIntent)
    }

}