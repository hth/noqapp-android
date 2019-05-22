package com.noqapp.android.merchant.model.response.api;

import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.merchant.presenter.beans.body.StoreSetting;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by hitender on 5/22/19.
 */
public interface ScheduleApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("api/m/schedule/showSchedule/{codeQR}.json")
    Call<JsonScheduleList> showSchedule(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Path("codeQR")
            String codeQR
    );
}
