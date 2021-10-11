package com.noqapp.android.client.views.version_2.market_place.property_rental_details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.FragmentPropertyRentalBinding
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.market_place.PostPropertyRentalViewModel
import com.noqapp.android.client.views.version_2.market_place.post_property_rental.PostMarketplacePropertyRentalActivity
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.noqapp.android.common.model.types.BusinessTypeEnum

class PropertyRentalFragment : BaseFragment() {

    private lateinit var fragmentPropertyRentalBinding: FragmentPropertyRentalBinding
    private lateinit var propertyRentalAdapter: PropertyRentalAdapter

    private lateinit var marketPlaceViewModel: PostPropertyRentalViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPropertyRentalBinding =
            FragmentPropertyRentalBinding.inflate(inflater, container, false)
        marketPlaceViewModel =
            ViewModelProvider(requireActivity())[PostPropertyRentalViewModel::class.java]
        return fragmentPropertyRentalBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        setListeners()
        setUpRecyclerView()
        observeData()
    }

    private fun setListeners() {
        fragmentPropertyRentalBinding.fabPost.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    PostMarketplacePropertyRentalActivity::class.java
                )
            )
        }
    }

    private fun observeData() {
        marketPlaceViewModel.marketplaceElasticListLiveData.observe(this, {
            fragmentPropertyRentalBinding.shimmerLayout.stopShimmer()
            fragmentPropertyRentalBinding.shimmerLayout.visibility = View.GONE
            fragmentPropertyRentalBinding.rvMarketPlace.visibility = View.VISIBLE
            it?.let { marketPlaceElasticList ->
                propertyRentalAdapter.addMarketPlaces(marketPlaceElasticList.marketplaceElastics)
            }
        })

        marketPlaceViewModel.authenticationError.observe(this, {
            if (it) {
                super.authenticationFailure()
                fragmentPropertyRentalBinding.shimmerLayout.stopShimmer()
                fragmentPropertyRentalBinding.shimmerLayout.visibility = View.GONE
                marketPlaceViewModel.authenticationError.value = false
            }
        })

        marketPlaceViewModel.errorEncounteredJsonLiveData.observe(this, {
            fragmentPropertyRentalBinding.shimmerLayout.stopShimmer()
            fragmentPropertyRentalBinding.shimmerLayout.visibility = View.GONE
            super.responseErrorPresenter(it)
        })

        marketPlaceViewModel.searchStoreQueryLiveData.observe(this, {
            fragmentPropertyRentalBinding.shimmerLayout.startShimmer()
            it.searchedOnBusinessType = BusinessTypeEnum.PR
            marketPlaceViewModel.getMarketPlace(it)
        })
    }

    private fun setUpRecyclerView() {
        propertyRentalAdapter = PropertyRentalAdapter(mutableListOf()) { marketPlace, view ->
            onMarketPlaceItemClicked(marketPlace, view)
        }
        fragmentPropertyRentalBinding.rvMarketPlace.layoutManager =
            LinearLayoutManager(requireContext())
        fragmentPropertyRentalBinding.rvMarketPlace.adapter = propertyRentalAdapter
    }

    private fun onMarketPlaceItemClicked(marketPlace: MarketplaceElastic?, view: View) {
        marketPlace?.let {
            when (view.id) {
                R.id.btn_view_details -> {
                    marketPlaceViewModel.viewDetails(it.id)
                    // startActivity(Intent(this, MarketPlaceDetailsActivity::class.java))
                }
                R.id.btn_call_agent -> {
                    marketPlaceViewModel.initiateContact(it.id)
                    // startActivity(Intent(this, MarketPlaceDetailsActivity::class.java))
                }
            }
        }
    }

}
