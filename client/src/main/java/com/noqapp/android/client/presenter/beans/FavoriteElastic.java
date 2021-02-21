package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.ActionTypeEnum;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
public class FavoriteElastic implements Serializable {

    @JsonProperty("fs")
    private Set<BizStoreElastic> favoriteSuggested = new HashSet<>();

    @JsonProperty("ft")
    private Set<BizStoreElastic> favoriteTagged = new HashSet<>();

    @JsonProperty("at")
    private ActionTypeEnum actionType;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public Set<BizStoreElastic> getFavoriteSuggested() {
        return favoriteSuggested;
    }

    public FavoriteElastic addFavoriteSuggested(BizStoreElastic favoriteSuggested) {
        this.favoriteSuggested.add(favoriteSuggested);
        return this;
    }

    public Set<BizStoreElastic> getFavoriteTagged() {
        return favoriteTagged;
    }

    public FavoriteElastic addFavoriteTagged(BizStoreElastic favoriteTagged) {
        this.favoriteTagged.add(favoriteTagged);
        return this;
    }

    public ActionTypeEnum getActionType() {
        return actionType;
    }

    public FavoriteElastic setActionType(ActionTypeEnum actionType) {
        this.actionType = actionType;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public FavoriteElastic setCodeQR(String codeQR) {
        this.codeQR = codeQR;
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
        final StringBuilder sb = new StringBuilder("FavoriteElastic{");
        sb.append("favoriteSuggested=").append(favoriteSuggested);
        sb.append(", favoriteTagged=").append(favoriteTagged);
        sb.append(", actionType=").append(actionType);
        sb.append(", codeQR='").append(codeQR).append('\'');
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}