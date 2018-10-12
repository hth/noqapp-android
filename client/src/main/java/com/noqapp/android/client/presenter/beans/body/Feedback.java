package com.noqapp.android.client.presenter.beans.body;

import com.noqapp.android.common.model.types.MessageOriginEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * hitender
 * 10/4/18 5:07 PM
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
public class Feedback implements Serializable{

    @JsonProperty("s")
    private String subject;

    @JsonProperty("b")
    private String body;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty ("t")
    private int token;

    @JsonProperty ("mo")
    private MessageOriginEnum messageOrigin;

    public String getSubject() {
        return subject;
    }

    public Feedback setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getBody() {
        return body;
    }

    public Feedback setBody(String body) {
        this.body = body;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public Feedback setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public int getToken() {
        return token;
    }

    public Feedback setToken(int token) {
        this.token = token;
        return this;
    }

    public MessageOriginEnum getMessageOrigin() {
        return messageOrigin;
    }

    public Feedback setMessageOrigin(MessageOriginEnum messageOrigin) {
        this.messageOrigin = messageOrigin;
        return this;
    }
}