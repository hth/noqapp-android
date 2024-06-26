package com.noqapp.android.client.views.version_2.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.noqapp.android.client.BuildConfig
import com.noqapp.android.client.model.api.FavouriteApiImpl
import com.noqapp.android.client.model.api.TokenQueueApiImpl
import com.noqapp.android.client.model.api.SearchApiImpl
import com.noqapp.android.client.model.open.SearchImpl
import com.noqapp.android.client.model.response.v3.api.NoQueueClientApi
import com.noqapp.android.client.presenter.FavouriteListPresenter
import com.noqapp.android.client.presenter.SearchBusinessStorePresenter
import com.noqapp.android.client.presenter.TokenAndQueuePresenter
import com.noqapp.android.client.presenter.beans.*
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.utils.NetworkUtils
import com.noqapp.android.client.utils.ShowAlertInformation
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.client.views.activities.NoQueueClientApplication
import com.noqapp.android.client.views.version_2.db.NoQueueAppDB
import com.noqapp.android.client.views.version_2.db.helper_models.ForegroundNotificationModel
import com.noqapp.android.common.beans.DeviceRegistered
import com.noqapp.android.common.beans.ErrorEncounteredJson
import com.noqapp.android.common.beans.JsonSchedule
import com.noqapp.android.common.beans.body.DeviceToken
import com.noqapp.android.common.customviews.CustomToast
import com.noqapp.android.common.pojos.DisplayNotification
import com.noqapp.android.common.utils.CommonHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class HomeViewModel(val applicationContext: Application) : AndroidViewModel(applicationContext),
    SearchBusinessStorePresenter, TokenAndQueuePresenter, FavouriteListPresenter {
    val tag: String = HomeViewModel::class.java.simpleName

    val searchStoreQueryLiveData = MutableLiveData<SearchQuery>()
    val currentQueueErrorLiveData = MutableLiveData<Boolean>()
    val nearMeErrorLiveData = MutableLiveData<Boolean>()
    val nearMeResponse = MutableLiveData<BizStoreElasticList?>()
    val authenticationFailureLiveData = MutableLiveData<Boolean>()
    val favoritesListResponseLiveData = MutableLiveData<FavoriteElastic?>()
    val errorLiveData = MutableLiveData<ErrorEncounteredJson>()

    val jsonScheduledAppointmentLiveData = MutableLiveData<List<JsonSchedule>>()

    private var searchImpl: SearchImpl
    private var searchApiImpl: SearchApiImpl
    private var tokenQueueApiImpl: TokenQueueApiImpl

    private var noQueueClientApi: NoQueueClientApi


    val currentTokenAndQueueListLiveData: LiveData<List<JsonTokenAndQueue>> = liveData {
        val tokenAndQueueList =
            NoQueueAppDB.dbInstance(applicationContext).tokenAndQueueDao().getCurrentQueueList()
        emitSource(tokenAndQueueList)
    }

    suspend fun getCurrentQueueObjectList(codeQR: String?): List<JsonTokenAndQueue>? {
        return NoQueueAppDB.dbInstance(applicationContext).tokenAndQueueDao()
            .getCurrentQueueObjectList(codeQR)
    }

    val historyTokenAndQueueListLiveData: LiveData<List<JsonTokenAndQueue>> = liveData {
        val tokenAndQueueList =
            NoQueueAppDB.dbInstance(applicationContext).tokenAndQueueDao().getHistoryQueueList()
        emitSource(tokenAndQueueList)
    }

    val notificationListLiveData: LiveData<List<DisplayNotification>> = liveData {
        val notificationList =
            NoQueueAppDB.dbInstance(applicationContext).notificationDao().getNotificationsList()
        emitSource(notificationList)
    }

    val notificationCountLiveData: LiveData<Int> = liveData {
        val notificationCount = NoQueueAppDB.dbInstance(applicationContext).notificationDao()
            .getNotificationCount(Constants.KEY_UNREAD)
        emitSource(notificationCount)
    }

    val foregroundNotificationLiveData: LiveData<ForegroundNotificationModel> = liveData {
        val foregroundNotification =
            NoQueueAppDB.dbInstance(applicationContext).foregroundNotificationDao()
                .getForegroundNotification()
        emitSource(foregroundNotification)
    }

    init {
        currentQueueErrorLiveData.value = false
        nearMeErrorLiveData.value = false
        searchImpl = SearchImpl(this)
        searchApiImpl = SearchApiImpl(this)
        tokenQueueApiImpl = TokenQueueApiImpl()
        tokenQueueApiImpl.setTokenAndQueuePresenter(this)
        noQueueClientApi = NoQueueClientApplication.getNoQueueClientApi()
    }

    fun fetchNearMe(deviceId: String, searchQuery: SearchQuery) {
        searchImpl.business(deviceId, searchQuery)
    }

    fun fetchActiveTokenQueueList() {
        tokenQueueApiImpl.getAllJoinedQueues(
            UserUtils.getDeviceId(),
            UserUtils.getEmail(),
            UserUtils.getAuth()
        )
    }

    fun fetchFavouritesRecentVisitList() {
        if (NetworkUtils.isConnectingToInternet(applicationContext)) {
            val favouriteApiImpl = FavouriteApiImpl()
            favouriteApiImpl.setFavouriteListPresenter(this)
            favouriteApiImpl.favorite(
                UserUtils.getDeviceId(),
                UserUtils.getEmail(),
                UserUtils.getAuth()
            )
        } else {
            ShowAlertInformation.showNetworkDialog(applicationContext)
        }
    }

    fun getReviewData(qrCode: String?, token: String?): LiveData<ReviewData> {
        return NoQueueAppDB.dbInstance(applicationContext).reviewDao().getReviewData(qrCode, token)
    }

    suspend fun getReviewDataSimple(qrCode: String?, token: String?): ReviewData? {
        return NoQueueAppDB.dbInstance(applicationContext).reviewDao()
            .getReviewDataSimple(qrCode, token)
    }

    fun getReviewData(reviewType: String): LiveData<ReviewData> {
        return NoQueueAppDB.dbInstance(applicationContext).reviewDao().getReviewData(reviewType)
    }

    suspend fun getCurrentQueueObject(codeQR: String?, token: String?): JsonTokenAndQueue? {
        return NoQueueAppDB.dbInstance(applicationContext).tokenAndQueueDao()
            .getCurrentQueueObject(codeQR, token?.toInt())
    }

    suspend fun getHistoryQueueObject(codeQR: String?, token: String?): JsonTokenAndQueue? {
        return NoQueueAppDB.dbInstance(applicationContext).tokenAndQueueDao()
            .getHistoryQueueObject(codeQR, token?.toInt())
    }

    fun updateReviewData(reviewData: ReviewData) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                NoQueueAppDB.dbInstance(applicationContext).reviewDao().update(reviewData)
            }
        }
    }

    fun insertReviewData(reviewData: ReviewData) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                NoQueueAppDB.dbInstance(applicationContext).reviewDao().insertReviewData(reviewData)
            }
        }
    }

    override fun nearMeTempleResponse(bizStoreElasticList: BizStoreElasticList?) {
    }

    override fun nearMeMerchantError() {
    }

    override fun nearMeHospitalResponse(bizStoreElasticList: BizStoreElasticList?) {
    }

    override fun nearMeCanteenResponse(bizStoreElasticList: BizStoreElasticList?) {
    }

    override fun nearMeRestaurantsError() {
    }

    override fun responseErrorPresenter(eej: ErrorEncounteredJson?) {
        errorLiveData.value = eej
    }

    override fun responseErrorPresenter(errorCode: Int) {
    }

    override fun favouriteListResponse(favoriteElastic: FavoriteElastic?) {
        favoritesListResponseLiveData.value = favoriteElastic
    }

    override fun historyQueueResponse(
        tokenAndQueues: MutableList<JsonTokenAndQueue>?,
        sinceBeginning: Boolean
    ) {
        viewModelScope.launch {
            tokenAndQueues?.let {
                NoQueueAppDB.dbInstance(applicationContext).tokenAndQueueDao().deleteHistoryQueue()
                it.forEach { tokenAndQueue ->
                    tokenAndQueue.historyQueue = 1
                }
                NoQueueAppDB.dbInstance(applicationContext).tokenAndQueueDao().saveCurrentQueue(it)
            }
        }
    }

    override fun nearMeHospitalError() {
    }

    override fun nearMeMerchant(bizStoreElasticList: BizStoreElasticList?) {
        nearMeResponse.value = bizStoreElasticList
    }

    override fun nearMeRestaurantsResponse(bizStoreElasticList: BizStoreElasticList?) {
        nearMeResponse.value = bizStoreElasticList
    }

    override fun authenticationFailure() {
        authenticationFailureLiveData.value = true
    }

    override fun currentQueueResponse(tokenAndQueues: JsonTokenAndQueueList?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tokenAndQueues?.let {
                    NoQueueAppDB.dbInstance(applicationContext).tokenAndQueueDao()
                        .deleteCurrentQueue()
                    it.tokenAndQueues.forEach { tokenAndQueue ->
                        tokenAndQueue.historyQueue = 0
                    }
                    NoQueueAppDB.dbInstance(applicationContext).tokenAndQueueDao()
                        .saveCurrentQueue(it.tokenAndQueues)
                    jsonScheduledAppointmentLiveData.postValue(it.jsonScheduleList.jsonSchedules)
                }
            }
        }
    }

    override fun historyQueueError() {
    }

    override fun currentQueueError() {
        currentQueueErrorLiveData.value = true
    }

    override fun nearMeCanteenError() {
    }

    override fun nearMeTempleError() {
    }

    fun updateCurrentListQueueObject(
        codeQR: String?,
        servingNumber: String,
        displayServingNumber: String,
        token: Int
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                NoQueueAppDB.dbInstance(applicationContext).tokenAndQueueDao()
                    .updateCurrentListQueueObject(
                        codeQR,
                        servingNumber,
                        displayServingNumber,
                        token
                    )
            }
        }
    }

    fun deleteReview(codeQr: String?, token: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                NoQueueAppDB.dbInstance(applicationContext).reviewDao()
                    .deleteReviewData(codeQr, token)
            }
        }
    }

    fun deleteToken(codeQr: String?, token: Int?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                NoQueueAppDB.dbInstance(applicationContext).tokenAndQueueDao()
                    .deleteTokenQueue(codeQr, token)
            }
        }
    }

    suspend fun getNotifications(): List<DisplayNotification> {
        return NoQueueAppDB.dbInstance(applicationContext).notificationDao().getNotifications()
    }

    fun updateDisplayNotification(displayNotification: DisplayNotification) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                NoQueueAppDB.dbInstance(applicationContext).notificationDao()
                    .updateNotification(displayNotification)
            }
        }
    }

    fun deleteForegroundNotification() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                NoQueueAppDB.dbInstance(applicationContext).foregroundNotificationDao()
                    .deleteForegroundNotification()
            }
        }
    }

    fun clearForegroundNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            NoQueueAppDB.dbInstance(applicationContext).foregroundNotificationDao()
                .clearForegroundNotifications()
        }
    }

    fun clearReviewData() {
        viewModelScope.launch(Dispatchers.IO) {
            NoQueueAppDB.dbInstance(applicationContext).reviewDao().clearReviewData()
        }
    }

    fun clearNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            NoQueueAppDB.dbInstance(applicationContext).notificationDao().deleteNotifications()
        }
    }

    fun deleteNotification(key: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            NoQueueAppDB.dbInstance(applicationContext).notificationDao().deleteNotification(key)
        }
    }

    fun clearTokenAndQueue() {
        viewModelScope.launch(Dispatchers.IO) {
            NoQueueAppDB.dbInstance(applicationContext).tokenAndQueueDao().clearTokenAndQueue()
        }
    }

    fun callRegistrationService() {
        val deviceToken = DeviceToken(
            NoQueueClientApplication.getTokenFCM(),
            Constants.appVersion(),
            CommonHelper.getLocation(
                NoQueueClientApplication.location.latitude,
                NoQueueClientApplication.location.longitude
            )
        )

        viewModelScope.launch {
            var deviceRegistered:DeviceRegistered?

            if (UserUtils.isLogin()) {
                deviceRegistered = noQueueClientApi.register(
                    UserUtils.getDeviceId(),
                    Constants.DEVICE_TYPE, BuildConfig.APP_FLAVOR,
                    UserUtils.getEmail(),
                    UserUtils.getAuth(),
                    deviceToken
                )
            } else {
                deviceRegistered= noQueueClientApi.register(Constants.DEVICE_TYPE, BuildConfig.APP_FLAVOR, deviceToken)
            }
            if (1 == deviceRegistered?.registered) {

                val jsonUserAddress = CommonHelper.getAddress(
                    deviceRegistered?.geoPointOfQ.lat,
                    deviceRegistered?.geoPointOfQ.lon,
                    NoQueueClientApplication.noQueueClientApplication
                )
                NoQueueClientApplication.cityName = jsonUserAddress.locationAsString
                val locationPref = NoQueueClientApplication.getLocationPreference()
                    .setArea(jsonUserAddress.area)
                    .setTown(jsonUserAddress.town)
                    .setLatitude(deviceRegistered?.geoPointOfQ.lat)
                    .setLongitude(deviceRegistered?.geoPointOfQ.lon)
                NoQueueClientApplication.setLocationPreference(locationPref)
                NoQueueClientApplication.setDeviceID(deviceRegistered?.deviceId)
                NoQueueClientApplication.location.latitude = locationPref.latitude
                NoQueueClientApplication.location.longitude = locationPref.longitude
            } else {
                try {
                    CustomToast().showToast(NoQueueClientApplication.noQueueClientApplication, "Device registration error")
                } catch (e: Exception) {

                }
            }


        }

    }


}
