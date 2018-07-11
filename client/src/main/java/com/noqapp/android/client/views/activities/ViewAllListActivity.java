package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.NearMeModel;
import com.noqapp.android.client.presenter.NearMePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.StoreInfoParam;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.SortPlaces;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.StoreInfoViewAllAdapter;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chandra on 5/7/17.
 */
public class ViewAllListActivity extends AppCompatActivity implements StoreInfoViewAllAdapter.OnItemClickListener, NearMePresenter {


    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
    @BindView(R.id.rv_merchant_around_you)
    protected RecyclerView rv_merchant_around_you;
    private ArrayList<BizStoreElastic> listData;
    private StoreInfoViewAllAdapter storeInfoViewAllAdapter;
    private StoreInfoViewAllAdapter.OnItemClickListener listener;
    private String scrollId = "";
    private String city = "";
    private String lat = "";
    private String longitute = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        ButterKnife.bind(this);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText("View All");
        listener = this;
        //getString(R.string.medical_history));
        NearMeModel.nearMePresenter = this;
        listData = (ArrayList<BizStoreElastic>) getIntent().getExtras().getSerializable("list");
        city = getIntent().getStringExtra("city");
        lat = getIntent().getStringExtra("lat");
        longitute = getIntent().getStringExtra("long");
        scrollId = getIntent().getStringExtra("scrollId");
        rv_merchant_around_you.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_merchant_around_you.setLayoutManager(horizontalLayoutManagaer);
        rv_merchant_around_you.setItemAnimator(new DefaultItemAnimator());
        // rv_merchant_around_you.addItemDecoration(new VerticalSpaceItemDecoration(2));
        storeInfoViewAllAdapter = new StoreInfoViewAllAdapter(listData, this, listener, rv_merchant_around_you);
        rv_merchant_around_you.setAdapter(storeInfoViewAllAdapter);
        storeInfoViewAllAdapter.setOnLoadMoreListener(new StoreInfoViewAllAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
                listData.add(null);
                rv_merchant_around_you.post(new Runnable() {
                    public void run() {
                        storeInfoViewAllAdapter.notifyItemInserted(listData.size() - 1);
                        storeInfoViewAllAdapter.notifyDataSetChanged();
                    }
                });

                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    StoreInfoParam storeInfoParam = new StoreInfoParam();
                    storeInfoParam.setCityName(city);
                    storeInfoParam.setLatitude(lat);
                    storeInfoParam.setLongitude(longitute);
                    storeInfoParam.setFilters("xyz");
                    storeInfoParam.setScrollId(scrollId);
                    NearMeModel.nearMeStore(UserUtils.getDeviceId(), storeInfoParam);
                } else {
                    ShowAlertInformation.showNetworkDialog(ViewAllListActivity.this);
                }
            }
        });


    }

    @Override
    public void onStoreItemClick(BizStoreElastic item, View view, int pos) {
        switch (item.getBusinessType()) {
            case DO:
            case BK:
                // open hospital/Bank profile
                Bundle b = new Bundle();
                b.putString(NoQueueBaseFragment.KEY_CODE_QR, item.getCodeQR());
                b.putBoolean(NoQueueBaseFragment.KEY_FROM_LIST, false);
                b.putBoolean(NoQueueBaseFragment.KEY_IS_HISTORY, false);
                b.putBoolean("CallCategory", true);
                b.putBoolean("isCategoryData", false);
                Intent in = new Intent(this, CategoryInfoActivity.class);
                in.putExtra("bundle", b);
                startActivity(in);
                break;
            default:
                // open order screen
                Intent intent = new Intent(this, StoreDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("BizStoreElastic", item);
                intent.putExtras(bundle);
                startActivity(intent);
        }
    }

    @Override
    public void nearMeResponse(BizStoreElasticList bizStoreElasticList) {

        ArrayList<BizStoreElastic> nearMeData = new ArrayList<>();
        nearMeData.addAll(bizStoreElasticList.getBizStoreElastics());
        scrollId = bizStoreElasticList.getScrollId();
        //sort the list, give the Comparator the current location
        Collections.sort(nearMeData, new SortPlaces(new LatLng(Double.parseDouble(lat), Double.parseDouble(longitute))));
        //   remove progress item
        listData.remove(listData.size() - 1);
        storeInfoViewAllAdapter.notifyItemRemoved(listData.size());
        //add all items
        listData.addAll(nearMeData);
        storeInfoViewAllAdapter.notifyDataSetChanged();
        storeInfoViewAllAdapter.setLoaded();
        //or you can add all at once but do not forget to call storeInfoViewAllAdapter.notifyDataSetChanged();

    }

    @Override
    public void nearMeError() {
        //LaunchActivity.getLaunchActivity().dismissProgress();

    }
}
