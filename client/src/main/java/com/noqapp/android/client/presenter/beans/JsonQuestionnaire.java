package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * User: hitender
 * Date: 10/21/19 6:51 AM
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
public class JsonQuestionnaire implements Serializable {

    @JsonProperty("id")
    private String questionnaireId;

    @JsonProperty("ti")
    private String title;

    @JsonProperty("bn")
    private String bizNameId;

    @JsonProperty("qs")
    private Map<Locale, List<SurveyQuestion>> questions;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getQuestionnaireId() {
        return questionnaireId;
    }

    public JsonQuestionnaire setQuestionnaireId(String questionnaireId) {
        this.questionnaireId = questionnaireId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public JsonQuestionnaire setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public JsonQuestionnaire setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public Map<Locale, List<SurveyQuestion>> getQuestions() {
        return questions;
    }

    public JsonQuestionnaire setQuestions(Map<Locale, List<SurveyQuestion>> questions) {
        this.questions = questions;
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
        return "JsonQuestionnaire{" +
                "bizNameId='" + bizNameId + '\'' +
                ", questions=" + questions +
                ", error=" + error +
                '}';
    }
}