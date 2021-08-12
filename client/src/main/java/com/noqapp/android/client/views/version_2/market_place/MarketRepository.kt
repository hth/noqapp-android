package com.noqapp.android.client.views.version_2.market_place

import com.noqapp.android.client.model.response.api.SearchApi
import com.noqapp.android.client.network.RetrofitClient
import com.noqapp.android.client.presenter.beans.body.SearchQuery
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.common.beans.ErrorEncounteredJson
import com.noqapp.android.common.beans.marketplace.MarketplaceElasticList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarketRepository {

    private var searchApi: SearchApi = RetrofitClient.getClient().create(SearchApi::class.java)

    fun getMarketPlace(
        did: String, mail: String, auth: String,
        searchQuery: SearchQuery,
        complete: (MarketplaceElasticList?) -> Unit,
        catch: (ErrorEncounteredJson?) -> Unit,
        authenticationError: () -> Unit
    ) {
        searchApi.marketplace(did, Constants.DEVICE_TYPE, mail, auth, searchQuery).enqueue(object : Callback<MarketplaceElasticList> {
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

}