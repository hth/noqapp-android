package com.noqapp.android.merchant.presenter.beans;

import com.noqapp.android.common.beans.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 2018-12-17 18:11
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
public class JsonQueueTV extends AbstractDomain implements Serializable {

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("wp")
    private String webProfileId;

    @JsonProperty("qps")
    private List<JsonQueuedPersonTV> jsonQueuedPersonTVList = new ArrayList<>();

    public String getCodeQR() {
        return codeQR;
    }

    public JsonQueueTV setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getWebProfileId() {
        return webProfileId;
    }

    public JsonQueueTV setWebProfileId(String webProfileId) {
        this.webProfileId = webProfileId;
        return this;
    }

    public List<JsonQueuedPersonTV> getJsonQueuedPersonTVList() {
        return jsonQueuedPersonTVList;
    }

    public JsonQueueTV setJsonQueuedPersonTVList(List<JsonQueuedPersonTV> jsonQueuedPersonTVList) {
        this.jsonQueuedPersonTVList = jsonQueuedPersonTVList;
        return this;
    }

    public JsonQueueTV addJsonQueuedPersonTVList(JsonQueuedPersonTV jsonQueuedPersonTV) {
        this.jsonQueuedPersonTVList.add(jsonQueuedPersonTV);
        return this;
    }
}
