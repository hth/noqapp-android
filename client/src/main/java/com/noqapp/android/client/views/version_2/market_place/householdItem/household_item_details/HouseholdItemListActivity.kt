package com.noqapp.android.client.views.version_2.market_place.householdItem.household_item_details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityHouseholdItemListBinding
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.utils.PaginationListener
import com.noqapp.android.client.utils.PaginationListener.PAGE_START
import com.noqapp.android.client.views.activities.LocationBaseActivity
import com.noqapp.android.client.views.version_2.market_place.householdItem.HouseholdItemViewModel
import com.noqapp.android.client.views.version_2.market_place.householdItem.post_household_item.PostHouseholdItemActivity
import com.noqapp.android.client.views.version_2.market_place.propertyRental.property_rental_details.ViewPropertyRentalDetailsActivity
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.noqapp.android.common.model.types.BusinessTypeEnum

class HouseholdItemListActivity : LocationBaseActivity() {

    private lateinit var activityHouseholdItemListBinding: ActivityHouseholdItemListBinding
    private lateinit var householdItemListAdapter: HouseholdItemListAdapter

    private lateinit var householdItemViewModel: HouseholdItemViewModel

    private var from: Int = PAGE_START
    private var size: Int = 3
    private var isLastPage = false
    private var isItemLoading = false
    private var itemCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityHouseholdItemListBinding = ActivityHouseholdItemListBinding.inflate(LayoutInflater.from(this))
        setContentView(activityHouseholdItemListBinding.root)
        householdItemViewModel = ViewModelProvider(this)[HouseholdItemViewModel::class.java]

        activityHouseholdItemListBinding.swipeRefreshLayout.setOnRefreshListener {
            householdItemViewModel.searchStoreQueryLiveData.value?.let {
                activityHouseholdItemListBinding.rlEmpty.visibility = View.GONE
                activityHouseholdItemListBinding.shimmerLayout.startShimmer()
                householdItemListAdapter.clear()
                from = PAGE_START
                size = 3
                it.searchedOnBusinessType = BusinessTypeEnum.PR
                it.from = from
                it.size = size
                householdItemViewModel.getMarketPlace(it)
            }
        }

        setSupportActionBar(activityHouseholdItemListBinding.toolbar)

        activityHouseholdItemListBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        setListeners()
        setUpRecyclerView()
        observeData()
    }

    private fun setUpRecyclerView() {
        householdItemListAdapter = HouseholdItemListAdapter(mutableListOf()) { marketPlace, view ->
            onMarketPlaceItemClicked(marketPlace, view)
        }
        val layoutManager = LinearLayoutManager(this)
        activityHouseholdItemListBinding.rvMarketPlace.layoutManager = layoutManager
        activityHouseholdItemListBinding.rvMarketPlace.adapter = householdItemListAdapter

        activityHouseholdItemListBinding.rvMarketPlace.addOnScrollListener(object :
            PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                isItemLoading = true
                householdItemViewModel.searchStoreQueryLiveData.value?.let {
                    it.searchedOnBusinessType = BusinessTypeEnum.PR
                    size += 3
                    from += 3
                    it.from = from
                    it.size = size
                    householdItemListAdapter.addLoading()
                    householdItemViewModel.getMarketPlace(it)
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
        activityHouseholdItemListBinding.clLocationAccessRequired.visibility = View.VISIBLE
    }

    override fun locationPermissionGranted() {
        activityHouseholdItemListBinding.clLocationAccessRequired.visibility = View.GONE
    }

    private fun setListeners() {
        activityHouseholdItemListBinding.fabPost.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    PostHouseholdItemActivity::class.java
                )
            )
        }
    }

    private fun observeData() {
        householdItemViewModel.marketplaceElasticListLiveData.observe(this, {
            activityHouseholdItemListBinding.shimmerLayout.stopShimmer()
            activityHouseholdItemListBinding.shimmerLayout.visibility = View.GONE
            activityHouseholdItemListBinding.rvMarketPlace.visibility = View.VISIBLE
            activityHouseholdItemListBinding.swipeRefreshLayout.isRefreshing = false

            if (from != PAGE_START) {
                householdItemListAdapter.removeLoading()
            }
            if (it.marketplaceElastics.isEmpty() && householdItemListAdapter.itemCount == 0) {
                activityHouseholdItemListBinding.rlEmpty.visibility = View.VISIBLE
            } else {
                it?.let { marketPlaceElasticList ->
                    activityHouseholdItemListBinding.rlEmpty.visibility = View.GONE
                    householdItemListAdapter.addMarketPlaces(marketPlaceElasticList.marketplaceElastics)
                }
            }
            isItemLoading = false
        })

        householdItemViewModel.authenticationError.observe(this, {
            if (it) {
                super.authenticationFailure()
                activityHouseholdItemListBinding.shimmerLayout.stopShimmer()
                activityHouseholdItemListBinding.shimmerLayout.visibility = View.GONE
                householdItemViewModel.authenticationError.value = false
            }
        })

        householdItemViewModel.errorEncounteredJsonLiveData.observe(this, {
            activityHouseholdItemListBinding.shimmerLayout.stopShimmer()
            activityHouseholdItemListBinding.shimmerLayout.visibility = View.GONE
            super.responseErrorPresenter(it)
        })

        householdItemViewModel.searchStoreQueryLiveData.observe(this, {
            activityHouseholdItemListBinding.shimmerLayout.startShimmer()
            it.searchedOnBusinessType = BusinessTypeEnum.PR
            householdItemViewModel.getMarketPlace(it)
        })

        householdItemViewModel.shownInterestLiveData.observe(this, {
            if (it) {
                showSnackbar(R.string.txt_owner_notified)
                householdItemViewModel.shownInterestLiveData.value = false
            }
        })
    }

    private fun onMarketPlaceItemClicked(marketPlace: MarketplaceElastic?, view: View) {
        marketPlace?.let {
            when (view.id) {
                R.id.btn_view_details -> {
                    householdItemViewModel.viewDetails(it.id)
                    val propertyDetailsIntent =
                        Intent(this, ViewPropertyRentalDetailsActivity::class.java).apply {
                            putExtra(Constants.POST_PROPERTY_RENTAL, marketPlace)
                        }
                    startActivity(propertyDetailsIntent)
                }
                R.id.btn_call_agent -> {
                    householdItemViewModel.initiateContact(it.id)
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

        householdItemViewModel.searchStoreQueryLiveData.value = searchStoreQuery
    }
}
