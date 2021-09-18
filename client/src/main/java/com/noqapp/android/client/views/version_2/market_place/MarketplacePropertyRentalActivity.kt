package com.noqapp.android.client.views.version_2.market_place

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.noqapp.android.client.databinding.ActivityMarketPlaceBinding
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.views.activities.LocationBaseActivity
import com.noqapp.android.client.views.version_2.market_place.market_place_details.MarketPlaceListFragment
import com.noqapp.android.client.views.version_2.market_place.my_posts.MyMarketPlacePostsFragment

class MarketplacePropertyRentalActivity : LocationBaseActivity() {

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

        marketplacePropertyRentalViewModel.searchStoreQueryLiveData.value = searchStoreQuery
    }

    override fun locationPermissionRequired() {
    }

    override fun locationPermissionGranted() {
    }

    private lateinit var activityMarketPlaceBinding: ActivityMarketPlaceBinding
    private val marketplacePropertyRentalViewModel: MarketplacePropertyRentalViewModel by viewModels()
    private lateinit var marketplacePropertyRentalFragmentAdapter: MarketplacePropertyRentalFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMarketPlaceBinding = ActivityMarketPlaceBinding.inflate(LayoutInflater.from(this))
        setContentView(activityMarketPlaceBinding.root)
        setSupportActionBar(activityMarketPlaceBinding.toolbar)

        setListeners()
        setUpViewPager()
    }

    private fun setListeners() {
        activityMarketPlaceBinding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setUpViewPager() {
        marketplacePropertyRentalFragmentAdapter = MarketplacePropertyRentalFragmentAdapter(mutableListOf(), this)
        activityMarketPlaceBinding.viewPager.adapter = marketplacePropertyRentalFragmentAdapter
        val fragmentList = mutableListOf<Fragment>()
        fragmentList.add(MarketPlaceListFragment())
        fragmentList.add(MyMarketPlacePostsFragment())
        marketplacePropertyRentalFragmentAdapter.addFragments(fragmentList)
        TabLayoutMediator(activityMarketPlaceBinding.tabsMarketPlace, activityMarketPlaceBinding.viewPager) { tab, position ->
            if (position == 0) {
                tab.text = "User Posts"
            } else {
                tab.text = "My Posts"
            }
        }.attach()
    }


}