package com.noqapp.android.client.presenter.beans;

import com.noqapp.android.client.model.types.QuestionTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
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

    @JsonProperty("bn")
    private String bizNameId;

    @JsonProperty("qs")
    private Map<Locale, Map<String, QuestionTypeEnum>> questions;

    public String getBizNameId() {
        return bizNameId;
    }

    public JsonQuestionnaire setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public Map<Locale, Map<String, QuestionTypeEnum>> getQuestions() {
        return questions;
    }

    public JsonQuestionnaire setQuestions(Map<Locale, Map<String, QuestionTypeEnum>> questions) {
        this.questions = questions;
        return this;
    }
}