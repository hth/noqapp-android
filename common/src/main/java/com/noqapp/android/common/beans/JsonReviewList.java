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

    @JsonProperty("d")
    private String displayName;

    @JsonProperty("rs")
    private List<JsonReview> jsonReviews = new ArrayList<>();

    @JsonProperty("ar")
    private int aggregateRatingCount;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getDisplayName() {
        return displayName;
    }

    public JsonReviewList setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

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

    public ErrorEncounteredJson getError() {
        return error;
    }

    public JsonReviewList setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JsonReviewList{");
        sb.append("displayName='").append(displayName).append('\'');
        sb.append(", jsonReviews=").append(jsonReviews);
        sb.append(", aggregateRatingCount=").append(aggregateRatingCount);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}
