package com.noqapp.android.merchant.presenter.beans;

import com.noqapp.android.common.beans.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 2019-07-29 23:50
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
public class JsonCheckAsset extends AbstractDomain implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("fl")
    private String floor;

    @JsonProperty("rn")
    private String roomNumber;

    @JsonProperty("an")
    private String assetName;

    private boolean status;

    public String getId() {
        return id;
    }

    public JsonCheckAsset setId(String id) {
        this.id = id;
        return this;
    }

    public String getFloor() {
        return floor;
    }

    public JsonCheckAsset setFloor(String floor) {
        this.floor = floor;
        return this;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public JsonCheckAsset setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
        return this;
    }

    public String getAssetName() {
        return assetName;
    }

    public JsonCheckAsset setAssetName(String assetName) {
        this.assetName = assetName;
        return this;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JsonCheckAsset{");
        sb.append("id='").append(id).append('\'');
        sb.append(", floor='").append(floor).append('\'');
        sb.append(", roomNumber='").append(roomNumber).append('\'');
        sb.append(", assetName='").append(assetName).append('\'');
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
