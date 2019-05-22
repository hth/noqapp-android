package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.common.presenter.AppointmentPresenter;
import com.noqapp.android.merchant.model.response.api.ScheduleApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleApiCalls {
    private static final String TAG = ScheduleApiCalls.class.getSimpleName();

    private static final ScheduleApiUrls scheduleApiUrls;
    private AppointmentPresenter appointmentPresenter;

    public void setAppointmentPresenter(AppointmentPresenter appointmentPresenter) {
        this.appointmentPresenter = appointmentPresenter;
    }

    static {
        scheduleApiUrls = RetrofitClient.getClient().create(ScheduleApiUrls.class);
    }


    public void showSchedule(String did, String mail, String auth, String month, String codeQR) {
        scheduleApiUrls.showSchedule(did, Constants.DEVICE_TYPE, mail, auth, month, codeQR).enqueue(new Callback<JsonScheduleList>() {
            @Override
            public void onResponse(@NonNull Call<JsonScheduleList> call, @NonNull Response<JsonScheduleList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("showSchedule fetch", String.valueOf(response.body()));
                        appointmentPresenter.appointmentResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to fetch showSchedule");
                        appointmentPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        appointmentPresenter.authenticationFailure();
                    } else {
                        appointmentPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonScheduleList> call, @NonNull Throwable t) {
                Log.e("failure showSchedule", t.getLocalizedMessage(), t);
                appointmentPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void scheduleForDay(String did, String mail, String auth, String day, String codeQR) {
        scheduleApiUrls.scheduleForDay(did, Constants.DEVICE_TYPE, mail, auth, day, codeQR).enqueue(new Callback<JsonScheduleList>() {
            @Override
            public void onResponse(@NonNull Call<JsonScheduleList> call, @NonNull Response<JsonScheduleList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("scheduleForDay fetch", String.valueOf(response.body()));
                        appointmentPresenter.appointmentResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to fetch scheduleForDay");
                        appointmentPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        appointmentPresenter.authenticationFailure();
                    } else {
                        appointmentPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonScheduleList> call, @NonNull Throwable t) {
                Log.e("failure scheduleForDay", t.getLocalizedMessage(), t);
                appointmentPresenter.responseErrorPresenter(null);
            }
        });
    }

}

