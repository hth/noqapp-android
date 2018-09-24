package com.noqapp.android.merchant.model;

import com.noqapp.android.merchant.interfaces.FilePresenter;
import com.noqapp.android.merchant.interfaces.PreferredBusinessPresenter;
import com.noqapp.android.merchant.model.response.api.health.PreferredStoreService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusinessList;
import com.noqapp.android.merchant.utils.Constants;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PreferredBusinessModel {
    private static final String TAG = PreferredBusinessModel.class.getSimpleName();

    private static final PreferredStoreService preferredStoreService;
    private PreferredBusinessPresenter preferredBusinessPresenter;

    public void setFilePresenter(FilePresenter filePresenter) {
        this.filePresenter = filePresenter;
    }

    private FilePresenter filePresenter;

    public PreferredBusinessModel(PreferredBusinessPresenter preferredBusinessPresenter) {
        this.preferredBusinessPresenter = preferredBusinessPresenter;
    }
    static {
        preferredStoreService = RetrofitClient.getClient().create(PreferredStoreService.class);
    }

    public void getAllPreferredStores(String did, String mail, String auth, String codeQR) {
        preferredStoreService.getAllPreferredStores(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonPreferredBusinessList>() {
            @Override
            public void onResponse(@NonNull Call<JsonPreferredBusinessList> call, @NonNull Response<JsonPreferredBusinessList> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    preferredBusinessPresenter.authenticationFailure();
                    return;
                }
                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Response", String.valueOf(response.body()));
                    preferredBusinessPresenter.preferredBusinessResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed image upload");
                    preferredBusinessPresenter.responseErrorPresenter(response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPreferredBusinessList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                preferredBusinessPresenter.preferredBusinessError();
            }
        });
    }

    public void fetchFile(String did, String mail, String auth,String codeQR, String bizStoreId) {
        preferredStoreService.file(did, Constants.DEVICE_TYPE, mail, auth, codeQR,bizStoreId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    filePresenter.authenticationFailure();
                    return;
                }
                if (null != response.body() ) {
                    Log.d("Response", String.valueOf(response.body()));
                    try {
                        int count;
                        InputStream input = new BufferedInputStream(response.body().byteStream());
                        File directory = new File(Environment.getExternalStorageDirectory() + "/UnZipped");
                        if (!directory.exists()) {
                            directory.mkdir();
                        }
                        File file = new File(Environment.getExternalStorageDirectory() + "/UnZipped/temp.tar.gz");
                        FileOutputStream output = new FileOutputStream(file);
                        Log.d(TAG, "file saved at " + file.getAbsolutePath());
                        byte data[] = new byte[1024];
                        while ((count = input.read(data)) != -1) {
                            output.write(data, 0, count);
                        }
                        output.flush();
                        output.close();
                        input.close();
                        filePresenter.fileResponse(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                        filePresenter.fileResponse(null);
                    }
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed image upload");
                    filePresenter.fileError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                filePresenter.fileError();
            }
        });
    }



}
