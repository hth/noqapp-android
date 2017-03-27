package com.noqapp.client.networkUtilities;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * User: omkar
 * Date: 3/26/17 11:52 PM
 */
public class RetrofitClient {

    private static Retrofit retrofit = null;
    public static String BaseURL = "http://192.168.0.101:9090/token-mobile/";

    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
