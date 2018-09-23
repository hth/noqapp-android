package com.noqapp.android.client.model;

import com.noqapp.android.client.model.response.api.ProfileService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.MigrateEmailPresenter;
import com.noqapp.android.client.presenter.ProfileAddressPresenter;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.JsonUserAddress;
import com.noqapp.android.client.presenter.beans.JsonUserAddressList;
import com.noqapp.android.client.presenter.beans.body.ChangeMailOTP;
import com.noqapp.android.client.presenter.beans.body.MigrateMail;
import com.noqapp.android.client.presenter.beans.body.MigratePhone;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.UpdateProfile;
import com.noqapp.android.common.presenter.ImageUploadPresenter;

import android.support.annotation.NonNull;
import android.util.Log;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileModel {

    private final String TAG = ProfileModel.class.getSimpleName();
    private static final ProfileService profileService;
    private ProfilePresenter profilePresenter;
    private ImageUploadPresenter imageUploadPresenter;
    private ProfileAddressPresenter profileAddressPresenter;
    private MigrateEmailPresenter migrateEmailPresenter;

    public void setMigrateEmailPresenter(MigrateEmailPresenter migrateEmailPresenter) {
        this.migrateEmailPresenter = migrateEmailPresenter;
    }

    public void setProfileAddressPresenter(ProfileAddressPresenter profileAddressPresenter) {
        this.profileAddressPresenter = profileAddressPresenter;
    }

    public void setProfilePresenter(ProfilePresenter profilePresenter) {
        this.profilePresenter = profilePresenter;
    }

    public void setImageUploadPresenter(ImageUploadPresenter imageUploadPresenter) {
        this.imageUploadPresenter = imageUploadPresenter;
    }

    static {
        profileService = RetrofitClient.getClient().create(ProfileService.class);
    }

    public void fetchProfile(final String mail, final String auth) {
        profileService.fetch(mail, auth).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    profilePresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Response", String.valueOf(response.body()));
                    profilePresenter.profileResponse(response.body(), mail, auth);
                } else {
                    //TODO something logical
                    Log.e(TAG, "Get state of queue upon scan");
                    profilePresenter.responseErrorPresenter(response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profilePresenter.profileError();
            }
        });
    }

    public void updateProfile(final String mail, final String auth, UpdateProfile updateProfile) {
        profileService.update(mail, auth, updateProfile).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    profilePresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Update profile", String.valueOf(response.body()));
                    profilePresenter.profileResponse(response.body(), mail, auth);
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed updating profile " + response.body().getError());
                    profilePresenter.responseErrorPresenter(response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profilePresenter.profileError();
            }
        });
    }

    public void migrate(final String mail, final String auth, MigratePhone migratePhone) {
        profileService.migrate(mail, auth, migratePhone).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    profilePresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Response", String.valueOf(response.body()));
                    profilePresenter.profileResponse(
                            response.body(),
                            response.headers().get(APIConstant.Key.XR_MAIL),
                            response.headers().get(APIConstant.Key.XR_AUTH));
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed migrating profile");
                    profilePresenter.responseErrorPresenter(response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profilePresenter.profileError();
            }
        });
    }

    public void getProfileAllAddress(final String mail, final String auth) {
        profileService.address(mail, auth).enqueue(new Callback<JsonUserAddressList>() {
            @Override
            public void onResponse(@NonNull Call<JsonUserAddressList> call, @NonNull Response<JsonUserAddressList> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    profileAddressPresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Response", String.valueOf(response.body()));
                    profileAddressPresenter.profileAddressResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed migrating profile");
                    profileAddressPresenter.responseErrorPresenter(response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonUserAddressList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profileAddressPresenter.profileAddressError();
            }
        });
    }

    public void addProfileAddress(final String mail, final String auth, JsonUserAddress jsonUserAddress) {
        profileService.addressAdd(mail, auth, jsonUserAddress).enqueue(new Callback<JsonUserAddressList>() {
            @Override
            public void onResponse(@NonNull Call<JsonUserAddressList> call, @NonNull Response<JsonUserAddressList> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    profileAddressPresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Response", String.valueOf(response.body()));
                    profileAddressPresenter.profileAddressResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed migrating profile");
                    profileAddressPresenter.responseErrorPresenter(response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonUserAddressList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profileAddressPresenter.profileAddressError();
            }
        });
    }

    public void deleteProfileAddress(final String mail, final String auth, JsonUserAddress jsonUserAddress) {
        profileService.addressDelete(mail, auth, jsonUserAddress).enqueue(new Callback<JsonUserAddressList>() {
            @Override
            public void onResponse(@NonNull Call<JsonUserAddressList> call, @NonNull Response<JsonUserAddressList> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    profileAddressPresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Response", String.valueOf(response.body()));
                    profileAddressPresenter.profileAddressResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed migrating profile");
                    profileAddressPresenter.responseErrorPresenter(response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonUserAddressList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profileAddressPresenter.profileAddressError();
            }
        });
    }

    public void uploadImage(String did, String mail, String auth, MultipartBody.Part profileImageFile, RequestBody profileImageOfQid) {
        profileService.upload(did, Constants.DEVICE_TYPE, mail, auth, profileImageFile, profileImageOfQid).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    imageUploadPresenter.authenticationFailure(response.code());
                    return;
                }
                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Response", String.valueOf(response.body()));
                    imageUploadPresenter.imageUploadResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed image upload");
                    imageUploadPresenter.responseErrorPresenter(response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                imageUploadPresenter.imageUploadError();
            }
        });
    }

    public void changeMail(final String mail, final String auth, MigrateMail migrateMail) {
        profileService.changeMail(mail, auth, migrateMail).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    migrateEmailPresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("changeMail response", String.valueOf(response.body()));
                    migrateEmailPresenter.migrateEmailResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed updating changeMail " + response.body().getError());
                    migrateEmailPresenter.responseErrorPresenter(response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                migrateEmailPresenter.migrateEmailError();
            }
        });
    }

    public void migrateMail(final String mail, final String auth, ChangeMailOTP changeMailOTP) {
        profileService.migrateMail(mail, auth, changeMailOTP).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    profilePresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Response", String.valueOf(response.body()));
                    profilePresenter.profileResponse(response.body(), response.headers().get(APIConstant.Key.XR_MAIL),
                            response.headers().get(APIConstant.Key.XR_AUTH));
                } else {
                    //TODO something logical
                    Log.e(TAG, "Get state of queue upon scan");
                    profilePresenter.responseErrorPresenter(response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profilePresenter.profileError();
            }
        });
    }
}
