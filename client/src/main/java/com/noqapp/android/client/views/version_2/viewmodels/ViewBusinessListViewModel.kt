package com.noqapp.android.client.views.version_2.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.noqapp.android.client.model.api.SearchBusinessStoreApiImpl
import com.noqapp.android.client.model.open.SearchBusinessStoreImpl
import com.noqapp.android.client.presenter.SearchBusinessStorePresenter
import com.noqapp.android.client.presenter.beans.BizStoreElasticList
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.client.views.activities.AppInitialize
import com.noqapp.android.common.beans.ErrorEncounteredJson

class ViewBusinessListViewModel: ViewModel(), SearchBusinessStorePresenter {
    val errorLiveData = MutableLiveData<String>()
    val businessListResponse = MutableLiveData<BizStoreElasticList?>()
    private var searchBusinessStoreImpl: SearchBusinessStoreImpl = SearchBusinessStoreImpl(this)
    private var searchBusinessStoreApiImpl: SearchBusinessStoreApiImpl = SearchBusinessStoreApiImpl(this)

    fun fetchBusinessList(searchStoreQuery: SearchStoreQuery?) {
        if (UserUtils.isLogin()) {
            searchBusinessStoreApiImpl.business(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), searchStoreQuery)
        } else {
            searchBusinessStoreImpl.business(AppInitialize.getDeviceId(), searchStoreQuery)
        }
    }

    override fun authenticationFailure() {
    }

    override fun responseErrorPresenter(eej: ErrorEncounteredJson?) {
        errorLiveData.value = "Oops! Something went wrong!"
        eej?.run {
            errorLiveData.value = reason
        }
    }

    override fun responseErrorPresenter(errorCode: Int) {
        errorLiveData.value = "Oops! Something went wrong!"
    }

    override fun nearMeMerchant(bizStoreElasticList: BizStoreElasticList?) {
        businessListResponse.value = bizStoreElasticList
    }

    override fun nearMeMerchantError() {
        //errorLiveData.value = true
    }

    override fun nearMeHospitalResponse(bizStoreElasticList: BizStoreElasticList?) {
        businessListResponse.value = bizStoreElasticList
    }

    override fun nearMeHospitalError() {
        //errorLiveData.value = true
    }

    override fun nearMeCanteenResponse(bizStoreElasticList: BizStoreElasticList?) {
        businessListResponse.value = bizStoreElasticList
    }

    override fun nearMeCanteenError() {
        //errorLiveData.value = true
    }

    override fun nearMeRestaurantsResponse(bizStoreElasticList: BizStoreElasticList?) {
        businessListResponse.value = bizStoreElasticList
    }

    override fun nearMeRestaurantsError() {
        //errorLiveData.value = true
    }

    override fun nearMeTempleResponse(bizStoreElasticList: BizStoreElasticList?) {
        businessListResponse.value = bizStoreElasticList
    }

    override fun nearMeTempleError() {
        //errorLiveData.value = true
    }
}
