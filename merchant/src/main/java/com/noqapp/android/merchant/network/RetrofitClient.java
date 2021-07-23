package com.noqapp.android.merchant.network;

import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.views.activities.AppInitialize;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
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
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS);

            builder.addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                    .addHeader("x-r-ver", BuildConfig.VERSION_NAME)
                    .addHeader("x-r-fla", BuildConfig.APP_FLAVOR)
                    .addHeader("x-r-lat", String.valueOf(0.0))
                    .addHeader("x-r-lng", String.valueOf(0.0))
                    .addHeader("x-r-did", AppInitialize.getDeviceId() == null ? "" : AppInitialize.getDeviceId())
                    .addHeader("x-r-mail", AppInitialize.getMail())
                    .addHeader("x-r-qid", AppInitialize.getUserProfile() == null ? "" : AppInitialize.getUserProfile().getQueueUserId()).build();
                return chain.proceed(request);
            });

            retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.NOQAPP_MOBILE)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(builder.build())
                .build();
        }
        return retrofit;
    }
}
