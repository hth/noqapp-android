package com.noqapp.android.client.views.version_2.housing.lists

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityHousingListBinding
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.utils.PaginationListener
import com.noqapp.android.client.utils.PaginationListener.PAGE_START
import com.noqapp.android.client.views.activities.LocationBaseActivity
import com.noqapp.android.client.views.version_2.housing.HousingViewModel
import com.noqapp.android.client.views.version_2.housing.post_housing_item.PostHouseholdItemActivity
import com.noqapp.android.client.views.version_2.market_place.propertyRental.property_rental_details.ViewPropertyRentalDetailsActivity
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.noqapp.android.common.model.types.BusinessTypeEnum

class HousingListActivity : LocationBaseActivity() {

    private lateinit var activityHousingListBinding: ActivityHousingListBinding
    private lateinit var housingListAdapter: HousingListAdapter

    private lateinit var housingViewModel: HousingViewModel

    private var from: Int = PAGE_START
    private var size: Int = 3
    private var isLastPage = false
    private var isItemLoading = false
    private var itemCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityHousingListBinding = ActivityHousingListBinding.inflate(LayoutInflater.from(this))
        setContentView(activityHousingListBinding.root)
        housingViewModel = ViewModelProvider(this)[HousingViewModel::class.java]

        activityHousingListBinding.swipeRefreshLayout.setOnRefreshListener {
            housingViewModel.searchStoreQueryLiveData.value?.let {
                activityHousingListBinding.rlEmpty.visibility = View.GONE
                activityHousingListBinding.shimmerLayout.startShimmer()
                housingListAdapter.clear()
                from = PAGE_START
                size = 3
                it.searchedOnBusinessType = BusinessTypeEnum.PR
                it.from = from
                it.size = size
                housingViewModel.getMarketPlace(it)
            }
        }

        setSupportActionBar(activityHousingListBinding.toolbar)

        activityHousingListBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        setListeners()
        setUpRecyclerView()
        observeData()
    }

    private fun setUpRecyclerView() {
        housingListAdapter = HousingListAdapter(mutableListOf()) { marketPlace, view ->
            onMarketPlaceItemClicked(marketPlace, view)
        }
        val layoutManager = LinearLayoutManager(this)
        activityHousingListBinding.rvMarketPlace.layoutManager = layoutManager
        activityHousingListBinding.rvMarketPlace.adapter = housingListAdapter

        activityHousingListBinding.rvMarketPlace.addOnScrollListener(object :
            PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                isItemLoading = true
                housingViewModel.searchStoreQueryLiveData.value?.let {
                    it.searchedOnBusinessType = BusinessTypeEnum.PR
                    size += 3
                    from += 3
                    it.from = from
                    it.size = size
                    housingListAdapter.addLoading()
                    housingViewModel.getMarketPlace(it)
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
        activityHousingListBinding.clLocationAccessRequired.visibility = View.VISIBLE
    }

    override fun locationPermissionGranted() {
        activityHousingListBinding.clLocationAccessRequired.visibility = View.GONE
    }

    private fun setListeners() {
        activityHousingListBinding.fabPost.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    PostHouseholdItemActivity::class.java
                )
            )
        }
    }

    private fun observeData() {
        housingViewModel.marketplaceElasticListLiveData.observe(this, {
            activityHousingListBinding.shimmerLayout.stopShimmer()
            activityHousingListBinding.shimmerLayout.visibility = View.GONE
            activityHousingListBinding.rvMarketPlace.visibility = View.VISIBLE
            activityHousingListBinding.swipeRefreshLayout.isRefreshing = false

            if (from != PAGE_START) {
                housingListAdapter.removeLoading()
            }
            if (it.marketplaceElastics.isEmpty() && housingListAdapter.itemCount == 0) {
                activityHousingListBinding.rlEmpty.visibility = View.VISIBLE
            } else {
                it?.let { marketPlaceElasticList ->
                    activityHousingListBinding.rlEmpty.visibility = View.GONE
                    housingListAdapter.addMarketPlaces(marketPlaceElasticList.marketplaceElastics)
                }
            }
            isItemLoading = false
        })

        housingViewModel.authenticationError.observe(this, {
            if (it) {
                super.authenticationFailure()
                activityHousingListBinding.shimmerLayout.stopShimmer()
                activityHousingListBinding.shimmerLayout.visibility = View.GONE
                housingViewModel.authenticationError.value = false
            }
        })

        housingViewModel.errorEncounteredJsonLiveData.observe(this, {
            activityHousingListBinding.shimmerLayout.stopShimmer()
            activityHousingListBinding.shimmerLayout.visibility = View.GONE
            super.responseErrorPresenter(it)
        })

        housingViewModel.searchStoreQueryLiveData.observe(this, {
            activityHousingListBinding.shimmerLayout.startShimmer()
            it.searchedOnBusinessType = BusinessTypeEnum.PR
            housingViewModel.getMarketPlace(it)
        })

        housingViewModel.shownInterestLiveData.observe(this, {
            if (it) {
                showSnackbar(R.string.txt_owner_notified)
                housingViewModel.shownInterestLiveData.value = false
            }
        })
    }

    private fun onMarketPlaceItemClicked(marketPlace: MarketplaceElastic?, view: View) {
        marketPlace?.let {
            when (view.id) {
                R.id.btn_view_details -> {
                    housingViewModel.viewDetails(it.id)
                    val propertyDetailsIntent =
                        Intent(this, ViewPropertyRentalDetailsActivity::class.java).apply {
                            putExtra(Constants.POST_PROPERTY_RENTAL, marketPlace)
                        }
                    startActivity(propertyDetailsIntent)
                }
                R.id.btn_call_agent -> {
                    housingViewModel.initiateContact(it.id)
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

        housingViewModel.searchStoreQueryLiveData.value = searchStoreQuery
    }
}
