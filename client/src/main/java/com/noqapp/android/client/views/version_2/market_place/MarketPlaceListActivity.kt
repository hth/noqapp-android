package com.noqapp.android.client.views.version_2.market_place

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityMarketPlaceListingBinding
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.views.activities.LocationBaseActivity
import com.noqapp.android.client.views.version_2.market_place.market_place_details.MarketPlaceDetailsActivity
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.noqapp.android.common.model.types.BusinessTypeEnum

class MarketPlaceListActivity : LocationBaseActivity() {

    private lateinit var activityMarketPlaceListingBinding: ActivityMarketPlaceListingBinding
    private lateinit var marketPlaceAdapter: MarketPlaceAdapter

    private val marketPlaceViewModel: MarketPlaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMarketPlaceListingBinding =
            ActivityMarketPlaceListingBinding.inflate(LayoutInflater.from(this))
        setContentView(activityMarketPlaceListingBinding.root)
        setUpRecyclerView()
        observeData()
    }

    private fun observeData() {
        marketPlaceViewModel.marketPlaceElasticListLiveData.observe(this, {
            activityMarketPlaceListingBinding.shimmerLayout.stopShimmer()
            activityMarketPlaceListingBinding.shimmerLayout.visibility = View.GONE
            activityMarketPlaceListingBinding.rvMarketPlace.visibility = View.VISIBLE
            it?.let { marketPlaceElasticList ->
                marketPlaceAdapter.addMarketPlaces(marketPlaceElasticList.marketplaceElastics)
            }
        })

        marketPlaceViewModel.authenticationError.observe(this, {
            if (it) {
                super.authenticationFailure()
                activityMarketPlaceListingBinding.shimmerLayout.stopShimmer()
                activityMarketPlaceListingBinding.shimmerLayout.visibility = View.GONE
                marketPlaceViewModel.authenticationError.value = false
            }
        })

        marketPlaceViewModel.errorEncounteredJsonLiveData.observe(this, {
            activityMarketPlaceListingBinding.shimmerLayout.stopShimmer()
            activityMarketPlaceListingBinding.shimmerLayout.visibility = View.GONE
            super.responseErrorPresenter(it)
        })

        marketPlaceViewModel.searchStoreQueryLiveData.observe(this, {
            activityMarketPlaceListingBinding.shimmerLayout.startShimmer()
            it.searchedOnBusinessType = BusinessTypeEnum.PR
            marketPlaceViewModel.getMarketPlace(it)
        })
    }

    private fun setUpRecyclerView() {
        marketPlaceAdapter = MarketPlaceAdapter(mutableListOf()) { marketPlace, view ->
            onMarketPlaceItemClicked(marketPlace, view)
        }
        activityMarketPlaceListingBinding.rvMarketPlace.layoutManager = LinearLayoutManager(this)
        activityMarketPlaceListingBinding.rvMarketPlace.adapter = marketPlaceAdapter
    }

    private fun onMarketPlaceItemClicked(marketPlace: MarketplaceElastic?, view: View) {
        marketPlace?.let {
            when (view.id) {
                R.id.btn_view_details -> {
                    marketPlaceViewModel.viewDetails(it.id)
                    startActivity(Intent(this, MarketPlaceDetailsActivity::class.java))
                }
                R.id.btn_call_agent -> {
                    marketPlaceViewModel.initiateContact(it.id)
                    startActivity(Intent(this, MarketPlaceDetailsActivity::class.java))
                }
            }
        }
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
        activityMarketPlaceListingBinding.clLocationAccessRequired.visibility = View.VISIBLE
    }

    override fun locationPermissionGranted() {
        activityMarketPlaceListingBinding.clLocationAccessRequired.visibility = View.GONE
    }
}
