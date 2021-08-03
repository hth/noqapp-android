package com.noqapp.android.client.model.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.Appointment;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.common.presenter.AppointmentPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentImpl {
    private static final String TAG = AppointmentImpl.class.getSimpleName();

    private static final Appointment APPOINTMENT;
    private AppointmentPresenter appointmentPresenter;

    public void setAppointmentPresenter(AppointmentPresenter appointmentPresenter) {
        this.appointmentPresenter = appointmentPresenter;
    }

    static {
        APPOINTMENT = RetrofitClient.getClient().create(Appointment.class);
    }

    public void scheduleForMonth(String did, String mail, String auth, String month, String codeQR) {
        APPOINTMENT.scheduleForMonth(did, Constants.DEVICE_TYPE, mail, auth, month, codeQR).enqueue(new Callback<JsonScheduleList>() {
            @Override
            public void onResponse(@NonNull Call<JsonScheduleList> call, @NonNull Response<JsonScheduleList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, "showSchedule fetch " + response.body());
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
        APPOINTMENT.scheduleForDay(did, Constants.DEVICE_TYPE, mail, auth, day, codeQR).enqueue(new Callback<JsonScheduleList>() {
            @Override
            public void onResponse(@NonNull Call<JsonScheduleList> call, @NonNull Response<JsonScheduleList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, "scheduleForDay fetch " + response.body());
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

    public void bookAppointment(String did, String mail, String auth, JsonSchedule jsonSchedule) {
        try {
            APPOINTMENT.bookAppointment(did, Constants.DEVICE_TYPE, mail, auth, jsonSchedule).enqueue(new Callback<JsonSchedule>() {
                @Override
                public void onResponse(@NonNull Call<JsonSchedule> call, @NonNull Response<JsonSchedule> response) {
                    if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                        if (null != response.body() && null == response.body().getError()) {
                            Log.d(TAG, "bookAppointment fetch " + response.body());
                            appointmentPresenter.appointmentBookingResponse(response.body());
                        } else {
                            Log.e(TAG, "Failed to bookAppointment");
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
                    Log.e(TAG, "Failure bookAppointment " + t.getLocalizedMessage(), t);
                    appointmentPresenter.responseErrorPresenter(null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelAppointment(String did, String mail, String auth, JsonSchedule jsonSchedule) {
        APPOINTMENT.cancelAppointment(did, Constants.DEVICE_TYPE, mail, auth, jsonSchedule).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, "cancelAppointment fetch " + response.body());
                        appointmentPresenter.appointmentCancelResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to cancelAppointment");
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
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Failure cancelAppointment " + t.getLocalizedMessage(), t);
                appointmentPresenter.responseErrorPresenter(null);
            }
        });
    }


    public void allAppointments(String did, String mail, String auth) {
        APPOINTMENT.allAppointments(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonScheduleList>() {
            @Override
            public void onResponse(@NonNull Call<JsonScheduleList> call, @NonNull Response<JsonScheduleList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, "allAppointments fetch " + response.body());
                        appointmentPresenter.appointmentResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to fetch allAppointments");
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
                Log.e(TAG, "Failure allAppointments " + t.getLocalizedMessage(), t);
                appointmentPresenter.responseErrorPresenter(null);
            }
        });
    }


    public void allPastAppointments(String did, String mail, String auth) {
        APPOINTMENT.allPastAppointments(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonScheduleList>() {
            @Override
            public void onResponse(@NonNull Call<JsonScheduleList> call, @NonNull Response<JsonScheduleList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, "allPastAppointments fetch " + response.body());
                        appointmentPresenter.appointmentResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to fetch allPastAppointments");
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
                Log.e(TAG, "Failure allPastAppointments " + t.getLocalizedMessage(), t);
                appointmentPresenter.responseErrorPresenter(null);
            }
        });
    }
}


