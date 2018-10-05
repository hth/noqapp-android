package com.noqapp.android.client.presenter.beans;

import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.QueueUserStateEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

/**
 * hitender
 * 10/5/18 9:40 AM
 */
@SuppressWarnings ({
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
public class JsonQueueHistorical extends AbstractDomain {

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty ("qid")
    private String queueUserId;

    @JsonProperty ("tn")
    private int tokenNumber;

    @JsonProperty ("dn")
    private String displayName;

    @JsonProperty ("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty ("qs")
    private QueueUserStateEnum queueUserState;

    @JsonProperty ("ra")
    private int ratingCount;

    @JsonProperty ("hr")
    private int hoursSaved;

    @JsonProperty ("rv")
    private String review;

    @JsonProperty ("sn")
    private String serverName;

    @JsonProperty ("sb")
    private Date serviceBeginTime;

    @JsonProperty ("se")
    private Date serviceEndTime;

    @JsonProperty ("gq")
    private String guardianQid;
}