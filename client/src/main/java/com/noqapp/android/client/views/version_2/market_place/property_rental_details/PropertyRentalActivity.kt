package com.noqapp.android.client.views.version_2.market_place.property_rental_details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityPropertyRentalBinding
import com.noqapp.android.client.views.activities.BaseActivity
import com.noqapp.android.client.views.version_2.market_place.PostPropertyRentalViewModel
import com.noqapp.android.client.views.version_2.market_place.post_property_rental.PostMarketplacePropertyRentalActivity
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.noqapp.android.common.model.types.BusinessTypeEnum

class PropertyRentalActivity : BaseActivity() {

    private lateinit var activityPropertyRentalBinding: ActivityPropertyRentalBinding
    private lateinit var propertyRentalAdapter: PropertyRentalAdapter

    private lateinit var postPropertyRentalViewModel: PostPropertyRentalViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPropertyRentalBinding =
            ActivityPropertyRentalBinding.inflate(LayoutInflater.from(this))
        setContentView(activityPropertyRentalBinding.root)
        postPropertyRentalViewModel =
            ViewModelProvider(this)[PostPropertyRentalViewModel::class.java]

        setSupportActionBar(activityPropertyRentalBinding.toolbar)

        activityPropertyRentalBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        setListeners()
        setUpRecyclerView()
        observeData()
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
            it?.let { marketPlaceElasticList ->
                propertyRentalAdapter.addMarketPlaces(marketPlaceElasticList.marketplaceElastics)
            }
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
    }

    private fun setUpRecyclerView() {
        propertyRentalAdapter = PropertyRentalAdapter(mutableListOf()) { marketPlace, view ->
            onMarketPlaceItemClicked(marketPlace, view)
        }
        activityPropertyRentalBinding.rvMarketPlace.layoutManager =
            LinearLayoutManager(this)
        activityPropertyRentalBinding.rvMarketPlace.adapter = propertyRentalAdapter
    }

    private fun onMarketPlaceItemClicked(marketPlace: MarketplaceElastic?, view: View) {
        marketPlace?.let {
            when (view.id) {
                R.id.btn_view_details -> {
                    postPropertyRentalViewModel.viewDetails(it.id)
                    // startActivity(Intent(this, MarketPlaceDetailsActivity::class.java))
                }
                R.id.btn_call_agent -> {
                    postPropertyRentalViewModel.initiateContact(it.id)
                    // startActivity(Intent(this, MarketPlaceDetailsActivity::class.java))
                }
            }
        }
    }

}
