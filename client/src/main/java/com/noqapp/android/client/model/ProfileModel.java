package com.noqapp.android.client.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.client.model.response.api.ProfileService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.JsonProfile;
import com.noqapp.android.client.presenter.beans.body.Registration;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileModel {

    private static final String TAG = ProfileModel.class.getSimpleName();

    private static final ProfileService profileService;
    public static ProfilePresenter profilePresenter;

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

    public static void updateProfile(final String mail, final String auth, Registration registration ) {
        profileService.update(mail, auth,registration).enqueue(new Callback<JsonProfile>() {
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

    public static void migrateMobileNo(final String mail, final String auth, Registration registration ) {
        profileService.migrate(mail, auth,registration).enqueue(new Callback<JsonProfile>() {
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
}
