package com.noqapp.android.client.views.version_2.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.noqapp.android.client.model.FavouriteApiCall
import com.noqapp.android.client.model.QueueApiAuthenticCall
import com.noqapp.android.client.model.SearchBusinessStoreApiAuthenticCalls
import com.noqapp.android.client.model.SearchBusinessStoreApiCalls
import com.noqapp.android.client.presenter.FavouriteListPresenter
import com.noqapp.android.client.presenter.SearchBusinessStorePresenter
import com.noqapp.android.client.presenter.TokenAndQueuePresenter
import com.noqapp.android.client.presenter.beans.BizStoreElasticList
import com.noqapp.android.client.presenter.beans.FavoriteElastic
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueueList
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery
import com.noqapp.android.client.utils.NetworkUtils
import com.noqapp.android.client.utils.ShowAlertInformation
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.client.views.version_2.db.NoQueueAppDB
import com.noqapp.android.common.beans.ErrorEncounteredJson
import com.noqapp.android.common.pojos.DisplayNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(val applicationContext: Application) : AndroidViewModel(applicationContext), SearchBusinessStorePresenter, TokenAndQueuePresenter, FavouriteListPresenter {

    val searchStoreQueryLiveData = MutableLiveData<SearchStoreQuery>()
    val currentQueueErrorLiveData = MutableLiveData<Boolean>()
    val nearMeErrorLiveData = MutableLiveData<Boolean>()
    val nearMeResponse = MutableLiveData<BizStoreElasticList?>()
    val favoritesListResponseLiveData = MutableLiveData<FavoriteElastic?>()
    private var searchBusinessStoreApiCalls: SearchBusinessStoreApiCalls
    private var searchBusinessStoreApiAuthenticCalls: SearchBusinessStoreApiAuthenticCalls
    private var queueApiAuthenticCall: QueueApiAuthenticCall

    val currentTokenAndQueueListLiveData : LiveData<List<JsonTokenAndQueue>> = liveData {
        val tokenAndQueueList = NoQueueAppDB.dbInstance(applicationContext).tokenAndQueueDao().getCurrentQueueList()
        emitSource(tokenAndQueueList)
    }

    val historyTokenAndQueueListLiveData : LiveData<List<JsonTokenAndQueue>> = liveData {
        val tokenAndQueueList = NoQueueAppDB.dbInstance(applicationContext).tokenAndQueueDao().getHistoryQueueList()
        emitSource(tokenAndQueueList)
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
        queueApiAuthenticCall.getAllJoinedQueues(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth())
    }

    fun fetchFavouritesRecentVisitList(context: Context) {
        if (NetworkUtils.isConnectingToInternet(context)) {
            val favouriteApiCall = FavouriteApiCall()
            favouriteApiCall.setFavouriteListPresenter(this)
            favouriteApiCall.favorite(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth())
        } else {
            ShowAlertInformation.showNetworkDialog(context)
        }
    }

    fun getNotificationsList(): LiveData<List<DisplayNotification>> {
        return NoQueueAppDB.dbInstance(applicationContext).notificationDao().getNotificationsList()
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
    }

    override fun responseErrorPresenter(errorCode: Int) {
    }

    override fun favouriteListResponse(favoriteElastic: FavoriteElastic?) {
        favoritesListResponseLiveData.value = favoriteElastic
    }

    override fun historyQueueResponse(tokenAndQueues: MutableList<JsonTokenAndQueue>?, sinceBeginning: Boolean) {
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
        currentQueueErrorLiveData.value = true
    }

    override fun currentQueueResponse(tokenAndQueues: JsonTokenAndQueueList?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tokenAndQueues?.let {
                    NoQueueAppDB.dbInstance(applicationContext).tokenAndQueueDao().deleteCurrentQueue()
                    it.tokenAndQueues.forEach { tokenAndQueue ->
                        tokenAndQueue.historyQueue = 0
                    }
                    NoQueueAppDB.dbInstance(applicationContext).tokenAndQueueDao().saveCurrentQueue(it.tokenAndQueues)
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

}