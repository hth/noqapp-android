package com.noqapp.android.client.views.version_2.market_place.householdItem

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.noqapp.android.client.model.response.api.MarketplaceHouseholdItemApi
import com.noqapp.android.client.model.response.api.SearchApi
import com.noqapp.android.client.network.RetrofitClient
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.views.version_2.db.NoQueueAppDB
import com.noqapp.android.common.beans.ErrorEncounteredJson
import com.noqapp.android.common.beans.JsonResponse
import com.noqapp.android.common.beans.marketplace.JsonHouseholdItem
import com.noqapp.android.common.beans.marketplace.MarketplaceElasticList
import com.noqapp.android.common.pojos.HouseHoldItemEntity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HouseholdItemRepository {
    private val TAG = HouseholdItemRepository::class.java.simpleName

    private var searchApi: SearchApi = RetrofitClient.getClient().create(SearchApi::class.java)
    private var marketplaceHouseholdItemApi: MarketplaceHouseholdItemApi = RetrofitClient.getClient().create(MarketplaceHouseholdItemApi::class.java)

    fun getMarketPlace(
        did: String, mail: String, auth: String,
        searchQuery: SearchQuery,
        complete: (MarketplaceElasticList?) -> Unit,
        catch: (ErrorEncounteredJson?) -> Unit,
        authenticationError: () -> Unit
    ) {
        searchApi.marketplace(did, Constants.DEVICE_TYPE, mail, auth, searchQuery)
            .enqueue(object : Callback<MarketplaceElasticList> {
                override fun onResponse(
                    call: Call<MarketplaceElasticList>,
                    response: Response<MarketplaceElasticList>
                ) {
                    if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                        complete(response.body())
                    } else {
                        if (response.code() == Constants.INVALID_CREDENTIAL) {
                            authenticationError()
                        } else {
                            catch(response.body()?.error)
                        }
                    }
                }

                override fun onFailure(call: Call<MarketplaceElasticList>, t: Throwable) {
                    catch(null)
                }
            })
    }

    fun postMarketPlace(
        did: String,
        mail: String,
        auth: String,
        jsonHouseholdItem: JsonHouseholdItem,
        complete: (JsonHouseholdItem?) -> Unit,
        catch: (ErrorEncounteredJson?) -> Unit,
        authenticationError: () -> Unit
    ) {
        marketplaceHouseholdItemApi.postOnMarketplace(did, Constants.DEVICE_TYPE, mail, auth, jsonHouseholdItem)
            .enqueue(object : Callback<JsonHouseholdItem> {
                override fun onResponse(
                    call: Call<JsonHouseholdItem>,
                    response: Response<JsonHouseholdItem>
                ) {
                    if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                        complete(response.body())
                    } else {
                        if (response.code() == Constants.INVALID_CREDENTIAL) {
                            authenticationError()
                        } else {
                            catch(response.body()?.error)
                        }
                    }
                }

                override fun onFailure(call: Call<JsonHouseholdItem>, t: Throwable) {
                    catch(null)
                }

            })
    }

    fun postMarketPlaceImages(
        did: String,
        mail: String,
        auth: String,
        multipartFile: MultipartBody.Part, postId: RequestBody,
        complete: (JsonResponse?) -> Unit,
        catch: (ErrorEncounteredJson?) -> Unit,
        authenticationError: () -> Unit
    ) {
        marketplaceHouseholdItemApi.uploadImage(did, Constants.DEVICE_TYPE, mail, auth, multipartFile, postId).enqueue(
            object  : Callback<JsonResponse> {
                override fun onResponse(
                    call: Call<JsonResponse>,
                    response: Response<JsonResponse>
                ) {
                    if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                        complete(response.body())
                    } else {
                        if (response.code() == Constants.INVALID_CREDENTIAL) {
                            authenticationError()
                        } else {
                            Log.e(TAG, "Failed uploadImage" + response.code())
                            catch(response.body()?.error)
                        }
                    }
                }

                override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                    catch(null)
                }
            }
        )
    }

    fun initiateCall(
        did: String,
        mail: String,
        auth: String,
        jsonHouseholdItem: JsonHouseholdItem,
        complete: () -> Unit,
        catch: (ErrorEncounteredJson?) -> Unit,
        authenticationError: () -> Unit
    ) {
        marketplaceHouseholdItemApi.initiateContact(did, Constants.DEVICE_TYPE, mail, auth, jsonHouseholdItem)
            .enqueue(object : Callback<JsonResponse> {
                override fun onResponse(
                    call: Call<JsonResponse>,
                    response: Response<JsonResponse>
                ) {
                    if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                        complete()
                    } else {
                        if (response.code() == Constants.INVALID_CREDENTIAL) {
                            authenticationError()
                        } else {
                            catch(response.body()?.error)
                        }
                    }
                }

                override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                    catch(null)
                }
            })
    }

    fun viewDetails(
        did: String,
        mail: String,
        auth: String,
        jsonHouseholdItem: JsonHouseholdItem,
        complete: () -> Unit,
        catch: (ErrorEncounteredJson?) -> Unit,
        authenticationError: () -> Unit
    ) {
        marketplaceHouseholdItemApi.viewMarketplace(did, Constants.DEVICE_TYPE, mail, auth, jsonHouseholdItem)
            .enqueue(object : Callback<JsonResponse> {
                override fun onResponse(
                    call: Call<JsonResponse>,
                    response: Response<JsonResponse>
                ) {
                    if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                        complete()
                    } else {
                        if (response.code() == Constants.INVALID_CREDENTIAL) {
                            authenticationError()
                        } else {
                            catch(response.body()?.error)
                        }
                    }
                }

                override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                    catch(null)
                }
            })
    }

    suspend fun insertHouseHoldItem(context: Context, houseHoldItemEntity: HouseHoldItemEntity?) {
        NoQueueAppDB.dbInstance(context).houseHoldItemDao().insertHouseHoldItem(houseHoldItemEntity)
    }

    fun getHouseHoldItem(context: Context): LiveData<List<HouseHoldItemEntity>> {
        return NoQueueAppDB.dbInstance(context).houseHoldItemDao().getHouseHoldItem()
    }

    suspend fun deletePostsLocally(context: Context) {
        NoQueueAppDB.dbInstance(context).houseHoldItemDao().deleteHouseHoldItem()
    }
}