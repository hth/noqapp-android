package com.noqapp.android.client.presenter.beans.body.mail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 9/3/18 4:28 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeMailOTP {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty ("name")
    private String name;

    @JsonProperty ("mailOTP")
    private String mailOTP;

    private ChangeMailOTP(String userId, String name, String mailOTP) {
        this.userId = userId;
        this.name = name;
        this.mailOTP = mailOTP;
    }

    public static ChangeMailOTP newInstance(String userId, String name, String mailOTP) {
        return new ChangeMailOTP(userId, name, mailOTP);
    }

    public ChangeMailOTP() {
    }

    public String getUserId() {
        return userId;
    }

    public ChangeMailOTP setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getName() {
        return name;
    }

    public ChangeMailOTP setName(String name) {
        this.name = name;
        return this;
    }

    public String getMailOTP() {
        return mailOTP;
    }

    public ChangeMailOTP setMailOTP(String mailOTP) {
        this.mailOTP = mailOTP;
        return this;
    }
}