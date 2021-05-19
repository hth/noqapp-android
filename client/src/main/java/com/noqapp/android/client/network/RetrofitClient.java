package com.noqapp.android.client.network;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.views.activities.AppInitialize;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
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
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS);

            builder.addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                    .addHeader("x-r-ver", BuildConfig.VERSION_NAME)
                    .addHeader("x-r-fla", BuildConfig.APP_FLAVOR)
                    .addHeader("x-r-lat", String.valueOf(AppInitialize.location.getLatitude()))
                    .addHeader("x-r-lng", String.valueOf(AppInitialize.location.getLongitude())).build();
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
