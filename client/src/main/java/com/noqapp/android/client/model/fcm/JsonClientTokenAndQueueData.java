package com.noqapp.android.client.model.fcm;

import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.common.fcm.data.JsonData;
import com.noqapp.android.common.model.types.MessageOriginEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-04-12 11:47
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
public class JsonClientTokenAndQueueData extends JsonData implements Serializable {

    @JsonProperty("mo")
    private MessageOriginEnum messageOrigin;

    @JsonProperty("tqs")
    private List<JsonTokenAndQueue> tokenAndQueues = new ArrayList<>();

    public MessageOriginEnum getMessageOrigin() {
        return messageOrigin;
    }

    public JsonClientTokenAndQueueData setMessageOrigin(MessageOriginEnum messageOrigin) {
        this.messageOrigin = messageOrigin;
        return this;
    }

    public List<JsonTokenAndQueue> getTokenAndQueues() {
        return tokenAndQueues;
    }

    public JsonClientTokenAndQueueData setTokenAndQueues(List<JsonTokenAndQueue> tokenAndQueues) {
        this.tokenAndQueues = tokenAndQueues;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("JsonClientTokenAndQueueData{");
        sb.append("messageOrigin=").append(messageOrigin);
        sb.append(", tokenAndQueues=").append(tokenAndQueues);
        sb.append('}');
        return sb.toString();
    }
}
