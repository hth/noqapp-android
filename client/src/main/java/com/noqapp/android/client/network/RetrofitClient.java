package com.noqapp.android.client.network;

import android.os.Build;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.views.activities.NoQueueClientApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * User: hitender
 * Date: 3/26/17 11:52 PM
 */
public class RetrofitClient {
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (null == retrofit) {
            long TIME_OUT = 35;
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS);

            builder.addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                                       .addHeader("x-r-ver", BuildConfig.VERSION_NAME)
                                       .addHeader("x-r-fla", BuildConfig.APP_FLAVOR)
                                       .addHeader("x-r-mod", Build.MODEL + ", " + Build.BRAND + ", " + Build.MANUFACTURER)
                                       .addHeader("x-r-lat", String.valueOf(NoQueueClientApplication.location.getLatitude()))
                                       .addHeader("x-r-lng", String.valueOf(NoQueueClientApplication.location.getLongitude()))
                                       .addHeader("x-r-did", NoQueueClientApplication.getDeviceId() == null ? "" : NoQueueClientApplication.getDeviceId())
                                       .addHeader("x-r-mail", NoQueueClientApplication.getMail())
                                       .addHeader("x-r-qid", NoQueueClientApplication.getUserProfile() == null ? "" : NoQueueClientApplication.getUserProfile().getQueueUserId()).build();
                return chain.proceed(request);
            });
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(logging);
            }

            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.NOQAPP_MOBILE)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .client(builder.build())
                    .build();
        }
        return retrofit;
    }
}
