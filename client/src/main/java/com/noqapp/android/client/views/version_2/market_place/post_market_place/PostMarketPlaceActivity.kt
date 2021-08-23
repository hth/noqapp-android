package com.noqapp.android.client.views.version_2.market_place.post_market_place

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.noqapp.android.client.databinding.ActivityPostMarketPlaceBinding
import com.noqapp.android.client.views.activities.LocationBaseActivity
import com.noqapp.android.client.views.version_2.market_place.MarketPlaceViewModel
import com.noqapp.android.common.model.types.category.RentalTypeEnum

class PostMarketPlaceActivity : LocationBaseActivity() {

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
        this.address = addressOutput
        this.town = town
        this.city = area
        activityPostMarketPlaceBinding.etAddress.setText(addressOutput)
        activityPostMarketPlaceBinding.etTown.setText(town)
        activityPostMarketPlaceBinding.etCity.setText(city)
        latitude?.let {
            this.latitude = latitude
        }
        longitude?.let {
            this.longitude = longitude
        }
    }

    override fun locationPermissionRequired() {

    }

    override fun locationPermissionGranted() {

    }

    private lateinit var activityPostMarketPlaceBinding: ActivityPostMarketPlaceBinding
    private val marketPlaceViewModel: MarketPlaceViewModel by viewModels()
    private var title: String? = null
    private var address: String? = null
    private var town: String? = null
    private var city: String? = null
    private var bathroom = 0
    private var bedroom = 0
    private var carpetArea = 0
    private var latitude = 0.0
    private var longitude = 0.0
    private var rentalType = RentalTypeEnum.T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPostMarketPlaceBinding = ActivityPostMarketPlaceBinding.inflate(LayoutInflater.from(this))
        setContentView(activityPostMarketPlaceBinding.root)
        setListeners()
        observeData()
    }

    private fun observeData() {
        marketPlaceViewModel.postMarketPlaceElasticLiveData.observe(this, {
            dismissProgress()
            it?.let {
                finish()
            }
        })

        marketPlaceViewModel.authenticationError.observe(this, {
            if (it) {
                dismissProgress()
                super.authenticationFailure()
                marketPlaceViewModel.authenticationError.value = false
            }
        })

        marketPlaceViewModel.errorEncounteredJsonLiveData.observe(this, {
            dismissProgress()
            super.responseErrorPresenter(it)
        })
    }

    private fun setListeners() {
        activityPostMarketPlaceBinding.btnPost.setOnClickListener {
            showProgress()
            activityPostMarketPlaceBinding.etBathRoom.text?.let {
                bathroom = it.toString().toInt()
            }

            activityPostMarketPlaceBinding.etCarpetArea.text?.let {
                carpetArea = it.toString().toInt()
            }

            activityPostMarketPlaceBinding.etTown.text?.let {
                town = it.toString()
            }

            activityPostMarketPlaceBinding.etCity.text?.let {
                city = it.toString()
            }

            activityPostMarketPlaceBinding.etAddress.text?.let {
                address = it.toString()
            }

            activityPostMarketPlaceBinding.etTitle.text?.let {
                title = it.toString()
            }

            showProgress()
            marketPlaceViewModel.postMarketPlace(title, bathroom, bedroom, carpetArea, town, city, address, rentalType, latitude, longitude)
        }
    }

}