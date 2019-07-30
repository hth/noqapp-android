package com.noqapp.android.merchant.presenter.beans;

import com.noqapp.android.common.beans.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
public class JsonCheckAssetList extends AbstractDomain {

    private List<JsonCheckAsset> jsonCheckAssets = new LinkedList<>();

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
}
