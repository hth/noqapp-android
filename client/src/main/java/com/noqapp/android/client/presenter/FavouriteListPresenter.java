package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.FavoriteElastic;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface FavouriteListPresenter extends ResponseErrorPresenter {
    void favouriteListResponse(FavoriteElastic favoriteElastic);
}
