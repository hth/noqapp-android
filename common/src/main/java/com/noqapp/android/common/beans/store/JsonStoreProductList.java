package com.noqapp.android.common.beans.store;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 10/3/20 9:28 PM
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
public class JsonStoreProductList implements Serializable {

    @JsonProperty("products")
    private List<JsonStoreProduct> jsonStoreProducts = new LinkedList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public List<JsonStoreProduct> getJsonStoreProducts() {
        return jsonStoreProducts;
    }

    public JsonStoreProductList addJsonStoreProduct(JsonStoreProduct jsonStoreProduct) {
        this.jsonStoreProducts.add(jsonStoreProduct);
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }
}
