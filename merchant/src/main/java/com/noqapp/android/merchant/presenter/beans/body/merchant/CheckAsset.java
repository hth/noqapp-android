package com.noqapp.android.merchant.presenter.beans.body.merchant;

import com.noqapp.android.common.beans.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 2019-07-30 10:15
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
public class CheckAsset extends AbstractDomain implements Serializable {

    @JsonProperty("bn")
    private String bizNameId;

    @JsonProperty("fl")
    private String floor;

    @JsonProperty("rn")
    private String roomNumber;

    @JsonProperty("an")
    private String assetName;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getBizNameId() {
        return bizNameId;
    }

    public CheckAsset setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getFloor() {
        return floor;
    }

    public CheckAsset setFloor(String floor) {
        this.floor = floor;
        return this;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public CheckAsset setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
        return this;
    }

    public String getAssetName() {
        return assetName;
    }

    public CheckAsset setAssetName(String assetName) {
        this.assetName = assetName;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CheckAsset{");
        sb.append("bizNameId='").append(bizNameId).append('\'');
        sb.append(", floor='").append(floor).append('\'');
        sb.append(", roomNumber='").append(roomNumber).append('\'');
        sb.append(", assetName='").append(assetName).append('\'');
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}