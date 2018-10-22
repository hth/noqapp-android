package com.noqapp.android.client.presenter.beans;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonHour;
import com.noqapp.android.common.beans.store.JsonStoreCategory;
import com.noqapp.android.common.beans.store.JsonStoreProduct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hitender on 3/23/18.
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
public class JsonStore implements Serializable {

    @JsonProperty("queue")
    private JsonQueue jsonQueue;

    @JsonProperty("hours")
    private List<JsonHour> jsonHours = new LinkedList<>();

    @JsonProperty("categories")
    private List<JsonStoreCategory> jsonStoreCategories = new LinkedList<>();

    @JsonProperty("products")
    private List<JsonStoreProduct> jsonStoreProducts = new LinkedList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public JsonQueue getJsonQueue() {
        return jsonQueue;
    }

    public JsonStore setJsonQueue(JsonQueue jsonQueue) {
        this.jsonQueue = jsonQueue;
        return this;
    }

    public List<JsonHour> getJsonHours() {
        return jsonHours;
    }

    public JsonStore setJsonHours(List<JsonHour> jsonHours) {
        this.jsonHours = jsonHours;
        return this;
    }

    public List<JsonStoreCategory> getJsonStoreCategories() {
        return jsonStoreCategories;
    }

    public JsonStore setJsonStoreCategories(List<JsonStoreCategory> jsonStoreCategories) {
        this.jsonStoreCategories = jsonStoreCategories;
        return this;
    }

    public List<JsonStoreProduct> getJsonStoreProducts() {
        return jsonStoreProducts;
    }

    public JsonStore setJsonStoreProducts(List<JsonStoreProduct> jsonStoreProducts) {
        this.jsonStoreProducts = jsonStoreProducts;
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
        final StringBuffer sb = new StringBuffer("JsonStore{");
        sb.append("jsonQueue=").append(jsonQueue);
        sb.append(", jsonHours=").append(jsonHours);
        sb.append(", jsonStoreCategories=").append(jsonStoreCategories);
        sb.append(", jsonStoreProducts=").append(jsonStoreProducts);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}
