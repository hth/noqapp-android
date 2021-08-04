package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.BusinessTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hitender on 3/22/18.
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
public class BizStoreElasticList {

    @JsonProperty("si")
    private String scrollId;

    @JsonProperty("from")
    private int from;

    @JsonProperty("size")
    private int size;

    @JsonProperty("cityName")
    private String cityName;

    @JsonProperty("categories")
    private List<JsonCategory> jsonCategories = new ArrayList<>();

    @JsonProperty("result")
    private List<BizStoreElastic> bizStoreElastics = new ArrayList<>();

    @JsonProperty("bt")
    private BusinessTypeEnum searchedOnBusinessType;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getScrollId() {
        return scrollId;
    }

    public BizStoreElasticList setScrollId(String scrollId) {
        this.scrollId = scrollId;
        return this;
    }

    public int getFrom() {
        return from;
    }

    public BizStoreElasticList setFrom(int from) {
        this.from = from;
        return this;
    }

    public int getSize() {
        return size;
    }

    public BizStoreElasticList setSize(int size) {
        this.size = size;
        return this;
    }

    public String getCityName() {
        return cityName;
    }

    public BizStoreElasticList setCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public List<JsonCategory> getJsonCategories() {
        return jsonCategories;
    }

    public BizStoreElasticList setJsonCategories(List<JsonCategory> jsonCategories) {
        this.jsonCategories = jsonCategories;
        return this;
    }

    public List<BizStoreElastic> getBizStoreElastics() {
        return bizStoreElastics;
    }

    public BizStoreElasticList setBizStoreElastics(List<BizStoreElastic> bizStoreElastics) {
        this.bizStoreElastics = bizStoreElastics;
        return this;
    }

    public BusinessTypeEnum getSearchedOnBusinessType() {
        return searchedOnBusinessType;
    }

    public BizStoreElasticList setSearchedOnBusinessType(BusinessTypeEnum searchedOnBusinessType) {
        this.searchedOnBusinessType = searchedOnBusinessType;
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
        return "BizStoreElasticList{" +
            "scrollId='" + scrollId + '\'' +
            ", cityName='" + cityName + '\'' +
            ", jsonCategories=" + jsonCategories +
            ", bizStoreElastics=" + bizStoreElastics +
            ", searchedOnBusinessType=" + searchedOnBusinessType +
            ", error=" + error +
            '}';
    }
}