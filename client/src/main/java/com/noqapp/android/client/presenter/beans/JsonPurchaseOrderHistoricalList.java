package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 10/5/18 11:08 AM
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
public class JsonPurchaseOrderHistoricalList extends AbstractDomain implements Serializable{

    @JsonProperty("pos")
    private List<JsonPurchaseOrderHistorical> jsonPurchaseOrderHistoricals = new ArrayList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public List<JsonPurchaseOrderHistorical> getJsonPurchaseOrderHistoricals() {
        return jsonPurchaseOrderHistoricals;
    }

    public JsonPurchaseOrderHistoricalList setJsonPurchaseOrderHistoricals(List<JsonPurchaseOrderHistorical> jsonPurchaseOrderHistoricals) {
        this.jsonPurchaseOrderHistoricals = jsonPurchaseOrderHistoricals;
        return this;
    }

    public JsonPurchaseOrderHistoricalList addJsonPurchaseOrderHistorical(JsonPurchaseOrderHistorical jsonPurchaseOrderHistorical) {
        this.jsonPurchaseOrderHistoricals.add(jsonPurchaseOrderHistorical);
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public JsonPurchaseOrderHistoricalList setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return "JsonPurchaseOrderHistoricalList{" +
                "jsonPurchaseOrderHistoricals=" + jsonPurchaseOrderHistoricals +
                ", error=" + error +
                '}';
    }
}
