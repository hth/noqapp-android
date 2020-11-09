package com.noqapp.android.merchant.model.response.api.health;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.merchant.presenter.beans.JsonMasterLab;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface MasterLabApiUrls {

    @GET("api/m/h/lab/file.json")
    Call<ResponseBody> file(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/h/lab/flag.json")
    Call<JsonResponse> flag(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonMasterLab jsonMasterLab
    );
}
