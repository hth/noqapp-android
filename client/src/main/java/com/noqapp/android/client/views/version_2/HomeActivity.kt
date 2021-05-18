package com.noqapp.android.client.views.version_2

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.ExpandableListView.OnChildClickListener
import android.widget.ExpandableListView.OnGroupClickListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityHomeBinding
import com.noqapp.android.client.model.database.utils.NotificationDB
import com.noqapp.android.client.model.database.utils.ReviewDB
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery
import com.noqapp.android.client.utils.*
import com.noqapp.android.client.views.activities.*
import com.noqapp.android.client.views.adapters.DrawerExpandableListAdapter
import com.noqapp.android.client.views.customviews.BadgeDrawable
import com.noqapp.android.client.views.version_2.fragments.HomeFragmentInteractionListener
import com.noqapp.android.client.views.version_2.viewmodels.HomeViewModel
import com.noqapp.android.common.beans.DeviceRegistered
import com.noqapp.android.common.customviews.CustomToast
import com.noqapp.android.common.pojos.MenuDrawer
import com.noqapp.android.common.presenter.DeviceRegisterPresenter
import com.noqapp.android.common.utils.NetworkUtil
import com.noqapp.android.common.utils.PermissionUtils
import com.noqapp.android.common.views.activities.AppsLinksActivity

class HomeActivity : LocationBaseActivity(), DeviceRegisterPresenter, SharedPreferences.OnSharedPreferenceChangeListener, HomeFragmentInteractionListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private val TAG = HomeActivity::class.java.simpleName

    override fun displayAddressOutput(addressOutput: String?, countryShortName: String?, area: String?, town: String?, district: String?, state: String?, stateShortName: String?, latitude: Double?, longitude: Double?) {
        activityHomeBinding.tvLocation.text = town

        val searchStoreQuery = SearchStoreQuery()
        area?.let {
            searchStoreQuery.cityName = town
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
    private var expandableListAdapter: DrawerExpandableListAdapter? = null
    private val fcmNotificationReceiver: LaunchActivity.FcmNotificationReceiver? = null
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[HomeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityHomeBinding = ActivityHomeBinding.inflate(LayoutInflater.from(this))
        setContentView(activityHomeBinding.root)
        setSupportActionBar(activityHomeBinding.toolbar)
        setUpExpandableList(UserUtils.isLogin())
        updateNotificationBadgeCount()
        setUpNavigation()

        setListeners()

        observeValues()
    }

    private fun observeValues() {
        homeViewModel.searchStoreQueryLiveData.observe(this, {
            activityHomeBinding.tvLocation.text = it.cityName
        })
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
        activityHomeBinding.expandableDrawerListView.setOnGroupClickListener(OnGroupClickListener { parent: ExpandableListView?, v: View?, groupPosition: Int, id: Long ->
            if (menuDrawerItems.get(groupPosition).isGroup()) {
                if (!menuDrawerItems.get(groupPosition).hasChildren()) {
                    val drawableId: Int = menuDrawerItems.get(groupPosition).getIcon()
                    menuClick(drawableId)
                    activityHomeBinding.drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            false
        })
        activityHomeBinding.expandableDrawerListView.setOnChildClickListener(OnChildClickListener { _: ExpandableListView?, v: View?, groupPosition: Int, childPosition: Int, _: Long ->
            val model: MenuDrawer = menuDrawerItems[groupPosition].childList[childPosition]
            val drawableId = model.icon
            menuClick(drawableId)
            activityHomeBinding.drawerLayout.closeDrawer(GravityCompat.START)
            false
        })
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
                return true
            }
            R.id.menuFavourite -> {
                navController.navigate(R.id.favouritesFragment)
                return true
            }
            R.id.menuPost -> {
                return true
            }
        }
        return false
    }
}