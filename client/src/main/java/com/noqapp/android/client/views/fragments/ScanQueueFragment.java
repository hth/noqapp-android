package com.noqapp.android.client.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.types.BusinessTypeEnum;
import com.noqapp.android.client.model.types.NearMeModel;
import com.noqapp.android.client.model.types.StoreModel;
import com.noqapp.android.client.presenter.NearMePresenter;
import com.noqapp.android.client.presenter.StorePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonStore;
import com.noqapp.android.client.presenter.beans.body.StoreInfoParam;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.DoctorProfileActivity;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.StoreDetailActivity;
import com.noqapp.android.client.views.adapters.RecyclerCustomAdapter;
import com.noqapp.android.client.views.adapters.StoreInfoAdapter;
import com.noqapp.android.client.views.toremove.DataModel;
import com.noqapp.android.client.views.toremove.MyData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanQueueFragment extends Scanner implements RecyclerCustomAdapter.OnItemClickListener,NearMePresenter,StorePresenter,StoreInfoAdapter.OnItemClickListener{
    private final String TAG = ScanQueueFragment.class.getSimpleName();

    @BindView(R.id.cv_scan)
    protected CardView cv_scan;

    @BindView(R.id.recyclerView)
    protected RecyclerView recyclerView;
    @BindView(R.id.rv_merchant_around_you)
    protected RecyclerView rv_merchant_around_you;
    private String currentTab = "";
    private boolean fromList = false;
    private static RecyclerView.Adapter adapter;
    private StoreInfoAdapter storeInfoAdapter;
    private static ArrayList<DataModel> data;
    private static ArrayList<BizStoreElastic> data1;
    private  RecyclerCustomAdapter.OnItemClickListener listener;
    private  StoreInfoAdapter.OnItemClickListener listener1;

    public ScanQueueFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_scan_queue, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentTab = LaunchActivity.tabHome;
        Bundle bundle = getArguments();
        if (null != bundle) {
            if (bundle.getBoolean(KEY_FROM_LIST, false)) {
                // don 't start the scanner
                currentTab = LaunchActivity.tabList;
                fromList = true;
            } else {
                startScanningBarcode();
            }
        } else {
            // startScanningBarcode();
            // commented due to last discussion that barcode should not start automatically
        }
        listener = this;
        listener1 = this;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        data = new ArrayList<DataModel>();
        for (int i = 0; i < MyData.nameArray.length; i++) {
            data.add(new DataModel(
                    MyData.nameArray[i],
                    "",
                    0,
                    MyData.drawableArray[i]
            ));
        }
        adapter = new RecyclerCustomAdapter(data,getActivity(), listener);
        recyclerView.setAdapter(adapter);
        rv_merchant_around_you.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer1
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_merchant_around_you.setLayoutManager(horizontalLayoutManagaer1);
       // rv_merchant_around_you.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.HORIZONTAL));
        rv_merchant_around_you.setItemAnimator(new DefaultItemAnimator());


        getNearMeInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!fromList)// to not modify the actionbar if it is coming from list
            LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.tab_scan));
        LaunchActivity.getLaunchActivity().enableDisableBack(false);
    }

    @OnClick(R.id.cv_scan)
    public void scanQR() {
        startScanningBarcode();
    }

    @Override
    protected void barcodeResult(String codeQR, boolean isCategoryData) {
        Bundle b = new Bundle();
        b.putString(KEY_CODE_QR, codeQR);
        b.putBoolean(KEY_FROM_LIST, fromList);
        b.putBoolean(KEY_IS_HISTORY, false);
        if (isCategoryData) {
            CategoryInfoFragment cif = new CategoryInfoFragment();
            cif.setArguments(b);
            replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, cif, TAG, currentTab);
        } else {
            JoinFragment jf = new JoinFragment();
            b.putBoolean("isCategoryData", false);
            jf.setArguments(b);
            replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, jf, TAG, currentTab);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    public void onItemClick(DataModel item, View view,int pos) {
        if(pos%2==0) {
            Intent in = new Intent(getActivity(), StoreDetailActivity.class);
            in.putExtra("store_name", item.getName());
            startActivity(in);
        }else{
            Intent in = new Intent(getActivity(), DoctorProfileActivity.class);
           // in.putExtra("store_name",item.getName());
            startActivity(in);
        }
    }

    private void getNearMeInfo(){
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            StoreInfoParam storeInfoParam = new StoreInfoParam();
            storeInfoParam.setCityName("Vashi");
            storeInfoParam.setLatitude(String.valueOf(19.004550));
            storeInfoParam.setLongitude(String.valueOf(73.014529));
            storeInfoParam.setFilters("xyz");

                        /* New instance of progressbar because it is a new activity. */
//            progressDialog = new ProgressDialog(ReviewActivity.this);
//            progressDialog.setIndeterminate(true);
//            progressDialog.setMessage("Updating...");
//            progressDialog.show();
            NearMeModel.nearMePresenter = this;
            NearMeModel.nearMeStore(UserUtils.getDeviceId(),storeInfoParam);
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    @Override
    public void nearMeResponse(BizStoreElasticList bizStoreElasticList) {

        data1 = new ArrayList<>();
        for (int i = 0; i < bizStoreElasticList.getBizStoreElastics().size(); i++) {
            data1.add(bizStoreElasticList.getBizStoreElastics().get(i));
        }
        storeInfoAdapter = new StoreInfoAdapter(data1,getActivity(), listener1);
        rv_merchant_around_you.setAdapter(storeInfoAdapter);
        Log.v("NearMe",bizStoreElasticList.toString());

    }

    @Override
    public void nearMeError() {

    }

    @Override
    public void storeResponse(JsonStore jsonStore) {
        Toast.makeText(getActivity(),"jsonStore response success",Toast.LENGTH_LONG).show();
        Log.v("jsonStore response :", jsonStore.toString());

        if(jsonStore.getJsonQueue().getBusinessType() == BusinessTypeEnum.DO||
                jsonStore.getJsonQueue().getBusinessType() == BusinessTypeEnum.HO){
            // open hospital profile
        }else{
            //open store profile
            Intent in = new Intent(getActivity(), StoreDetailActivity.class);
            in.putExtra("store_name", jsonStore.getJsonQueue().getBusinessName());
            startActivity(in);
        }
    }

    @Override
    public void storeError() {

    }

    @Override
    public void authenticationFailure(int errorCode) {

    }

    @Override
    public void onStoreItemClick(BizStoreElastic item, View view, int pos) {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
           // LaunchActivity.getLaunchActivity().progressDialog.show();
            StoreModel.storePresenter = this;
            StoreModel.getStoreService(UserUtils.getDeviceId(), item.getCodeQR());
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }
}
