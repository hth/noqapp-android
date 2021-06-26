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
import com.google.android.material.tabs.TabLayout
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.FragmentHomeNewBinding
import com.noqapp.android.client.databinding.ViewIndicatorBinding
import com.noqapp.android.client.presenter.beans.BizStoreElastic
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue
import com.noqapp.android.client.presenter.beans.ReviewData
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery
import com.noqapp.android.client.utils.*
import com.noqapp.android.client.views.activities.AfterJoinActivity
import com.noqapp.android.client.views.activities.AppInitialize
import com.noqapp.android.client.views.activities.BlinkerActivity
import com.noqapp.android.client.views.activities.OrderConfirmActivity
import com.noqapp.android.client.views.adapters.StoreInfoAdapter
import com.noqapp.android.client.views.adapters.TokenAndQueueAdapter
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.NavigationBundleUtils
import com.noqapp.android.client.views.version_2.db.helper_models.ForegroundNotificationModel
import com.noqapp.android.client.views.version_2.viewmodels.HomeViewModel
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech
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
        fragmentHomeNewBinding.tabNearMeRecentVisits.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    if (it.position == 0) {
                        searchStoreQuery?.let { searchStoreQuery ->
                            homeViewModel.fetchNearMe(UserUtils.getDeviceId(), searchStoreQuery)
                        }
                    } else {
                        homeViewModel.fetchFavouritesRecentVisitList()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.let {
                    if (it.position == 0) {
                        searchStoreQuery?.let { searchStoreQuery ->
                            homeViewModel.fetchNearMe(UserUtils.getDeviceId(), searchStoreQuery)
                        }
                    } else {
                        homeViewModel.fetchFavouritesRecentVisitList()
                    }
                }
            }
        })

        fragmentHomeNewBinding.clJobs.setOnClickListener {
            val homeFragmentDirections = HomeFragmentDirections.actionHomeToUnderDevelopmentFragmentDestination("Anything")
            findNavController().navigate(homeFragmentDirections)
        }

        fragmentHomeNewBinding.clHousing.setOnClickListener {
            val homeFragmentDirections = HomeFragmentDirections.actionHomeToUnderDevelopmentFragmentDestination("Anything")
            findNavController().navigate(homeFragmentDirections)
        }

        fragmentHomeNewBinding.clMarketplace.setOnClickListener {
            val homeFragmentDirections = HomeFragmentDirections.actionHomeToUnderDevelopmentFragmentDestination("Anything")
            findNavController().navigate(homeFragmentDirections)
        }

        fragmentHomeNewBinding.clRestaurant.setOnClickListener {
            val navigationDirections = HomeFragmentDirections.actionHomeToViewBusinessDestination(BusinessTypeEnum.RS)
            findNavController().navigate(navigationDirections)
        }

        fragmentHomeNewBinding.clHospital.setOnClickListener {
            val navigationDirections = HomeFragmentDirections.actionHomeToViewBusinessDestination(BusinessTypeEnum.HS)
            findNavController().navigate(navigationDirections)
        }

        fragmentHomeNewBinding.clUsrCsd.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeToViewBusinessDestination(BusinessTypeEnum.CD))
        }

        fragmentHomeNewBinding.clGrocery.setOnClickListener {
            val navigationDirections = HomeFragmentDirections.actionHomeToViewBusinessDestination(BusinessTypeEnum.GS)
            findNavController().navigate(navigationDirections)
        }

        fragmentHomeNewBinding.clSchool.setOnClickListener {
            val homeFragmentDirections = HomeFragmentDirections.actionHomeToUnderDevelopmentFragmentDestination("Anything")
            findNavController().navigate(homeFragmentDirections)
        }

        fragmentHomeNewBinding.clCafe.setOnClickListener {
            val navigationDirections = HomeFragmentDirections.actionHomeToViewBusinessDestination(BusinessTypeEnum.CF)
            findNavController().navigate(navigationDirections)
        }

        fragmentHomeNewBinding.clGenericStore.setOnClickListener {
            val navigationDirections = HomeFragmentDirections.actionHomeToViewBusinessDestination(BusinessTypeEnum.ST)
            findNavController().navigate(navigationDirections)
        }
    }

    private fun observeValues() {
        homeViewModel.searchStoreQueryLiveData.observe(viewLifecycleOwner, {
            it.searchedOnBusinessType = BusinessTypeEnum.ZZ
            searchStoreQuery = it
            fragmentHomeNewBinding.pbRecentVisitsNearMe.visibility = View.VISIBLE
            homeViewModel.fetchNearMe(UserUtils.getDeviceId(), it)
        })

        homeViewModel.nearMeResponse.observe(viewLifecycleOwner, { bizStoreElasticList ->
            bizStoreElasticList?.bizStoreElastics?.let {
                searchStoreQuery?.let { searchStoreQueryVal ->
                    Collections.sort(it, SortPlaces(GeoIP(searchStoreQueryVal.latitude.toDouble(), searchStoreQueryVal.longitude.toDouble())))
                    val storeInfoAdapter = StoreInfoAdapter(it, activity, this, searchStoreQueryVal.latitude.toDouble(), searchStoreQueryVal.longitude.toDouble())
                    fragmentHomeNewBinding.rvRecentVisitsNearMe.adapter = storeInfoAdapter
                    fragmentHomeNewBinding.pbRecentVisitsNearMe.visibility = View.GONE
                }
            }
        })

        /** Recent to be listed in order of last joined activity. Displays based on recently visited. */
        homeViewModel.favoritesListResponseLiveData.observe(viewLifecycleOwner, {
            it?.favoriteSuggested?.let {
                searchStoreQuery?.let { searchStoreQueryVal ->
                    val storeInfoAdapter = StoreInfoAdapter(it, activity, this, searchStoreQueryVal.latitude.toDouble(), searchStoreQueryVal.longitude.toDouble())
                    fragmentHomeNewBinding.rvRecentVisitsNearMe.adapter = storeInfoAdapter
                    fragmentHomeNewBinding.pbRecentVisitsNearMe.visibility = View.GONE
                }
            }
        })

        homeViewModel.nearMeErrorLiveData.observe(viewLifecycleOwner, {
            if (it) {
                fragmentHomeNewBinding.cvRecentVisits.visibility = View.GONE
            }
        })

        homeViewModel.currentTokenAndQueueListLiveData.observe(viewLifecycleOwner, { tokenAndQueuesList ->
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

//    fun updateListFromNotification(jq: JsonTokenAndQueue, jsonTextToSpeeches: List<JsonTextToSpeech?>?, msgId: String?) {
//        jq.displayServingNumber?.let { displayServingNumber ->
//
//            val isUserTurn = jq.afterHowLong() == 0
//            if (isUserTurn) {
//
//                homeViewModel.getReviewData(jq.codeQR, jq.token.toString()).observe(viewLifecycleOwner, Observer {
//                    val showBuzzer: Boolean = if (null != it) {
//                        it.isBuzzerShow != "1"
//                        // update
//                    } else {
//                        //insert
//                        val reviewData = ReviewData()
//                        reviewData.isReviewShown = "-1"
//                        reviewData.codeQR = jq.codeQR
//                        reviewData.token = jq.token.toString()
//                        reviewData.queueUserId = jq.queueUserId
//                        reviewData.isBuzzerShow = "-1"
//                        reviewData.isSkipped = "-1"
//                        reviewData.gotoCounter = ""
//                        reviewData.type = Constants.NotificationTypeConstant.FOREGROUND
//
//                        homeViewModel.insertReviewData(reviewData)
//                        true
//                    }
//                    if (showBuzzer) {
//                        if (QueueOrderTypeEnum.Q == jq.businessType.queueOrderType) {
//
//                            val cv = ContentValues()
//                            cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "1")
//                            ReviewDB.updateReviewRecord(jq.codeQR, jq.token.toString(), cv)
//                            val blinker = Intent(activity, BlinkerActivity::class.java)
//                            blinker.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                            activity!!.applicationContext.startActivity(blinker)
//                            if (AppInitialize.isMsgAnnouncementEnable()) {
//                                LaunchActivity.getLaunchActivity().makeAnnouncement(jsonTextToSpeeches, msgId)
//                            }
//                        } else {
//                            when (jq.purchaseOrderState) {
//                                PurchaseOrderStateEnum.RP, PurchaseOrderStateEnum.RD -> {
//                                    val cv = ContentValues()
//                                    cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "1")
//                                    ReviewDB.updateReviewRecord(jq.codeQR, jq.token.toString(), cv)
//                                    val blinker = Intent(activity, BlinkerActivity::class.java)
//                                    blinker.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                    activity!!.applicationContext.startActivity(blinker)
//                                    if (AppInitialize.isMsgAnnouncementEnable()) {
//                                        LaunchActivity.getLaunchActivity().makeAnnouncement(jsonTextToSpeeches, msgId)
//                                    }
//                                }
//                                PurchaseOrderStateEnum.CO -> {
//                                }
//                                else -> {
//                                }
//                            }
//                        }
//                    }
//                })
//            }
//        }
//    }


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
        compositePageTransformer.addTransformer(MarginPageTransformer(32))
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

interface HomeFragmentInteractionListener {
    fun makeAnnouncement(jsonTextToSpeeches: List<JsonTextToSpeech?>, msgId: String)
    fun callReviewActivity(codeQr: String, token: String)
}
