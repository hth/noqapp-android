package com.noqapp.android.client.views.version_2.market_place

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.common.beans.ErrorEncounteredJson
import com.noqapp.android.common.beans.JsonResponse
import com.noqapp.android.common.beans.marketplace.JsonMarketplace
import com.noqapp.android.common.beans.marketplace.JsonPropertyRental
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.noqapp.android.common.beans.marketplace.MarketplaceElasticList
import com.noqapp.android.common.model.types.BusinessTypeEnum
import com.noqapp.android.common.model.types.category.RentalTypeEnum
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*

class MarketplacePropertyRentalViewModel : ViewModel() {
    val tag: String = MarketplacePropertyRentalViewModel::class.java.simpleName

    val marketPlaceElasticListLiveData = MutableLiveData<MarketplaceElasticList>()
    val postMarketPlaceElasticLiveData = MutableLiveData<MarketplaceElastic>()
    val errorEncounteredJsonLiveData = MutableLiveData<ErrorEncounteredJson>()
    val authenticationError = MutableLiveData(false)
    val searchStoreQueryLiveData = MutableLiveData<SearchQuery>()
    val postImagesLiveData = MutableLiveData<JsonResponse>()

    private var marketRepository: MarketplacePropertyRentalRepository = MarketplacePropertyRentalRepository()

    fun getMarketPlace(searchQuery: SearchQuery) {
        Log.i(tag, "Search $searchQuery");
        marketRepository.getMarketPlace(
            UserUtils.getDeviceId(),
            UserUtils.getEmail(),
            UserUtils.getAuth(),
            searchQuery,
            {
                marketPlaceElasticListLiveData.postValue(it)
            },
            {
                errorEncounteredJsonLiveData.postValue(it)
            },
            {
                authenticationError.postValue(true)
            })
    }

    fun postMarketPlace(
        title: String?,
        bathRoom: Int,
        bedroom: Int,
        carpetArea: Int,
        town: String?,
        city: String?,
        address: String?,
        rentalTypeEnum: RentalTypeEnum,
        latitude: Double,
        longitude: Double
    ) {
        val jsonPropertyRental = JsonPropertyRental()
        jsonPropertyRental.bathroom = bathRoom
        jsonPropertyRental.bedroom = bedroom
        jsonPropertyRental.carpetArea = carpetArea
        jsonPropertyRental.town = town
        jsonPropertyRental.city = city
        jsonPropertyRental.address = address
        jsonPropertyRental.publishUntil = Date()
        jsonPropertyRental.rentalType = rentalTypeEnum
        jsonPropertyRental.rentalAvailableDay = Date().toString()
        jsonPropertyRental.businessType = BusinessTypeEnum.PR
        jsonPropertyRental.coordinate = doubleArrayOf(latitude, longitude)
        jsonPropertyRental.title = title
        jsonPropertyRental.description = "This is test data"
        Log.i(tag, "Post $jsonPropertyRental")

        marketRepository.postMarketPlace(
            UserUtils.getDeviceId(),
            UserUtils.getEmail(),
            UserUtils.getAuth(),
            jsonPropertyRental,
            {
                postMarketPlaceElasticLiveData.postValue(it)
            },
            {
                errorEncounteredJsonLiveData.postValue(it)
            },
            {
                authenticationError.postValue(true)
            })
    }

    fun initiateContact(id: String) {
        val jsonMarketPlace = JsonPropertyRental()
        jsonMarketPlace.id = id
        marketRepository.initiateCall(
            UserUtils.getDeviceId(),
            UserUtils.getEmail(),
            UserUtils.getAuth(),
            jsonMarketPlace,
            {
                Log.d("Success", "Initiated contact successfully")
            },
            {
                errorEncounteredJsonLiveData.postValue(it)
            },
            {
                authenticationError.postValue(true)
            })
    }

    fun viewDetails(id: String) {
        val jsonMarketPlace = JsonPropertyRental()
        jsonMarketPlace.id = id
        marketRepository.viewDetails(
            UserUtils.getDeviceId(),
            UserUtils.getEmail(),
            UserUtils.getAuth(),
            jsonMarketPlace,
            {
                Log.d("Success", "View details api called")
            },
            {
                errorEncounteredJsonLiveData.postValue(it)
            },
            {
                authenticationError.postValue(true)
            })
    }

    fun postImages(
        did: String,
        mail: String,
        auth: String,
        multipartFile: MultipartBody.Part, postId: RequestBody, businessTypeAsString: RequestBody
    ) {
        marketRepository.postMarketPlaceImages(
            did,
            mail,
            auth,
            multipartFile,
            postId,
            businessTypeAsString,
            {
                postImagesLiveData.postValue(it)
            },
            {
                errorEncounteredJsonLiveData.postValue(it)
            },
            {
                authenticationError.postValue(true)
            })
    }
}
