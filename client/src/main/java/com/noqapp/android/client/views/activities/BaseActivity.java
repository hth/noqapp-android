package com.noqapp.android.client.views.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.api.FavouriteApiImpl;
import com.noqapp.android.client.presenter.beans.FavoriteElastic;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ContextUtils;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.LocaleHelper;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.version_2.HomeActivity;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.ActionTypeEnum;
import com.noqapp.android.common.presenter.FavouritePresenter;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.common.utils.CustomProgressBar;
import com.noqapp.android.common.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity implements ResponseErrorPresenter, FavouritePresenter {
    private CustomProgressBar customProgressBar;
    protected ImageView iv_home;
    protected ImageView actionbarBack;
    protected ImageView iv_favourite;
    protected TextView tv_toolbar_title;
    private NetworkUtil networkUtil;
    protected boolean isFavourite = false;
    protected String codeQR = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customProgressBar = new CustomProgressBar(this);
        networkUtil = new NetworkUtil(this);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    protected void dismissProgress() {
        if (null != customProgressBar) {
            customProgressBar.dismissProgress();
        }
    }

    protected void showProgress() {
        if (null != customProgressBar) {
            customProgressBar.showProgress();
        }
    }

    protected void setProgressCancel(boolean isCancelled) {
        if (null != customProgressBar) {
            customProgressBar.setProgressCancel(isCancelled);
        }
    }

    protected void setProgressMessage(String msg) {
        if (null != customProgressBar) {
            customProgressBar.setProgressMessage(msg);
        }
    }

    protected void initActionsViews(boolean isHomeVisible) {
        iv_home = findViewById(R.id.iv_home);
        iv_favourite = findViewById(R.id.iv_favourite);
        actionbarBack = findViewById(R.id.actionbarBack);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        iv_home.setVisibility(isHomeVisible ? View.VISIBLE : View.INVISIBLE);
        if (AppInitialize.isLockMode) {
            iv_home.setVisibility(View.INVISIBLE);
        }
        actionbarBack.setOnClickListener((View v) -> finish());
        iv_home.setOnClickListener((View v) -> {
            Intent goToA = new Intent(BaseActivity.this, HomeActivity.class);
            goToA.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(goToA);
        });

        if (iv_favourite != null) {
            iv_favourite.setOnClickListener((View v) -> markFavourite());
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String localLanguage = LocaleHelper.INSTANCE.getLocaleLanguage(newBase);
        Locale localeToSwitchTo = new Locale(localLanguage);
        ContextWrapper localeUpdatedContext = ContextUtils.Companion.updateLocale(newBase, localeToSwitchTo);
        super.attachBaseContext(localeUpdatedContext);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing(this, () -> {
            Intent loginIntent = new Intent(BaseActivity.this, LoginActivity.class);
            loginIntent.putExtra("fromHome", true);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            return null;
        });
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        if (errorCode == Constants.INVALID_BAR_CODE) {
            ShowAlertInformation.showBarcodeErrorDialog(this);
        } else {
            new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
        }
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej) {
            new ErrorResponseHandler().processError(this, eej);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgress();
    }

    protected void replaceFragmentWithoutBackStack(int container, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragment).commit();
    }

    protected void hideSoftKeys(boolean isKioskMode) {
        if (isKioskMode) {
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE;

            getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            });
        }
    }

    protected boolean isOnline() {
        return networkUtil.isOnline();
    }

    protected void enableFavourite(String codeQR) {
        this.codeQR = codeQR;
        isFavourite = AppInitialize.getFavouriteList().contains(codeQR);
        iv_favourite.setVisibility(View.VISIBLE);
        iv_favourite.setBackground(isFavourite
            ? ContextCompat.getDrawable(this, R.drawable.heart_fill)
            : ContextCompat.getDrawable(this, R.drawable.heart_orange));
    }

    private void markFavourite() {
        showProgress();
        FavouriteApiImpl favouriteApiImpl = new FavouriteApiImpl();
        favouriteApiImpl.setFavouritePresenter(this);
        FavoriteElastic favoriteElastic = new FavoriteElastic();
        favoriteElastic.setActionType(isFavourite ? ActionTypeEnum.REMOVE : ActionTypeEnum.ADD);
        favoriteElastic.setCodeQR(codeQR);
        favouriteApiImpl.actionOnFavorite(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), favoriteElastic);
    }

    @Override
    public void favouriteResponse(JsonResponse jsonResponse) {
        dismissProgress();
        if (null != jsonResponse) {
            if (jsonResponse.getResponse() == Constants.SUCCESS) {
                ArrayList<String> favouriteList = AppInitialize.getFavouriteList();
                if (isFavourite) {
                    favouriteList.remove(codeQR);
                } else {
                    favouriteList.add(codeQR);
                }
                AppInitialize.saveFavouriteList(favouriteList);
                enableFavourite(codeQR);
                new CustomToast().showToast(this, "Favourite updated successfully!!!");
            } else {
                new CustomToast().showToast(this, "Favourite update failed!!!");
            }
        } else {
            new CustomToast().showToast(this, "Favourite update failed!!!");
        }
    }
}
