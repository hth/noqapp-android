package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.ActionTypeEnum;

import java.io.Serializable;
import java.util.ArrayList;
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
public class FavoriteElastic implements Serializable {

    @JsonProperty("fs")
    private List<BizStoreElastic> favoriteSuggested = new LinkedList<>();

    @JsonProperty("ft")
    private List<BizStoreElastic> favoriteTagged = new LinkedList<>();

    @JsonProperty("fsb")
    private List<String> favoriteSuggestedBizNameIds = new LinkedList<>();

    @JsonProperty("ftb")
    private List<String> favoriteTaggedBizNameIds = new LinkedList<>();

    @JsonProperty("at")
    private ActionTypeEnum actionType;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public List<BizStoreElastic> getFavoriteSuggested() {
        return favoriteSuggested;
    }

    public FavoriteElastic setFavoriteSuggested(List<BizStoreElastic> favoriteSuggested) {
        this.favoriteSuggested = favoriteSuggested;
        return this;
    }

    public List<BizStoreElastic> getFavoriteTagged() {
        return favoriteTagged;
    }

    public FavoriteElastic setFavoriteTagged(List<BizStoreElastic> favoriteTagged) {
        this.favoriteTagged = favoriteTagged;
        return this;
    }

    public List<String> getFavoriteSuggestedBizNameIds() {
        return favoriteSuggestedBizNameIds;
    }

    public FavoriteElastic setFavoriteSuggestedBizNameIds(List<String> favoriteSuggestedBizNameIds) {
        this.favoriteSuggestedBizNameIds = favoriteSuggestedBizNameIds;
        return this;
    }

    public List<String> getFavoriteTaggedBizNameIds() {
        return favoriteTaggedBizNameIds;
    }

    public FavoriteElastic setFavoriteTaggedBizNameIds(List<String> favoriteTaggedBizNameIds) {
        this.favoriteTaggedBizNameIds = favoriteTaggedBizNameIds;
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
