package com.noqapp.android.client.views.version_2.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.FragmentHomeNewBinding
import com.noqapp.android.client.presenter.beans.BizStoreElastic
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery
import com.noqapp.android.client.utils.SortPlaces
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.client.views.adapters.StoreInfoAdapter
import com.noqapp.android.client.views.adapters.TokenAndQueueAdapter
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.NavigationBundleUtils
import com.noqapp.android.client.views.version_2.viewmodels.HomeViewModel
import com.noqapp.android.common.model.types.BusinessTypeEnum
import com.noqapp.android.common.utils.GeoIP
import java.util.Collections

class HomeFragment : BaseFragment(), StoreInfoAdapter.OnItemClickListener {
    private lateinit var fragmentHomeNewBinding: FragmentHomeNewBinding
    private lateinit var tokenAndQueueAndQueueAdapter: TokenAndQueueAdapter
    private lateinit var homeFragmentInteractionListener: HomeFragmentInteractionListener
    private var searchStoreQuery: SearchStoreQuery? = null

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory(requireActivity().application))[HomeViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragmentInteractionListener) {
            homeFragmentInteractionListener = context
        } else {
            throw IllegalStateException("HomeActivity must implement HomeFragmentInteractionListener.")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentHomeNewBinding = FragmentHomeNewBinding.bind(LayoutInflater.from(context).inflate(R.layout.fragment_home_new, container, false))
        return fragmentHomeNewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()
        setUpRecyclerView()

        homeViewModel.fetchActiveTokenQueueList()

        setClickListeners()

        observeValues()
    }

    private fun setClickListeners() {
        fragmentHomeNewBinding.ivRestaurant.setOnClickListener {
            
            val navigationDirections = HomeFragmentDirections.actionHomeToViewBusinessDestination(BusinessTypeEnum.RS)
            findNavController().navigate(navigationDirections)
        }

        fragmentHomeNewBinding.ivHospital.setOnClickListener {
            val navigationDirections = HomeFragmentDirections.actionHomeToViewBusinessDestination(BusinessTypeEnum.HS)
            findNavController().navigate(navigationDirections)
        }

        fragmentHomeNewBinding.ivUsrCsd.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeToViewBusinessDestination(BusinessTypeEnum.CD))
        }

        fragmentHomeNewBinding.ivGrocery.setOnClickListener {
            val navigationDirections = HomeFragmentDirections.actionHomeToViewBusinessDestination(BusinessTypeEnum.GS)
            findNavController().navigate(navigationDirections)
        }

        fragmentHomeNewBinding.ivSchool.setOnClickListener {
     //       val navigationDirections = HomeFragmentDirections.actionHomeToViewBusinessDestination(BusinessTypeEnum.RS)
       //     findNavController().navigate(navigationDirections)
        }

        fragmentHomeNewBinding.ivCafeteria.setOnClickListener {
            val navigationDirections = HomeFragmentDirections.actionHomeToViewBusinessDestination(BusinessTypeEnum.CF)
            findNavController().navigate(navigationDirections)
        }

        fragmentHomeNewBinding.ivGenericStore.setOnClickListener {
            val navigationDirections = HomeFragmentDirections.actionHomeToViewBusinessDestination(BusinessTypeEnum.ST)
            findNavController().navigate(navigationDirections)
        }

    }

    private fun observeValues() {
        homeViewModel.searchStoreQueryLiveData.observe(viewLifecycleOwner, Observer {
            searchStoreQuery = it
            fragmentHomeNewBinding.pbRecentVisitsNearMe.visibility = View.VISIBLE
            homeViewModel.fetchNearMeRecentVisits(UserUtils.getDeviceId(), it)
        })

        homeViewModel.nearMeResponse.observe(viewLifecycleOwner, Observer { bizStoreElasticList ->
            bizStoreElasticList?.bizStoreElastics?.let {
                searchStoreQuery?.let { searchStoreQueryVal ->
                    Collections.sort(it, SortPlaces(GeoIP(searchStoreQueryVal.latitude.toDouble(), searchStoreQueryVal.longitude.toDouble())))
                    val storeInfoAdapter = StoreInfoAdapter(it, activity, this, searchStoreQueryVal.latitude.toDouble(), searchStoreQueryVal.longitude.toDouble())
                    fragmentHomeNewBinding.rvRecentVisitsNearMe.adapter = storeInfoAdapter
                    fragmentHomeNewBinding.pbRecentVisitsNearMe.visibility = View.GONE
                }
            }
        })

        homeViewModel.nearMeErrorLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                fragmentHomeNewBinding.cvRecentVisits.visibility = View.GONE
            }
        })

        homeViewModel.getCurrentTokenAndQueue().observe(viewLifecycleOwner, Observer { tokenAndQueuesList ->
            tokenAndQueuesList?.let {
                if (tokenAndQueuesList.isNullOrEmpty()) {
                    fragmentHomeNewBinding.cvTokens.visibility = View.GONE
                }
                tokenAndQueueAndQueueAdapter.addItems(tokenAndQueuesList)
                fragmentHomeNewBinding.llIndicator.removeAllViews()
                tokenAndQueuesList.forEach { _ ->
                    addIndicator()
                }
            }
        })

        homeViewModel.currentQueueErrorLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                fragmentHomeNewBinding.cvTokens.visibility = View.GONE
            }
        })
    }

    private fun setUpRecyclerView() {
        fragmentHomeNewBinding.rvRecentVisitsNearMe.setHasFixedSize(true)
        fragmentHomeNewBinding.rvRecentVisitsNearMe.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        fragmentHomeNewBinding.rvRecentVisitsNearMe.itemAnimator = DefaultItemAnimator()
    }

    private fun setUpViewPager() {
        tokenAndQueueAndQueueAdapter = TokenAndQueueAdapter(requireContext(), mutableListOf())
        fragmentHomeNewBinding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        fragmentHomeNewBinding.viewpager.adapter = tokenAndQueueAndQueueAdapter
    }

    private fun addIndicator() {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.view_indicator, null)
        fragmentHomeNewBinding.llIndicator.addView(view)
    }

    override fun onStoreItemClick(item: BizStoreElastic?) {
        item?.let {
            val navigationBundleUtils = NavigationBundleUtils()
            navigationBundleUtils.navigateToStore(requireActivity(), item)
        } ?: run {
            Log.d(HomeFragment::class.java.simpleName, "BizStoreElastic is null")
        }
    }
}

interface HomeFragmentInteractionListener {}