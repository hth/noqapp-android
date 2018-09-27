package com.noqapp.android.client.views.fragments;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.NearMeModel;
import com.noqapp.android.client.presenter.NearMePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.StoreInfoParam;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.SortPlaces;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.CategoryInfoActivity;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.StoreDetailActivity;
import com.noqapp.android.client.views.adapters.StoreInfoViewAllAdapter;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import com.google.android.gms.maps.model.LatLng;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;


public class OrderFragment extends Fragment implements StoreInfoViewAllAdapter.OnItemClickListener, NearMePresenter {
    private RecyclerView rv_merchant_around_you;
    private ArrayList<BizStoreElastic> listData;
    private StoreInfoViewAllAdapter storeInfoViewAllAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        rv_merchant_around_you = view.findViewById(R.id.rv_recent_activity);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            StoreInfoParam storeInfoParam = new StoreInfoParam();
            storeInfoParam.setCityName(LaunchActivity.getLaunchActivity().getDefaultCity());
            storeInfoParam.setLatitude(String.valueOf(LaunchActivity.getLaunchActivity().getDefaultLatitude()));
            storeInfoParam.setLongitude(String.valueOf(LaunchActivity.getLaunchActivity().getDefaultLongitude()));
            storeInfoParam.setFilters("xyz");
            storeInfoParam.setScrollId("");
            new NearMeModel(this).nearMeStore(UserUtils.getDeviceId(), storeInfoParam);
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
        listData = new ArrayList<>();
        rv_merchant_around_you.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_merchant_around_you.setLayoutManager(horizontalLayoutManagaer);
        rv_merchant_around_you.setItemAnimator(new DefaultItemAnimator());
        // rv_merchant_around_you.addItemDecoration(new VerticalSpaceItemDecoration(2));

        return view;
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
                b.putBoolean("CallCategory", true);
                b.putBoolean("isCategoryData", false);
                b.putSerializable("BizStoreElastic", item);
                Intent in = new Intent(getActivity(), CategoryInfoActivity.class);
                in.putExtra("bundle", b);
                startActivity(in);
                break;
            default:
                // open order screen
                Intent intent = new Intent(getActivity(), StoreDetailActivity.class);
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
        //sort the list, give the Comparator the current location
        Collections.sort(nearMeData, new SortPlaces(new LatLng(LaunchActivity.getLaunchActivity().getDefaultLatitude(), LaunchActivity.getLaunchActivity().getDefaultLongitude())));
        listData = nearMeData;
        //add all items
        storeInfoViewAllAdapter = new StoreInfoViewAllAdapter(listData, getActivity(), this);
        rv_merchant_around_you.setAdapter(storeInfoViewAllAdapter);

    }

    @Override
    public void nearMeError() {
        //LaunchActivity.getLaunchActivity().dismissProgress();

    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        new ErrorResponseHandler().processError(getActivity(), eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
    }

    @Override
    public void authenticationFailure() {
        //dismissProgress();
        // AppUtilities.authenticationProcessing(this, errorCode);
    }

}
