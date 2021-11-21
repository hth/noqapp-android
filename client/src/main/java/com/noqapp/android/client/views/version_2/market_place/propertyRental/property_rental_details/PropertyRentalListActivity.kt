package com.noqapp.android.client.views.version_2.market_place.propertyRental.property_rental_details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityPropertyRentalListBinding
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.utils.PaginationListener
import com.noqapp.android.client.utils.PaginationListener.PAGE_START
import com.noqapp.android.client.views.activities.LocationBaseActivity
import com.noqapp.android.client.views.version_2.market_place.propertyRental.PropertyRentalViewModel
import com.noqapp.android.client.views.version_2.market_place.propertyRental.post_property_rental.PostPropertyRentalActivity
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.noqapp.android.common.model.types.BusinessTypeEnum

class PropertyRentalListActivity : LocationBaseActivity() {

    private lateinit var activityPropertyRentalListBinding: ActivityPropertyRentalListBinding
    private lateinit var propertyRentalListAdapter: PropertyRentalListAdapter

    private lateinit var propertyRentalViewModel: PropertyRentalViewModel

    private var from: Int = PAGE_START
    private var size: Int = 0
    private var isLastPage = false
    private var isItemLoading = false
    private var itemCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPropertyRentalListBinding = ActivityPropertyRentalListBinding.inflate(LayoutInflater.from(this))
        setContentView(activityPropertyRentalListBinding.root)
        propertyRentalViewModel = ViewModelProvider(this)[PropertyRentalViewModel::class.java]

        activityPropertyRentalListBinding.swipeRefreshLayout.setOnRefreshListener {
            propertyRentalViewModel.searchStoreQueryLiveData.value?.let {
                activityPropertyRentalListBinding.rlEmpty.visibility = View.GONE
                activityPropertyRentalListBinding.shimmerLayout.startShimmer()
                propertyRentalListAdapter.clear()
                from = PAGE_START
                size = 0
                it.searchedOnBusinessType = BusinessTypeEnum.PR
                it.from = from
                it.size = size
                propertyRentalViewModel.getMarketPlace(it)
            }
        }

        setSupportActionBar(activityPropertyRentalListBinding.toolbar)

        activityPropertyRentalListBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        setListeners()
        setUpRecyclerView()
        observeData()
    }

    private fun setUpRecyclerView() {
        propertyRentalListAdapter = PropertyRentalListAdapter(mutableListOf()) { marketPlace, view ->
            onMarketPlaceItemClicked(marketPlace, view)
        }

        val layoutManager = LinearLayoutManager(this)
        activityPropertyRentalListBinding.rvMarketPlace.layoutManager = layoutManager
        activityPropertyRentalListBinding.rvMarketPlace.adapter = propertyRentalListAdapter

        activityPropertyRentalListBinding.rvMarketPlace.addOnScrollListener(object :
            PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                isItemLoading = true
                propertyRentalViewModel.searchStoreQueryLiveData.value?.let {
                    it.searchedOnBusinessType = BusinessTypeEnum.PR
                    it.from = from
                    it.size = size
                    propertyRentalListAdapter.addLoading()
                    propertyRentalViewModel.getMarketPlace(it)
                }
            }

            override fun isLastPage(): Boolean {
                return false
            }

            override fun isLoading(): Boolean {
                return isItemLoading
            }
        })
    }

    override fun locationPermissionRequired() {
        activityPropertyRentalListBinding.clLocationAccessRequired.visibility = View.VISIBLE
    }

    override fun locationPermissionGranted() {
        activityPropertyRentalListBinding.clLocationAccessRequired.visibility = View.GONE
    }

    private fun setListeners() {
        activityPropertyRentalListBinding.fabPost.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    PostPropertyRentalActivity::class.java
                )
            )
        }
    }

    private fun observeData() {
        propertyRentalViewModel.marketplaceElasticListLiveData.observe(this, {
            from = it.from
            size = it.size

            activityPropertyRentalListBinding.shimmerLayout.stopShimmer()
            activityPropertyRentalListBinding.shimmerLayout.visibility = View.GONE
            activityPropertyRentalListBinding.rvMarketPlace.visibility = View.VISIBLE
            activityPropertyRentalListBinding.swipeRefreshLayout.isRefreshing = false

            if (from != PAGE_START) {
                propertyRentalListAdapter.removeLoading()
            }
            if (it.marketplaceElastics.isEmpty() && propertyRentalListAdapter.itemCount == 0) {
                activityPropertyRentalListBinding.rlEmpty.visibility = View.VISIBLE
            } else {
                it?.let { marketPlaceElasticList ->
                    activityPropertyRentalListBinding.rlEmpty.visibility = View.GONE
                    propertyRentalListAdapter.addMarketPlaces(marketPlaceElasticList.marketplaceElastics)
                }
            }
            isItemLoading = false
        })

        propertyRentalViewModel.authenticationError.observe(this, {
            if (it) {
                super.authenticationFailure()
                activityPropertyRentalListBinding.shimmerLayout.stopShimmer()
                activityPropertyRentalListBinding.shimmerLayout.visibility = View.GONE
                propertyRentalViewModel.authenticationError.value = false
            }
        })

        propertyRentalViewModel.errorEncounteredJsonLiveData.observe(this, {
            activityPropertyRentalListBinding.shimmerLayout.stopShimmer()
            activityPropertyRentalListBinding.shimmerLayout.visibility = View.GONE
            super.responseErrorPresenter(it)
        })

        propertyRentalViewModel.searchStoreQueryLiveData.observe(this, {
            activityPropertyRentalListBinding.shimmerLayout.startShimmer()
            it.searchedOnBusinessType = BusinessTypeEnum.PR
            it.size = size
            it.from = from
            propertyRentalViewModel.getMarketPlace(it)
        })

        propertyRentalViewModel.shownInterestLiveData.observe(this, {
            if (it) {
                showSnackbar(R.string.txt_owner_notified)
                propertyRentalViewModel.shownInterestLiveData.value = false
            }
        })
    }

    private fun onMarketPlaceItemClicked(marketPlace: MarketplaceElastic?, view: View) {
        marketPlace?.let {
            when (view.id) {
                R.id.btn_view_details -> {
                    propertyRentalViewModel.viewDetails(it.id)
                    val propertyDetailsIntent =
                        Intent(this, ViewPropertyRentalDetailsActivity::class.java).apply {
                            putExtra(Constants.POST_PROPERTY_RENTAL, marketPlace)
                        }
                    startActivity(propertyDetailsIntent)
                }
                R.id.btn_call_agent -> {
                    propertyRentalViewModel.initiateContact(it.id)
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

        searchStoreQuery.latitude = latitude.toString()
        searchStoreQuery.longitude = longitude.toString()

        searchStoreQuery.filters = ""
        searchStoreQuery.scrollId = ""

        propertyRentalViewModel.searchStoreQueryLiveData.value = searchStoreQuery
    }
}
