package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.FavouriteApiCall;
import com.noqapp.android.client.presenter.FavouriteListPresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.FavoriteElastic;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.StoreInfoViewAllAdapter;
import com.noqapp.android.common.model.types.BusinessSupportEnum;
import com.noqapp.android.common.model.types.BusinessTypeEnum;

import java.util.List;

public class FavouriteListActivity extends BaseActivity implements StoreInfoViewAllAdapter.OnItemClickListener,
        FavouriteListPresenter {
    private final String TAG = FavouriteListActivity.class.getSimpleName();
    private RecyclerView rcv_favourite;
    private boolean isFirstTime = false;
    private RelativeLayout rl_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        initActionsViews(true);
        rcv_favourite = findViewById(R.id.rcv_favourite);
        rl_empty = findViewById(R.id.rl_empty);
        tv_toolbar_title.setText(getString(R.string.favourite));
        rcv_favourite.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv_favourite.setLayoutManager(horizontalLayoutManager);
        rcv_favourite.setItemAnimator(new DefaultItemAnimator());

        callFavouriteApi();
    }

    @Override
    public void onStoreItemClick(BizStoreElastic item) {
        Intent in;
        Bundle b = new Bundle();
        switch (item.getBusinessType()) {
            case DO:
            case CD:
            case CDQ:
            case BK:
                // open hospital profile
                in = new Intent(this, BeforeJoinActivity.class);
                in.putExtra(IBConstant.KEY_IS_DO, item.getBusinessType() == BusinessTypeEnum.DO);
                in.putExtra(IBConstant.KEY_CODE_QR, item.getCodeQR());
                in.putExtra(IBConstant.KEY_FROM_LIST, false);
                in.putExtra(IBConstant.KEY_IS_CATEGORY, false);
                in.putExtra(IBConstant.KEY_IMAGE_URL, AppUtils.getImageUrls(BuildConfig.PROFILE_BUCKET, item.getDisplayImage()));
                startActivity(in);
                break;
            case PH:
                // open order screen
                in = new Intent(this, StoreDetailActivity.class);
                b.putSerializable("BizStoreElastic", item);
                in.putExtras(b);
                startActivity(in);
                break;
            case RSQ:
            case GSQ:
            case BAQ:
            case CFQ:
            case FTQ:
            case STQ:
            case HS:
            case PW:
                //@TODO Modification done due to corona crisis, Re-check all the functionality
                //proper testing required
                if (BusinessSupportEnum.OQ == item.getBusinessType().getBusinessSupport()) {
                    in = new Intent(this, BeforeJoinOrderQueueActivity.class);
                    b.putString(IBConstant.KEY_CODE_QR, item.getCodeQR());
                    b.putBoolean(IBConstant.KEY_FROM_LIST, false);
                    b.putBoolean(IBConstant.KEY_IS_CATEGORY, false);
                    b.putSerializable("BizStoreElastic", item);
                    in.putExtras(b);
                    startActivity(in);
                } else {
                    Log.d(TAG, "Reached un-supported condition");
                    FirebaseCrashlytics.getInstance().log("Reached un-supported condition " + item.getBusinessType());
                }
                break;
            default:
                // open order screen
                in = new Intent(this, StoreWithMenuActivity.class);
                b.putSerializable("BizStoreElastic", item);
                in.putExtras(b);
                startActivity(in);
        }
    }

    @Override
    public void favouriteListResponse(FavoriteElastic favoriteElastic) {
        String lat = String.valueOf(AppInitialize.location.getLatitude());
        String lng = String.valueOf(AppInitialize.location.getLongitude());
        List<BizStoreElastic> list = favoriteElastic.getFavoriteTagged();
        if (null != list && list.size() == 0) {
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            rl_empty.setVisibility(View.GONE);
        }
        StoreInfoViewAllAdapter storeInfoViewAllAdapter = new StoreInfoViewAllAdapter(list, this, this, Double.parseDouble(lat), Double.parseDouble(lng));
        rcv_favourite.setAdapter(storeInfoViewAllAdapter);
        isFirstTime = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstTime) {
            callFavouriteApi();
        }
    }

    private void callFavouriteApi() {
        if (NetworkUtils.isConnectingToInternet(this)) {
            FavouriteApiCall favouriteApiCall = new FavouriteApiCall();
            favouriteApiCall.setFavouriteListPresenter(this);
            favouriteApiCall.favorite(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }
}
