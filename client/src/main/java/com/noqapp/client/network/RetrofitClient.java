package com.noqapp.client.network;

import com.noqapp.client.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static com.noqapp.client.BuildConfig.NOQAPP_MOBILE;

/**
 * User: omkar
 * Date: 3/26/17 11:52 PM
 */
public class RetrofitClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.NOQAPP_MOBILE)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
