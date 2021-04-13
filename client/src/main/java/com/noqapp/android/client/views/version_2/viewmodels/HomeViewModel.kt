package com.noqapp.android.client.views.version_2.viewmodels

import android.content.Context
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
import com.noqapp.android.common.beans.ErrorEncounteredJson

class HomeViewModel : ViewModel(), SearchBusinessStorePresenter, TokenAndQueuePresenter {

    val searchStoreQueryLiveData = MutableLiveData<SearchStoreQuery>()
    val currentQueueErrorLiveData = MutableLiveData<Boolean>()
    val nearMeErrorLiveData = MutableLiveData<Boolean>()
    val nearMeResponse = MutableLiveData<BizStoreElasticList?>()
    val currentQueueResponse = MutableLiveData<JsonTokenAndQueueList?>()
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

    override fun historyQueueResponse(tokenAndQueues: MutableList<JsonTokenAndQueue>?, sinceBeginning: Boolean) {
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
        currentQueueResponse.value = tokenAndQueues
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