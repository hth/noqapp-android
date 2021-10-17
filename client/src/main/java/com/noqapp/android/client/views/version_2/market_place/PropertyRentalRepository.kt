package com.noqapp.android.client.views.version_2.market_place

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.noqapp.android.client.model.response.api.MarketplacePropertyRentalApi
import com.noqapp.android.client.model.response.api.SearchApi
import com.noqapp.android.client.network.RetrofitClient
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.views.version_2.db.NoQueueAppDB
import com.noqapp.android.common.beans.ErrorEncounteredJson
import com.noqapp.android.common.beans.JsonResponse
import com.noqapp.android.common.beans.marketplace.JsonPropertyRental
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.noqapp.android.common.beans.marketplace.MarketplaceElasticList
import com.noqapp.android.common.pojos.PropertyRentalEntity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PropertyRentalRepository {
    private val TAG = PropertyRentalRepository::class.java.simpleName

    private var searchApi: SearchApi = RetrofitClient.getClient().create(SearchApi::class.java)
    private var marketplacePropertyRentalApi: MarketplacePropertyRentalApi = RetrofitClient.getClient().create(MarketplacePropertyRentalApi::class.java)

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
        jsonPropertyRental: JsonPropertyRental,
        complete: (MarketplaceElastic?) -> Unit,
        catch: (ErrorEncounteredJson?) -> Unit,
        authenticationError: () -> Unit
    ) {
        marketplacePropertyRentalApi.postOnMarketplace(did, Constants.DEVICE_TYPE, mail, auth, jsonPropertyRental)
            .enqueue(object : Callback<MarketplaceElastic> {
                override fun onResponse(
                    call: Call<MarketplaceElastic>,
                    response: Response<MarketplaceElastic>
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

                override fun onFailure(call: Call<MarketplaceElastic>, t: Throwable) {
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
        marketplacePropertyRentalApi.uploadImage(did, Constants.DEVICE_TYPE, mail, auth, multipartFile, postId).enqueue(
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
        jsonPropertyRental: JsonPropertyRental,
        complete: () -> Unit,
        catch: (ErrorEncounteredJson?) -> Unit,
        authenticationError: () -> Unit
    ) {
        marketplacePropertyRentalApi.initiateContact(did, Constants.DEVICE_TYPE, mail, auth, jsonPropertyRental)
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
        jsonPropertyRental: JsonPropertyRental,
        complete: () -> Unit,
        catch: (ErrorEncounteredJson?) -> Unit,
        authenticationError: () -> Unit
    ) {
        marketplacePropertyRentalApi.viewMarketplace(did, Constants.DEVICE_TYPE, mail, auth, jsonPropertyRental)
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

    suspend fun insertPropertyRental(context: Context, propertyRentalEntity: PropertyRentalEntity?) {
        NoQueueAppDB.dbInstance(context).propertyRentalDao().insertPropertyRental(propertyRentalEntity)
    }

    fun getPropertyRental(context: Context): LiveData<List<PropertyRentalEntity>> {
        return NoQueueAppDB.dbInstance(context).propertyRentalDao().getPropertyRental()
    }

    suspend fun deletePostsLocally(context: Context, propertyRentalEntity: PropertyRentalEntity?) {
        NoQueueAppDB.dbInstance(context).propertyRentalDao().deletePropertyRental(propertyRentalEntity)
    }
}
