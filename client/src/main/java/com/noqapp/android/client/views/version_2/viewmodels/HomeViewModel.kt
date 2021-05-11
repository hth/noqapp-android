package com.noqapp.android.client.views.version_2.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.noqapp.android.client.model.FavouriteApiCall
import com.noqapp.android.client.model.QueueApiAuthenticCall
import com.noqapp.android.client.model.SearchBusinessStoreApiCalls
import com.noqapp.android.client.presenter.FavouriteListPresenter
import com.noqapp.android.client.presenter.SearchBusinessStorePresenter
import com.noqapp.android.client.presenter.TokenAndQueuePresenter
import com.noqapp.android.client.presenter.beans.BizStoreElasticList
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueueList
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery
import com.noqapp.android.client.utils.NetworkUtils
import com.noqapp.android.client.utils.ShowAlertInformation
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.client.views.version_2.db.NoqAppDB
import com.noqapp.android.common.beans.ErrorEncounteredJson
import com.noqapp.android.common.pojos.DisplayNotification

class HomeViewModel(val applicationContext: Application) : AndroidViewModel(applicationContext), SearchBusinessStorePresenter, TokenAndQueuePresenter {

    val searchStoreQueryLiveData = MutableLiveData<SearchStoreQuery>()
    val currentQueueErrorLiveData = MutableLiveData<Boolean>()
    val nearMeErrorLiveData = MutableLiveData<Boolean>()
    val nearMeResponse = MutableLiveData<BizStoreElasticList?>()
    val businessListResponse = MutableLiveData<BizStoreElasticList?>()
    private var searchBusinessStoreApiCalls: SearchBusinessStoreApiCalls
    private var queueApiAuthenticCall: QueueApiAuthenticCall

    init {
        currentQueueErrorLiveData.value = false
        nearMeErrorLiveData.value = false
        searchBusinessStoreApiCalls = SearchBusinessStoreApiCalls(this)
        queueApiAuthenticCall = QueueApiAuthenticCall()
        queueApiAuthenticCall.setTokenAndQueuePresenter(this)
    }

    fun fetchNearMeRecentVisits(deviceId: String, searchStoreQuery: SearchStoreQuery) {
        searchBusinessStoreApiCalls.business(deviceId, searchStoreQuery)
    }

    fun fetchActiveTokenQueueList() {
        queueApiAuthenticCall.getAllJoinedQueues(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth())
    }

    fun fetchFavouritesList(context: Context, favouritesListPresenter: FavouriteListPresenter) {
        if (NetworkUtils.isConnectingToInternet(context)) {
            val favouriteApiCall = FavouriteApiCall()
            favouriteApiCall.setFavouriteListPresenter(favouritesListPresenter)
            favouriteApiCall.favorite(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth())
        } else {
            ShowAlertInformation.showNetworkDialog(context)
        }
    }

    fun fetchBusinessList(searchStoreQuery: SearchStoreQuery?) {
        searchBusinessStoreApiCalls.search(UserUtils.getDeviceId(), searchStoreQuery)
    }

    fun getNotificationsList(): LiveData<List<DisplayNotification>> {
        return NoqAppDB.getNoqAppDbInstance(applicationContext).notificationDao().getNotificationsList()
    }

    fun getCurrentTokenAndQueue(): LiveData<List<JsonTokenAndQueue>> {
        return NoqAppDB.getNoqAppDbInstance(applicationContext).tokenAndQueueDao().getCurrentQueueList()
    }

    fun getHistoryTokenAndQueue(): LiveData<List<JsonTokenAndQueue>> {
        return NoqAppDB.getNoqAppDbInstance(applicationContext).tokenAndQueueDao().getHistoryQueueList()
    }

    override fun nearMeTempleResponse(bizStoreElasticList: BizStoreElasticList?) {
    }

    override fun businessList(bizStoreElasticList: BizStoreElasticList?) {
        businessListResponse.value = bizStoreElasticList
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

    override fun historyQueueResponse(tokenAndQueues: MutableList<JsonTokenAndQueue>?, sinceBeginning: Boolean) {
        tokenAndQueues?.let {
            NoqAppDB.getNoqAppDbInstance(applicationContext).tokenAndQueueDao().deleteHistoryQueue()
            it.forEach { tokenAndQueue ->
                tokenAndQueue.isHistoryQueue = 1
            }
            NoqAppDB.getNoqAppDbInstance(applicationContext).tokenAndQueueDao().saveCurrentQueue(it)
        }
    }

    override fun nearMeHospitalError() {
    }

    override fun nearMeMerchant(bizStoreElasticList: BizStoreElasticList?) {
        nearMeResponse.value = bizStoreElasticList
    }

    override fun nearMeRestaurantsResponse(bizStoreElasticList: BizStoreElasticList?) {
    }

    override fun authenticationFailure() {
        currentQueueErrorLiveData.value = true
    }

    override fun currentQueueResponse(tokenAndQueues: JsonTokenAndQueueList?) {
        tokenAndQueues?.let {
            NoqAppDB.getNoqAppDbInstance(applicationContext).tokenAndQueueDao().deleteCurrentQueue()
            it.tokenAndQueues.forEach { tokenAndQueue ->
                tokenAndQueue.isHistoryQueue = 0
            }
            NoqAppDB.getNoqAppDbInstance(applicationContext).tokenAndQueueDao().saveCurrentQueue(it.tokenAndQueues)
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