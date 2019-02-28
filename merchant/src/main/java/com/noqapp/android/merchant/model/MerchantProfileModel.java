package com.noqapp.android.merchant.model;

import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.UpdateProfile;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.model.response.api.MerchantProfileService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.JsonMerchant;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.MerchantPresenter;
import com.noqapp.android.merchant.views.interfaces.MerchantProfessionalPresenter;
import com.noqapp.android.merchant.views.interfaces.ProfilePresenter;

import androidx.annotation.NonNull;
import android.util.Log;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 4/22/17 11:53 AM
 */
public class MerchantProfileModel {
    private static final String TAG = MerchantProfileModel.class.getSimpleName();

    protected static final MerchantProfileService merchantProfileService;
    private ImageUploadPresenter imageUploadPresenter;
    private MerchantPresenter merchantPresenter;
    private ProfilePresenter profilePresenter;
    private MerchantProfessionalPresenter merchantProfessionalPresenter;

    public void setMerchantProfessionalPresenter(MerchantProfessionalPresenter merchantProfessionalPresenter) {
        this.merchantProfessionalPresenter = merchantProfessionalPresenter;
    }

    public void setMerchantPresenter(MerchantPresenter merchantPresenter) {
        this.merchantPresenter = merchantPresenter;
    }

    public void setProfilePresenter(ProfilePresenter profilePresenter) {
        this.profilePresenter = profilePresenter;
    }

    public void setImageUploadPresenter(ImageUploadPresenter imageUploadPresenter) {
        this.imageUploadPresenter = imageUploadPresenter;
    }

    static {
        merchantProfileService = RetrofitClient.getClient().create(MerchantProfileService.class);
    }

    /**
     * @param mail
     * @param auth
     */
    public void fetch(String did, String mail, String auth) {
        merchantProfileService.fetch(did, Constants.DEVICE_TYPE, BuildConfig.APP_FLAVOR, mail, auth).enqueue(new Callback<JsonMerchant>() {
            @Override
            public void onResponse(@NonNull Call<JsonMerchant> call, @NonNull Response<JsonMerchant> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        merchantPresenter.merchantResponse(response.body());
                        Log.d("fetch", String.valueOf(response.body()));
                    } else {
                        Log.e(TAG, "Empty fetch");
                        merchantPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        merchantPresenter.authenticationFailure();
                    } else {
                        merchantPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonMerchant> call, @NonNull Throwable t) {
                Log.e("onFailure fetch", t.getLocalizedMessage(), t);
                merchantPresenter.merchantError();
            }
        });
    }

    public void updateProfile(final String mail, final String auth, UpdateProfile updateProfile) {
        merchantProfileService.update(mail, auth, updateProfile).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Update profile", String.valueOf(response.body()));
                        profilePresenter.profileResponse(response.body(), mail, auth);
                    } else {
                        Log.e(TAG, "Failed updating profile " + response.body().getError());
                        profilePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        profilePresenter.authenticationFailure();
                    } else {
                        profilePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("Update profile", t.getLocalizedMessage(), t);
                profilePresenter.profileError();
            }
        });
    }

    public void updateProfessionalProfile(final String mail, final String auth, JsonProfessionalProfilePersonal jsonProfessionalProfilePersonal) {
        merchantProfileService.update(mail, auth, jsonProfessionalProfilePersonal).enqueue(new Callback<JsonProfessionalProfilePersonal>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfessionalProfilePersonal> call, @NonNull Response<JsonProfessionalProfilePersonal> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Update profess profile", String.valueOf(response.body()));
                        merchantProfessionalPresenter.merchantProfessionalResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed updating professional profile " + response.body().getError());
                        merchantProfessionalPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        merchantProfessionalPresenter.authenticationFailure();
                    } else {
                        merchantProfessionalPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfessionalProfilePersonal> call, @NonNull Throwable t) {
                Log.e("update profess", t.getLocalizedMessage(), t);
                merchantProfessionalPresenter.merchantProfessionalError();
            }
        });
    }

    public void uploadImage(String did, String mail, String auth, MultipartBody.Part profileImageFile, RequestBody profileImageOfQid) {
        merchantProfileService.upload(did, Constants.DEVICE_TYPE, mail, auth, profileImageFile, profileImageOfQid).enqueue(new Callback<JsonResponse>() {
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

    public void removeImage(String did, String mail, String auth, UpdateProfile updateProfile) {
        merchantProfileService.remove(did, Constants.DEVICE_TYPE, mail, auth, updateProfile).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response uploadImage", String.valueOf(response.body()));
                        imageUploadPresenter.imageRemoveResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed image remove");
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
                Log.e("uploadImage failure", t.getLocalizedMessage(), t);
                imageUploadPresenter.imageUploadError();
            }
        });
    }

}
