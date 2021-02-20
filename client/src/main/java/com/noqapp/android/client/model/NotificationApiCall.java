package com.noqapp.android.client.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.NotificationApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.Notification;
import com.noqapp.android.common.presenter.NotificationPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationApiCall {
    private final String TAG = RegisterApiCall.class.getSimpleName();
    private static final NotificationApiUrls notificationApiUrls;
    private static final com.noqapp.android.client.model.response.open.NotificationApiUrls notificationApiUrlsOpen;
    private NotificationPresenter notificationPresenter;

    public NotificationApiCall(NotificationPresenter notificationPresenter) {
        this.notificationPresenter = notificationPresenter;
    }

    static {
        notificationApiUrls = RetrofitClient.getClient().create(NotificationApiUrls.class);
        notificationApiUrlsOpen = RetrofitClient.getClient().create(com.noqapp.android.client.model.response.open.NotificationApiUrls.class);
    }

    public void notificationViewed(String did, String mail, String auth, Notification notification) {
        notificationApiUrls.notificationViewed(did, Constants.DEVICE_TYPE, mail, auth, notification).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response notification", String.valueOf(response.body()));
                        notificationPresenter.notificationResponse(response.body());
                    } else {
                        Log.e(TAG, "Error notification" + response.body().getError());
                        notificationPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        notificationPresenter.authenticationFailure();
                    } else {
                        notificationPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("Failure notification", t.getLocalizedMessage(), t);
                notificationPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void notificationViewed(String did, Notification notification) {
        notificationApiUrlsOpen.notificationViewed(did, Constants.DEVICE_TYPE, notification).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response notification", String.valueOf(response.body()));
                        notificationPresenter.notificationResponse(response.body());
                    } else {
                        Log.e(TAG, "Error notification" + response.body().getError());
                        notificationPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        notificationPresenter.authenticationFailure();
                    } else {
                        notificationPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("Failure notification", t.getLocalizedMessage(), t);
                notificationPresenter.responseErrorPresenter(null);
            }
        });
    }
}
