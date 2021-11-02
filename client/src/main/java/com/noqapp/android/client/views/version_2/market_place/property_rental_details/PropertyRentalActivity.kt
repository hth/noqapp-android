package com.noqapp.android.client.views.version_2.market_place.property_rental_details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityPropertyRentalBinding
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.utils.PaginationListener
import com.noqapp.android.client.utils.PaginationListener.PAGE_START
import com.noqapp.android.client.views.activities.LocationBaseActivity
import com.noqapp.android.client.views.version_2.market_place.PostPropertyRentalViewModel
import com.noqapp.android.client.views.version_2.market_place.post_property_rental.PostMarketplacePropertyRentalActivity
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.noqapp.android.common.model.types.BusinessTypeEnum

class PropertyRentalActivity : LocationBaseActivity() {

    private lateinit var activityPropertyRentalBinding: ActivityPropertyRentalBinding
    private lateinit var propertyRentalAdapter: PropertyRentalAdapter

    private lateinit var postPropertyRentalViewModel: PostPropertyRentalViewModel

    private var from: Int = PAGE_START
    private var size: Int = 3
    private var isLastPage = false
    private var isItemLoading = false
    private var itemCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPropertyRentalBinding = ActivityPropertyRentalBinding.inflate(LayoutInflater.from(this))
        setContentView(activityPropertyRentalBinding.root)
        postPropertyRentalViewModel = ViewModelProvider(this)[PostPropertyRentalViewModel::class.java]

        activityPropertyRentalBinding.swipeRefreshLayout.setOnRefreshListener {
            postPropertyRentalViewModel.searchStoreQueryLiveData.value?.let {
                activityPropertyRentalBinding.rlEmpty.visibility = View.GONE
                activityPropertyRentalBinding.shimmerLayout.startShimmer()
                propertyRentalAdapter.clear()
                from = PAGE_START
                size = 3
                it.searchedOnBusinessType = BusinessTypeEnum.PR
                it.from = from
                it.size = size
                postPropertyRentalViewModel.getMarketPlace(it)
            }
        }

        setSupportActionBar(activityPropertyRentalBinding.toolbar)

        activityPropertyRentalBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        setListeners()
        setUpRecyclerView()
        observeData()
    }

    private fun setUpRecyclerView() {
        propertyRentalAdapter = PropertyRentalAdapter(mutableListOf()) { marketPlace, view ->
            onMarketPlaceItemClicked(marketPlace, view)
        }
        val layoutManager = LinearLayoutManager(this)
        activityPropertyRentalBinding.rvMarketPlace.layoutManager = layoutManager
        activityPropertyRentalBinding.rvMarketPlace.adapter = propertyRentalAdapter

        activityPropertyRentalBinding.rvMarketPlace.addOnScrollListener(object :
            PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                isItemLoading = true
                postPropertyRentalViewModel.searchStoreQueryLiveData.value?.let {
                    it.searchedOnBusinessType = BusinessTypeEnum.PR
                    size += 3
                    from += 3
                    it.from = from
                    it.size = size
                    propertyRentalAdapter.addLoading()
                    postPropertyRentalViewModel.getMarketPlace(it)
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
        activityPropertyRentalBinding.clLocationAccessRequired.visibility = View.VISIBLE
    }

    override fun locationPermissionGranted() {
        activityPropertyRentalBinding.clLocationAccessRequired.visibility = View.GONE
    }

    private fun setListeners() {
        activityPropertyRentalBinding.fabPost.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    PostMarketplacePropertyRentalActivity::class.java
                )
            )
        }
    }

    private fun observeData() {
        postPropertyRentalViewModel.marketplaceElasticListLiveData.observe(this, {
            activityPropertyRentalBinding.shimmerLayout.stopShimmer()
            activityPropertyRentalBinding.shimmerLayout.visibility = View.GONE
            activityPropertyRentalBinding.rvMarketPlace.visibility = View.VISIBLE
            activityPropertyRentalBinding.swipeRefreshLayout.isRefreshing = false

            if (from != PAGE_START) {
                propertyRentalAdapter.removeLoading()
            }
            if (it.marketplaceElastics.isEmpty() && propertyRentalAdapter.itemCount == 0) {
                activityPropertyRentalBinding.rlEmpty.visibility = View.VISIBLE
            } else {
                it?.let { marketPlaceElasticList ->
                    activityPropertyRentalBinding.rlEmpty.visibility = View.GONE
                    propertyRentalAdapter.addMarketPlaces(marketPlaceElasticList.marketplaceElastics)
                }
            }
            isItemLoading = false
        })

        postPropertyRentalViewModel.authenticationError.observe(this, {
            if (it) {
                super.authenticationFailure()
                activityPropertyRentalBinding.shimmerLayout.stopShimmer()
                activityPropertyRentalBinding.shimmerLayout.visibility = View.GONE
                postPropertyRentalViewModel.authenticationError.value = false
            }
        })

        postPropertyRentalViewModel.errorEncounteredJsonLiveData.observe(this, {
            activityPropertyRentalBinding.shimmerLayout.stopShimmer()
            activityPropertyRentalBinding.shimmerLayout.visibility = View.GONE
            super.responseErrorPresenter(it)
        })

        postPropertyRentalViewModel.searchStoreQueryLiveData.observe(this, {
            activityPropertyRentalBinding.shimmerLayout.startShimmer()
            it.searchedOnBusinessType = BusinessTypeEnum.PR
            postPropertyRentalViewModel.getMarketPlace(it)
        })

        postPropertyRentalViewModel.shownInterestLiveData.observe(this, {
            if (it) {
                showSnackbar(R.string.txt_owner_notified)
                postPropertyRentalViewModel.shownInterestLiveData.value = false
            }
        })
    }

    private fun onMarketPlaceItemClicked(marketPlace: MarketplaceElastic?, view: View) {
        marketPlace?.let {
            when (view.id) {
                R.id.btn_view_details -> {
                    postPropertyRentalViewModel.viewDetails(it.id)
                    val propertyDetailsIntent =
                        Intent(this, ViewPropertyRentalDetailsActivity::class.java).apply {
                            putExtra(Constants.POST_PROPERTY_RENTAL, marketPlace)
                        }
                    startActivity(propertyDetailsIntent)
                }
                R.id.btn_call_agent -> {
                    postPropertyRentalViewModel.initiateContact(it.id)
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

        postPropertyRentalViewModel.searchStoreQueryLiveData.value = searchStoreQuery
    }
}
