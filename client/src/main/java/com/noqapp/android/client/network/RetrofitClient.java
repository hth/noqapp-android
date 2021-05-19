package com.noqapp.android.client.network;


import com.noqapp.android.client.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * User: omkar
 * Date: 3/26/17 11:52 PM
 */
public class RetrofitClient {
    private static Retrofit retrofit = null;
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
