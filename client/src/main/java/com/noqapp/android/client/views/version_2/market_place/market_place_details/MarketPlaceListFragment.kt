package com.noqapp.android.client.views.version_2.market_place.market_place_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.FragmentMarketPlaceListBinding
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.market_place.MarketplacePropertyRentalViewModel
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.noqapp.android.common.model.types.BusinessTypeEnum

class MarketPlaceListFragment : BaseFragment() {

    private lateinit var fragmentMarketPlaceListBinding: FragmentMarketPlaceListBinding
    private lateinit var marketPlaceAdapter: MarketPlaceAdapter

    private lateinit var marketPlaceViewModel: MarketplacePropertyRentalViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMarketPlaceListBinding = FragmentMarketPlaceListBinding.inflate(inflater, container, false)
        marketPlaceViewModel = ViewModelProvider(requireActivity())[MarketplacePropertyRentalViewModel::class.java]
        return fragmentMarketPlaceListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        setUpRecyclerView()
        observeData()
    }

    private fun observeData() {
        marketPlaceViewModel.marketplaceElasticListLiveData.observe(this, {
            fragmentMarketPlaceListBinding.shimmerLayout.stopShimmer()
            fragmentMarketPlaceListBinding.shimmerLayout.visibility = View.GONE
            fragmentMarketPlaceListBinding.rvMarketPlace.visibility = View.VISIBLE
            it?.let { marketPlaceElasticList ->
                marketPlaceAdapter.addMarketPlaces(marketPlaceElasticList.marketplaceElastics)
            }
        })

        marketPlaceViewModel.authenticationError.observe(this, {
            if (it) {
                super.authenticationFailure()
                fragmentMarketPlaceListBinding.shimmerLayout.stopShimmer()
                fragmentMarketPlaceListBinding.shimmerLayout.visibility = View.GONE
                marketPlaceViewModel.authenticationError.value = false
            }
        })

        marketPlaceViewModel.errorEncounteredJsonLiveData.observe(this, {
            fragmentMarketPlaceListBinding.shimmerLayout.stopShimmer()
            fragmentMarketPlaceListBinding.shimmerLayout.visibility = View.GONE
            super.responseErrorPresenter(it)
        })

        marketPlaceViewModel.searchStoreQueryLiveData.observe(this, {
            fragmentMarketPlaceListBinding.shimmerLayout.startShimmer()
            it.searchedOnBusinessType = BusinessTypeEnum.PR
            marketPlaceViewModel.getMarketPlace(it)
        })
    }

    private fun setUpRecyclerView() {
        marketPlaceAdapter = MarketPlaceAdapter(mutableListOf()) { marketPlace, view ->
            onMarketPlaceItemClicked(marketPlace, view)
        }
        fragmentMarketPlaceListBinding.rvMarketPlace.layoutManager = LinearLayoutManager(requireContext())
        fragmentMarketPlaceListBinding.rvMarketPlace.adapter = marketPlaceAdapter
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
