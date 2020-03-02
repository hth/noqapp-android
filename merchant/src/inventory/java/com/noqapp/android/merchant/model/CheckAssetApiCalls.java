package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.merchant.interfaces.BizNamePresenter;
import com.noqapp.android.merchant.interfaces.CheckAssetPresenter;
import com.noqapp.android.merchant.model.response.api.inventory.CheckAssetApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAssetList;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CheckAsset;
import com.noqapp.android.merchant.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckAssetApiCalls {
    private static final String TAG = CheckAssetApiCalls.class.getSimpleName();
    private static final CheckAssetApiUrls checkAssetApiUrls;
    private CheckAssetPresenter checkAssetPresenter;
    private BizNamePresenter bizNamePresenter;


    public void setCheckAssetPresenter(CheckAssetPresenter checkAssetPresenter) {
        this.checkAssetPresenter = checkAssetPresenter;
    }

    public void setBizNamePresenter(BizNamePresenter bizNamePresenter) {
        this.bizNamePresenter = bizNamePresenter;
    }

    static {
        checkAssetApiUrls = RetrofitClient.getClient().create(CheckAssetApiUrls.class);
    }

    public void bizName(String did, String mail, String auth, CheckAsset checkAsset) {
        checkAssetApiUrls.bizName(did, Constants.DEVICE_TYPE, mail, auth, checkAsset).enqueue(new Callback<CheckAsset>() {
            @Override
            public void onResponse(@NonNull Call<CheckAsset> call, @NonNull Response<CheckAsset> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, String.valueOf(response.body()));
                        bizNamePresenter.bizNameResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to fetch bizName");
                        bizNamePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        bizNamePresenter.authenticationFailure();
                    } else {
                        bizNamePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CheckAsset> call, @NonNull Throwable t) {
                Log.e(TAG, "Failure bizName " + t.getLocalizedMessage(), t);
                bizNamePresenter.responseErrorPresenter(null);
            }
        });
    }

    public void floors(String did, String mail, String auth, CheckAsset checkAsset) {
        checkAssetApiUrls.floors(did, Constants.DEVICE_TYPE, mail, auth, checkAsset).enqueue(new Callback<JsonCheckAssetList>() {
            @Override
            public void onResponse(@NonNull Call<JsonCheckAssetList> call, @NonNull Response<JsonCheckAssetList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, String.valueOf(response.body()));
                        checkAssetPresenter.jsonCheckAssetListResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to fetch floors");
                        checkAssetPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        checkAssetPresenter.authenticationFailure();
                    } else {
                        checkAssetPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonCheckAssetList> call, @NonNull Throwable t) {
                Log.e(TAG, "Failure floors " + t.getLocalizedMessage(), t);
                checkAssetPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void rooms(String did, String mail, String auth, CheckAsset checkAsset) {
        checkAssetApiUrls.rooms(did, Constants.DEVICE_TYPE, mail, auth, checkAsset).enqueue(new Callback<JsonCheckAssetList>() {
            @Override
            public void onResponse(@NonNull Call<JsonCheckAssetList> call, @NonNull Response<JsonCheckAssetList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, String.valueOf(response.body()));
                        checkAssetPresenter.jsonCheckAssetListResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to fetch rooms");
                        checkAssetPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        checkAssetPresenter.authenticationFailure();
                    } else {
                        checkAssetPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonCheckAssetList> call, @NonNull Throwable t) {
                Log.e(TAG, "Failure rooms " + t.getLocalizedMessage(), t);
                checkAssetPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void assetsInRoom(String did, String mail, String auth, CheckAsset checkAsset) {
        checkAssetApiUrls.assetsInRoom(did, Constants.DEVICE_TYPE, mail, auth, checkAsset).enqueue(new Callback<JsonCheckAssetList>() {
            @Override
            public void onResponse(@NonNull Call<JsonCheckAssetList> call, @NonNull Response<JsonCheckAssetList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, String.valueOf(response.body()));
                        checkAssetPresenter.jsonCheckAssetListResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to fetch assetsInRoom");
                        checkAssetPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        checkAssetPresenter.authenticationFailure();
                    } else {
                        checkAssetPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonCheckAssetList> call, @NonNull Throwable t) {
                Log.e(TAG, "Failure assetsInRoom " + t.getLocalizedMessage(), t);
                checkAssetPresenter.responseErrorPresenter(null);
            }
        });
    }
}


