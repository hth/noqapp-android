package com.noqapp.android.client.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.client.model.response.api.ProfileService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.ImageUploadPresenter;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.JsonProfile;
import com.noqapp.android.client.presenter.beans.JsonResponse;
import com.noqapp.android.client.presenter.beans.JsonUserAddress;
import com.noqapp.android.client.presenter.beans.JsonUserAddressList;
import com.noqapp.android.client.presenter.beans.body.MigrateProfile;
import com.noqapp.android.client.presenter.beans.body.UpdateProfile;
import com.noqapp.android.client.utils.Constants;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileModel {

    private static final String TAG = ProfileModel.class.getSimpleName();

    private static final ProfileService profileService;
    public static ProfilePresenter profilePresenter;
    public static ImageUploadPresenter imageUploadPresenter;

    static {
        profileService = RetrofitClient.getClient().create(ProfileService.class);
    }


    public static void fetchProfile(final String mail, final String auth) {
        profileService.fetch(mail, auth).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (null != response.body()) {
                    Log.d("Response", String.valueOf(response.body()));
                    profilePresenter.queueResponse(response.body(), mail, auth);
                } else {
                    //TODO something logical
                    Log.e(TAG, "Get state of queue upon scan");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);

                profilePresenter.queueError();
            }
        });
    }

    public static void updateProfile(final String mail, final String auth, UpdateProfile updateProfile) {
        profileService.update(mail, auth, updateProfile).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == 401) {
                    profilePresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Update profile", String.valueOf(response.body()));
                    profilePresenter.queueResponse(response.body(), mail, auth);
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed updating profile " + response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profilePresenter.queueError();
            }
        });
    }

    public static void migrate(final String mail, final String auth, MigrateProfile migrateProfile) {
        profileService.migrate(mail, auth, migrateProfile).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == 401) {
                    profilePresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Response", String.valueOf(response.body()));
                    profilePresenter.queueResponse(response.body(), response.headers().get(APIConstant.Key.XR_MAIL),
                            response.headers().get(APIConstant.Key.XR_AUTH));
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed migrating profile");
                    profilePresenter.queueError(response.body().getError().getReason());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profilePresenter.queueError();
            }
        });
    }

    public static void getProfileAllAddress(final String mail, final String auth) {
        profileService.address(mail, auth).enqueue(new Callback<JsonUserAddressList>() {
            @Override
            public void onResponse(@NonNull Call<JsonUserAddressList> call, @NonNull Response<JsonUserAddressList> response) {
                if (response.code() == 401) {
                    profilePresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() //&& null == response.body().getError()
                        ) {
                    Log.d("Response", String.valueOf(response.body()));
                    profilePresenter.profileAddressResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed migrating profile");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonUserAddressList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profilePresenter.queueError();
            }
        });
    }

    public static void addProfileAddress(final String mail, final String auth,JsonUserAddress jsonUserAddress) {
        profileService.addressAdd(mail, auth,jsonUserAddress).enqueue(new Callback<JsonUserAddressList>() {
            @Override
            public void onResponse(@NonNull Call<JsonUserAddressList> call, @NonNull Response<JsonUserAddressList> response) {
                if (response.code() == 401) {
                    profilePresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() //&& null == response.body().getError()
                        ) {
                    Log.d("Response", String.valueOf(response.body()));
                    profilePresenter.profileAddressResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed migrating profile");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonUserAddressList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profilePresenter.queueError();
            }
        });
    }

    public static void deleteProfileAddress(final String mail, final String auth,JsonUserAddress jsonUserAddress) {
        profileService.addressDelete(mail, auth,jsonUserAddress).enqueue(new Callback<JsonUserAddressList>() {
            @Override
            public void onResponse(@NonNull Call<JsonUserAddressList> call, @NonNull Response<JsonUserAddressList> response) {
                if (response.code() == 401) {
                    profilePresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() //&& null == response.body().getError()
                        ) {
                    Log.d("Response", String.valueOf(response.body()));
                    profilePresenter.profileAddressResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed migrating profile");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonUserAddressList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profilePresenter.queueError();
            }
        });
    }

    public static void uploadImage(String did, String mail, String auth, MultipartBody.Part file) {
        profileService.upload(did, Constants.DEVICE_TYPE, mail, auth, file).enqueue(new Callback<JsonResponse>() {
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
