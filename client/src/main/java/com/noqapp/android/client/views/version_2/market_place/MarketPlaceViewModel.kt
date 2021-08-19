package com.noqapp.android.client.views.version_2.market_place

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.common.beans.ErrorEncounteredJson
import com.noqapp.android.common.beans.marketplace.MarketplaceElasticList

class MarketPlaceViewModel: ViewModel() {
    val tag: String = MarketPlaceViewModel::class.java.simpleName

    val marketPlaceElasticListLiveData = MutableLiveData<MarketplaceElasticList>()
    val errorEncounteredJsonLiveData = MutableLiveData<ErrorEncounteredJson>()
    val authenticationError = MutableLiveData(false)
    val searchStoreQueryLiveData = MutableLiveData<SearchQuery>()

    private var marketRepository: MarketRepository = MarketRepository()

    fun getMarketPlace(searchQuery: SearchQuery) {
        Log.i(tag, "Search $searchQuery");
        marketRepository.getMarketPlace(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), searchQuery, {
            marketPlaceElasticListLiveData.postValue(it)
        }, {
            errorEncounteredJsonLiveData.postValue(it)
        }, {
            authenticationError.postValue(true)
        })
    }
}
