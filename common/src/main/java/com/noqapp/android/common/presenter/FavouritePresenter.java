package com.noqapp.android.common.presenter;

import com.noqapp.android.common.beans.JsonResponse;

public interface FavouritePresenter extends ResponseErrorPresenter{

    void favouriteResponse(JsonResponse jsonResponse);

}