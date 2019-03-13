package com.noqapp.android.merchant.model;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.merchant.model.response.api.QueueSettingApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.body.QueueSetting;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.QueueSettingPresenter;

import org.apache.commons.lang3.StringUtils;

import androidx.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: chandra
 * Date: 7/15/17 10:27 AM
 */
public class QueueSettingApiCalls {

    private static final String TAG = QueueSettingApiCalls.class.getSimpleName();
    private static final QueueSettingApiUrls queueSettingApiUrls;
    private QueueSettingPresenter queueSettingPresenter;

    public QueueSettingApiCalls(QueueSettingPresenter queueSettingPresenter) {
        this.queueSettingPresenter = queueSettingPresenter;
    }

    static {
        queueSettingApiUrls = RetrofitClient.getClient().create(QueueSettingApiUrls.class);
    }

    public void getQueueState(String did, String mail, String auth, String codeQR) {
        queueSettingApiUrls.getQueueState(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<QueueSetting>() {
            @Override
            public void onResponse(@NonNull Call<QueueSetting> call, @NonNull Response<QueueSetting> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("getQueueState", String.valueOf(response.body()));
                        queueSettingPresenter.queueSettingResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while getQueueState");
                        queueSettingPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        queueSettingPresenter.authenticationFailure();
                    } else {
                        queueSettingPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QueueSetting> call, @NonNull Throwable t) {
                Log.e("fail getQueueState", t.getLocalizedMessage(), t);
                queueSettingPresenter.queueSettingError();
            }
        });
    }

    public void removeSchedule(String did, String mail, String auth, String codeQR) {
        queueSettingApiUrls.removeSchedule(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<QueueSetting>() {
            @Override
            public void onResponse(@NonNull Call<QueueSetting> call, @NonNull Response<QueueSetting> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("removeSchedule", String.valueOf(response.body()));
                        queueSettingPresenter.queueSettingResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while removeSchedule");
                        queueSettingPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        queueSettingPresenter.authenticationFailure();
                    } else {
                        queueSettingPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QueueSetting> call, @NonNull Throwable t) {
                Log.e("fail removeSchedule", t.getLocalizedMessage(), t);
                queueSettingPresenter.queueSettingError();
            }
        });
    }


    public void modify(String did, String mail, String auth, QueueSetting queueSetting) {
        queueSettingApiUrls.modify(did, Constants.DEVICE_TYPE, mail, auth, queueSetting).enqueue(new Callback<QueueSetting>() {
            @Override
            public void onResponse(@NonNull Call<QueueSetting> call, @NonNull Response<QueueSetting> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        if (StringUtils.isNotBlank(response.body().getCodeQR())) {
                            Log.d(TAG, "Modify setting, response jsonToken" + response.body().toString());
                            queueSettingPresenter.queueSettingResponse(response.body());
                        } else {
                            Log.e(TAG, "Failed to modify setting");
                            queueSettingPresenter.queueSettingError();
                        }
                    } else if (response.body() != null && response.body().getError() != null) {
                        ErrorEncounteredJson errorEncounteredJson = response.body().getError();
                        Log.e(TAG, "Got error" + errorEncounteredJson.getReason());
                        queueSettingPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        queueSettingPresenter.authenticationFailure();
                    } else {
                        queueSettingPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QueueSetting> call, @NonNull Throwable t) {
                Log.e("fail modify", t.getLocalizedMessage(), t);
                queueSettingPresenter.queueSettingError();
            }
        });
    }

    public void serviceCost(String did, String mail, String auth, QueueSetting queueSetting) {
        queueSettingApiUrls.serviceCost(did, Constants.DEVICE_TYPE, mail, auth, queueSetting).enqueue(new Callback<QueueSetting>() {
            @Override
            public void onResponse(@NonNull Call<QueueSetting> call, @NonNull Response<QueueSetting> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        if (StringUtils.isNotBlank(response.body().getCodeQR())) {
                            Log.d(TAG, "serviceCost setting, response jsonToken" + response.body().toString());
                            queueSettingPresenter.queueSettingResponse(response.body());
                        } else {
                            Log.e(TAG, "Failed to serviceCost setting");
                            queueSettingPresenter.queueSettingError();
                        }
                    } else if (response.body() != null && response.body().getError() != null) {
                        ErrorEncounteredJson errorEncounteredJson = response.body().getError();
                        Log.e(TAG, "Got errorserviceCost" + errorEncounteredJson.getReason());
                        queueSettingPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        queueSettingPresenter.authenticationFailure();
                    } else {
                        queueSettingPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QueueSetting> call, @NonNull Throwable t) {
                Log.e("fail serviceCost", t.getLocalizedMessage(), t);
                queueSettingPresenter.queueSettingError();
            }
        });
    }
}
