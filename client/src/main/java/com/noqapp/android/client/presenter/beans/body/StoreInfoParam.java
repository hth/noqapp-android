package com.noqapp.android.client.presenter.beans.body;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
public class StoreInfoParam {

    @JsonProperty("q")
    private String query;

    @JsonProperty("cityName")
    private String cityName;

    @JsonProperty("lat")
    private String latitude;

    @JsonProperty("lng")
    private String longitude;

    @JsonProperty("scrollId")
    private String scrollId;

    /* Apply specific filter on fields set on app, like city. */
    @JsonProperty("filters")
    private String filters;


    public String getQuery() {
        return query;
    }

    public StoreInfoParam setQuery(String query) {
        this.query = query;
        return this;
    }

    public String getCityName() {
        return cityName;
    }

    public StoreInfoParam setCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public String getLatitude() {
        return latitude;
    }

    public StoreInfoParam setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getLongitude() {
        return longitude;
    }

    public StoreInfoParam setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getFilters() {
        return filters;
    }

    public StoreInfoParam setFilters(String filters) {
        this.filters = filters;
        return this;
    }

    public String getScrollId() {
        return scrollId;
    }

    public StoreInfoParam setScrollId(String scrollId) {
        this.scrollId = scrollId;
        return this;
    }
}
