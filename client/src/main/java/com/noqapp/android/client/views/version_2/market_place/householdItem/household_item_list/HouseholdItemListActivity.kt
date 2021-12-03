package com.noqapp.android.client.views.version_2.market_place.householdItem.household_item_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityHouseholdItemListBinding
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.utils.PaginationListener
import com.noqapp.android.client.utils.PaginationListener.PAGE_START
import com.noqapp.android.client.views.activities.BaseActivity
import com.noqapp.android.client.views.version_2.HomeActivity
import com.noqapp.android.client.views.version_2.market_place.householdItem.HouseholdItemViewModel
import com.noqapp.android.client.views.version_2.market_place.householdItem.household_item_details.ViewHouseHoldItemDetailsActivity
import com.noqapp.android.client.views.version_2.market_place.householdItem.post_household_item.PostHouseholdItemActivity
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.noqapp.android.common.model.types.BusinessTypeEnum

class HouseholdItemListActivity : BaseActivity() {

    private lateinit var activityHouseholdItemListBinding: ActivityHouseholdItemListBinding
    private lateinit var householdItemListAdapter: HouseholdItemListAdapter

    private lateinit var householdItemViewModel: HouseholdItemViewModel

    private var from: Int = PAGE_START
    private var size: Int = 0
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
                size = 0
                it.searchedOnBusinessType = BusinessTypeEnum.HI
                it.from = from
                it.size = size
                householdItemViewModel.getMarketPlace(it)
            }
        }

        setSupportActionBar(activityHouseholdItemListBinding.toolbar)

        activityHouseholdItemListBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        val searchStoreQuery = SearchQuery()
        val area = HomeActivity.locationArea
        val town = HomeActivity.locationTown
        searchStoreQuery.cityName = AppUtils.getLocationAsString(area, town)
        searchStoreQuery.latitude = HomeActivity.locationLatitude.toString()
        searchStoreQuery.longitude = HomeActivity.locationLongitude.toString()

        searchStoreQuery.filters = ""
        searchStoreQuery.scrollId = ""

        householdItemViewModel.searchStoreQueryLiveData.value = searchStoreQuery
        setListeners()
        setUpRecyclerView()
        observeData()
    }

    private fun setUpRecyclerView() {
        householdItemListAdapter = HouseholdItemListAdapter(mutableListOf()) { marketPlace, view ->
            onMarketPlaceItemClicked(marketPlace, view)
        }
        val layoutManager = GridLayoutManager(this, 2)
        activityHouseholdItemListBinding.rvMarketPlace.layoutManager = layoutManager
        activityHouseholdItemListBinding.rvMarketPlace.adapter = householdItemListAdapter

        activityHouseholdItemListBinding.rvMarketPlace.addOnScrollListener(object :
            PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                isItemLoading = true
                householdItemViewModel.searchStoreQueryLiveData.value?.let {
                    it.searchedOnBusinessType = BusinessTypeEnum.HI
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
            from = it.from
            size = it.size

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
            it.searchedOnBusinessType = BusinessTypeEnum.HI
            it.size = size
            it.from = from
            householdItemViewModel.getMarketPlace(it)
        })

        householdItemViewModel.shownInterestLiveData.observe(this, {
            if (it) {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.txt_owner_notified),
                    Snackbar.LENGTH_SHORT)
                    .show()
                householdItemViewModel.shownInterestLiveData.value = false
            }
        })
    }

    private fun onMarketPlaceItemClicked(marketPlace: MarketplaceElastic?, view: View) {
        marketPlace?.let {
            when (view.id) {
                R.id.cv_house_hold_item -> {
                    householdItemViewModel.viewDetails(it.id)
                    val propertyDetailsIntent =
                        Intent(this, ViewHouseHoldItemDetailsActivity::class.java).apply {
                            putExtra(Constants.POST_PROPERTY_RENTAL, marketPlace)
                        }
                    startActivity(propertyDetailsIntent)
                }
            }
        }
    }

}
