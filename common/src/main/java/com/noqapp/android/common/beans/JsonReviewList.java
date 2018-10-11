package com.noqapp.android.common.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 10/10/18 10:18 PM
 */
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
public class JsonReviewList extends AbstractDomain {

    @JsonProperty("rs")
    private List<JsonReview> jsonReviews = new ArrayList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public List<JsonReview> getJsonReviews() {
        return jsonReviews;
    }

    public JsonReviewList setJsonReviews(List<JsonReview> jsonReviews) {
        this.jsonReviews = jsonReviews;
        return this;
    }

    public JsonReviewList addJsonReview(JsonReview jsonReview) {
        this.jsonReviews.add(jsonReview);
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
        sb.append("jsonReviews=").append(jsonReviews);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}
