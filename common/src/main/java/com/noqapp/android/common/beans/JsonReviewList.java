package com.noqapp.android.common.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings ({
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
public class JsonReviewList extends AbstractDomain implements Serializable {

    @JsonProperty("rs")
    private List<JsonReview> jsonReviews = new ArrayList<>();

    @JsonProperty("ar")
    private int aggregateRatingCount;

    @JsonProperty ("qr")
    private String codeQR;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public List<JsonReview> getJsonReviews() {
        return jsonReviews;
    }

    public JsonReviewList setJsonReviews(List<JsonReview> jsonReviews) {
        this.jsonReviews = jsonReviews;
        return this;
    }

    public int getAggregateRatingCount() {
        return aggregateRatingCount;
    }

    public JsonReviewList setAggregateRatingCount(int aggregateRatingCount) {
        this.aggregateRatingCount = aggregateRatingCount;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonReviewList setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public JsonReviewList setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return "JsonReviewList{" +
                "jsonReviews=" + jsonReviews +
                ", aggregateRatingCount=" + aggregateRatingCount +
                ", error=" + error +
                '}';
    }
}
