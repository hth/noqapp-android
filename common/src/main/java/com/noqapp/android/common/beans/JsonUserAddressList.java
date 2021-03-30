package com.noqapp.android.common.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;

/**
 * hitender
 * 5/16/18 10:12 AM
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonUserAddressList extends AbstractDomain implements Parcelable {

    @JsonProperty("ads")
    private ArrayList<JsonUserAddress> jsonUserAddresses = new ArrayList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public JsonUserAddressList() {
    }

    protected JsonUserAddressList(Parcel in) {
        jsonUserAddresses = in.createTypedArrayList(JsonUserAddress.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(jsonUserAddresses);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JsonUserAddressList> CREATOR = new Creator<JsonUserAddressList>() {
        @Override
        public JsonUserAddressList createFromParcel(Parcel in) {
            return new JsonUserAddressList(in);
        }

        @Override
        public JsonUserAddressList[] newArray(int size) {
            return new JsonUserAddressList[size];
        }
    };

    public ArrayList<JsonUserAddress> getJsonUserAddresses() {
        return jsonUserAddresses;
    }

    public JsonUserAddressList setJsonUserAddresses(ArrayList<JsonUserAddress> jsonUserAddresses) {
        this.jsonUserAddresses = jsonUserAddresses;
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
        final StringBuffer sb = new StringBuffer("JsonUserAddressList{");
        sb.append("jsonUserAddresses=").append(jsonUserAddresses);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}