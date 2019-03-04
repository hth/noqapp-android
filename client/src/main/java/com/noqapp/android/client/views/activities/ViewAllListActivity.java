package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.SearchBusinessStoreApiCall;
import com.noqapp.android.client.presenter.SearchBusinessStorePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.SortPlaces;
import com.noqapp.android.client.views.adapters.StoreInfoViewAllAdapter;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import com.google.android.gms.maps.model.LatLng;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by chandra on 5/7/17.
 */
public class ViewAllListActivity extends AppCompatActivity implements StoreInfoViewAllAdapter.OnItemClickListener, SearchBusinessStorePresenter {

    private ArrayList<BizStoreElastic> listData;
    private StoreInfoViewAllAdapter storeInfoViewAllAdapter;

    private String scrollId = "";
    private String lat = "";
    private String longitute = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        RecyclerView rv_merchant_around_you = findViewById(R.id.rv_merchant_around_you);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_view_all));
        SearchBusinessStoreApiCall searchBusinessStoreModel = new SearchBusinessStoreApiCall(this);
        listData = (ArrayList<BizStoreElastic>) getIntent().getExtras().getSerializable("list");
        if (null == listData)
            listData = new ArrayList<>();
        String city = getIntent().getStringExtra("city");
        lat = getIntent().getStringExtra("lat");
        longitute = getIntent().getStringExtra("long");
        scrollId = getIntent().getStringExtra("scrollId");
        rv_merchant_around_you.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_merchant_around_you.setLayoutManager(horizontalLayoutManagaer);
        rv_merchant_around_you.setItemAnimator(new DefaultItemAnimator());
        // rv_merchant_around_you.addItemDecoration(new VerticalSpaceItemDecoration(2));
        storeInfoViewAllAdapter = new StoreInfoViewAllAdapter(listData, this, this, Double.parseDouble(lat), Double.parseDouble(longitute));
        rv_merchant_around_you.setAdapter(storeInfoViewAllAdapter);

    }

    @Override
    public void onStoreItemClick(BizStoreElastic item, View view, int pos) {
        switch (item.getBusinessType()) {
            case DO:
            case BK:
            case HS:
                // open hospital/Bank profile
                Bundle b = new Bundle();
                b.putString(NoQueueBaseFragment.KEY_CODE_QR, item.getCodeQR());
                b.putBoolean(NoQueueBaseFragment.KEY_FROM_LIST, false);
                b.putBoolean("CallCategory", true);
                b.putBoolean("isCategoryData", false);
                b.putSerializable("BizStoreElastic", item);
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
        storeInfoViewAllAdapter.notifyItemRemoved(listData.size());
        //add all items
        listData.addAll(nearMeData);
        storeInfoViewAllAdapter.notifyDataSetChanged();

    }

    @Override
    public void nearMeError() {
        //LaunchActivity.getLaunchActivity().dismissProgress();

    }

    @Override
    public void nearMeHospitalResponse(BizStoreElasticList bizStoreElasticList) {
        //Do nothing
    }

    @Override
    public void nearMeHospitalError() {

    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void authenticationFailure() {
        //dismissProgress();
        // AppUtilities.authenticationProcessing(this, errorCode);
    }
}
