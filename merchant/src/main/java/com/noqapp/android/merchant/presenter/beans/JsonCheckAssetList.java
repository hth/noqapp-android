package com.noqapp.android.merchant.presenter.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.noqapp.android.common.beans.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-07-30 00:47
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
public class JsonCheckAssetList extends AbstractDomain implements Serializable {

    private List<JsonCheckAsset> jsonCheckAssets = new LinkedList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public List<JsonCheckAsset> getJsonCheckAssets() {
        return jsonCheckAssets;
    }

    public JsonCheckAssetList setJsonCheckAssets(List<JsonCheckAsset> jsonCheckAssets) {
        this.jsonCheckAssets = jsonCheckAssets;
        return this;
    }

    public JsonCheckAssetList addJsonCheckAsset(JsonCheckAsset jsonCheckAsset) {
        this.jsonCheckAssets.add(jsonCheckAsset);
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JsonCheckAssetList{");
        sb.append("jsonCheckAssets=").append(jsonCheckAssets);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}
