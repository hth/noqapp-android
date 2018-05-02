package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

    @JsonProperty("cityName")
    private String cityName;

    @JsonProperty("categories")
    private List<JsonCategory> jsonCategories = new ArrayList<>();

    @JsonProperty("result")
    private List<BizStoreElastic> bizStoreElastics = new ArrayList<>();

    public String getScrollId() {
        return scrollId;
    }

    public BizStoreElasticList setScrollId(String scrollId) {
        this.scrollId = scrollId;
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
}