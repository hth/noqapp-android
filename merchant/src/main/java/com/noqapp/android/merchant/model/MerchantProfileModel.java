package com.noqapp.android.merchant.model;

import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.UpdateProfile;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.merchant.model.response.api.MerchantProfileService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.JsonMerchant;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.MerchantPresenter;
import com.noqapp.android.merchant.views.interfaces.MerchantProfessionalPresenter;
import com.noqapp.android.merchant.views.interfaces.ProfilePresenter;

import android.support.annotation.NonNull;
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
    public ImageUploadPresenter imageUploadPresenter;
    public MerchantPresenter merchantPresenter;
    public ProfilePresenter profilePresenter;
    public MerchantProfessionalPresenter merchantProfessionalPresenter;

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
    public void fetch(String mail, String auth) {
        merchantProfileService.fetch(mail, auth).enqueue(new Callback<JsonMerchant>() {
            @Override
            public void onResponse(@NonNull Call<JsonMerchant> call, @NonNull Response<JsonMerchant> response) {
                if (response.code() == 401) {
                    merchantPresenter.authenticationFailure(response.code());
                    return;
                }

                if (response.body() != null) {
                    merchantPresenter.merchantResponse(response.body());
                    Log.d("Response", String.valueOf(response.body()));
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonMerchant> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
            }
        });
    }

    public void updateProfile(final String mail, final String auth, UpdateProfile updateProfile) {
        merchantProfileService.update(mail, auth, updateProfile).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == 401) {
                    profilePresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Update profile", String.valueOf(response.body()));
                    profilePresenter.profileResponse(response.body(), mail, auth);
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed updating profile " + response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profilePresenter.profileError();
            }
        });
    }

    public void updateProfessionalProfile(final String mail, final String auth, JsonProfessionalProfilePersonal jsonProfessionalProfilePersonal) {
        merchantProfileService.update(mail, auth, jsonProfessionalProfilePersonal).enqueue(new Callback<JsonProfessionalProfilePersonal>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfessionalProfilePersonal> call, @NonNull Response<JsonProfessionalProfilePersonal> response) {
                if (response.code() == 401) {
                    merchantProfessionalPresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Update profile", String.valueOf(response.body()));
                    merchantProfessionalPresenter.merchantProfessionalResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed updating profile " + response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfessionalProfilePersonal> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                merchantProfessionalPresenter.merchantProfessionalError();
            }
        });
    }

    public void uploadImage(String did, String mail, String auth, MultipartBody.Part profileImageFile, RequestBody profileImageOfQid) {
        merchantProfileService.upload(did, Constants.DEVICE_TYPE, mail, auth, profileImageFile, profileImageOfQid).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    imageUploadPresenter.authenticationFailure(response.code());
                    return;
                }
                if (null != response.body()) {
                    Log.d("Response", String.valueOf(response.body()));
                    imageUploadPresenter.imageUploadResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed image upload");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                imageUploadPresenter.imageUploadError();
            }
        });
    }
}
