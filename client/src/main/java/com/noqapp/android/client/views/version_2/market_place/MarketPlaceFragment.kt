package com.noqapp.android.client.views.version_2.market_place

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqapp.android.client.databinding.ActivityMarketPlaceBinding
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.viewmodels.HomeViewModel
import com.noqapp.android.common.model.types.BusinessTypeEnum

class MarketPlaceFragment : BaseFragment() {

    private lateinit var activityMarketPlaceBinding: ActivityMarketPlaceBinding
    private lateinit var marketPlaceAdapter: MarketPlaceAdapter

    private val marketPlaceViewModel: MarketPlaceViewModel by viewModels()
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activityMarketPlaceBinding = ActivityMarketPlaceBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        return activityMarketPlaceBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        setUpRecyclerView()
        observeData()
    }

    private fun observeData() {
        marketPlaceViewModel.marketPlaceElasticListLiveData.observe(viewLifecycleOwner, {
            dismissProgress()
            it?.let { marketPlaceElasticList ->
                marketPlaceAdapter.addMarketPlaces(marketPlaceElasticList.marketplaceElastics)
            }
        })

        marketPlaceViewModel.authenticationError.observe(viewLifecycleOwner, {
            if (it) {
                super.authenticationFailure()
                marketPlaceViewModel.authenticationError.value = false
            }
        })

        marketPlaceViewModel.errorEncounteredJsonLiveData.observe(viewLifecycleOwner, {
            super.responseErrorPresenter(it)
        })

        homeViewModel.searchStoreQueryLiveData.observe(viewLifecycleOwner, {
            showProgress()
            it.searchedOnBusinessType = BusinessTypeEnum.PR
            marketPlaceViewModel.getMarketPlace(it)
        })
    }

    private fun setUpRecyclerView() {
        marketPlaceAdapter = MarketPlaceAdapter(mutableListOf())
        activityMarketPlaceBinding.rvMarketPlace.layoutManager =
            LinearLayoutManager(requireContext())
        activityMarketPlaceBinding.rvMarketPlace.adapter = marketPlaceAdapter
    }

}