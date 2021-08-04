package com.noqapp.android.common.beans.marketplace;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.BusinessTypeEnum;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 3/7/21 8:23 PM
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
public class MarketplaceElasticList {

    @JsonProperty("si")
    private String scrollId;

    @JsonProperty("from")
    private int from;

    @JsonProperty("size")
    private int size;

    @JsonProperty("result")
    private List<MarketplaceElastic> marketplaceElastics = new LinkedList<>();

    @JsonProperty("bt")
    private BusinessTypeEnum searchedOnBusinessType;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getScrollId() {
        return scrollId;
    }

    public MarketplaceElasticList setScrollId(String scrollId) {
        this.scrollId = scrollId;
        return this;
    }

    public int getFrom() {
        return from;
    }

    public MarketplaceElasticList setFrom(int from) {
        this.from = from;
        return this;
    }

    public int getSize() {
        return size;
    }

    public MarketplaceElasticList setSize(int size) {
        this.size = size;
        return this;
    }

    public List<MarketplaceElastic> getMarketplaceElastics() {
        return marketplaceElastics;
    }

    public MarketplaceElasticList setMarketplaceElastics(List<MarketplaceElastic> marketplaceElastics) {
        this.marketplaceElastics = marketplaceElastics;
        return this;
    }

    public BusinessTypeEnum getSearchedOnBusinessType() {
        return searchedOnBusinessType;
    }

    public MarketplaceElasticList setSearchedOnBusinessType(BusinessTypeEnum searchedOnBusinessType) {
        this.searchedOnBusinessType = searchedOnBusinessType;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public MarketplaceElasticList setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }
}
