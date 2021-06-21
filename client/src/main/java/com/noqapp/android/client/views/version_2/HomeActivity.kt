package com.noqapp.android.client.views.version_2

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.lifecycle.Observer
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
import com.noqapp.android.client.model.database.utils.NotificationDB
import com.noqapp.android.client.model.database.utils.ReviewDB
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery
import com.noqapp.android.client.utils.*
import com.noqapp.android.client.views.activities.*
import com.noqapp.android.client.views.adapters.DrawerExpandableListAdapter
import com.noqapp.android.client.views.customviews.BadgeDrawable
import com.noqapp.android.client.views.version_2.db.helper_models.ForegroundNotificationModel
import com.noqapp.android.client.views.version_2.fragments.HomeFragmentDirections
import com.noqapp.android.client.views.version_2.fragments.HomeFragmentInteractionListener
import com.noqapp.android.client.views.version_2.viewmodels.HomeViewModel
import com.noqapp.android.common.beans.DeviceRegistered
import com.noqapp.android.common.customviews.CustomToast
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech
import com.noqapp.android.common.model.types.MessageOriginEnum
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum
import com.noqapp.android.common.pojos.MenuDrawer
import com.noqapp.android.common.presenter.DeviceRegisterPresenter
import com.noqapp.android.common.utils.NetworkUtil
import com.noqapp.android.common.utils.PermissionUtils
import com.noqapp.android.common.utils.TextToSpeechHelper
import com.noqapp.android.common.views.activities.AppsLinksActivity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : LocationBaseActivity(), DeviceRegisterPresenter, SharedPreferences.OnSharedPreferenceChangeListener, HomeFragmentInteractionListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private val TAG = HomeActivity::class.java.simpleName

    override fun displayAddressOutput(addressOutput: String?, countryShortName: String?, area: String?, town: String?, district: String?, state: String?, stateShortName: String?, latitude: Double?, longitude: Double?) {
        activityHomeBinding.tvLocation.text = AppUtils.getLocationAsString(area, town)

        val searchStoreQuery = SearchStoreQuery()
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

        homeViewModel.searchStoreQueryLiveData.value = searchStoreQuery
    }

    private val menuDrawerItems = mutableListOf<MenuDrawer>()
    private lateinit var activityHomeBinding: ActivityHomeBinding
    private lateinit var navHeaderMainBinding: NavHeaderMainBinding
    private var expandableListAdapter: DrawerExpandableListAdapter? = null
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private var textToSpeechHelper: TextToSpeechHelper? = null
    private val cacheMsgIds = CacheBuilder.newBuilder().maximumSize(1).build<String, java.util.ArrayList<String>>()
    private val MSG_IDS = "messageIds"
    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[HomeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityHomeBinding = ActivityHomeBinding.inflate(LayoutInflater.from(this))
        setContentView(activityHomeBinding.root)
        setSupportActionBar(activityHomeBinding.toolbar)
        textToSpeechHelper = TextToSpeechHelper(applicationContext)

        setUpExpandableList(UserUtils.isLogin())
        updateNotificationBadgeCount()
        setUpNavigation()

        if (BuildConfig.DEBUG) {
            activityHomeBinding.llOldVersion.visibility = View.VISIBLE
        } else {
            activityHomeBinding.llOldVersion.visibility = View.GONE
        }

        addHeaderView()
        setListeners()
        updateDrawerUI()

        observeValues()
    }

    private fun addHeaderView() {
        navHeaderMainBinding = NavHeaderMainBinding.inflate(LayoutInflater.from(this))
        activityHomeBinding.expandableDrawerListView.addHeaderView(navHeaderMainBinding.root)
    }

    private fun observeValues() {
        homeViewModel.searchStoreQueryLiveData.observe(this, Observer {
            activityHomeBinding.tvLocation.text = it.cityName
        })

        homeViewModel.foregroundNotificationLiveData.observe(this, { foregroundNotification ->
            foregroundNotification?.let {
                handleBuzzer(foregroundNotification)
            }
        })

        homeViewModel.getReviewData(Constants.NotificationTypeConstant.FOREGROUND).observe(this, Observer {
            it?.let {
                if (it.isSkipped == "1") {
                    CustomToast().showToast(this, "You were skipped")
                    callSkipScreen(it.codeQR)
                } else if (it.isReviewShown != "1" && it.isSkipped != "1") {
                    callReviewActivity(it.codeQR, it.token)
                }
            }
        })

        homeViewModel.notificationListLiveData.observe(this, Observer {
            it?.let { displayNotificationList ->
                if (displayNotificationList.isNotEmpty()) {
                    val displayNotification = displayNotificationList.last()
                    if (!displayNotification.popUpShown) {
                        ShowAlertInformation.showInfoDisplayDialog(this, displayNotification.title, displayNotification.body)
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
        activityHomeBinding.llOldVersion.setOnClickListener {
            val intent = Intent(this, LaunchActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(AppInitialize.TOKEN_FCM, intent.getStringExtra(AppInitialize.TOKEN_FCM))
                putExtra("deviceId", intent.getStringExtra("deviceId"))
            }
            startActivity(intent)
            finish()
        }

        activityHomeBinding.tvLocation.setOnClickListener {
            navController.navigate(R.id.changeLocationFragment)
        }

        navHeaderMainBinding.root.setOnClickListener {
            if (UserUtils.isLogin()) {
                val intent = Intent(this, UserProfileActivity::class.java)
                startActivity(intent)
            } else {
                CustomToast().showToast(this, "Please login to view profile")
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
                        .load(AppUtils.getImageUrls(BuildConfig.PROFILE_BUCKET, AppInitialize.getUserProfileUri()))
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
        activityHomeBinding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    private fun updateNotificationBadgeCount() {
        val notifyCount = NotificationDB.getNotificationCount()
        expandableListAdapter?.notifyDataSetChanged()
        supportActionBar?.setHomeAsUpIndicator(setBadgeCount(this, R.drawable.ic_burger, notifyCount))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
    }

    private fun setUpExpandableList(isLogin: Boolean) {
        /* Fill menu items */
        menuDrawerItems.clear()
        if (isCountryIndia()) {
            val healthList: MutableList<MenuDrawer> = ArrayList()
            healthList.add(MenuDrawer(getString(R.string.medical_profiles), false, false, R.drawable.medical_profile))
            healthList.add(MenuDrawer(getString(R.string.medical_history), false, false, R.drawable.medical_history))
            menuDrawerItems.add(MenuDrawer(getString(R.string.health_care), true, true, R.drawable.health_care, healthList))
            menuDrawerItems.add(MenuDrawer(getString(R.string.appointments), true, false, R.drawable.appointment))
        }
        menuDrawerItems.add(MenuDrawer(getString(R.string.order_history), true, false, R.drawable.purchase_order))
        if (isLogin) {
            menuDrawerItems.add(MenuDrawer(getString(R.string.merchant_account), true, false, R.drawable.merchant_account))
            menuDrawerItems.add(MenuDrawer(getString(R.string.favourite), true, false, R.drawable.ic_favorite))
        }
        menuDrawerItems.add(MenuDrawer(getString(R.string.offers), true, false, R.drawable.offers))
        menuDrawerItems.add(MenuDrawer(getString(R.string.notification_setting), true, false, R.drawable.ic_notification))
        val settingList: MutableList<MenuDrawer> = ArrayList()
        settingList.add(MenuDrawer(getString(R.string.share), false, false, R.drawable.ic_menu_share))
        settingList.add(MenuDrawer(getString(R.string.invite), false, false, R.drawable.invite))
        settingList.add(MenuDrawer(getString(R.string.legal), false, false, R.drawable.legal))
        settingList.add(MenuDrawer(getString(R.string.ratetheapp), false, false, R.drawable.ic_star))
        settingList.add(MenuDrawer(getString(R.string.language_setting), false, false, R.drawable.language))
        if (isLogin) {
            settingList.add(MenuDrawer(getString(R.string.preference_settings), false, false, R.drawable.settings))
            settingList.add(MenuDrawer(getString(R.string.logout), false, false, R.drawable.ic_logout))
        }
        menuDrawerItems.add(MenuDrawer(getString(R.string.action_settings), true, true, R.drawable.settings_square, settingList))
        menuDrawerItems.add(MenuDrawer(getString(R.string.title_activity_contact_us), true, false, R.drawable.contact_us))
        if (!AppUtils.isRelease()) {
            menuDrawerItems.add(MenuDrawer(getString(R.string.noqueue_apps), true, false, R.drawable.apps))
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
                `in`.putExtra(IBConstant.KEY_URL, if (UserUtils.isLogin()) Constants.URL_MERCHANT_LOGIN else Constants.URL_MERCHANT_REGISTER)
                startActivity(`in`)
            } else {
                ShowAlertInformation.showNetworkDialog(this)
            }
            R.drawable.purchase_order -> {
                val `in` = Intent(this, OrderQueueHistoryActivity::class.java)
                startActivity(`in`)
            }
            R.drawable.ic_favorite -> {
                val `in` = Intent(this, FavouriteListActivity::class.java)
                startActivity(`in`)
            }
            R.id.nav_app_setting -> {
                val `in` = Intent(this, SettingsActivity::class.java)
                startActivity(`in`)
            }
            R.drawable.offers -> {
                if (UserUtils.isLogin()) {
                    val `in` = Intent(this, CouponsActivity::class.java)
                    startActivity(`in`)
                } else {
                    CustomToast().showToast(this, "Please login to see the details")
                }
            }
            R.drawable.settings -> {
                val `in` = Intent(this, PreferenceSettings::class.java)
                startActivity(`in`)
            }
            R.drawable.ic_notification -> {
                val `in` = Intent(this, NotificationActivity::class.java)
                startActivity(`in`)
            }
            R.drawable.ic_logout -> {
                val showDialog = ShowCustomDialog(this, true)
                showDialog.setDialogClickListener(object : ShowCustomDialog.DialogClickListener {
                    override fun btnPositiveClick() {
                        AppInitialize.clearPreferences()
                        NotificationDB.clearNotificationTable()
                        ReviewDB.clearReviewTable()
                        reCreateDeviceID(this@HomeActivity, this@HomeActivity)
                    }

                    override fun btnNegativeClick() {
                        //Do nothing
                    }
                })
                showDialog.displayDialog(getString(R.string.logout), getString(R.string.logout_msg))
            }
            R.drawable.medical_history -> {
                if (UserUtils.isLogin()) {
                    val `in` = Intent(this, MedicalHistoryActivity::class.java)
                    startActivity(`in`)
                } else {
                    CustomToast().showToast(this, "Please login to see the details")
                }
            }
            R.drawable.medical_profile -> {
                if (UserUtils.isLogin()) {
                    val `in` = Intent(this, AllUsersProfileActivity::class.java)
                    startActivity(`in`)
                } else {
                    CustomToast().showToast(this, "Please login to see the details")
                }
            }
            R.drawable.appointment -> {
                if (UserUtils.isLogin()) {
                    val `in` = Intent(this, AppointmentActivity::class.java)
                    startActivity(`in`)
                } else {
                    CustomToast().showToast(this, "Please login to see the details")
                }
            }
            R.drawable.language -> {
                val claIntent = Intent(this, ChangeLanguageActivity::class.java)
                startActivity(claIntent)
            }
            R.drawable.contact_us -> {
                val `in` = Intent(this, ContactUsActivity::class.java)
                startActivity(`in`)
            }
            R.drawable.apps -> {
                val `in` = Intent(this, AppsLinksActivity::class.java)
                startActivity(`in`)
            }
            R.drawable.ic_star -> AppUtils.openPlayStore(this)
            R.drawable.ic_menu_share ->                 // @TODO revert the permission changes when permission enabled in manifest
                if (PermissionUtils.isExternalStoragePermissionAllowed(this)) {
                    AppUtils.shareTheApp(this)
                } else {
                    PermissionUtils.requestStoragePermission(this)
                }
            R.drawable.legal -> {
                val `in` = Intent(this, PrivacyActivity::class.java)
                startActivity(`in`)
            }
            R.drawable.invite -> {
                val `in` = Intent(this, InviteActivity::class.java)
                startActivity(`in`)
            }
        }
    }

    private fun isCountryIndia(): Boolean {
        return LaunchActivity.COUNTRY_CODE.equals("India", ignoreCase = true) || LaunchActivity.COUNTRY_CODE.equals("IN", ignoreCase = true)
    }

    fun reCreateDeviceID(context: Activity, deviceRegisterPresenter: DeviceRegisterPresenter?) {
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
        val icon = ContextCompat.getDrawable(context, R.drawable.ic_badge_drawable) as LayerDrawable?
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
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    override fun deviceRegisterError() {

    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {

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
                navController.navigate(R.id.homeFragment)
                return true
            }
            R.id.menuNotification -> {
                navController.navigate(R.id.notificationFragment)
                return true
            }
            R.id.menuSearch -> {
                val homeFragmentDirections = HomeFragmentDirections.actionHomeToUnderDevelopmentFragmentDestination("Anything")
                navController.navigate(homeFragmentDirections)
                return true
            }
            R.id.menuFavourite -> {
                navController.navigate(R.id.favouritesFragment)
                return true
            }
            R.id.menuPost -> {
                val homeFragmentDirections = HomeFragmentDirections.actionHomeToUnderDevelopmentFragmentDestination("Anything")
                navController.navigate(homeFragmentDirections)
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

    private fun callReviewActivity(jsonTokenAndQueue: JsonTokenAndQueue?, codeQr: String, token: String) {
        if (jsonTokenAndQueue != null) {
            val reviewIntent = Intent(this, ReviewActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(IBConstant.KEY_DATA_OBJECT, jsonTokenAndQueue)
            reviewIntent.putExtras(bundle)
            startActivityForResult(reviewIntent, Constants.requestCodeJoinQActivity)
            Log.v("Review screen call: ", jsonTokenAndQueue.toString())
            val jsonTokenAndQueueArrayList = TokenAndQueueDB.getCurrentQueueObjectList(codeQR)
            if (jsonTokenAndQueueArrayList.size == 1) {
                /* Un-subscribe the topic. */
                FirebaseMessaging.getInstance().unsubscribeFromTopic(jsonTokenAndQueue.topic + "_A")
            }
        } else {
            homeViewModel.deleteReview(codeQr, token)
        }
    }
}