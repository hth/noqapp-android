package com.noqapp.android.client.presenter.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 2018-12-07 12:35
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
public class JsonFeedList extends AbstractDomain implements Parcelable {

    public JsonFeedList() {
    }

    @JsonProperty("fds")
    private List<JsonFeed> jsonFeeds = new LinkedList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    protected JsonFeedList(Parcel in) {
        jsonFeeds = in.createTypedArrayList(JsonFeed.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(jsonFeeds);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JsonFeedList> CREATOR = new Creator<JsonFeedList>() {
        @Override
        public JsonFeedList createFromParcel(Parcel in) {
            return new JsonFeedList(in);
        }

        @Override
        public JsonFeedList[] newArray(int size) {
            return new JsonFeedList[size];
        }
    };

    public List<JsonFeed> getJsonFeeds() {
        return jsonFeeds;
    }

    public JsonFeedList setJsonFeeds(List<JsonFeed> jsonFeeds) {
        this.jsonFeeds = jsonFeeds;
        return this;
    }

    public JsonFeedList addJsonFeed(JsonFeed jsonFeed) {
        this.jsonFeeds.add(jsonFeed);
        return this;
    }


    public ErrorEncounteredJson getError() {
        return error;
    }

    public JsonFeedList setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return "JsonFeedList{" +
                "jsonFeeds=" + jsonFeeds +
                ", error=" + error +
                '}';
    }
}