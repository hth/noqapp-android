package com.noqapp.android.client.presenter.beans;

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
public class JsonStore implements Serializable {

    @JsonProperty("queue")
    private JsonQueue jsonQueue;

    @JsonProperty("hours")
    private List<JsonHour> jsonHours = new LinkedList<>();

    @JsonProperty("categories")
    private List<JsonStoreCategory> jsonStoreCategories = new LinkedList<>();

    @JsonProperty("products")
    private List<JsonStoreProduct> jsonStoreProducts = new LinkedList<>();

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
}
