package com.noqapp.android.client.views.version_2

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.common.cache.CacheBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.noqapp.android.client.BuildConfig
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityHomeBinding
import com.noqapp.android.client.databinding.NavHeaderMainBinding
import com.noqapp.android.client.model.open.DeviceClientImpl
import com.noqapp.android.client.presenter.AppBlacklistPresenter
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.*
import com.noqapp.android.client.views.activities.*
import com.noqapp.android.client.views.adapters.DrawerExpandableListAdapter
import com.noqapp.android.client.views.customviews.BadgeDrawable
import com.noqapp.android.client.views.version_2.db.helper_models.ForegroundNotificationModel
import com.noqapp.android.client.views.version_2.fragments.HomeFragmentInteractionListener
import com.noqapp.android.client.views.version_2.market_place.householdItem.household_item_list.HouseholdItemListActivity
import com.noqapp.android.client.views.version_2.market_place.propertyRental.property_rental_details.PropertyRentalListActivity
import com.noqapp.android.client.views.version_2.viewmodels.HomeViewModel
import com.noqapp.android.common.beans.DeviceRegistered
import com.noqapp.android.common.beans.ErrorEncounteredJson
import com.noqapp.android.common.beans.JsonLatestAppVersion
import com.noqapp.android.common.customviews.CustomToast
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech
import com.noqapp.android.common.model.types.MessageOriginEnum
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum
import com.noqapp.android.common.pojos.MenuDrawer
import com.noqapp.android.common.presenter.DeviceRegisterListener
import com.noqapp.android.common.utils.NetworkUtil
import com.noqapp.android.common.utils.PermissionUtils
import com.noqapp.android.common.utils.TextToSpeechHelper
import com.noqapp.android.common.utils.Version
import com.noqapp.android.common.views.activities.AppUpdateActivity
import com.noqapp.android.common.views.activities.AppsLinksActivity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class HomeActivity : LocationBaseActivity(), DeviceRegisterListener,
    HomeFragmentInteractionListener,
    BottomNavigationView.OnNavigationItemSelectedListener, AppBlacklistPresenter {
    private val TAG = HomeActivity::class.java.simpleName

    companion object {
         var locationLatitude = 0.0
         var locationLongitude = 0.0
         var locationArea = ""
         var locationTown = ""
    }

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
        activityHomeBinding.tvLocation.text = AppUtils.getLocationAsString(area, town)
        locationArea = area ?: ""
        locationTown = town ?: ""
        val searchStoreQuery = SearchQuery()
        area?.let {
            searchStoreQuery.cityName = AppUtils.getLocationAsString(area, town)
        }
        latitude?.let {
            searchStoreQuery.latitude = it.toString()
            locationLatitude = it
        }
        longitude?.let {
            searchStoreQuery.longitude = it.toString()
            locationLongitude = it
        }
        searchStoreQuery.filters = ""
        searchStoreQuery.scrollId = ""

        homeViewModel.searchStoreQueryLiveData.value = searchStoreQuery
        this.searchQuery = searchStoreQuery
    }

    private val menuDrawerItems = mutableListOf<MenuDrawer>()
    private lateinit var activityHomeBinding: ActivityHomeBinding
    private lateinit var navHeaderMainBinding: NavHeaderMainBinding
    private var expandableListAdapter: DrawerExpandableListAdapter? = null
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private var textToSpeechHelper: TextToSpeechHelper? = null
    private var isRateUsFirstTime = true
    private var searchQuery: SearchQuery? = null
    private var checkIfAppIsSupported = true

    private val cacheMsgIds = CacheBuilder.newBuilder().maximumSize(1).build<String, ArrayList<String>>()
    private val MSG_IDS = "messageIds"

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        )[HomeViewModel::class.java]
    }

    override fun locationPermissionRequired() {
        activityHomeBinding.clLocationAccessRequired.visibility = View.VISIBLE
    }

    override fun locationPermissionGranted() {
        activityHomeBinding.clLocationAccessRequired.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppInitialize.setLocationChangedManually(false)
        activityHomeBinding = ActivityHomeBinding.inflate(LayoutInflater.from(this))
        setContentView(activityHomeBinding.root)
        setSupportActionBar(activityHomeBinding.toolbar)
        textToSpeechHelper = TextToSpeechHelper(applicationContext)

        setUpExpandableList(UserUtils.isLogin())

        if (checkIfAppIsSupported) {
            checkIfAppIsSupportedAnyMore()
        }

        updateNotificationBadgeCount()
        setUpNavigation()

        addHeaderView()
        setListeners()
        updateDrawerUI()

        showLoginScreen()

        observeValues()

        FirebaseMessaging.getInstance().token.addOnSuccessListener(this) { token: String? ->
            AppInitialize.setTokenFCM(token)
            reCreateDeviceID(this, this)
        }
    }

    /** Check if this current version of device is supported. */
    private fun checkIfAppIsSupportedAnyMore() {
        val deviceClientImpl = DeviceClientImpl()
        deviceClientImpl.setAppBlacklistPresenter(this)
        deviceClientImpl.isSupportedAppVersion()
    }

    private fun showLoginScreen() {
        if (AppInitialize.getShowHelper()) {
            activityHomeBinding.btnChangeLanguage.setOnClickListener {
                val claIntent = Intent(this, ChangeLanguageActivity::class.java)
                startActivity(claIntent)
                AppInitialize.setShowHelper(true)
            }
            activityHomeBinding.rlHelper.visibility = View.VISIBLE
            activityHomeBinding.btnSkip.setOnClickListener { v: View? ->
                activityHomeBinding.rlHelper.visibility = View.GONE
            }
            activityHomeBinding.btnLogin.setOnClickListener {
                activityHomeBinding.rlHelper.visibility = View.GONE
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            }
            AppInitialize.setShowHelper(false)
        } else {
            if (isRateUsFirstTime) {
                RateTheAppManager().appLaunched(this)
                isRateUsFirstTime = false
            }
        }
    }

    private fun addHeaderView() {
        navHeaderMainBinding = NavHeaderMainBinding.inflate(LayoutInflater.from(this))
        activityHomeBinding.expandableDrawerListView.addHeaderView(navHeaderMainBinding.root)
    }

    private fun observeValues() {
        homeViewModel.searchStoreQueryLiveData.observe(this, {
            activityHomeBinding.tvLocation.text = it.cityName
        })

        homeViewModel.foregroundNotificationLiveData.observe(this, { foregroundNotification ->
            foregroundNotification?.let {
                handleBuzzer(foregroundNotification)
            }
        })

        homeViewModel.getReviewData(Constants.NotificationTypeConstant.FOREGROUND)
            .observe(this, {
                it?.let {
                    if (it.isSkipped == "1") {
                        CustomToast().showToast(this, getString(R.string.txt_you_were_skipped))
                        callSkipScreen(it.codeQR)
                    } else if (it.isReviewShown != "1" && it.isSkipped != "1") {
                        callReviewActivity(it.codeQR, it.token)
                    }
                }
            })

        homeViewModel.notificationListLiveData.observe(this, {
            it?.let { displayNotificationList ->
                if (displayNotificationList.isNotEmpty()) {
                    val displayNotification = displayNotificationList.first()
                    if (!displayNotification.popUpShown) {
                        ShowAlertInformation.showInfoDisplayDialog(
                            this,
                            displayNotification.title,
                            displayNotification.body
                        )
                        displayNotification.popUpShown = true
                        homeViewModel.updateDisplayNotification(displayNotification)
                    }
                }
            }
        })
    }

    private fun callSkipScreen(codeQR: String?) {
        val skipIntent = Intent(this, BeforeJoinActivity::class.java)
        skipIntent.putExtra(IBConstant.KEY_CODE_QR, codeQR)
        skipIntent.putExtra(IBConstant.KEY_FROM_LIST, false)
        skipIntent.putExtra(IBConstant.KEY_IS_REJOIN, true)
        skipIntent.putExtra(IBConstant.KEY_IS_CATEGORY, false)
        startActivity(skipIntent)
    }

    private fun handleBuzzer(foregroundNotification: ForegroundNotificationModel) {
        if (foregroundNotification.currentServing == foregroundNotification.userCurrentToken) {
            if (MessageOriginEnum.valueOf(foregroundNotification.messageOrigin) == MessageOriginEnum.Q) {
                val blinkerIntent = Intent(this, BlinkerActivity::class.java)
                blinkerIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(blinkerIntent)

                if (AppInitialize.isMsgAnnouncementEnable()) {
                    if (foregroundNotification.jsonTextToSpeeches != null) {
                        makeAnnouncement(
                            foregroundNotification.jsonTextToSpeeches!!,
                            foregroundNotification.msgId
                        )
                    }
                }

            } else if (MessageOriginEnum.valueOf(foregroundNotification.messageOrigin) == MessageOriginEnum.O) {
                if (foregroundNotification.purchaseOrderStateEnum == PurchaseOrderStateEnum.RD || foregroundNotification.purchaseOrderStateEnum == PurchaseOrderStateEnum.RP) {
                    val blinkerIntent = Intent(this, BlinkerActivity::class.java)
                    blinkerIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(blinkerIntent)

                    if (AppInitialize.isMsgAnnouncementEnable()) {
                        if (foregroundNotification.jsonTextToSpeeches != null) {
                            makeAnnouncement(
                                foregroundNotification.jsonTextToSpeeches!!,
                                foregroundNotification.msgId
                            )
                        }
                    }
                }
            }
            homeViewModel.deleteForegroundNotification()
        }
    }

    private fun setListeners() {
        activityHomeBinding.tvLocation.setOnClickListener {
            navController.navigate(R.id.changeLocationFragment)
        }

        activityHomeBinding.btnAllowLocationAccess.setOnClickListener {
            requestPermissions()
        }

        navHeaderMainBinding.root.setOnClickListener {
            if (UserUtils.isLogin()) {
                val intent = Intent(this, UserProfileActivity::class.java)
                startActivity(intent)
            } else {
                CustomToast().showToast(this, getString(R.string.txt_please_login_to_view_profile))
                val loginIntent = Intent(this, LoginActivity::class.java)
                loginIntent.putExtra("fromHome", true)
                startActivity(loginIntent)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            if (it.getBooleanExtra("fromLogin", false)) {
                updateDrawerUI()
                setUpExpandableList(UserUtils.isLogin())
            }
        }
    }

    private fun updateDrawerUI() {
        if (UserUtils.isLogin()) {
            navHeaderMainBinding.tvEmail.text = AppInitialize.getActualMail()
            navHeaderMainBinding.tvName.text = AppInitialize.getUserName()
        } else {
            navHeaderMainBinding.tvEmail.text = getString(R.string.txt_please_login)
            navHeaderMainBinding.tvName.text = getString(R.string.txt_guest_user)
        }

        Picasso.get().load(ImageUtils.getProfilePlaceholder()).into(navHeaderMainBinding.ivProfile)
        try {
            if (!TextUtils.isEmpty(AppInitialize.getUserProfileUri())) {
                Picasso.get()
                    .load(
                        AppUtils.getImageUrls(
                            BuildConfig.PROFILE_BUCKET,
                            AppInitialize.getUserProfileUri()
                        )
                    )
                    .placeholder(ImageUtils.getProfilePlaceholder(this))
                    .error(ImageUtils.getProfileErrorPlaceholder(this))
                    .into(navHeaderMainBinding.ivProfile)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG, "Failed Update Drawer UI", e)
        }
    }

    private fun setUpNavigation() {
        navHostFragment = supportFragmentManager.findFragmentById(R.id.homeNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(activityHomeBinding.bottomNavigationView, navController)
        activityHomeBinding.bottomNavigationView.setOnItemSelectedListener(this)

        navHostFragment.childFragmentManager.addOnBackStackChangedListener {
            navController.currentDestination?.id?.let {
                when (it) {
                    R.id.favouritesFragment, R.id.action_notification_to_favourites -> activityHomeBinding.bottomNavigationView.menu.findItem(
                        R.id.menuFavourite
                    ).isChecked = true
                    R.id.notificationFragment, R.id.action_favourites_to_notification -> activityHomeBinding.bottomNavigationView.menu.findItem(
                        R.id.menuNotification
                    ).isChecked = true
                    R.id.homeFragment, R.id.action_favorites_to_home, R.id.action_notification_to_home -> activityHomeBinding.bottomNavigationView.menu.findItem(
                        R.id.menuHome
                    ).isChecked = true
                    else -> {
                    }
                }
            }
        }
    }

    private fun updateNotificationBadgeCount() {
        homeViewModel.notificationCountLiveData.observe(this, { nc ->
            nc?.let { notificationCount ->
                if (notificationCount > 0) {
                    activityHomeBinding.bottomNavigationView.getOrCreateBadge(R.id.menuNotification).number = notificationCount
                } else {
                    activityHomeBinding.bottomNavigationView.removeBadge(R.id.menuNotification)
                }

                navController.currentDestination?.id?.let {
                    when (it) {
                        R.id.notificationFragment, R.id.action_favourites_to_notification -> {
                            activityHomeBinding.bottomNavigationView.removeBadge(R.id.menuNotification)
                        }
                    }
                }
            }
        })

        expandableListAdapter?.notifyDataSetChanged()
        supportActionBar?.setHomeAsUpIndicator(
            setBadgeCount(
                this,
                R.drawable.ic_burger,
                0
            )
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
    }

    private fun setUpExpandableList(isLogin: Boolean) {
        /* Fill menu items */
        menuDrawerItems.clear()
        if (isCountryIndia()) {
            val healthList: MutableList<MenuDrawer> = ArrayList()
            healthList.add(
                MenuDrawer(
                    getString(R.string.medical_profiles),
                    false,
                    false,
                    R.drawable.medical_profile
                )
            )
            healthList.add(
                MenuDrawer(
                    getString(R.string.medical_history),
                    false,
                    false,
                    R.drawable.medical_history
                )
            )
            menuDrawerItems.add(
                MenuDrawer(
                    getString(R.string.health_care),
                    true,
                    true,
                    R.drawable.health_care,
                    healthList
                )
            )
            menuDrawerItems.add(
                MenuDrawer(
                    getString(R.string.txt_appointments),
                    true,
                    false,
                    R.drawable.appointment
                )
            )
        }
        menuDrawerItems.add(
            MenuDrawer(
                getString(R.string.order_history),
                true,
                false,
                R.drawable.purchase_order
            )
        )
        if (isLogin) {
            menuDrawerItems.add(
                MenuDrawer(
                    getString(R.string.merchant_account),
                    true,
                    false,
                    R.drawable.merchant_account
                )
            )
        }
        menuDrawerItems.add(MenuDrawer(getString(R.string.offers), true, false, R.drawable.offers))
        val settingList: MutableList<MenuDrawer> = ArrayList()
        settingList.add(
            MenuDrawer(
                getString(R.string.share),
                false,
                false,
                R.drawable.ic_menu_share
            )
        )
        settingList.add(MenuDrawer(getString(R.string.invite), false, false, R.drawable.invite))
        settingList.add(MenuDrawer(getString(R.string.legal), false, false, R.drawable.legal))
        settingList.add(
            MenuDrawer(
                getString(R.string.ratetheapp),
                false,
                false,
                R.drawable.ic_star
            )
        )
        settingList.add(
            MenuDrawer(
                getString(R.string.language_setting),
                false,
                false,
                R.drawable.language
            )
        )
        if (isLogin) {
            settingList.add(
                MenuDrawer(
                    getString(R.string.preference_settings),
                    false,
                    false,
                    R.drawable.settings
                )
            )
            settingList.add(
                MenuDrawer(
                    getString(R.string.logout),
                    false,
                    false,
                    R.drawable.ic_logout
                )
            )
        }
        menuDrawerItems.add(
            MenuDrawer(
                getString(R.string.action_settings),
                true,
                true,
                R.drawable.settings_square,
                settingList
            )
        )
        menuDrawerItems.add(
            MenuDrawer(
                getString(R.string.title_activity_contact_us),
                true,
                false,
                R.drawable.contact_us
            )
        )
        if (!AppUtils.isRelease()) {
            menuDrawerItems.add(
                MenuDrawer(
                    getString(R.string.noqueue_apps),
                    true,
                    false,
                    R.drawable.apps
                )
            )
        }
        expandableListAdapter = DrawerExpandableListAdapter(this, menuDrawerItems)
        activityHomeBinding.expandableDrawerListView.setAdapter(expandableListAdapter)
        if (activityHomeBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            activityHomeBinding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        activityHomeBinding.expandableDrawerListView.setOnGroupClickListener { parent: ExpandableListView?, v: View?, groupPosition: Int, id: Long ->
            if (menuDrawerItems.get(groupPosition).isGroup()) {
                if (!menuDrawerItems.get(groupPosition).hasChildren()) {
                    val drawableId: Int = menuDrawerItems.get(groupPosition).getIcon()
                    menuClick(drawableId)
                    activityHomeBinding.drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            false
        }
        activityHomeBinding.expandableDrawerListView.setOnChildClickListener { _: ExpandableListView?, v: View?, groupPosition: Int, childPosition: Int, _: Long ->
            val model: MenuDrawer = menuDrawerItems[groupPosition].childList[childPosition]
            val drawableId = model.icon
            menuClick(drawableId)
            activityHomeBinding.drawerLayout.closeDrawer(GravityCompat.START)
            false
        }
    }

    private fun menuClick(drawable: Int) {
        when (drawable) {
            R.drawable.merchant_account -> if (isOnline) {
                val `in` = Intent(this, WebViewActivity::class.java)
                `in`.putExtra(
                    IBConstant.KEY_URL,
                    if (UserUtils.isLogin()) Constants.URL_MERCHANT_LOGIN else Constants.URL_MERCHANT_REGISTER
                )
                startActivity(`in`)
            } else {
                ShowAlertInformation.showNetworkDialog(this)
            }
            R.drawable.purchase_order -> {
                startActivity(Intent(this, OrderQueueHistoryActivity::class.java))
            }
            R.drawable.ic_favorite -> {
                startActivity(Intent(this, FavouriteListActivity::class.java))
            }
            R.id.nav_app_setting -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.drawable.offers -> {
                navigateToScreenAfterLogin(CouponsActivity::class.java)
            }
            R.drawable.settings -> {
                navigateToScreenAfterLogin(PreferenceSettings::class.java)
            }
            R.drawable.ic_notification -> {
                navController.navigate(R.id.notificationFragment)
            }
            R.drawable.ic_logout -> {
                val showDialog = ShowCustomDialog(this, true)
                showDialog.setDialogClickListener(object : ShowCustomDialog.DialogClickListener {
                    override fun btnPositiveClick() {
                        AppInitialize.clearPreferences()
                        homeViewModel.clearTokenAndQueue()
                        homeViewModel.clearForegroundNotifications()
                        homeViewModel.clearReviewData()
                        reCreateDeviceID(this@HomeActivity, this@HomeActivity)
                    }

                    override fun btnNegativeClick() {
                        //Do nothing
                    }
                })
                showDialog.displayDialog(getString(R.string.logout), getString(R.string.logout_msg))
            }
            R.drawable.medical_history -> {
                navigateToScreenAfterLogin(MedicalHistoryActivity::class.java)
            }
            R.drawable.medical_profile -> {
                navigateToScreenAfterLogin(AllUsersProfileActivity::class.java)
            }
            R.drawable.appointment -> {
                navigateToScreenAfterLogin(AppointmentActivity::class.java)
            }
            R.drawable.language -> {
                startActivity(Intent(this, ChangeLanguageActivity::class.java))
            }
            R.drawable.contact_us -> {
                startActivity(Intent(this, ContactUsActivity::class.java))
            }
            R.drawable.apps -> {
                startActivity(Intent(this, AppsLinksActivity::class.java))
            }
            R.drawable.ic_star -> AppUtils.openPlayStore(this)
            R.drawable.ic_menu_share ->                 // @TODO revert the permission changes when permission enabled in manifest
                if (PermissionUtils.isExternalStoragePermissionAllowed(this)) {
                    AppUtils.shareTheApp(this)
                } else {
                    PermissionUtils.requestStoragePermission(this)
                }
            R.drawable.legal -> {
                startActivity(Intent(this, PrivacyActivity::class.java))
            }
            R.drawable.invite -> {
                startActivity(Intent(this, InviteActivity::class.java))
            }
        }
    }

    private fun isCountryIndia(): Boolean {
        return true
    }

    fun reCreateDeviceID(context: Activity, deviceRegisterPresenter: DeviceRegisterListener?) {
        if (NetworkUtil(context).isOnline) {
            AppInitialize.fetchDeviceId(deviceRegisterPresenter)
        } else {
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            builder.setTitle(null)
            val customDialogView = inflater.inflate(R.layout.dialog_general, null, false)
            val tvTitle = customDialogView.findViewById<TextView>(R.id.tvtitle)
            val tvMsg = customDialogView.findViewById<TextView>(R.id.tvMsg)
            tvTitle.text = context.getString(R.string.networkerror)
            tvMsg.text = context.getString(R.string.offline)
            builder.setView(customDialogView)
            val mAlertDialog = builder.create()
            mAlertDialog.setCanceledOnTouchOutside(false)
            val btnYes = customDialogView.findViewById<Button>(R.id.btn_yes)
            btnYes.setOnClickListener { v: View? ->
                mAlertDialog.dismiss()
                context.finish()
            }
            mAlertDialog.show()
            Log.w(TAG, "No network found")
        }
    }

    private fun setBadgeCount(context: Context, res: Int, badgeCount: Int): Drawable? {
        val icon =
            ContextCompat.getDrawable(context, R.drawable.ic_badge_drawable) as LayerDrawable?
        val mainIcon = ContextCompat.getDrawable(context, res)
        val badge = BadgeDrawable(context)
        badge.setCount(badgeCount.toString())
        icon!!.mutate()
        icon.setDrawableByLayerId(R.id.ic_badge, badge)
        icon.setDrawableByLayerId(R.id.ic_main_icon, mainIcon)
        return icon
    }

    override fun deviceRegisterResponse(deviceRegistered: DeviceRegistered?) {
        /* dismissProgress(); no progress bar silent call here */
        AppInitialize.processRegisterDeviceIdResponse(deviceRegistered, this)
        updateDrawerUI()
    }

    override fun deviceRegisterError() {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (activityHomeBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                activityHomeBinding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                activityHomeBinding.drawerLayout.openDrawer(GravityCompat.START)
            }

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (activityHomeBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            activityHomeBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuHome -> {
                navController.currentDestination?.id?.let {
                    when (it) {
                        R.id.favouritesFragment, R.id.action_notification_to_favourites -> {
                            navController.navigate(R.id.action_favorites_to_home)
                        }
                        R.id.notificationFragment, R.id.action_favourites_to_notification -> {
                            navController.navigate(R.id.action_notification_to_home)
                        }
                        else -> navController.navigate(R.id.homeFragment)
                    }
                }
                return true
            }
            R.id.menuNotification -> {
                navController.currentDestination?.id?.let {
                    when (it) {
                        R.id.favouritesFragment, R.id.action_notification_to_favourites -> {
                            navController.navigate(R.id.action_favourites_to_notification)
                        }
                        else -> navController.navigate(R.id.notificationFragment)
                    }
                }

                homeViewModel.viewModelScope.launch(Dispatchers.IO) {
                    val notificationsList = homeViewModel.getNotifications()
                    notificationsList.forEach {
                        it.status = Constants.KEY_READ
                        homeViewModel.updateDisplayNotification(it)
                    }
                }

                return true
            }
            R.id.menuSearch -> {
                searchQuery?.let {
                    NavigationBundleUtils.navigateToSearch(this, it)
                }
                return false
            }
            R.id.menuFavourite -> {
                navController.currentDestination?.id?.let {
                    when (it) {
                        R.id.notificationFragment, R.id.action_favourites_to_notification -> {
                            navController.navigate(R.id.action_notification_to_favourites)
                        }
                        else -> navController.navigate(R.id.favouritesFragment)
                    }
                }
                return true
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.requestCodeJoinQActivity && resultCode == Activity.RESULT_OK) {
            val codeQr = data?.getStringExtra(Constants.QRCODE)
            val token = data?.getStringExtra(Constants.TOKEN)
            homeViewModel.deleteReview(codeQr, token)
            homeViewModel.deleteToken(codeQr, token?.toInt())
        }
    }

    override fun makeAnnouncement(jsonTextToSpeeches: List<JsonTextToSpeech?>, msgId: String) {
        if (null == cacheMsgIds.getIfPresent(MSG_IDS)) {
            cacheMsgIds.put(MSG_IDS, java.util.ArrayList<String>())
        }
        cacheMsgIds.getIfPresent(MSG_IDS)?.let { msgIds ->
            if (!TextUtils.isEmpty(msgId) && !msgIds.contains(msgId)) {
                msgIds.add(msgId)
                cacheMsgIds.put(MSG_IDS, msgIds)
                textToSpeechHelper?.makeAnnouncement(jsonTextToSpeeches)
            }
        }
    }

    override fun callReviewActivity(codeQr: String, token: String) {
        try {
            homeViewModel.viewModelScope.launch {
                val jsonTokenAndQueueCurrent = homeViewModel.getCurrentQueueObject(codeQr, token)
                val jsonTokenAndQueueHistory = homeViewModel.getHistoryQueueObject(codeQr, token)
                jsonTokenAndQueueCurrent?.let {
                    callReviewActivity(it, codeQr, token)
                } ?: run {
                    callReviewActivity(jsonTokenAndQueueHistory, codeQr, token)
                }
            }
        } catch (e: java.lang.Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun navigateToPropertyRentalScreen() {
        navigateToScreenAfterLogin(PropertyRentalListActivity::class.java)
    }

    override fun navigateToHouseholdItemScreen() {
        navigateToScreenAfterLogin(HouseholdItemListActivity::class.java)
    }

    private fun navigateToScreenAfterLogin(navigateToActivity: Class<*>? ) {
        if (UserUtils.isLogin()) {
            startActivity(Intent(this, navigateToActivity))
        } else {
            CustomToast().showToast(
                this,
                getString(R.string.txt_please_login_to_see_the_details)
            )
        }
    }

    private fun callReviewActivity(
        jsonTokenAndQueue: JsonTokenAndQueue?,
        codeQr: String,
        token: String
    ) {
        if (jsonTokenAndQueue != null) {
            val reviewIntent = Intent(this, ReviewActivity::class.java)
            reviewIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            val bundle = Bundle()
            bundle.putSerializable(IBConstant.KEY_DATA_OBJECT, jsonTokenAndQueue)
            reviewIntent.putExtras(bundle)
            startActivityForResult(reviewIntent, Constants.requestCodeJoinQActivity)
            Log.v("Review screen call: ", jsonTokenAndQueue.toString())
            homeViewModel.viewModelScope.launch(Dispatchers.IO) {
                val jsonTokenAndQueueArrayList = homeViewModel.getCurrentQueueObjectList(codeQR)
                if (jsonTokenAndQueueArrayList?.size == 1) {
                    FirebaseMessaging.getInstance()
                        .unsubscribeFromTopic(jsonTokenAndQueue.topic + "_A")
                }
            }
        } else {
            homeViewModel.deleteReview(codeQr, token)
        }
    }

    override fun appBlacklistError(eej: ErrorEncounteredJson?) {
        eej?.let {
            if (MobileSystemErrorCodeEnum.valueOf(eej.systemError) == MobileSystemErrorCodeEnum.MOBILE_UPGRADE) {
                val intent = Intent(this, AppUpdateActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                ErrorResponseHandler().processError(this, eej)
            }
        }
    }

    override fun appBlacklistResponse(jsonLatestAppVersion: JsonLatestAppVersion?) {
        if (null != jsonLatestAppVersion && !TextUtils.isEmpty(jsonLatestAppVersion.latestAppVersion)) {
            checkIfAppIsSupported = false
            try {
                val appVersion = Version(Constants.appVersion())
                val serverSupportedVersion = Version(jsonLatestAppVersion.latestAppVersion)
                if (appVersion.compareTo(serverSupportedVersion) < 0) {
                    ShowAlertInformation.showThemePlayStoreDialog(
                        this,
                        getString(R.string.playstore_update_title),
                        getString(R.string.playstore_update_msg),
                        true
                    )
                }
            } catch (e: java.lang.Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.e(
                    HomeActivity::class.java.simpleName,
                    "Compare version check reason=" + e.localizedMessage,
                    e
                )
            }
        }
    }
}
