package com.noqapp.android.client.views.version_2.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.FragmentHomeNewBinding
import com.noqapp.android.client.databinding.ViewIndicatorBinding
import com.noqapp.android.client.presenter.beans.BizStoreElastic
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.utils.IBConstant
import com.noqapp.android.client.utils.SortPlaces
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.client.views.activities.AfterJoinActivity
import com.noqapp.android.client.views.activities.OrderConfirmActivity
import com.noqapp.android.client.views.adapters.StoreInfoAdapter
import com.noqapp.android.client.views.adapters.TokenAndQueueAdapter
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.NavigationBundleUtils
import com.noqapp.android.client.views.version_2.viewmodels.HomeViewModel
import com.noqapp.android.common.model.types.BusinessTypeEnum
import com.noqapp.android.common.model.types.QueueOrderTypeEnum
import com.noqapp.android.common.utils.GeoIP
import java.util.*
import kotlin.math.abs

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
        fragmentHomeNewBinding = FragmentHomeNewBinding.inflate(inflater, container, false)
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

        fragmentHomeNewBinding.ivJobs.setOnClickListener {
            findNavController().navigate(R.id.underDevelopmentFragment)
        }

        fragmentHomeNewBinding.ivHousing.setOnClickListener {
            findNavController().navigate(R.id.underDevelopmentFragment)
        }

        fragmentHomeNewBinding.ivMarketPlace.setOnClickListener {
            findNavController().navigate(R.id.underDevelopmentFragment)
        }

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
            findNavController().navigate(R.id.underDevelopmentFragment)
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
            it.searchedOnBusinessType = BusinessTypeEnum.ZZ
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

        homeViewModel.fetchNearMeRecentVisits()

        homeViewModel.nearMeErrorLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                fragmentHomeNewBinding.cvRecentVisits.visibility = View.GONE
            }
        })

        homeViewModel.currentTokenAndQueueListLiveData.observe(viewLifecycleOwner, Observer { tokenAndQueuesList ->
            tokenAndQueuesList?.let {
                if (tokenAndQueuesList.isNullOrEmpty()) {
                    fragmentHomeNewBinding.cvTokens.visibility = View.GONE
                } else {
                    fragmentHomeNewBinding.cvTokens.visibility = View.VISIBLE
                }
                tokenAndQueueAndQueueAdapter.addItems(tokenAndQueuesList)

                addIndicator(tokenAndQueuesList, 0)
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
        tokenAndQueueAndQueueAdapter = TokenAndQueueAdapter(requireContext(), mutableListOf()) {
            onTokenClicked(it)
        }

        fragmentHomeNewBinding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        fragmentHomeNewBinding.viewpager.adapter = tokenAndQueueAndQueueAdapter

        fragmentHomeNewBinding.viewpager.clipToPadding = false
        fragmentHomeNewBinding.viewpager.clipChildren = false
        fragmentHomeNewBinding.viewpager.offscreenPageLimit = 3
        fragmentHomeNewBinding.viewpager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }

        fragmentHomeNewBinding.viewpager.setPageTransformer(compositePageTransformer)
        fragmentHomeNewBinding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                homeViewModel.currentTokenAndQueueListLiveData.observe(viewLifecycleOwner, {
                    addIndicator(it, position)
                })
            }
        })
    }

    private fun onTokenClicked(jsonTokenAndQueue: JsonTokenAndQueue) {
        if (jsonTokenAndQueue.businessType.queueOrderType == QueueOrderTypeEnum.Q) {
            val intent = Intent(activity, AfterJoinActivity::class.java).apply {
                putExtra(IBConstant.KEY_CODE_QR, jsonTokenAndQueue.codeQR)
                putExtra("qUserId", jsonTokenAndQueue.queueUserId)
                putExtra(IBConstant.KEY_FROM_LIST, true)
                putExtra(IBConstant.KEY_JSON_TOKEN_QUEUE, jsonTokenAndQueue)
            }
            startActivity(intent)
        } else {
            val intent = Intent(activity, OrderConfirmActivity::class.java).apply {
                val bundle = Bundle()
                bundle.putBoolean(IBConstant.KEY_FROM_LIST, true)
                bundle.putString(IBConstant.KEY_CODE_QR, jsonTokenAndQueue.codeQR)
                bundle.putInt("token", jsonTokenAndQueue.token)
                bundle.putInt("currentServing", jsonTokenAndQueue.servingNumber)
                bundle.putString("displayCurrentServing", jsonTokenAndQueue.displayServingNumber)
                bundle.putString("GeoHash", jsonTokenAndQueue.geoHash)
                bundle.putString(IBConstant.KEY_STORE_NAME, jsonTokenAndQueue.displayName)
                bundle.putString(IBConstant.KEY_STORE_ADDRESS, jsonTokenAndQueue.storeAddress)
                bundle.putString(AppUtils.CURRENCY_SYMBOL, AppUtils.getCurrencySymbol(jsonTokenAndQueue.countryShortName))
                putExtras(bundle)
            }
            startActivity(intent)
        }
    }

    private fun addIndicator(tokenANdQueueList: List<JsonTokenAndQueue>, selectedPosition: Int) {
        fragmentHomeNewBinding.llIndicator.removeAllViews()
        tokenANdQueueList.forEachIndexed { index, _ ->
            val viewIndicatorBinding = ViewIndicatorBinding.inflate(LayoutInflater.from(requireContext()))
            if (index == selectedPosition) {
                viewIndicatorBinding.viewIndicator.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
            }
            fragmentHomeNewBinding.llIndicator.addView(viewIndicatorBinding.root)
        }
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