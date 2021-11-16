package com.noqapp.android.client.views.version_2.market_place.property_rental_details

import android.content.ActivityNotFoundException
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
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityViewPropertyRentalDetailsBinding
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.utils.GeoHashUtils
import com.noqapp.android.client.views.activities.BaseActivity
import com.noqapp.android.client.views.activities.LocationBaseActivity
import com.noqapp.android.client.views.version_2.market_place.PostPropertyRentalViewModel
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.noqapp.android.common.model.types.category.RentalTypeEnum
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

class ViewPropertyRentalDetailsActivity : LocationBaseActivity(), OnMapReadyCallback {
    private lateinit var activityViewPropertyRentalDetailsBinding: ActivityViewPropertyRentalDetailsBinding
    private lateinit var propertyRentalViewModel: PostPropertyRentalViewModel
    private lateinit var marketPlaceElastic: MarketplaceElastic
    private var latitude = 0.0
    private var longitude = 0.0
    private var currentLatitude: Double? = 0.0
    private var currentLongitude: Double? = 0.0

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
        currentLatitude = latitude
        currentLongitude = longitude
    }

    override fun locationPermissionRequired() {

    }

    override fun locationPermissionGranted() {

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
        val nf: NumberFormat =
            NumberFormat.getCurrencyInstance(Locale("en", marketPlaceElastic.countryShortName))
        activityViewPropertyRentalDetailsBinding.tvTitle.text = marketPlaceElastic.title
        activityViewPropertyRentalDetailsBinding.tvPrice.text =
            nf.format(BigDecimal(marketPlaceElastic.productPrice)) + "/-"
        activityViewPropertyRentalDetailsBinding.tvDescription.text = marketPlaceElastic.description
        activityViewPropertyRentalDetailsBinding.toolbar.title = marketPlaceElastic.title
        activityViewPropertyRentalDetailsBinding.tvBedrooms.text =
            getString(R.string.txt_number_of_bedrooms) + " " + marketPlaceElastic.getValueFromTag("BE")
        activityViewPropertyRentalDetailsBinding.tvBathrooms.text =
            getString(R.string.txt_number_of_bathrooms) + " " + marketPlaceElastic.getValueFromTag("BR")
        activityViewPropertyRentalDetailsBinding.tvCarpetArea.text =
            getString(R.string.txt_carpet_area) + " " + marketPlaceElastic.getValueFromTag("CA")
        //Rental Type = RentalTypeEnum.valueOf(marketPlaceElastic.getValueFromTag("RT"))
        activityViewPropertyRentalDetailsBinding.tvAvailableFrom.text =
            getString(R.string.txt_available_from) + " " + marketPlaceElastic.getValueFromTag("RA")

        latitude = GeoHashUtils.decodeLatitude(marketPlaceElastic.geoHash)
        longitude = GeoHashUtils.decodeLongitude(marketPlaceElastic.geoHash)

        activityViewPropertyRentalDetailsBinding.tvAddress.text = marketPlaceElastic.townCity()
        activityViewPropertyRentalDetailsBinding.tvRating.text = marketPlaceElastic.rating

        activityViewPropertyRentalDetailsBinding.tvPropertyViews.text = marketPlaceElastic.viewCount.toString() + " " + if (marketPlaceElastic.viewCount > 1)  { getString(R.string.txt_views) } else { getString(R.string.txt_view) }

        setUpViewPager()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
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

        activityViewPropertyRentalDetailsBinding.tvAddress.setOnClickListener {
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