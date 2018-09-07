package com.noqapp.android.common.beans.body;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import android.os.Build;
import android.util.Log;

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
    private String fcmToken;

    @JsonProperty("mo")
    private String model;

    @JsonProperty("os")
    private String osVersion;

    public DeviceToken(String fcmToken) {
        this.fcmToken = fcmToken;
        this.model = Build.MODEL;
        this.osVersion = Build.VERSION.RELEASE;
    }
}
