package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.FavouriteApiCall;
import com.noqapp.android.client.presenter.FavouriteListPresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.FavoriteElastic;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.StoreInfoViewAllAdapter;
import com.noqapp.android.common.model.types.BusinessSupportEnum;

import java.util.ArrayList;

public class FavouriteListActivity extends BaseActivity implements StoreInfoViewAllAdapter.OnItemClickListener,
        FavouriteListPresenter {
    private final String TAG = FavouriteListActivity.class.getSimpleName();
    private RecyclerView rv_merchant_around_you;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        initActionsViews(false);
        rv_merchant_around_you = findViewById(R.id.rv_merchant_around_you);
        tv_toolbar_title.setText(getString(R.string.favourite));
        rv_merchant_around_you.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_merchant_around_you.setLayoutManager(horizontalLayoutManager);
        rv_merchant_around_you.setItemAnimator(new DefaultItemAnimator());

        if (NetworkUtils.isConnectingToInternet(this)) {
            FavouriteApiCall favouriteApiCall = new FavouriteApiCall();
            favouriteApiCall.setFavouriteListPresenter(this);
            favouriteApiCall.favorite(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    @Override
    public void onStoreItemClick(BizStoreElastic item) {
        Intent in;
        Bundle b = new Bundle();
        switch (item.getBusinessType()) {
            //Level up
            case DO:
            case CD:
            case CDQ:
            case BK:
            case HS:
            case PW:
                // open hospital/Bank profile
                b.putString(IBConstant.KEY_CODE_QR, item.getCodeQR());
                b.putBoolean(IBConstant.KEY_FROM_LIST, false);
                b.putBoolean(IBConstant.KEY_CALL_CATEGORY, true);
                b.putBoolean(IBConstant.KEY_IS_CATEGORY, false);
                b.putSerializable("BizStoreElastic", item);
                in = new Intent(this, CategoryInfoActivity.class);
                in.putExtra("bundle", b);
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
        ArrayList<BizStoreElastic> list = (ArrayList<BizStoreElastic>) favoriteElastic.getFavoriteTagged();
        StoreInfoViewAllAdapter storeInfoViewAllAdapter = new StoreInfoViewAllAdapter(list, this, this, Double.parseDouble(lat), Double.parseDouble(lng));
        rv_merchant_around_you.setAdapter(storeInfoViewAllAdapter);
    }
}
