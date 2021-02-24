package com.noqapp.android.merchant.model.response.api;

import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.merchant.presenter.beans.body.merchant.BookSchedule;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by hitender on 5/22/19.
 */
public interface ScheduleApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE}
     */
    @GET("api/m/schedule/scheduleForMonth/{month}/{codeQR}")
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
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE}
     */
    @GET("api/m/schedule/scheduleForDay/{day}/{codeQR}")
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
            String month,

            @Path("codeQR")
            String codeQR
    );


    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#FAILED_TO_FIND_APPOINTMENT}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/schedule/action")
    Call<JsonSchedule> scheduleAction(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonSchedule jsonSchedule
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#APPOINTMENT_ALREADY_EXISTS}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#CANNOT_BOOK_APPOINTMENT}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/schedule/bookSchedule")
    Call<JsonSchedule> bookSchedule(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            BookSchedule bookSchedule
    );
}
