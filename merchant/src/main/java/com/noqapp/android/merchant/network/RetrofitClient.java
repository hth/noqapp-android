package com.noqapp.android.merchant.network;

import com.noqapp.android.merchant.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * User: hitender
 * Date: 4/16/17 5:58 PM
 */

public class RetrofitClient {
    private static Retrofit retrofit;
    private static long TIME_OUT = 35;

    public static Retrofit getClient() {
        if (null == retrofit) {
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .build();
            retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.NOQAPP_MOBILE)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient)
                .build();
        }
        return retrofit;
    }
}
