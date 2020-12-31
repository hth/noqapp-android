package com.noqapp.android.common.fcm.data;

import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech;
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.List;

/**
 * User: hitender
 * Date: 3/7/17 11:07 AM
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
public abstract class JsonData extends AbstractDomain implements Serializable {

    @JsonProperty("f")
    private FirebaseMessageTypeEnum firebaseMessageType;

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("body")
    private String body;

    @JsonProperty("imageURL")
    private String imageURL;

    @JsonProperty("textToSpeeches")
    private List<JsonTextToSpeech> jsonTextToSpeeches;

    public FirebaseMessageTypeEnum getFirebaseMessageType() {
        return firebaseMessageType;
    }

    public JsonData setFirebaseMessageType(FirebaseMessageTypeEnum firebaseMessageType) {
        this.firebaseMessageType = firebaseMessageType;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public JsonData setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getBody() {
        return body;
    }

    public JsonData setBody(String body) {
        this.body = body;
        return this;
    }

    public String getImageURL() {
        return imageURL;
    }

    public JsonData setImageURL(String imageURL) {
        this.imageURL = imageURL;
        return this;
    }

    public List<JsonTextToSpeech> getJsonTextToSpeeches() {
        return jsonTextToSpeeches;
    }

    public JsonData setJsonTextToSpeeches(List<JsonTextToSpeech> jsonTextToSpeeches) {
        this.jsonTextToSpeeches = jsonTextToSpeeches;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("JsonData{");
        sb.append("firebaseMessageType=").append(firebaseMessageType);
        sb.append(", title='").append(title).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append(", imageURL='").append(imageURL).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
