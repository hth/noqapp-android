package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.merchant.model.response.api.StoreSettingApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.body.StoreSetting;
import com.noqapp.android.merchant.presenter.beans.body.merchant.StoreHours;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.StoreHoursSettingPresenter;
import com.noqapp.android.merchant.views.interfaces.StoreSettingPresenter;

import org.apache.commons.lang3.StringUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: chandra
 * Date: 7/15/17 10:27 AM
 */
public class StoreSettingApiCalls {

    private static final String TAG = StoreSettingApiCalls.class.getSimpleName();
    private static final StoreSettingApiUrls storeSettingApiUrls;
    private StoreSettingPresenter storeSettingPresenter;
    private StoreHoursSettingPresenter storeHoursSettingPresenter;

    public StoreSettingApiCalls(StoreSettingPresenter storeSettingPresenter) {
        this.storeSettingPresenter = storeSettingPresenter;
    }

    public StoreSettingApiCalls(StoreHoursSettingPresenter storeHoursSettingPresenter) {
        this.storeHoursSettingPresenter = storeHoursSettingPresenter;
    }

    static {
        storeSettingApiUrls = RetrofitClient.getClient().create(StoreSettingApiUrls.class);
    }

    public void getQueueState(String did, String mail, String auth, String codeQR) {
        storeSettingApiUrls.getQueueState(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<StoreSetting>() {
            @Override
            public void onResponse(@NonNull Call<StoreSetting> call, @NonNull Response<StoreSetting> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("getQueueState", String.valueOf(response.body()));
                        storeSettingPresenter.queueSettingResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while getQueueState");
                        storeSettingPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        storeSettingPresenter.authenticationFailure();
                    } else {
                        storeSettingPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<StoreSetting> call, @NonNull Throwable t) {
                Log.e("fail getQueueState", t.getLocalizedMessage(), t);
                storeSettingPresenter.queueSettingError();
            }
        });
    }

    public void removeSchedule(String did, String mail, String auth, String codeQR) {
        storeSettingApiUrls.removeSchedule(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<StoreSetting>() {
            @Override
            public void onResponse(@NonNull Call<StoreSetting> call, @NonNull Response<StoreSetting> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("removeSchedule", String.valueOf(response.body()));
                        storeSettingPresenter.queueSettingModifyResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while removeSchedule");
                        storeSettingPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        storeSettingPresenter.authenticationFailure();
                    } else {
                        storeSettingPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<StoreSetting> call, @NonNull Throwable t) {
                Log.e("fail removeSchedule", t.getLocalizedMessage(), t);
                storeSettingPresenter.queueSettingError();
            }
        });
    }


    public void modify(String did, String mail, String auth, StoreSetting storeSetting) {
        storeSettingApiUrls.modify(did, Constants.DEVICE_TYPE, mail, auth, storeSetting).enqueue(new Callback<StoreSetting>() {
            @Override
            public void onResponse(@NonNull Call<StoreSetting> call, @NonNull Response<StoreSetting> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        if (StringUtils.isNotBlank(response.body().getCodeQR())) {
                            Log.d(TAG, "Modify setting, response " + response.body().toString());
                            storeSettingPresenter.queueSettingModifyResponse(response.body());
                        } else {
                            Log.e(TAG, "Failed to modify setting");
                            storeSettingPresenter.queueSettingError();
                        }
                    } else if (response.body() != null && response.body().getError() != null) {
                        ErrorEncounteredJson errorEncounteredJson = response.body().getError();
                        Log.e(TAG, "Got error" + errorEncounteredJson.getReason());
                        storeSettingPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        storeSettingPresenter.authenticationFailure();
                    } else {
                        storeSettingPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<StoreSetting> call, @NonNull Throwable t) {
                Log.e("fail modify", t.getLocalizedMessage(), t);
                storeSettingPresenter.queueSettingError();
            }
        });
    }

    public void serviceCost(String did, String mail, String auth, StoreSetting storeSetting) {
        storeSettingApiUrls.serviceCost(did, Constants.DEVICE_TYPE, mail, auth, storeSetting).enqueue(new Callback<StoreSetting>() {
            @Override
            public void onResponse(@NonNull Call<StoreSetting> call, @NonNull Response<StoreSetting> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        if (StringUtils.isNotBlank(response.body().getCodeQR())) {
                            Log.d(TAG, "serviceCost setting, response" + response.body().toString());
                            storeSettingPresenter.queueSettingModifyResponse(response.body());
                        } else {
                            Log.e(TAG, "Failed to serviceCost setting");
                            storeSettingPresenter.queueSettingError();
                        }
                    } else if (response.body() != null && response.body().getError() != null) {
                        ErrorEncounteredJson errorEncounteredJson = response.body().getError();
                        Log.e(TAG, "Got error serviceCost" + errorEncounteredJson.getReason());
                        storeSettingPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        storeSettingPresenter.authenticationFailure();
                    } else {
                        storeSettingPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<StoreSetting> call, @NonNull Throwable t) {
                Log.e("fail serviceCost", t.getLocalizedMessage(), t);
                storeSettingPresenter.queueSettingError();
            }
        });
    }

    public void appointment(String did, String mail, String auth, StoreSetting storeSetting) {
        storeSettingApiUrls.appointment(did, Constants.DEVICE_TYPE, mail, auth, storeSetting).enqueue(new Callback<StoreSetting>() {
            @Override
            public void onResponse(@NonNull Call<StoreSetting> call, @NonNull Response<StoreSetting> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        if (StringUtils.isNotBlank(response.body().getCodeQR())) {
                            Log.d(TAG, "appointment setting, response" + response.body().toString());
                            storeSettingPresenter.queueSettingModifyResponse(response.body());
                        } else {
                            Log.e(TAG, "Failed to appointment setting");
                            storeSettingPresenter.queueSettingError();
                        }
                    } else if (response.body() != null && response.body().getError() != null) {
                        ErrorEncounteredJson errorEncounteredJson = response.body().getError();
                        Log.e(TAG, "Got error appointment" + errorEncounteredJson.getReason());
                        storeSettingPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        storeSettingPresenter.authenticationFailure();
                    } else {
                        storeSettingPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<StoreSetting> call, @NonNull Throwable t) {
                Log.e("fail appointment", t.getLocalizedMessage(), t);
                storeSettingPresenter.queueSettingError();
            }
        });
    }

    public void storeHours(String did, String mail, String auth, String codeQR) {
        storeSettingApiUrls.storeHours(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<StoreHours>() {
            @Override
            public void onResponse(@NonNull Call<StoreHours> call, @NonNull Response<StoreHours> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        if (StringUtils.isNotBlank(response.body().getCodeQR())) {
                            Log.d(TAG, "storeHours setting, response " + response.body().toString());
                            storeHoursSettingPresenter.queueStoreHoursSettingResponse(response.body());
                        } else {
                            Log.e(TAG, "Failed to storeHours setting");
                            storeHoursSettingPresenter.queueStoreHoursSettingError();
                        }
                    } else if (response.body() != null && response.body().getError() != null) {
                        ErrorEncounteredJson errorEncounteredJson = response.body().getError();
                        Log.e(TAG, "Got error" + errorEncounteredJson.getReason());
                        storeHoursSettingPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        storeHoursSettingPresenter.authenticationFailure();
                    } else {
                        storeHoursSettingPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<StoreHours> call, @NonNull Throwable t) {
                Log.e("fail storeHours", t.getLocalizedMessage(), t);
                storeHoursSettingPresenter.queueStoreHoursSettingError();
            }
        });
    }

    public void storeHoursUpdate(String did, String mail, String auth, StoreHours storeHours) {
        storeSettingApiUrls.storeHoursUpdate(did, Constants.DEVICE_TYPE, mail, auth, storeHours).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                       // if (StringUtils.isNotBlank(response.body().getCodeQR())) {
                            Log.d(TAG, "Modify setting, response " + response.body().toString());
                            storeHoursSettingPresenter.queueStoreHoursSettingModifyResponse(response.body());
//                        } else {
//                            Log.e(TAG, "Failed to modify setting");
//                            storeHoursSettingPresenter.queueStoreHoursSettingError();
//                        }
                    } else if (response.body() != null && response.body().getError() != null) {
                        ErrorEncounteredJson errorEncounteredJson = response.body().getError();
                        Log.e(TAG, "Got error" + errorEncounteredJson.getReason());
                        storeHoursSettingPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        storeHoursSettingPresenter.authenticationFailure();
                    } else {
                        storeHoursSettingPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("fail modify", t.getLocalizedMessage(), t);
                storeHoursSettingPresenter.queueStoreHoursSettingError();
            }
        });
    }

    public void notifyFreshStockArrival(String did, String mail, String auth) {
        storeSettingApiUrls.notifyFreshStockArrival(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, "notifyFreshStockArrival, response " + response.body().toString());
                        storeHoursSettingPresenter.queueStoreHoursSettingModifyResponse(response.body());
                    } else if (response.body() != null && response.body().getError() != null) {
                        ErrorEncounteredJson errorEncounteredJson = response.body().getError();
                        Log.e(TAG, "Got error" + errorEncounteredJson.getReason());
                        storeHoursSettingPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        storeHoursSettingPresenter.authenticationFailure();
                    } else {
                        storeHoursSettingPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("fail notifyFreshStock", t.getLocalizedMessage(), t);
                storeHoursSettingPresenter.queueStoreHoursSettingError();
            }
        });
    }
}
