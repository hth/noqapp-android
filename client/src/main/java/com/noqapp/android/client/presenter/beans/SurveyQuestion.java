package com.noqapp.android.client.presenter.beans;

import com.noqapp.android.client.model.types.QuestionTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.Set;

/**
 * Used in flow and as a form.
 * User: hitender
 * Date: 11/6/19 1:33 PM
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
public class SurveyQuestion implements Serializable {

    @JsonProperty("q")
    private String question;

    @JsonProperty("t")
    private QuestionTypeEnum questionType;

    @JsonProperty("a")
    private Set<String> availableResponses;

    @JsonProperty("r")
    private boolean required;

    /* Self defined category. */
    @JsonProperty("c")
    private String questionCategoryId;

    /* Skip works on boolean fields. */
    @JsonProperty("s")
    private String skipTo;

    public String getQuestion() {
        return question;
    }

    public SurveyQuestion setQuestion(String question) {
        this.question = question;
        return this;
    }

    public QuestionTypeEnum getQuestionType() {
        return questionType;
    }

    public SurveyQuestion setQuestionType(QuestionTypeEnum questionType) {
        this.questionType = questionType;
        return this;
    }

    public Set<String> getAvailableResponses() {
        return availableResponses;
    }

    public SurveyQuestion setAvailableResponses(Set<String> availableResponses) {
        this.availableResponses = availableResponses;
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public SurveyQuestion setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public String getQuestionCategoryId() {
        return questionCategoryId;
    }

    public SurveyQuestion setQuestionCategoryId(String questionCategoryId) {
        this.questionCategoryId = questionCategoryId;
        return this;
    }

    public String getSkipTo() {
        return skipTo;
    }

    public SurveyQuestion setSkipTo(String skipTo) {
        this.skipTo = skipTo;
        return this;
    }
}
