package com.noqapp.android.merchant.presenter.beans.body.merchant;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.List;

/**
 * hitender
 * 8/8/20 11:17 PM
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
public class StoreHours implements Serializable {

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("shs")
    private List<StoreHourDaily> storeHourDaily;

    public String getCodeQR() {
        return codeQR;
    }

    public StoreHours setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public List<StoreHourDaily> getStoreHourDaily() {
        return storeHourDaily;
    }

    public StoreHours setStoreHourDaily(List<StoreHourDaily> storeHourDaily) {
        this.storeHourDaily = storeHourDaily;
        return this;
    }
}
