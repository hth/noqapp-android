package com.noqapp.android.merchant.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.merchant.model.response.api.QueueSettingService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.ErrorEncounteredJson;
import com.noqapp.android.merchant.presenter.beans.body.QueueSetting;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.QueueSettingPresenter;

import org.apache.commons.lang3.StringUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: chandra
 * Date: 7/15/17 10:27 AM
 */
public class QueueSettingModel {
    private static final String TAG = QueueSettingModel.class.getSimpleName();

    private static final QueueSettingService queueSettingService;
    public static QueueSettingPresenter queueSettingPresenter;

    static {
        queueSettingService = RetrofitClient.getClient().create(QueueSettingService.class);
    }

    /**
     * Get setting for a specific queue.
     *
     * @param did
     * @param mail
     * @param auth
     */
    public static void getQueueState(String did, String mail, String auth, String codeQR) {
        queueSettingService.getQueueState(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<QueueSetting>() {
            @Override
            public void onResponse(@NonNull Call<QueueSetting> call, @NonNull Response<QueueSetting> response) {
                if (response.code() == 401) {
                    queueSettingPresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Get queue setting", String.valueOf(response.body()));
                    queueSettingPresenter.queueSettingResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Found error while get queue setting");
                }
            }

            @Override
            public void onFailure(@NonNull Call<QueueSetting> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                queueSettingPresenter.queueSettingError();
            }
        });
    }

    /**
     * @param did
     * @param mail
     * @param auth
     */
    public static void modify(String did, String mail, String auth, QueueSetting queueSetting) {
        queueSettingService.modify(did, Constants.DEVICE_TYPE, mail, auth, queueSetting).enqueue(new Callback<QueueSetting>() {
            @Override
            public void onResponse(@NonNull Call<QueueSetting> call, @NonNull Response<QueueSetting> response) {
                if (response.code() == 401) {
                    queueSettingPresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    if (StringUtils.isNotBlank(response.body().getCodeQR())) {
                        Log.d(TAG, "Modify setting, response jsonToken" + response.body().toString());
                        queueSettingPresenter.queueSettingResponse(response.body());
                    } else {
                        //TODO something logical
                        Log.e(TAG, "Failed to modify setting");
                    }
                } else if (response.body() != null && response.body().getError() != null) {
                    ErrorEncounteredJson errorEncounteredJson = response.body().getError();
                    Log.e(TAG, "Got error" + errorEncounteredJson.getReason());
                }
            }

            @Override
            public void onFailure(@NonNull Call<QueueSetting> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
            }
        });
    }
}
