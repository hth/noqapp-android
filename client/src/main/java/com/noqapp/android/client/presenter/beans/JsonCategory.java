package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Created by hitender on 12/29/17.
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
public class JsonCategory implements Serializable {

    @JsonProperty("bc")
    private String bizCategoryId;

    @JsonProperty("cn")
    private String categoryName;

    @JsonProperty("di")
    private String displayImage;

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public JsonCategory setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
        return this;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public JsonCategory setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public JsonCategory setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
        return this;
    }

    @Override
    public String toString() {
        return "JsonCategory{" +
            "bizCategoryId='" + bizCategoryId + '\'' +
            ", categoryName='" + categoryName + '\'' +
            ", displayImage='" + displayImage + '\'' +
            '}';
    }
}
