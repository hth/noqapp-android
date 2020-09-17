package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.common.presenter.AppointmentPresenter;
import com.noqapp.android.merchant.model.response.api.ScheduleApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.body.merchant.BookSchedule;
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

    public void scheduleForMonth(String did, String mail, String auth, String month, String codeQR) {
        scheduleApiUrls.scheduleForMonth(did, Constants.DEVICE_TYPE, mail, auth, month, codeQR).enqueue(new Callback<JsonScheduleList>() {
            @Override
            public void onResponse(@NonNull Call<JsonScheduleList> call, @NonNull Response<JsonScheduleList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, String.valueOf(response.body()));
                        appointmentPresenter.appointmentResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to fetch scheduleForMonth");
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
                Log.e(TAG, "Failure scheduleForMonth " + t.getLocalizedMessage(), t);
                appointmentPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void scheduleForDay(String did, String mail, String auth, String day, String codeQR) {
        scheduleApiUrls.scheduleForDay(did, Constants.DEVICE_TYPE, mail, auth, day, codeQR).enqueue(new Callback<JsonScheduleList>() {
            @Override
            public void onResponse(@NonNull Call<JsonScheduleList> call, @NonNull Response<JsonScheduleList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, "scheduleForDay fetch " + String.valueOf(response.body()));
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
                Log.e(TAG, "Failure scheduleForDay " + t.getLocalizedMessage(), t);
                appointmentPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void scheduleAction(String did, String mail, String auth, JsonSchedule jsonSchedule) {
        scheduleApiUrls.scheduleAction(did, Constants.DEVICE_TYPE, mail, auth, jsonSchedule).enqueue(new Callback<JsonSchedule>() {
            @Override
            public void onResponse(@NonNull Call<JsonSchedule> call, @NonNull Response<JsonSchedule> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, "scheduleAction fetch " + String.valueOf(response.body()));
                        appointmentPresenter.appointmentAcceptRejectResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to fetch scheduleAction");
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
            public void onFailure(@NonNull Call<JsonSchedule> call, @NonNull Throwable t) {
                Log.e(TAG, "Failure scheduleAction " + t.getLocalizedMessage(), t);
                appointmentPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void bookSchedule(String did, String mail, String auth, BookSchedule bookSchedule) {
        scheduleApiUrls.bookSchedule(did, Constants.DEVICE_TYPE, mail, auth, bookSchedule).enqueue(new Callback<JsonSchedule>() {
            @Override
            public void onResponse(@NonNull Call<JsonSchedule> call, @NonNull Response<JsonSchedule> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, "bookSchedule fetch " + String.valueOf(response.body()));
                        appointmentPresenter.appointmentBookingResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to fetch bookSchedule");
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
            public void onFailure(@NonNull Call<JsonSchedule> call, @NonNull Throwable t) {
                Log.e(TAG, "Failure bookSchedule " + t.getLocalizedMessage(), t);
                appointmentPresenter.responseErrorPresenter(null);
            }
        });
    }
}

