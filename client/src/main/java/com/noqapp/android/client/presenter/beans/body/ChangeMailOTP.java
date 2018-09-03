package com.noqapp.android.client.presenter.beans.body;

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
}