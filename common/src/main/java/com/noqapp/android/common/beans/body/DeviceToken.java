package com.noqapp.android.common.beans.body;

import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.utils.CommonHelper;

/**
 * User: hitender
 * Date: 4/2/17 6:37 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceToken {

    @JsonProperty("tk")
    private String tokenFCM;

    @JsonProperty("mo")
    private String model;

    @JsonProperty("os")
    private String osVersion;

    @JsonProperty("av")
    private String appVersion;

    @JsonProperty("ip")
    private String ipAddress;

    @JsonProperty("lng")
    private String longitude;

    @JsonProperty("lat")
    private String latitude;

    public DeviceToken(String tokenFCM, String appVersion, Location location) {
        this.tokenFCM = tokenFCM;
        this.model = Build.MODEL;
        this.osVersion = Build.VERSION.RELEASE;
        this.appVersion = appVersion;
        this.ipAddress = CommonHelper.getIPAddress();
        if (null != location) {
            this.longitude = String.valueOf(location.getLongitude());
            this.latitude = String.valueOf(location.getLatitude());
            Log.e("Location Device token", location.toString());
        }
    }
}
