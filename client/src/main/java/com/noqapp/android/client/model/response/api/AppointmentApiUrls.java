package com.noqapp.android.client.model.response.api;

import com.noqapp.android.common.beans.JsonScheduleList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by hitender on 5/22/19.
 */
public interface AppointmentApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("api/c/appointment/scheduleForMonth/{month}/{codeQR}.json")
    Call<JsonScheduleList> scheduleForMonth(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Path("month")
            String month,

            @Path("codeQR")
            String codeQR
    );


    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("api/c/appointment/scheduleForDay/{day}/{codeQR}.json")
    Call<JsonScheduleList> scheduleForDay(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Path("day")
            String day,

            @Path("codeQR")
            String codeQR
    );
}
