package com.noqapp.android.merchant.presenter.beans.body.merchant;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;

import java.util.List;

/**
 * hitender
 * 2020-06-13 16:51
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
public class MessageCustomer extends AbstractDomain {

    @JsonProperty("ti")
    private String title;

    @JsonProperty("bd")
    private String body;

    @JsonProperty("cqs")
    private List<String> codeQRs;

    /**
     * Failed to deliver to codeQRs due to access restrictions or other reasons.
     * Note: This could be removed.
     */
    @JsonProperty("fcqs")
    private List<String> failedCodeQRs;

    @JsonProperty("cn")
    private int sendMessageCount;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getCodeQRs() {
        return codeQRs;
    }

    public void setCodeQRs(List<String> codeQRs) {
        this.codeQRs = codeQRs;
    }

    public List<String> getFailedCodeQRs() {
        return failedCodeQRs;
    }

    public void setFailedCodeQRs(List<String> failedCodeQRs) {
        this.failedCodeQRs = failedCodeQRs;
    }

    public int getSendMessageCount() {
        return sendMessageCount;
    }

    public void setSendMessageCount(int sendMessageCount) {
        this.sendMessageCount = sendMessageCount;
    }
}

