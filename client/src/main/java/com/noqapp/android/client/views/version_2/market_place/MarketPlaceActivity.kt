package com.noqapp.android.client.views.version_2.market_place

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqapp.android.client.databinding.ActivityMarketPlaceBinding
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.views.activities.BaseActivity
import com.noqapp.android.client.views.activities.LocationBaseActivity
import com.noqapp.android.client.views.version_2.viewmodels.HomeViewModel
import com.noqapp.android.common.model.types.BusinessTypeEnum

class MarketPlaceActivity : LocationBaseActivity() {

    private lateinit var activityMarketPlaceBinding: ActivityMarketPlaceBinding
    private lateinit var marketPlaceAdapter: MarketPlaceAdapter

    private val marketPlaceViewModel: MarketPlaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMarketPlaceBinding = ActivityMarketPlaceBinding.inflate(LayoutInflater.from(this))
        setContentView(activityMarketPlaceBinding.root)
        setUpRecyclerView()
        observeData()
    }

    private fun observeData() {
        marketPlaceViewModel.marketPlaceElasticListLiveData.observe(this, {
            activityMarketPlaceBinding.shimmerLayout.stopShimmer()
            activityMarketPlaceBinding.shimmerLayout.visibility = View.GONE
            activityMarketPlaceBinding.rvMarketPlace.visibility = View.VISIBLE
            it?.let { marketPlaceElasticList ->
                marketPlaceAdapter.addMarketPlaces(marketPlaceElasticList.marketplaceElastics)
            }
        })

        marketPlaceViewModel.authenticationError.observe(this, {
            if (it) {
                super.authenticationFailure()
                marketPlaceViewModel.authenticationError.value = false
            }
        })

        marketPlaceViewModel.errorEncounteredJsonLiveData.observe(this, {
            super.responseErrorPresenter(it)
        })

        marketPlaceViewModel.searchStoreQueryLiveData.observe(this, {
            activityMarketPlaceBinding.shimmerLayout.startShimmer()
            it.searchedOnBusinessType = BusinessTypeEnum.PR
            marketPlaceViewModel.getMarketPlace(it)
        })
    }

    private fun setUpRecyclerView() {
        marketPlaceAdapter = MarketPlaceAdapter(mutableListOf())
        activityMarketPlaceBinding.rvMarketPlace.layoutManager = LinearLayoutManager(this)
        activityMarketPlaceBinding.rvMarketPlace.adapter = marketPlaceAdapter
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
        val searchStoreQuery = SearchQuery()
        area?.let {
            searchStoreQuery.cityName = AppUtils.getLocationAsString(area, town)
        }
        latitude?.let {
            searchStoreQuery.latitude = it.toString()
        }
        longitude?.let {
            searchStoreQuery.longitude = it.toString()
        }
        searchStoreQuery.filters = ""
        searchStoreQuery.scrollId = ""

        marketPlaceViewModel.searchStoreQueryLiveData.value = searchStoreQuery
    }

    override fun locationPermissionRequired() {
        activityMarketPlaceBinding.clLocationAccessRequired.visibility = View.VISIBLE
    }

    override fun locationPermissionGranted() {
        activityMarketPlaceBinding.clLocationAccessRequired.visibility = View.GONE
    }
}
