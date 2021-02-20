package com.noqapp.android.common.fcm.data;

import android.text.TextUtils;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech;
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.utils.deserialization.MapOfString;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @JsonDeserialize(using = MapOfString.class)
    @JsonProperty("translatedBody")
    private Map<String, String> translatedBody = new HashMap<>();

    @JsonProperty("imageURL")
    private String imageURL;

    @JsonProperty("textToSpeeches")
    private List<JsonTextToSpeech> jsonTextToSpeeches;

    public String getLocalLanguageMessageBody(String language) {
        String localLanguageMessageBody = translatedBody.get(language);
        if(TextUtils.isEmpty(localLanguageMessageBody)){
            return body;
        }else{
            return localLanguageMessageBody;
        }
    }

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

    public Map<String, String> getTranslatedBody() {
        return translatedBody;
    }

    public JsonData setTranslatedBody(Map<String, String> translatedBody) {
        this.translatedBody = translatedBody;
        return this;
    }

    public String getImageURL() {
        return imageURL;
    }

    public JsonData setImageURL(String imageURL) {
        this.imageURL = imageURL;
        return this;
    }

    public String getId() {
        return id;
    }

    public JsonData setId(String id) {
        this.id = id;
        return this;
    }

    public List<JsonTextToSpeech> getJsonTextToSpeeches() {
        return jsonTextToSpeeches;
    }

    public JsonData setJsonTextToSpeeches(List<JsonTextToSpeech> jsonTextToSpeeches) {
        this.jsonTextToSpeeches = jsonTextToSpeeches;
        return this;
    }

    public String getBodyBasedOnTargetLanguage(String targetLanguage) {
        if (StringUtils.isNotBlank(targetLanguage)) {
            return translatedBody.containsKey(targetLanguage) ? translatedBody.get(targetLanguage) : body;
        } else {
            return body;
        }

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JsonData{");
        sb.append("firebaseMessageType=").append(firebaseMessageType);
        sb.append(", id='").append(id).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append(", translatedBody=").append(translatedBody);
        sb.append(", imageURL='").append(imageURL).append('\'');
        sb.append(", jsonTextToSpeeches=").append(jsonTextToSpeeches);
        sb.append('}');
        return sb.toString();
    }
}
