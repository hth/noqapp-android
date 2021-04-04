package com.noqapp.android.client.presenter.beans.body;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.model.types.BusinessSupportEnum;
import com.noqapp.android.common.model.types.BusinessTypeEnum;

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
public class SearchStoreQuery {

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

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("bt")
    private BusinessTypeEnum searchedOnBusinessType = BusinessTypeEnum.ZZ;

    public String getQuery() {
        return query;
    }

    public SearchStoreQuery setQuery(String query) {
        this.query = query;
        return this;
    }

    public String getCityName() {
        return cityName;
    }

    public SearchStoreQuery setCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public String getLatitude() {
        return latitude;
    }

    public SearchStoreQuery setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getLongitude() {
        return longitude;
    }

    public SearchStoreQuery setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getFilters() {
        return filters;
    }

    public SearchStoreQuery setFilters(String filters) {
        this.filters = filters;
        return this;
    }

    public String getScrollId() {
        return scrollId;
    }

    public SearchStoreQuery setScrollId(String scrollId) {
        this.scrollId = scrollId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public SearchStoreQuery setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public BusinessTypeEnum getSearchedOnBusinessType() {
        return searchedOnBusinessType;
    }

    public SearchStoreQuery setSearchedOnBusinessType(BusinessTypeEnum searchedOnBusinessType) {
        this.searchedOnBusinessType = searchedOnBusinessType;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SearchStoreQuery{");
        sb.append("query='").append(query).append('\'');
        sb.append(", cityName='").append(cityName).append('\'');
        sb.append(", latitude='").append(latitude).append('\'');
        sb.append(", longitude='").append(longitude).append('\'');
        sb.append(", scrollId='").append(scrollId).append('\'');
        sb.append(", filters='").append(filters).append('\'');
        sb.append(", codeQR='").append(codeQR).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
