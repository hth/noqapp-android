package com.noqapp.android.merchant.model;

import com.noqapp.android.merchant.model.response.api.health.MasterLabService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.FilePresenter;

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

public class BaseMasterLabModel {
    protected static final String TAG = BaseMasterLabModel.class.getSimpleName();
    protected static final MasterLabService masterLabService;
    protected FilePresenter filePresenter;

    static {
        masterLabService = RetrofitClient.getClient().create(MasterLabService.class);
    }
    public void setFilePresenter(FilePresenter filePresenter) {
        this.filePresenter = filePresenter;
    }

    public void fetchFile(String did, String mail, String auth) {
        masterLabService.file(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body()) {
                        Log.d("fetchFile", String.valueOf(response.body()));
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
                        Log.e(TAG, "Failed fetchFile");
                        filePresenter.fileError();
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        filePresenter.authenticationFailure();
                    } else {
                        filePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("fail fetchFile", t.getLocalizedMessage(), t);
                filePresenter.fileError();
            }
        });
    }
}
