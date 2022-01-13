package com.noqapp.android.client.views.version_2.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.utils.IBConstant
import com.noqapp.android.client.utils.SortPlaces
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.client.views.activities.AfterJoinActivity
import com.noqapp.android.client.views.activities.AppointmentDetailActivity
import com.noqapp.android.client.views.activities.OrderConfirmActivity
import com.noqapp.android.client.views.adapters.ScheduledAppointmentAdapter
import com.noqapp.android.client.views.adapters.StoreInfoAdapter
import com.noqapp.android.client.views.adapters.TokenAndQueueAdapter
import com.noqapp.android.client.views.fragments.BaseFragment
import com.noqapp.android.client.views.version_2.NavigationBundleUtils
import com.noqapp.android.client.views.version_2.viewmodels.HomeViewModel
import com.noqapp.android.common.beans.JsonSchedule
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech
import com.noqapp.android.common.model.types.BusinessTypeEnum
import com.noqapp.android.common.model.types.QueueOrderTypeEnum
import com.noqapp.android.common.utils.GeoIP
import java.util.*
import kotlin.math.abs

class HomeFragment : BaseFragment(), StoreInfoAdapter.OnItemClickListener {
    private lateinit var fragmentHomeNewBinding: FragmentHomeNewBinding
    private lateinit var tokenAndQueueAndQueueAdapter: TokenAndQueueAdapter
    private lateinit var scheduledAppointmentAdapter: ScheduledAppointmentAdapter
    private lateinit var homeFragmentInteractionListener: HomeFragmentInteractionListener
    private var searchQuery: SearchQuery? = null
    private var showRecentVisitsFirst = true


    var nearMeList: List<BizStoreElastic>? = null
    var favouritesList: List<BizStoreElastic>? = null

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[HomeViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragmentInteractionListener) {
            homeFragmentInteractionListener = context
        } else {
            throw IllegalStateException("HomeActivity must implement HomeFragmentInteractionListener.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeNewBinding = FragmentHomeNewBinding.inflate(inflater, container, false)
        return fragmentHomeNewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()
        setUpRecyclerView()

        setClickListeners()
        setUpTabs()

        observeValues()
    }

    override fun onStart() {
        super.onStart()
        homeViewModel.fetchActiveTokenQueueList()
    }

    private fun setClickListeners() {
        fragmentHomeNewBinding.tabNearMeRecentVisits.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    if (it.position == 0) {
                        if (favouritesList == null || searchQuery == null) {
                            if (UserUtils.isLogin()) {
                                homeViewModel.fetchFavouritesRecentVisitList()
                            } else {
                                setEmptyData()
                            }
                        } else {
                            setFavouritesData(favouritesList!!)
                        }
                    } else {
                        searchQuery?.let { searchStoreQuery ->
                            if (nearMeList == null || searchQuery == null) {
                                homeViewModel.fetchNearMe(UserUtils.getDeviceId(), searchStoreQuery)
                            } else {
                                setNearmeData(nearMeList!!)
                            }
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
//                tab?.let {
//                    if (it.position == 0) {
//                        homeViewModel.fetchFavouritesRecentVisitList()
//                    } else {
//                        searchQuery?.let { searchStoreQuery ->
//                            homeViewModel.fetchNearMe(UserUtils.getDeviceId(), searchStoreQuery)
//                        }
//                    }
//                }
            }
        })

        fragmentHomeNewBinding.clRestaurant.setOnClickListener {
            val navigationDirections =
                HomeFragmentDirections.actionHomeToViewBusinessDestination(BusinessTypeEnum.RS)
            findNavController().navigate(navigationDirections)
        }

        fragmentHomeNewBinding.clHospital.setOnClickListener {
            val navigationDirections =
                HomeFragmentDirections.actionHomeToViewBusinessDestination(BusinessTypeEnum.HS)
            findNavController().navigate(navigationDirections)
        }

        fragmentHomeNewBinding.clUsrCsd.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeToViewBusinessDestination(
                    BusinessTypeEnum.CD
                )
            )
        }

        fragmentHomeNewBinding.clGrocery.setOnClickListener {
            val navigationDirections =
                HomeFragmentDirections.actionHomeToViewBusinessDestination(BusinessTypeEnum.GS)
            findNavController().navigate(navigationDirections)
        }

        fragmentHomeNewBinding.clGenericStore.setOnClickListener {
            val navigationDirections =
                HomeFragmentDirections.actionHomeToViewBusinessDestination(BusinessTypeEnum.ST)
            findNavController().navigate(navigationDirections)
        }

        fragmentHomeNewBinding.clPropertyRental.setOnClickListener {
            homeFragmentInteractionListener.navigateToPropertyRentalScreen()
        }

        fragmentHomeNewBinding.clHouseholdItem.setOnClickListener {
            homeFragmentInteractionListener.navigateToHouseholdItemScreen()
        }
    }


    private fun setEmptyData() {
        val storeInfoAdapter = StoreInfoAdapter(
            emptyList(),
            activity,
            this, 0.0, 0.0
        )
        fragmentHomeNewBinding.rvRecentVisitsNearMe.adapter = storeInfoAdapter
    }

    private fun setNearmeData(nearMeList: List<BizStoreElastic>) {
        val storeInfoAdapter = StoreInfoAdapter(
            nearMeList,
            activity,
            this,
            searchQuery!!.latitude.toDouble(),
            searchQuery!!.longitude.toDouble()
        )
        fragmentHomeNewBinding.rvRecentVisitsNearMe.adapter = storeInfoAdapter
    }

    private fun setFavouritesData(favouritesList: List<BizStoreElastic>) {
        val storeInfoAdapter = StoreInfoAdapter(
            favouritesList,
            activity,
            this,
            searchQuery!!.latitude.toDouble(),
            searchQuery!!.longitude.toDouble()
        )
        fragmentHomeNewBinding.rvRecentVisitsNearMe.adapter = storeInfoAdapter
        fragmentHomeNewBinding.pbRecentVisitsNearMe.visibility = View.GONE
    }

    private fun observeValues() {
        homeViewModel.searchStoreQueryLiveData.observe(viewLifecycleOwner, {
            it.searchedOnBusinessType = BusinessTypeEnum.ZZ
            searchQuery = it
            fragmentHomeNewBinding.pbRecentVisitsNearMe.visibility = View.VISIBLE
//            if (showRecentVisitsFirst) {
//                showRecentVisitsFirst = false
//                homeViewModel.fetchFavouritesRecentVisitList()
//            } else {
//                fragmentHomeNewBinding.tabNearMeRecentVisits.selectTab(
//                    fragmentHomeNewBinding.tabNearMeRecentVisits.getTabAt(
//                        1
//                    )
//                )
//            }
        })

        homeViewModel.nearMeResponse.observe(viewLifecycleOwner, { bizStoreElasticList ->
            bizStoreElasticList?.bizStoreElastics?.let {
                searchQuery?.let { searchStoreQueryVal ->
                    Collections.sort(
                        it,
                        SortPlaces(
                            GeoIP(
                                searchStoreQueryVal.latitude.toDouble(),
                                searchStoreQueryVal.longitude.toDouble()
                            )
                        )
                    )

                    nearMeList = it
                    val storeInfoAdapter = StoreInfoAdapter(
                        it,
                        activity,
                        this,
                        searchStoreQueryVal.latitude.toDouble(),
                        searchStoreQueryVal.longitude.toDouble()
                    )
                    fragmentHomeNewBinding.rvRecentVisitsNearMe.adapter = storeInfoAdapter
                    fragmentHomeNewBinding.pbRecentVisitsNearMe.visibility = View.GONE
                }
            }
        })

        /** Recent to be listed in order of last joined activity. Displays based on recently visited. */
        homeViewModel.favoritesListResponseLiveData.observe(viewLifecycleOwner, { favoriteElastic ->
            favoriteElastic?.favoriteSuggested?.let {
                searchQuery?.let { searchStoreQueryVal ->
                    favouritesList = it
                    val storeInfoAdapter = StoreInfoAdapter(
                        it,
                        activity,
                        this,
                        searchStoreQueryVal.latitude.toDouble(),
                        searchStoreQueryVal.longitude.toDouble()
                    )
                    fragmentHomeNewBinding.rvRecentVisitsNearMe.adapter = storeInfoAdapter
                    fragmentHomeNewBinding.pbRecentVisitsNearMe.visibility = View.GONE
//                    if (it.isNullOrEmpty()) {
//                        fragmentHomeNewBinding.tabNearMeRecentVisits.selectTab(
//                            fragmentHomeNewBinding.tabNearMeRecentVisits.getTabAt(
//                                1
//                            )
//                        )
//                    }
                }
            }
        })

        homeViewModel.nearMeErrorLiveData.observe(viewLifecycleOwner, {
            if (it) {
                homeViewModel.nearMeErrorLiveData.value = false
                fragmentHomeNewBinding.cvRecentVisits.visibility = View.GONE
                fragmentHomeNewBinding.pbRecentVisitsNearMe.visibility = View.GONE
            }
        })

        homeViewModel.currentTokenAndQueueListLiveData.observe(
            viewLifecycleOwner,
            { tokenAndQueuesList ->
                tokenAndQueuesList?.let {
                    if (it.isNullOrEmpty()) {
                        fragmentHomeNewBinding.cvTokens.visibility = View.GONE
                    } else {
                        fragmentHomeNewBinding.cvTokens.visibility = View.VISIBLE
                    }
                    tokenAndQueueAndQueueAdapter.addItems(it)

                    addIndicator(it, 0)
                }
            })

        homeViewModel.jsonScheduledAppointmentLiveData.observe(
            viewLifecycleOwner,
            { jsonScheduledAppointmentList ->
                jsonScheduledAppointmentList?.let {
                    if (it.isNullOrEmpty()) {
                        fragmentHomeNewBinding.cvAppointments.visibility = View.GONE
                    } else {
                        fragmentHomeNewBinding.cvAppointments.visibility = View.VISIBLE
                    }
                    scheduledAppointmentAdapter.addItems(it)

                    addAppointmentsIndicator(it, 0)
                }
            })

        homeViewModel.currentQueueErrorLiveData.observe(viewLifecycleOwner, {
            if (it) {
                fragmentHomeNewBinding.cvTokens.visibility = View.GONE
                showRecentVisitsFirst = false
                homeViewModel.currentQueueErrorLiveData.value = false
                fragmentHomeNewBinding.pbRecentVisitsNearMe.visibility = View.GONE
            }
        })

        homeViewModel.authenticationFailureLiveData.observe(viewLifecycleOwner, {
            if (it) {
                fragmentHomeNewBinding.cvTokens.visibility = View.GONE
                showRecentVisitsFirst = false
                homeViewModel.authenticationFailureLiveData.value = false
                fragmentHomeNewBinding.pbRecentVisitsNearMe.visibility = View.GONE
            }
        })

    }

    private fun setUpTabs() {
        fragmentHomeNewBinding.tabNearMeRecentVisits.removeAllTabs()

        val tabRecentVisits = fragmentHomeNewBinding.tabNearMeRecentVisits.newTab()
        tabRecentVisits.text = getString(R.string.tab_recent_visits)
        fragmentHomeNewBinding.tabNearMeRecentVisits.addTab(tabRecentVisits)

        val tabNearMe = fragmentHomeNewBinding.tabNearMeRecentVisits.newTab()
        tabNearMe.text = getString(R.string.tab_near_me)
        fragmentHomeNewBinding.tabNearMeRecentVisits.addTab(tabNearMe)
    }

    private fun setUpRecyclerView() {
        fragmentHomeNewBinding.rvRecentVisitsNearMe.setHasFixedSize(true)
        fragmentHomeNewBinding.rvRecentVisitsNearMe.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        fragmentHomeNewBinding.rvRecentVisitsNearMe.itemAnimator = DefaultItemAnimator()
    }

    private fun setUpViewPager() {
        tokenAndQueueAndQueueAdapter = TokenAndQueueAdapter(requireContext(), mutableListOf()) {
            onTokenClicked(it)
        }

        scheduledAppointmentAdapter =
            ScheduledAppointmentAdapter(requireContext(), mutableListOf()) {
                onAppointmentClicked(it)
            }

        fragmentHomeNewBinding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        fragmentHomeNewBinding.viewpager.adapter = tokenAndQueueAndQueueAdapter

        fragmentHomeNewBinding.viewpager.clipToPadding = false
        fragmentHomeNewBinding.viewpager.clipChildren = false
        fragmentHomeNewBinding.viewpager.offscreenPageLimit = 3
        fragmentHomeNewBinding.viewpager.getChildAt(0).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER

        fragmentHomeNewBinding.viewpagerAppointment.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        fragmentHomeNewBinding.viewpagerAppointment.adapter = scheduledAppointmentAdapter

        fragmentHomeNewBinding.viewpagerAppointment.clipToPadding = false
        fragmentHomeNewBinding.viewpagerAppointment.clipChildren = false
        fragmentHomeNewBinding.viewpagerAppointment.offscreenPageLimit = 3
        fragmentHomeNewBinding.viewpagerAppointment.getChildAt(0).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(32))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }

        val compositePageTransformerAppointments = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(54))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }

        fragmentHomeNewBinding.viewpager.setPageTransformer(compositePageTransformer)
        fragmentHomeNewBinding.viewpagerAppointment.setPageTransformer(compositePageTransformer)

        fragmentHomeNewBinding.viewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                homeViewModel.jsonScheduledAppointmentLiveData.observe(viewLifecycleOwner, {
                    addAppointmentsIndicator(it, position)
                })
            }
        })

        fragmentHomeNewBinding.viewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                homeViewModel.currentTokenAndQueueListLiveData.observe(viewLifecycleOwner, {
                    addIndicator(it, position)
                })
            }
        })
    }

    private fun onAppointmentClicked(jsonSchedule: JsonSchedule) {
        val intent = Intent(requireContext(), AppointmentDetailActivity::class.java)
        intent.putExtra(IBConstant.KEY_DATA_OBJECT, jsonSchedule)
        intent.putExtra(IBConstant.KEY_FROM_LIST, true)
        startActivity(intent);
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
                bundle.putString(
                    AppUtils.CURRENCY_SYMBOL,
                    AppUtils.getCurrencySymbol(jsonTokenAndQueue.countryShortName)
                )
                putExtras(bundle)
            }
            startActivity(intent)
        }
    }

    private fun addIndicator(tokenANdQueueList: List<JsonTokenAndQueue>, selectedPosition: Int) {
        fragmentHomeNewBinding.llIndicator.removeAllViews()
        tokenANdQueueList.forEachIndexed { index, _ ->
            val viewIndicatorBinding =
                ViewIndicatorBinding.inflate(LayoutInflater.from(requireContext()))
            if (index == selectedPosition) {
                viewIndicatorBinding.viewIndicator.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
            }
            fragmentHomeNewBinding.llIndicator.addView(viewIndicatorBinding.root)
        }
    }

    private fun addAppointmentsIndicator(
        jsonScheduleList: List<JsonSchedule>,
        selectedPosition: Int
    ) {
        fragmentHomeNewBinding.llAppointmentsIndicator.removeAllViews()
        jsonScheduleList.forEachIndexed { index, _ ->
            val viewIndicatorBinding =
                ViewIndicatorBinding.inflate(LayoutInflater.from(requireContext()))
            if (index == selectedPosition) {
                viewIndicatorBinding.viewIndicator.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_theme_color_select)
            }
            fragmentHomeNewBinding.llAppointmentsIndicator.addView(viewIndicatorBinding.root)
        }
    }

    override fun onStoreItemClick(item: BizStoreElastic?) {
        item?.let {
            NavigationBundleUtils.navigateToStore(requireActivity(), item)
        } ?: run {
            Log.d(HomeFragment::class.java.simpleName, "BizStoreElastic is null")
        }
    }
}

interface HomeFragmentInteractionListener {
    fun makeAnnouncement(jsonTextToSpeeches: List<JsonTextToSpeech?>, msgId: String)
    fun callReviewActivity(codeQr: String, token: String)
    fun navigateToPropertyRentalScreen()
    fun navigateToHouseholdItemScreen()
}
