package com.noqapp.android.client.views.version_2.market_place.householdItem

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
import com.noqapp.android.common.beans.marketplace.JsonHouseholdItem
import com.noqapp.android.common.beans.marketplace.MarketplaceElasticList
import com.noqapp.android.common.model.types.BusinessTypeEnum
import com.noqapp.android.common.model.types.category.HouseholdItemCategoryEnum
import com.noqapp.android.common.model.types.category.ItemConditionEnum
import com.noqapp.android.common.pojos.HouseHoldItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class HouseholdItemViewModel : ViewModel() {
    val tag: String = HouseholdItemViewModel::class.java.simpleName

    val marketplaceElasticListLiveData = MutableLiveData<MarketplaceElasticList>()
    val postMarketPlaceJsonLiveData = MutableLiveData<JsonHouseholdItem>()
    val errorEncounteredJsonLiveData = MutableLiveData<ErrorEncounteredJson>()
    val authenticationError = MutableLiveData(false)
    val searchStoreQueryLiveData = MutableLiveData<SearchQuery>()
    val postImagesLiveData = MutableLiveData<JsonResponse>()
    val shownInterestLiveData = MutableLiveData<Boolean>()

    private var householdItemRepository: HouseholdItemRepository = HouseholdItemRepository()

    fun getMarketPlace(searchQuery: SearchQuery) {
        Log.i(tag, "Search $searchQuery");
        householdItemRepository.getMarketPlace(
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
        town: String?,
        city: String?,
        address: String?,
        landmark: String?,
        latitude: Double,
        longitude: Double,
        itemConditionEnum: ItemConditionEnum,
        householdItemCategoryEnum: HouseholdItemCategoryEnum
    ) {
        val jsonHouseholdItem = JsonHouseholdItem()
        jsonHouseholdItem.town = town
        jsonHouseholdItem.city = city
        jsonHouseholdItem.address = address
        jsonHouseholdItem.businessType = BusinessTypeEnum.HI
        jsonHouseholdItem.coordinate = doubleArrayOf(latitude, longitude)
        jsonHouseholdItem.productPrice = productPrice * 100
        jsonHouseholdItem.title = title
        jsonHouseholdItem.description = description
        jsonHouseholdItem.landmark = landmark
        jsonHouseholdItem.itemCondition = itemConditionEnum
        jsonHouseholdItem.householdItemCategory = householdItemCategoryEnum
        Log.i(tag, "Post $jsonHouseholdItem")

        householdItemRepository.postMarketPlace(
            UserUtils.getDeviceId(),
            UserUtils.getEmail(),
            UserUtils.getAuth(),
            jsonHouseholdItem,
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
        val jsonHouseholdItem = JsonHouseholdItem()
        jsonHouseholdItem.id = id
        householdItemRepository.initiateCall(
            UserUtils.getDeviceId(),
            UserUtils.getEmail(),
            UserUtils.getAuth(),
            jsonHouseholdItem,
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
        val jsonHouseholdItem = JsonHouseholdItem()
        jsonHouseholdItem.id = id
        householdItemRepository.viewDetails(
            UserUtils.getDeviceId(),
            UserUtils.getEmail(),
            UserUtils.getAuth(),
            jsonHouseholdItem,
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
        householdItemRepository.postMarketPlaceImages(
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

    fun insertHouseHoldItem(context: Context, houseHoldItemEntity: HouseHoldItemEntity?) {
        viewModelScope.launch(Dispatchers.IO) {
            householdItemRepository.insertHouseHoldItem(context, houseHoldItemEntity)
        }
    }

    fun getHouseHoldItem(context: Context): LiveData<List<HouseHoldItemEntity>> {
        return householdItemRepository.getHouseHoldItem(context)
    }

    fun deletePostsLocally(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            householdItemRepository.deletePostsLocally(context)
        }
    }
}
