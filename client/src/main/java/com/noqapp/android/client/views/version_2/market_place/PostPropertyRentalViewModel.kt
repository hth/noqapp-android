package com.noqapp.android.client.views.version_2.market_place

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.common.beans.ErrorEncounteredJson
import com.noqapp.android.common.beans.JsonResponse
import com.noqapp.android.common.beans.marketplace.JsonPropertyRental
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.noqapp.android.common.beans.marketplace.MarketplaceElasticList
import com.noqapp.android.common.model.types.BusinessTypeEnum
import com.noqapp.android.common.model.types.category.RentalTypeEnum
import com.noqapp.android.common.utils.CommonHelper.SDF_YYYY_MM_DD
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*

class PostPropertyRentalViewModel : ViewModel() {
    val tag: String = PostPropertyRentalViewModel::class.java.simpleName

    val marketplaceElasticListLiveData = MutableLiveData<MarketplaceElasticList>()
    val postMarketPlaceElasticLiveData = MutableLiveData<MarketplaceElastic>()
    val errorEncounteredJsonLiveData = MutableLiveData<ErrorEncounteredJson>()
    val authenticationError = MutableLiveData(false)
    val searchStoreQueryLiveData = MutableLiveData<SearchQuery>()
    val postImagesLiveData = MutableLiveData<JsonResponse>()

    private var marketRepository: PropertyRentalRepository = PropertyRentalRepository()

    fun getMarketPlace(searchQuery: SearchQuery) {
        Log.i(tag, "Search $searchQuery");
        marketRepository.getMarketPlace(
            UserUtils.getDeviceId(),
            UserUtils.getEmail(),
            UserUtils.getAuth(),
            searchQuery,
            {
                marketplaceElasticListLiveData.postValue(it)
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
        //        jsonPropertyRental.publishUntil = Date() -- Server Managed
        jsonPropertyRental.rentalType = rentalTypeEnum
        jsonPropertyRental.rentalAvailableDay = SDF_YYYY_MM_DD.format(Date())
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

    fun postImages(multipartFile: MultipartBody.Part, postId: RequestBody) {
        marketRepository.postMarketPlaceImages(
            UserUtils.getDeviceId(),
            UserUtils.getEmail(),
            UserUtils.getAuth(),
            multipartFile,
            postId,
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
