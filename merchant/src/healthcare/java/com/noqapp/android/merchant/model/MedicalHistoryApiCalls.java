package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitScheduleList;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.common.presenter.HospitalVisitSchedulePresenter;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.merchant.interfaces.JsonMedicalRecordPresenter;
import com.noqapp.android.merchant.interfaces.UpdateObservationPresenter;
import com.noqapp.android.merchant.model.response.api.health.MedicalRecordApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CodeQRDateRangeLookup;
import com.noqapp.android.merchant.presenter.beans.body.merchant.FindMedicalProfile;
import com.noqapp.android.merchant.presenter.beans.body.merchant.HospitalVisitFor;
import com.noqapp.android.merchant.presenter.beans.body.merchant.LabFile;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.MedicalRecordListPresenter;
import com.noqapp.android.merchant.views.interfaces.MedicalRecordPresenter;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicalHistoryApiCalls {
    private static final String TAG = MedicalHistoryApiCalls.class.getSimpleName();

    private static final MedicalRecordApiUrls medicalRecordApiUrls;
    private MedicalRecordPresenter medicalRecordPresenter;
    private MedicalRecordListPresenter medicalRecordListPresenter;
    private QueuePersonListPresenter queuePersonListPresenter;
    private JsonMedicalRecordPresenter jsonMedicalRecordPresenter;
    private UpdateObservationPresenter updateObservationPresenter;
    private ImageUploadPresenter imageUploadPresenter;
    private HospitalVisitSchedulePresenter hospitalVisitSchedulePresenter;


    public MedicalHistoryApiCalls(HospitalVisitSchedulePresenter hospitalVisitSchedulePresenter) {
        this.hospitalVisitSchedulePresenter = hospitalVisitSchedulePresenter;
    }

    public MedicalHistoryApiCalls(UpdateObservationPresenter updateObservationPresenter) {
        this.updateObservationPresenter = updateObservationPresenter;
    }

    public MedicalHistoryApiCalls() {

    }

    public void setJsonMedicalRecordPresenter(JsonMedicalRecordPresenter jsonMedicalRecordPresenter) {
        this.jsonMedicalRecordPresenter = jsonMedicalRecordPresenter;
    }

    public MedicalHistoryApiCalls(MedicalRecordListPresenter medicalRecordListPresenter) {
        this.medicalRecordListPresenter = medicalRecordListPresenter;
    }

    public MedicalHistoryApiCalls(QueuePersonListPresenter queuePersonListPresenter) {
        this.queuePersonListPresenter = queuePersonListPresenter;
    }

    public MedicalHistoryApiCalls(ImageUploadPresenter imageUploadPresenter) {
        this.imageUploadPresenter = imageUploadPresenter;
    }

    public MedicalHistoryApiCalls(MedicalRecordPresenter medicalRecordPresenter) {
        this.medicalRecordPresenter = medicalRecordPresenter;
    }

    static {
        medicalRecordApiUrls = RetrofitClient.getClient().create(MedicalRecordApiUrls.class);
    }

    /**
     * @param did
     * @param mail
     * @param auth
     */
    public void update(String did, String mail, String auth, JsonMedicalRecord jsonMedicalRecord) {
        medicalRecordApiUrls.update(did, Constants.DEVICE_TYPE, mail, auth, jsonMedicalRecord).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("update", String.valueOf(response.body()));
                        medicalRecordPresenter.medicalRecordResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to update");
                        medicalRecordPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        medicalRecordPresenter.authenticationFailure();
                    } else {
                        medicalRecordPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("on Failure update", t.getLocalizedMessage(), t);
                medicalRecordPresenter.medicalRecordError();
            }
        });
    }


    public void historicalFiltered(String did, String mail, String auth, String medicalDepartment, FindMedicalProfile findMedicalProfile) {
        medicalRecordApiUrls.historicalFiltered(did, Constants.DEVICE_TYPE, mail, auth, medicalDepartment, findMedicalProfile).enqueue(new Callback<JsonMedicalRecordList>() {
            @Override
            public void onResponse(@NonNull Call<JsonMedicalRecordList> call, @NonNull Response<JsonMedicalRecordList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("historicalFiltered", String.valueOf(response.body()));
                        medicalRecordListPresenter.medicalRecordDentalListResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to fetch historicalFiltered");
                        medicalRecordListPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        medicalRecordListPresenter.authenticationFailure();
                    } else {
                        medicalRecordListPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonMedicalRecordList> call, @NonNull Throwable t) {
                Log.e("historicalFiltered Fail", t.getLocalizedMessage(), t);
                medicalRecordListPresenter.medicalRecordListError();
            }
        });
    }

    public void workHistory(String did, String mail, String auth, CodeQRDateRangeLookup codeQRDateRangeLookup) {
        medicalRecordApiUrls.workHistory(did, Constants.DEVICE_TYPE, mail, auth, codeQRDateRangeLookup).enqueue(new Callback<JsonMedicalRecordList>() {
            @Override
            public void onResponse(@NonNull Call<JsonMedicalRecordList> call, @NonNull Response<JsonMedicalRecordList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("workHistory", String.valueOf(response.body()));
                        medicalRecordListPresenter.medicalRecordListResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to fetch workHistory");
                        medicalRecordListPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        medicalRecordListPresenter.authenticationFailure();
                    } else {
                        medicalRecordListPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonMedicalRecordList> call, @NonNull Throwable t) {
                Log.e("workHistory Fail", t.getLocalizedMessage(), t);
                medicalRecordListPresenter.medicalRecordListError();
            }
        });
    }


    public void historical(String did, String mail, String auth, FindMedicalProfile findMedicalProfile) {
        medicalRecordApiUrls.historical(did, Constants.DEVICE_TYPE, mail, auth, findMedicalProfile).enqueue(new Callback<JsonMedicalRecordList>() {
            @Override
            public void onResponse(@NonNull Call<JsonMedicalRecordList> call, @NonNull Response<JsonMedicalRecordList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("historical", String.valueOf(response.body()));
                        medicalRecordListPresenter.medicalRecordListResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to fetch historical");
                        medicalRecordListPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        medicalRecordListPresenter.authenticationFailure();
                    } else {
                        medicalRecordListPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonMedicalRecordList> call, @NonNull Throwable t) {
                Log.e("historical onFailure", t.getLocalizedMessage(), t);
                medicalRecordListPresenter.medicalRecordListError();
            }
        });
    }

    public void getFollowUpList(String mail, String auth, String codeQR) {
        medicalRecordApiUrls.followUp(mail, auth, codeQR).enqueue(new Callback<JsonQueuePersonList>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueuePersonList> call, @NonNull Response<JsonQueuePersonList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("FollowUpList response", String.valueOf(response.body()));
                        queuePersonListPresenter.queuePersonListResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while FollowUpList");
                        queuePersonListPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        queuePersonListPresenter.authenticationFailure();
                    } else {
                        queuePersonListPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonQueuePersonList> call, @NonNull Throwable t) {
                Log.e("FollowUpList error", t.getLocalizedMessage(), t);
                queuePersonListPresenter.queuePersonListError();
            }
        });
    }

    public void retrieveMedicalRecord(String did, String mail, String auth, JsonMedicalRecord jsonMedicalRecord) {
        medicalRecordApiUrls.retrieve(did, Constants.DEVICE_TYPE, mail, auth, jsonMedicalRecord).enqueue(new Callback<JsonMedicalRecord>() {
            @Override
            public void onResponse(@NonNull Call<JsonMedicalRecord> call, @NonNull Response<JsonMedicalRecord> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("retrieve response", String.valueOf(response.body()));
                        jsonMedicalRecordPresenter.jsonMedicalRecordResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while retrieve");
                        jsonMedicalRecordPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        jsonMedicalRecordPresenter.authenticationFailure();
                    } else {
                        jsonMedicalRecordPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonMedicalRecord> call, @NonNull Throwable t) {
                Log.e("retrieve error", t.getLocalizedMessage(), t);
                jsonMedicalRecordPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void existsMedicalRecord(String did, String mail, String auth, String codeQR, String recordReferenceId) {
        medicalRecordApiUrls.exists(did, Constants.DEVICE_TYPE, mail, auth, codeQR, recordReferenceId).enqueue(new Callback<JsonMedicalRecord>() {
            @Override
            public void onResponse(@NonNull Call<JsonMedicalRecord> call, @NonNull Response<JsonMedicalRecord> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("retrieve response", String.valueOf(response.body()));
                        jsonMedicalRecordPresenter.jsonMedicalRecordResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while retrieve");
                        jsonMedicalRecordPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        jsonMedicalRecordPresenter.authenticationFailure();
                    } else {
                        jsonMedicalRecordPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonMedicalRecord> call, @NonNull Throwable t) {
                Log.e("retrieve error", t.getLocalizedMessage(), t);
                jsonMedicalRecordPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void appendImage(String did, String mail, String auth, MultipartBody.Part profileImageFile, RequestBody recordReferenceId) {
        medicalRecordApiUrls.appendImage(did, Constants.DEVICE_TYPE, mail, auth, profileImageFile, recordReferenceId).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("upload", String.valueOf(response.body()));
                        imageUploadPresenter.imageUploadResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed image upload");
                        imageUploadPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        imageUploadPresenter.authenticationFailure();
                    } else {
                        imageUploadPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("upload", t.getLocalizedMessage(), t);
                imageUploadPresenter.imageUploadError();
            }
        });
    }

    public void updateObservation(String did, String mail, String auth, LabFile labFile) {
        medicalRecordApiUrls.updateObservation(did, Constants.DEVICE_TYPE, mail, auth, labFile).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("updateObservation", String.valueOf(response.body()));
                        updateObservationPresenter.updateObservationResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed updateObservation");
                        updateObservationPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        updateObservationPresenter.authenticationFailure();
                    } else {
                        updateObservationPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("onFailUpdateObservation", t.getLocalizedMessage(), t);
                updateObservationPresenter.responseErrorPresenter(null);
            }
        });
    }


    public void removeImage(String did, String mail, String auth, JsonMedicalRecord jsonMedicalRecord) {
        medicalRecordApiUrls.removeImage(did, Constants.DEVICE_TYPE, mail, auth, jsonMedicalRecord).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("removeImage", String.valueOf(response.body()));
                        imageUploadPresenter.imageRemoveResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to removeImage");
                        imageUploadPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        imageUploadPresenter.authenticationFailure();
                    } else {
                        imageUploadPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("on Failure removeImage", t.getLocalizedMessage(), t);
                imageUploadPresenter.imageUploadError();
            }
        });
    }


    public void hospitalVisitSchedule(String did, String mail, String auth, FindMedicalProfile findMedicalProfile) {
        medicalRecordApiUrls.hospitalVisitSchedule(did, Constants.DEVICE_TYPE, mail, auth, findMedicalProfile).enqueue(new Callback<JsonHospitalVisitScheduleList>() {
            @Override
            public void onResponse(@NonNull Call<JsonHospitalVisitScheduleList> call, @NonNull Response<JsonHospitalVisitScheduleList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Resp hospitalVSchedule", String.valueOf(response.body()));
                        hospitalVisitSchedulePresenter.hospitalVisitScheduleResponse(response.body());
                    } else {
                        hospitalVisitSchedulePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        hospitalVisitSchedulePresenter.authenticationFailure();
                    } else {
                        hospitalVisitSchedulePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonHospitalVisitScheduleList> call, @NonNull Throwable t) {
                Log.e("hospitalVSchedule fail", t.getLocalizedMessage(), t);
                hospitalVisitSchedulePresenter.responseErrorPresenter(null);
            }
        });
    }

    public void modifyVisitingFor(String did, String mail, String auth, HospitalVisitFor hospitalVisitFor) {
        medicalRecordApiUrls.modifyVisitingFor(did, Constants.DEVICE_TYPE, mail, auth, hospitalVisitFor).enqueue(new Callback<JsonHospitalVisitSchedule>() {
            @Override
            public void onResponse(@NonNull Call<JsonHospitalVisitSchedule> call, @NonNull Response<JsonHospitalVisitSchedule> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Resp hospitalVSchedule", String.valueOf(response.body()));
                        hospitalVisitSchedulePresenter.hospitalVisitScheduleResponse(response.body());
                    } else {
                        hospitalVisitSchedulePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        hospitalVisitSchedulePresenter.authenticationFailure();
                    } else {
                        hospitalVisitSchedulePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonHospitalVisitSchedule> call, @NonNull Throwable t) {
                Log.e("hospitalVSchedule fail", t.getLocalizedMessage(), t);
                hospitalVisitSchedulePresenter.responseErrorPresenter(null);
            }
        });
    }

}
