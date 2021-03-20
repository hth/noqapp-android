package com.noqapp.android.client.views.version_2

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.ExpandableListView.OnChildClickListener
import android.widget.ExpandableListView.OnGroupClickListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.google.common.cache.CacheBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ActivityHomeBinding
import com.noqapp.android.client.model.database.DatabaseTable
import com.noqapp.android.client.model.database.utils.NotificationDB
import com.noqapp.android.client.model.database.utils.ReviewDB
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB
import com.noqapp.android.client.model.fcm.JsonClientTokenAndQueueData
import com.noqapp.android.client.network.NoQueueMessagingService
import com.noqapp.android.client.utils.*
import com.noqapp.android.client.views.activities.*
import com.noqapp.android.client.views.adapters.DrawerExpandableListAdapter
import com.noqapp.android.client.views.customviews.BadgeDrawable
import com.noqapp.android.common.beans.DeviceRegistered
import com.noqapp.android.common.customviews.CustomToast
import com.noqapp.android.common.fcm.data.*
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech
import com.noqapp.android.common.model.types.BusinessTypeEnum
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum
import com.noqapp.android.common.model.types.MessageOriginEnum
import com.noqapp.android.common.model.types.QueueUserStateEnum
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum
import com.noqapp.android.common.pojos.MenuDrawer
import com.noqapp.android.common.presenter.DeviceRegisterPresenter
import com.noqapp.android.common.utils.NetworkUtil
import com.noqapp.android.common.utils.PermissionUtils
import com.noqapp.android.common.views.activities.AppsLinksActivity
import org.apache.commons.lang3.StringUtils

class HomeActivity : LocationBaseActivity(), DeviceRegisterPresenter {

    override fun displayAddressOutput(addressOutput: String?, latitude: Double?, longitude: Double?) {

    }

    private val menuDrawerItems = mutableListOf<MenuDrawer>()
    private lateinit var binding: ActivityHomeBinding
    private val TAG = HomeActivity::class.java.simpleName
    private var expandableListAdapter: DrawerExpandableListAdapter? = null
    private val cacheMsgIds = CacheBuilder.newBuilder().maximumSize(1).build<String, java.util.ArrayList<String>>()
    private val MSG_IDS = "messageIds"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(LayoutInflater.from(this))
        val view = binding.root
        setContentView(view)
        setUpExpandableList(UserUtils.isLogin())
    }

    override fun onResume() {
        super.onResume()
        LaunchActivity.languagePref.registerOnSharedPreferenceChangeListener(this)
        updateNotificationBadgeCount()
        setUpExpandableList(UserUtils.isLogin())
        updateDrawerUI()

        /* Register new push message receiver by doing this, the activity will be notified each time a new message arrives. */
        if (null != fcmNotificationReceiver) {
            fcmNotificationReceiver.register(this, IntentFilter(Constants.PUSH_NOTIFICATION))
        }

        /* Clear the notification area when the app is opened */NoQueueMessagingService.clearNotifications(applicationContext)
        val reviewData = ReviewDB.getPendingReview()
        /* Shown only one time if the review is canceled */if (StringUtils.isNotBlank(reviewData.codeQR) && !AppInitialize.isReviewShown() && !AppInitialize.getShowHelper()) {
            callReviewActivity(reviewData.codeQR, reviewData.token)
            Log.d("onResume review screen", "review screen called")
        }
        val reviewDataSkip = ReviewDB.getSkippedQueue()
        /* Shown only one time if it is skipped */if (StringUtils.isNotBlank(reviewDataSkip.codeQR)) {
            ReviewDB.deleteReview(reviewData.codeQR, reviewData.token)
            CustomToast().showToast(this, "You were skipped")
        }
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
        binding.expandableDrawerListView.setAdapter(expandableListAdapter)
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        binding.expandableDrawerListView.setOnGroupClickListener(OnGroupClickListener { parent: ExpandableListView?, v: View?, groupPosition: Int, id: Long ->
            if (menuDrawerItems.get(groupPosition).isGroup()) {
                if (!menuDrawerItems.get(groupPosition).isHasChildren()) {
                    val drawableId: Int = menuDrawerItems.get(groupPosition).getIcon()
                    menuClick(drawableId)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            false
        })
        binding.expandableDrawerListView.setOnChildClickListener(OnChildClickListener { _: ExpandableListView?, v: View?, groupPosition: Int, childPosition: Int, _: Long ->
            val model: MenuDrawer = menuDrawerItems[groupPosition].childList[childPosition]
            val drawableId = model.icon
            menuClick(drawableId)
            binding.drawerLayout.closeDrawer(GravityCompat.START)
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
            val tvMsg = customDialogView.findViewById<TextView>(R.id.tv_msg)
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

    @SuppressLint("LongLogTag")
    fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == Constants.PUSH_NOTIFICATION) {
            // new push notification is received
            val payload = intent.getStringExtra(Constants.FIREBASE_TYPE)
            val codeQR = intent.getStringExtra(Constants.CODE_QR)
            Log.d(LaunchActivity.TAG, "payload=$payload codeQR=$codeQR")
            val jsonData = intent.getSerializableExtra("jsonData") as JsonData?
            if (jsonData is JsonTopicQueueData) {
                Log.e("onReceiveJsonTopicQdata", jsonData.toString())
            } else if (jsonData is JsonClientData) {
                Log.e("onReceiveJsonClientData", jsonData.toString())
            } else if (jsonData is JsonAlertData) {
                Log.e("onReceiveJsonAlertData", jsonData.toString())
            } else if (jsonData is JsonTopicOrderData) {
                Log.e("onReceiveJsonTopicOdata", jsonData.toString())
            } else if (jsonData is JsonClientTokenAndQueueData) {
                Log.e("JsonClientTokenAndQData", jsonData.toString())
            } else if (jsonData is JsonClientOrderData) {
                Log.e("JsonClientOrderData", jsonData.toString())
            } else if (jsonData is JsonChangeServiceTimeData) {
                Log.e("JsonChangeServiceTimeData", jsonData.toString())
            } else if (jsonData is JsonTopicAppointmentData) {
                Log.e("JsonTopicAppointData", jsonData.toString())
                NotificationDB.insertNotification(
                        NotificationDB.KEY_NOTIFY,
                        "",
                        jsonData.getBody(),
                        jsonData.getTitle(),
                        BusinessTypeEnum.PA.getName(),
                        jsonData.getImageURL())
            } else if (jsonData is JsonMedicalFollowUp) {
                Log.e("JsonMedicalFollowUp", jsonData.toString())
                NotificationDB.insertNotification(
                        NotificationDB.KEY_NOTIFY,
                        jsonData.codeQR,
                        jsonData.getBody(),
                        jsonData.getTitle(),
                        BusinessTypeEnum.PA.getName(),
                        jsonData.getImageURL())
            }
            if (StringUtils.isNotBlank(payload) && payload.equals(FirebaseMessageTypeEnum.P.getName(), ignoreCase = true)) {
                if (jsonData is JsonAlertData) {
                    /* Executed when app is in foreground. */
                    NotificationDB.insertNotification(
                            NotificationDB.KEY_NOTIFY,
                            jsonData.codeQR,
                            jsonData.getLocalLanguageMessageBody(LaunchActivity.language),
                            jsonData.getTitle(),
                            jsonData.businessType.getName(),
                            jsonData.getImageURL())

                    //Show some meaningful msg to the end user
                    ShowAlertInformation.showInfoDisplayDialog(this@LaunchActivity, jsonData.getTitle(), jsonData.getLocalLanguageMessageBody(LaunchActivity.language))
                    updateNotificationBadgeCount()
                } else if (jsonData is JsonClientData) {
                    val token = jsonData.token.toString()
                    val qid = jsonData.queueUserId
                    if (jsonData.queueUserState.getName().equals(QueueUserStateEnum.S.getName(), ignoreCase = true)) {
                        /*
                             * Save codeQR of review & show the review screen on app
                             * resume if there is any record in Review DB for queue review key
                             */
                        val reviewData = ReviewDB.getValue(codeQR, token)
                        if (null != reviewData) {
                            val cv = ContentValues()
                            cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, 1)
                            ReviewDB.updateReviewRecord(codeQR, token, cv)
                            /* update */
                        } else {
                            /* insert */
                            val cv = ContentValues()
                            cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, 1)
                            cv.put(DatabaseTable.Review.CODE_QR, codeQR)
                            cv.put(DatabaseTable.Review.TOKEN, token)
                            cv.put(DatabaseTable.Review.QID, qid)
                            cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1")
                            cv.put(DatabaseTable.Review.KEY_SKIP, "-1")
                            cv.put(DatabaseTable.Review.KEY_GOTO, "")
                            ReviewDB.insert(cv)
                        }

                        // Clear the App Shared Preferences entry for this queue
                        val prefs = applicationContext.getSharedPreferences(Constants.APP_PACKAGE, Context.MODE_PRIVATE)
                        prefs.edit().remove(String.format(Constants.ESTIMATED_WAIT_TIME_PREF_KEY, codeQR)).apply()
                        prefs.edit().remove(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, codeQR)).apply()
                        callReviewActivity(codeQR, token)
                        /* this code is added to close the join & after join screen if the request is processed */if (AppInitialize.activityCommunicator != null) {
                            AppInitialize.activityCommunicator.requestProcessed(codeQR, token)
                        }
                    } else if (jsonData.queueUserState.getName().equals(QueueUserStateEnum.N.getName(), ignoreCase = true)) {
                        val reviewData = ReviewDB.getValue(codeQR, token)
                        if (null != reviewData) {
                            val cv = ContentValues()
                            cv.put(DatabaseTable.Review.KEY_SKIP, 1)
                            ReviewDB.updateReviewRecord(codeQR, token, cv)
                            /* update */
                        } else {
                            /* insert */
                            val cv = ContentValues()
                            cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, -1)
                            cv.put(DatabaseTable.Review.CODE_QR, codeQR)
                            cv.put(DatabaseTable.Review.TOKEN, token)
                            cv.put(DatabaseTable.Review.QID, qid)
                            cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1")
                            cv.put(DatabaseTable.Review.KEY_SKIP, "-1")
                            cv.put(DatabaseTable.Review.KEY_GOTO, "")
                            ReviewDB.insert(cv)
                        }
                        callSkipScreen(codeQR, token, qid)
                    }
                } else if (jsonData is JsonClientOrderData) {
                    val token = jsonData.orderNumber.toString()
                    val qid = jsonData.queueUserId
                    if (jsonData.purchaseOrderState.getName().equals(PurchaseOrderStateEnum.OD.getName(), ignoreCase = true)) {
                        /*
                             * Save codeQR of review & show the review screen on app
                             * resume if there is any record in Review DB for queue review key
                             */
                        val reviewData = ReviewDB.getValue(codeQR, token)
                        if (null != reviewData) {
                            val cv = ContentValues()
                            cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, 1)
                            ReviewDB.updateReviewRecord(codeQR, token, cv)
                            /* update */
                        } else {
                            /* insert */
                            val cv = ContentValues()
                            cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, 1)
                            cv.put(DatabaseTable.Review.CODE_QR, codeQR)
                            cv.put(DatabaseTable.Review.TOKEN, token)
                            cv.put(DatabaseTable.Review.QID, qid)
                            cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1")
                            cv.put(DatabaseTable.Review.KEY_SKIP, "-1")
                            cv.put(DatabaseTable.Review.KEY_GOTO, "")
                            ReviewDB.insert(cv)
                        }
                        callReviewActivity(codeQR, token)
                        /*
                             * this code is added to close the join & after join screen if the request is processed
                             * Update the order screen/ Join Screen if open
                             */if (AppInitialize.activityCommunicator != null) {
                            AppInitialize.activityCommunicator.requestProcessed(codeQR, token)
                        }
                    }
                } else if (jsonData is JsonTopicOrderData) {
                    updateNotification(jsonData, codeQR)
                } else if (jsonData is JsonTopicQueueData) {
                    updateNotification(jsonData, codeQR)
                } else if (jsonData is JsonClientTokenAndQueueData) {
                    val jsonTokenAndQueueList = jsonData.tokenAndQueues
                    if (null != jsonTokenAndQueueList && jsonTokenAndQueueList.size > 0) {
                        TokenAndQueueDB.saveCurrentQueue(jsonTokenAndQueueList)
                    }
                    NotificationDB.insertNotification(
                            NotificationDB.KEY_NOTIFY,
                            jsonData.codeQR,
                            jsonData.getBody(),
                            jsonData.getTitle(),
                            BusinessTypeEnum.PA.getName(),
                            jsonData.getImageURL())
                    for (i in jsonTokenAndQueueList!!.indices) {
                        NoQueueMessagingService.subscribeTopics(jsonTokenAndQueueList[i].topic)
                    }
                    updateNotificationBadgeCount()
                    if (null != homeFragment) {
                        homeFragment.fetchCurrentAndHistoryList()
                    }
                }
            } else if (StringUtils.isNotBlank(payload) && payload.equals(FirebaseMessageTypeEnum.C.getName(), ignoreCase = true)) {
                if (jsonData is JsonAlertData) {
                    NotificationDB.insertNotification(
                            NotificationDB.KEY_NOTIFY,
                            jsonData.codeQR,
                            jsonData.getBody(),
                            jsonData.getTitle(),
                            jsonData.businessType.getName(),
                            jsonData.getImageURL())
                    /* Show some meaningful msg to the end user */ShowAlertInformation.showInfoDisplayDialog(this@LaunchActivity, jsonData.getTitle(), jsonData.getLocalLanguageMessageBody(LaunchActivity.language))
                    updateNotificationBadgeCount()
                } else if (jsonData is JsonChangeServiceTimeData) {
                    val jsonTokenAndQueue = TokenAndQueueDB.findByQRCode(jsonData.codeQR)
                    if (null != jsonTokenAndQueue) {
                        val jsonQueueChangeServiceTimes = jsonData.jsonQueueChangeServiceTimes
                        for (jsonQueueChangeServiceTime in jsonQueueChangeServiceTimes) {
                            if (jsonQueueChangeServiceTime.token == jsonTokenAndQueue.token) {
                                val body = """${jsonData.getBody()}
 Token: ${jsonQueueChangeServiceTime.displayToken}
 Previous: ${jsonQueueChangeServiceTime.oldTimeSlotMessage}
 Updated: ${jsonQueueChangeServiceTime.updatedTimeSlotMessage}"""
                                ShowAlertInformation.showInfoDisplayDialog(this@LaunchActivity, jsonData.getTitle(), body)
                                NotificationDB.insertNotification(
                                        NotificationDB.KEY_NOTIFY,
                                        jsonData.codeQR,
                                        body,
                                        jsonData.getTitle(),
                                        jsonData.businessType.getName(),
                                        jsonData.getImageURL())
                                updateNotificationBadgeCount()
                                if (null != homeFragment) {
                                    homeFragment.updateCurrentQueueList()
                                }
                            }
                        }
                    }
                } else {
                    updateNotification(jsonData, codeQR)
                }
            } else {
                CustomToast().showToast(LaunchActivity.launchActivity, "UnSupported Notification reached: $payload")
                FirebaseCrashlytics.getInstance().log("UnSupported Notification reached: $payload")
            }
        }
    }

    private fun updateNotification(jsonData: JsonData, codeQR: String) {
        try {
            var go_to = ""
            var messageOrigin = ""
            var current_serving = ""
            var jsonTextToSpeeches: List<JsonTextToSpeech?>? = null
            var msgId = ""
            var purchaseOrderStateEnum = PurchaseOrderStateEnum.IN
            if (jsonData is JsonTopicQueueData) {
                val jsonTopicQueueData = jsonData
                current_serving = jsonTopicQueueData.currentlyServing.toString()
                go_to = jsonTopicQueueData.goTo
                messageOrigin = jsonTopicQueueData.messageOrigin.name
                jsonTextToSpeeches = jsonData.getJsonTextToSpeeches()
                msgId = jsonTopicQueueData.messageId
            } else if (jsonData is JsonTopicOrderData) {
                val jsonTopicOrderData = jsonData
                current_serving = jsonTopicOrderData.currentlyServing.toString()
                go_to = jsonTopicOrderData.goTo
                messageOrigin = jsonTopicOrderData.messageOrigin.name
                purchaseOrderStateEnum = jsonTopicOrderData.purchaseOrderState
                jsonTextToSpeeches = jsonData.getJsonTextToSpeeches()
                msgId = jsonTopicOrderData.messageId
            }
            val jsonTokenAndQueueArrayList = TokenAndQueueDB.getCurrentQueueObjectList(codeQR)
            for (i in jsonTokenAndQueueArrayList.indices) {
                val jtk = jsonTokenAndQueueArrayList[i]
                if (null != jtk) {
                    /* update DB & after join screen */
                    if (current_serving.toInt() < jtk.servingNumber) {
                        /* Do nothing - In Case of getting service no less than what the object have */
                    } else {
                        jtk.servingNumber = current_serving.toInt()
                        TokenAndQueueDB.updateCurrentListQueueObject(codeQR, current_serving, jtk.token.toString())
                    }
                    if (jsonData is JsonTopicOrderData && jtk.token - current_serving.toInt() <= 0) {
                        jtk.purchaseOrderState = purchaseOrderStateEnum
                    }
                    /*
                     * Save codeQR of goto & show it in after join screen on app
                     * Review DB for review key && current serving == token no.
                     */if (current_serving.toInt() == jtk.token) {
                        // if (Integer.parseInt(current_serving) == jtk.getToken() && isReview) {
                        val reviewData = ReviewDB.getValue(codeQR, current_serving)
                        if (null != reviewData) {
                            val cv = ContentValues()
                            cv.put(DatabaseTable.Review.KEY_GOTO, go_to)
                            ReviewDB.updateReviewRecord(codeQR, current_serving, cv)
                            /* update */
                        } else {
                            /* insert */
                            val cv = ContentValues()
                            cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, -1)
                            cv.put(DatabaseTable.Review.CODE_QR, codeQR)
                            cv.put(DatabaseTable.Review.TOKEN, current_serving)
                            cv.put(DatabaseTable.Review.QID, jtk.queueUserId)
                            cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1")
                            cv.put(DatabaseTable.Review.KEY_SKIP, "-1")
                            cv.put(DatabaseTable.Review.KEY_GOTO, go_to)
                            ReviewDB.insert(cv)
                        }
                    }
                    if (jtk.isTokenExpired && jsonTokenAndQueueArrayList.size == 1) {
                        /* Un-subscribe the topic */
                        NoQueueMessagingService.unSubscribeTopics(jtk.topic)
                    }
                    if (AppInitialize.activityCommunicator != null) {
                        val isUpdated = AppInitialize.activityCommunicator.updateUI(codeQR, jtk, go_to)
                        if (isUpdated || jtk.servingNumber == jtk.token) {
                            val reviewData = ReviewDB.getValue(codeQR, current_serving)
                            if (null != reviewData) {
                                if (reviewData.isBuzzerShow != "1") {
                                    val cv = ContentValues()
                                    cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "1")
                                    ReviewDB.updateReviewRecord(codeQR, current_serving, cv)
                                    val blinker = Intent(this@LaunchActivity, BlinkerActivity::class.java)
                                    blinker.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    applicationContext.startActivity(blinker)
                                    if (AppInitialize.isMsgAnnouncementEnable()) {
                                        makeAnnouncement(jsonTextToSpeeches, msgId)
                                    }
                                } else {
                                    /* Blinker already shown */
                                }
                                /* update */
                            } else {
                                /* insert */
                                val cv = ContentValues()
                                cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, -1)
                                cv.put(DatabaseTable.Review.CODE_QR, codeQR)
                                cv.put(DatabaseTable.Review.TOKEN, current_serving)
                                cv.put(DatabaseTable.Review.QID, jtk.queueUserId)
                                cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "1")
                                cv.put(DatabaseTable.Review.KEY_SKIP, "-1")
                                cv.put(DatabaseTable.Review.KEY_GOTO, "")
                                ReviewDB.insert(cv)
                                val blinker = Intent(this@LaunchActivity, BlinkerActivity::class.java)
                                blinker.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                applicationContext.startActivity(blinker)
                                if (AppInitialize.isMsgAnnouncementEnable()) {
                                    makeAnnouncement(jsonTextToSpeeches, msgId)
                                }
                            }
                        }
                    }
                    try {
                        /* In case of order update the order status */
                        if (jsonData is JsonTopicOrderData) {
                            if (messageOrigin.equals(MessageOriginEnum.O.name, ignoreCase = true) && current_serving.toInt() == jtk.token) {
                                jtk.purchaseOrderState = jsonData.purchaseOrderState
                                TokenAndQueueDB.updateCurrentListOrderObject(codeQR, jtk.purchaseOrderState.getName(), jtk.token.toString())
                            }
                        }
                        homeFragment.updateListFromNotification(jtk, jsonTextToSpeeches, msgId)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        throw e
                    }
                } else {
                    Log.e(LaunchActivity.TAG, "codeQR=$codeQR current_serving=$current_serving goTo=$go_to")
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log("Failed on update notification")
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(LaunchActivity.TAG, "Failed on update notification " + e.localizedMessage)
        }
    }

    fun makeAnnouncement(jsonTextToSpeeches: List<JsonTextToSpeech?>?, msgId: String?) {
        if (null == cacheMsgIds.getIfPresent(MSG_IDS)) {
            cacheMsgIds.put(MSG_IDS, java.util.ArrayList<String>())
        }
        var msgIds: java.util.ArrayList<String?> = cacheMsgIds.getIfPresent(MSG_IDS)
        if (null == msgIds) {
            msgIds = java.util.ArrayList()
        }
        if (!TextUtils.isEmpty(msgId) && !msgIds.contains(msgId)) {
            msgIds.add(msgId)
            cacheMsgIds.put(MSG_IDS, msgIds)
            textToSpeechHelper.makeAnnouncement(jsonTextToSpeeches)
        }
    }

    private fun updateNotificationBadgeCount() {
        val notifyCount = NotificationDB.getNotificationCount()
        expandableListAdapter?.notifyDataSetChanged()
        if (null != supportActionBar) {
            supportActionBar!!.setHomeAsUpIndicator(setBadgeCount(this, R.drawable.ic_burger, notifyCount))
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowCustomEnabled(true) // enable overriding the default toolbar layout
            supportActionBar!!.setDisplayShowTitleEnabled(false)
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


}