package com.noqapp.android.client.views.version_2.market_place

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.UserUtils
import com.noqapp.android.common.beans.ErrorEncounteredJson
import com.noqapp.android.common.beans.JsonResponse
import com.noqapp.android.common.beans.marketplace.JsonPropertyRental
import com.noqapp.android.common.beans.marketplace.MarketplaceElasticList
import com.noqapp.android.common.model.types.BusinessTypeEnum
import com.noqapp.android.common.model.types.category.RentalTypeEnum
import com.noqapp.android.common.pojos.PropertyRentalEntity
import com.noqapp.android.common.utils.CommonHelper.SDF_YYYY_MM_DD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*

class PostPropertyRentalViewModel : ViewModel() {
    val tag: String = PostPropertyRentalViewModel::class.java.simpleName

    val marketplaceElasticListLiveData = MutableLiveData<MarketplaceElasticList>()
    val postMarketPlaceJsonLiveData = MutableLiveData<JsonPropertyRental>()
    val errorEncounteredJsonLiveData = MutableLiveData<ErrorEncounteredJson>()
    val authenticationError = MutableLiveData(false)
    val searchStoreQueryLiveData = MutableLiveData<SearchQuery>()
    val postImagesLiveData = MutableLiveData<JsonResponse>()
    val shownInterestLiveData = MutableLiveData<Boolean>()

    private var propertyRentalRepository: PropertyRentalRepository = PropertyRentalRepository()

    fun getMarketPlace(searchQuery: SearchQuery) {
        Log.i(tag, "Search $searchQuery");
        propertyRentalRepository.getMarketPlace(
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
        productPrice: Int,
        title: String,
        description: String,
        bathRoom: Int,
        bedroom: Int,
        carpetArea: Int,
        town: String?,
        city: String?,
        address: String?,
        landmark: String?,
        availableFrom: String?,
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
        jsonPropertyRental.rentalType = rentalTypeEnum
        jsonPropertyRental.rentalAvailableDay = SDF_YYYY_MM_DD.format(Date())
        jsonPropertyRental.businessType = BusinessTypeEnum.PR
        jsonPropertyRental.coordinate = doubleArrayOf(latitude, longitude)
        jsonPropertyRental.productPrice = productPrice
        jsonPropertyRental.title = title
        jsonPropertyRental.description = description
        jsonPropertyRental.landmark = landmark
        jsonPropertyRental.rentalAvailableDay = availableFrom
        Log.i(tag, "Post $jsonPropertyRental")

        propertyRentalRepository.postMarketPlace(
            UserUtils.getDeviceId(),
            UserUtils.getEmail(),
            UserUtils.getAuth(),
            jsonPropertyRental,
            {
                postMarketPlaceJsonLiveData.postValue(it)
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
        propertyRentalRepository.initiateCall(
            UserUtils.getDeviceId(),
            UserUtils.getEmail(),
            UserUtils.getAuth(),
            jsonMarketPlace,
            {
                shownInterestLiveData.postValue(true)
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
        propertyRentalRepository.viewDetails(
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
        propertyRentalRepository.postMarketPlaceImages(
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

    fun insertPropertyRental(context: Context, propertyRentalEntity: PropertyRentalEntity?) {
        viewModelScope.launch(Dispatchers.IO) {
            propertyRentalRepository.insertPropertyRental(context, propertyRentalEntity)
        }
    }

    fun getPropertyRental(context: Context): LiveData<List<PropertyRentalEntity>> {
        return propertyRentalRepository.getPropertyRental(context)
    }

    fun deletePostsLocally(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            propertyRentalRepository.deletePostsLocally(context)
        }
    }
}
