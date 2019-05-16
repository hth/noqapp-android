package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


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
public class JsonEventList extends AbstractDomain implements Serializable {

    @JsonProperty("fds")
    private List<JsonEvent> jsonEvents = new LinkedList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public List<JsonEvent> getJsonEvents() {
        return jsonEvents;
    }

    public void setJsonEvents(List<JsonEvent> jsonEvents) {
        this.jsonEvents = jsonEvents;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JsonEventList{");
        sb.append("jsonEvents=").append(jsonEvents);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}