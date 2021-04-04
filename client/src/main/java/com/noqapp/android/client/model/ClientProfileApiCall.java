package com.noqapp.android.client.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.ClientProfileApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.MigrateEmailPresenter;
import com.noqapp.android.client.presenter.ProfileAddressPresenter;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.body.MigrateMail;
import com.noqapp.android.client.presenter.beans.body.MigratePhone;
import com.noqapp.android.client.presenter.beans.body.mail.ChangeMailOTP;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonUserAddress;
import com.noqapp.android.common.beans.JsonUserAddressList;
import com.noqapp.android.common.beans.JsonUserPreference;
import com.noqapp.android.common.beans.body.UpdateProfile;
import com.noqapp.android.common.presenter.ImageUploadPresenter;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientProfileApiCall {

    private final String TAG = ClientProfileApiCall.class.getSimpleName();
    private static final ClientProfileApiUrls clientProfileApiUrls;
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
        clientProfileApiUrls = RetrofitClient.getClient().create(ClientProfileApiUrls.class);
    }

    public void fetchProfile(final String mail, final String auth) {
        clientProfileApiUrls.fetch(mail, auth).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response fetchProfile", String.valueOf(response.body()));
                        profilePresenter.profileResponse(response.body(), mail, auth);
                    } else {
                        Log.e(TAG, "fetchProfile error");
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
                Log.e("fetchProfile failure", t.getLocalizedMessage(), t);
                profilePresenter.profileError();
            }
        });
    }

    public void updateProfile(final String mail, final String auth, UpdateProfile updateProfile) {
        clientProfileApiUrls.update(mail, auth, updateProfile).enqueue(new Callback<JsonProfile>() {
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
                Log.e("onFail updating profile", t.getLocalizedMessage(), t);
                profilePresenter.profileError();
            }
        });
    }

    public void migrate(final String mail, final String auth, MigratePhone migratePhone) {
        clientProfileApiUrls.migrate(mail, auth, migratePhone).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response migrate", String.valueOf(response.body()));
                        profilePresenter.profileResponse(
                                response.body(),
                                response.headers().get(APIConstant.Key.XR_MAIL),
                                response.headers().get(APIConstant.Key.XR_AUTH));
                    } else {
                        Log.e(TAG, "Failed migrating profile");
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
                Log.e("onFailure migrate", t.getLocalizedMessage(), t);
                profilePresenter.profileError();
            }
        });
    }

    public void getProfileAllAddress(final String mail, final String auth) {
        clientProfileApiUrls.address(mail, auth).enqueue(new Callback<JsonUserAddressList>() {
            @Override
            public void onResponse(@NonNull Call<JsonUserAddressList> call, @NonNull Response<JsonUserAddressList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("getProfileAllAddress", String.valueOf(response.body()));
                        profileAddressPresenter.profileAddressResponse(response.body());
                    } else {

                        Log.e(TAG, "Failed getProfileAllAddress");
                        profileAddressPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        profileAddressPresenter.authenticationFailure();
                    } else {
                        profileAddressPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonUserAddressList> call, @NonNull Throwable t) {
                Log.e("getProfileAllAddress", t.getLocalizedMessage(), t);
                profileAddressPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void addProfileAddress(final String mail, final String auth, JsonUserAddress jsonUserAddress) {
        clientProfileApiUrls.addressAdd(mail, auth, jsonUserAddress).enqueue(new Callback<JsonUserAddressList>() {
            @Override
            public void onResponse(@NonNull Call<JsonUserAddressList> call, @NonNull Response<JsonUserAddressList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Resp: addProfileAddress", String.valueOf(response.body()));
                        profileAddressPresenter.profileAddressResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed addProfileAddress");
                        profileAddressPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        profileAddressPresenter.authenticationFailure();
                    } else {
                        profileAddressPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonUserAddressList> call, @NonNull Throwable t) {
                Log.e("Fail addProfileAddress", t.getLocalizedMessage(), t);
                profileAddressPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void deleteProfileAddress(final String mail, final String auth, JsonUserAddress jsonUserAddress) {
        clientProfileApiUrls.addressDelete(mail, auth, jsonUserAddress).enqueue(new Callback<JsonUserAddressList>() {
            @Override
            public void onResponse(@NonNull Call<JsonUserAddressList> call, @NonNull Response<JsonUserAddressList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Resp:addressDelete", String.valueOf(response.body()));
                        profileAddressPresenter.profileAddressResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed addressDelete");
                        profileAddressPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        profileAddressPresenter.authenticationFailure();
                    } else {
                        profileAddressPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonUserAddressList> call, @NonNull Throwable t) {
                Log.e("addressDelete onFailure", t.getLocalizedMessage(), t);
                profileAddressPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void uploadImage(String did, String mail, String auth, MultipartBody.Part profileImageFile, RequestBody profileImageOfQid) {
        clientProfileApiUrls.upload(did, Constants.DEVICE_TYPE, mail, auth, profileImageFile, profileImageOfQid).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response uploadImage", String.valueOf(response.body()));
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
                Log.e("uploadImage failure", t.getLocalizedMessage(), t);
                imageUploadPresenter.imageUploadError();
            }
        });
    }

    public void removeImage(String did, String mail, String auth, UpdateProfile updateProfile) {
        clientProfileApiUrls.remove(did, Constants.DEVICE_TYPE, mail, auth, updateProfile).enqueue(new Callback<JsonResponse>() {
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

    public void changeMail(final String mail, final String auth, MigrateMail migrateMail) {
        clientProfileApiUrls.changeMail(mail, auth, migrateMail).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("changeMail response", String.valueOf(response.body()));
                        migrateEmailPresenter.migrateEmailResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed updating changeMail " + response.body().getError());
                        migrateEmailPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        migrateEmailPresenter.authenticationFailure();
                    } else {
                        migrateEmailPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("onFailure changeMail", t.getLocalizedMessage(), t);
                migrateEmailPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void migrateMail(final String mail, final String auth, ChangeMailOTP changeMailOTP) {
        clientProfileApiUrls.migrateMail(mail, auth, changeMailOTP).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response migrateMail", String.valueOf(response.body()));
                        profilePresenter.profileResponse(
                            response.body(),
                            response.headers().get(APIConstant.Key.XR_MAIL),
                            response.headers().get(APIConstant.Key.XR_AUTH));
                    } else {
                        Log.e(TAG, "error migrateMail");
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
                Log.e("onFailure migrateMail", t.getLocalizedMessage(), t);
                profilePresenter.profileError();
            }
        });
    }

    public void setPrimaryAddress(String mail, String auth, JsonUserAddress jsonUserAddress) {
        clientProfileApiUrls.addressPrimary(mail, auth, jsonUserAddress).enqueue(new Callback<JsonUserAddressList>() {
            @Override
            public void onResponse(@NonNull Call<JsonUserAddressList> call, @NonNull Response<JsonUserAddressList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        profileAddressPresenter.profileAddressResponse(response.body());
                        Log.e("order address", String.valueOf(response.body()));
                    } else {
                        Log.d(TAG, "Empty order address");
                        profileAddressPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        profileAddressPresenter.authenticationFailure();
                    } else {
                        profileAddressPresenter.responseErrorPresenter(response.code());
                        Log.e(TAG, "" + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonUserAddressList> call, @NonNull Throwable t) {
                Log.e("onFail address", t.getLocalizedMessage(), t);
                profileAddressPresenter.responseErrorPresenter(null);
            }
        });
    }
}
