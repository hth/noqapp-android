package com.noqapp.android.client.views.version_2.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.noqapp.android.client.model.FavouriteApiCall
import com.noqapp.android.client.model.QueueApiAuthenticCall
import com.noqapp.android.client.model.SearchBusinessStoreApiAuthenticCalls
import com.noqapp.android.client.model.SearchBusinessStoreApiCalls
import com.noqapp.android.client.presenter.FavouriteListPresenter
import com.noqapp.android.client.presenter.SearchBusinessStorePresenter
import com.noqapp.android.client.presenter.TokenAndQueuePresenter
import com.noqapp.android.client.presenter.beans.*
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery
import com.noqapp.android.client.utils.NetworkUtils
import com.noqapp.android.client.utils.ShowAlertInformation
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.client.views.version_2.db.NoQueueAppDB
import com.noqapp.android.client.views.version_2.db.helper_models.ForegroundNotificationModel
import com.noqapp.android.common.beans.ErrorEncounteredJson
import com.noqapp.android.common.pojos.DisplayNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(val applicationContext: Application) : AndroidViewModel(applicationContext),
    SearchBusinessStorePresenter, TokenAndQueuePresenter, FavouriteListPresenter {
    val TAG = HomeViewModel::class.java.simpleName

    val searchStoreQueryLiveData = MutableLiveData<SearchStoreQuery>()
    val currentQueueErrorLiveData = MutableLiveData<Boolean>()
    val nearMeErrorLiveData = MutableLiveData<Boolean>()
    val nearMeResponse = MutableLiveData<BizStoreElasticList?>()
    val authenticationFailureLiveData = MutableLiveData<Boolean>()
    val favoritesListResponseLiveData = MutableLiveData<FavoriteElastic?>()
    val errorLiveData = MutableLiveData<ErrorEncounteredJson>()

    private var searchBusinessStoreApiCalls: SearchBusinessStoreApiCalls
    private var searchBusinessStoreApiAuthenticCalls: SearchBusinessStoreApiAuthenticCalls
    private var queueApiAuthenticCall: QueueApiAuthenticCall

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

    val foregroundNotificationLiveData: LiveData<ForegroundNotificationModel> = liveData {
        val foregroundNotification =
            NoQueueAppDB.dbInstance(applicationContext).foregroundNotificationDao()
                .getForegroundNotification()
        emitSource(foregroundNotification)
    }

    init {
        currentQueueErrorLiveData.value = false
        nearMeErrorLiveData.value = false
        searchBusinessStoreApiCalls = SearchBusinessStoreApiCalls(this)
        searchBusinessStoreApiAuthenticCalls = SearchBusinessStoreApiAuthenticCalls(this)
        queueApiAuthenticCall = QueueApiAuthenticCall()
        queueApiAuthenticCall.setTokenAndQueuePresenter(this)
    }

    fun fetchNearMe(deviceId: String, searchStoreQuery: SearchStoreQuery) {
        searchBusinessStoreApiCalls.business(deviceId, searchStoreQuery)
    }

    fun fetchActiveTokenQueueList() {
        queueApiAuthenticCall.getAllJoinedQueues(
            UserUtils.getDeviceId(),
            UserUtils.getEmail(),
            UserUtils.getAuth()
        )
    }

    fun fetchFavouritesRecentVisitList() {
        if (NetworkUtils.isConnectingToInternet(applicationContext)) {
            val favouriteApiCall = FavouriteApiCall()
            favouriteApiCall.setFavouriteListPresenter(this)
            favouriteApiCall.favorite(
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

}