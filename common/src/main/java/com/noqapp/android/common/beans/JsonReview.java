package com.noqapp.android.common.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * hitender
 * 10/10/18 10:14 PM
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
public class JsonReview extends AbstractDomain implements Serializable{

    @JsonProperty("ra")
    private int ratingCount;

    @JsonProperty("rv")
    private String review;

    @JsonProperty ("pi")
    private String profileImage;

    @JsonProperty ("nm")
    private String name;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("rs")
    private boolean reviewShow;

    public int getRatingCount() {
        return ratingCount;
    }

    public JsonReview setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
        return this;
    }

    public String getReview() {
        return review;
    }

    public JsonReview setReview(String review) {
        this.review = review;
        return this;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public JsonReview setProfileImage(String profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public String getName() {
        return name;
    }

    public JsonReview setName(String name) {
        this.name = name;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonReview setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public boolean isReviewShow() {
        return reviewShow;
    }

    public JsonReview setReviewShow(boolean reviewShow) {
        this.reviewShow = reviewShow;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JsonReview{");
        sb.append("ratingCount=").append(ratingCount);
        sb.append(", review='").append(review).append('\'');
        sb.append(", profileImage='").append(profileImage).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", queueUserId='").append(queueUserId).append('\'');
        sb.append(", reviewShow=").append(reviewShow);
        sb.append('}');
        return sb.toString();
    }
}
