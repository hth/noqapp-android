package com.noqapp.user.NetworkUtilities;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by omkar on 3/26/17.
 */

public class RetrofitClient {

    private static Retrofit retrofit = null;
    public static String BaseURL =  "http://192.168.0.101:9090/token-mobile/";

    public static Retrofit getClient(String baseUrl) {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
