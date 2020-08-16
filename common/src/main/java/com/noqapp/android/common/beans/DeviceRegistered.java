package com.noqapp.android.common.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.body.GeoPointOfQ;

/**
 * User: hitender
 * Date: 4/2/17 6:44 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude (JsonInclude.Include.NON_NULL)
public class DeviceRegistered {

    @JsonProperty("r")
    private int registered;

    @JsonProperty("did")
    private String deviceId;

    @JsonProperty("cor")
    private GeoPointOfQ geoPointOfQ;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public int getRegistered() {
        return registered;
    }

    public void setRegistered(int registered) {
        this.registered = registered;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public GeoPointOfQ getGeoPointOfQ() {
        return geoPointOfQ;
    }

    public void setGeoPointOfQ(GeoPointOfQ geoPointOfQ) {
        this.geoPointOfQ = geoPointOfQ;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "DeviceRegistered{" +
                "registered=" + registered +
                ", error=" + error +
                '}';
    }
}

