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
import android.widget.Button;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.types.NearMeModel;
import com.noqapp.android.client.presenter.NearMePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.StoreInfoParam;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.CategoryInfoActivity;
import com.noqapp.android.client.views.activities.DoctorProfile1Activity;
import com.noqapp.android.client.views.activities.DoctorProfileActivity;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.StoreDetailActivity;
import com.noqapp.android.client.views.activities.ViewAllListActivity;
import com.noqapp.android.client.views.adapters.RecyclerCustomAdapter;
import com.noqapp.android.client.views.adapters.StoreInfoAdapter;
import com.noqapp.android.client.views.toremove.DataModel;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanQueueFragment extends Scanner implements RecyclerCustomAdapter.OnItemClickListener,NearMePresenter,StoreInfoAdapter.OnItemClickListener{
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
    private static ArrayList<BizStoreElastic> nearMeData;
    private  RecyclerCustomAdapter.OnItemClickListener listener;
    private  StoreInfoAdapter.OnItemClickListener listener1;

    @BindView(R.id.tv_recent_view_all)
    protected TextView tv_recent_view_all;
    @BindView(R.id.tv_near_view_all)
    protected TextView tv_near_view_all;



    @BindView(R.id.btn_type_1)
    protected Button btn_type_1;


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
        String[] nameArray = {"Cakes", "Navratna", "Coffee Cafe Day", "Bikaner wala", "Haldiram"};
        for (int i = 0; i < nameArray.length; i++) {
            data.add(new DataModel(
                    nameArray[i],
                    "https://noqapp.com/imgs/240x120/b.jpeg"
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
//            CategoryInfoFragment cif = new CategoryInfoFragment();
//            cif.setArguments(b);
//            replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, cif, TAG, currentTab);

            Intent in = new Intent(getActivity(), CategoryInfoActivity.class);
            //TODO(chandra) Need to define b with a constant
            in.putExtra("b",b);
            getActivity().startActivity(in);

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

        nearMeData = new ArrayList<>();
        for (int i = 0; i < bizStoreElasticList.getBizStoreElastics().size(); i++) {
            nearMeData.add(bizStoreElasticList.getBizStoreElastics().get(i));
        }
        storeInfoAdapter = new StoreInfoAdapter(nearMeData,getActivity(), listener1);
        rv_merchant_around_you.setAdapter(storeInfoAdapter);
        Log.v("NearMe",bizStoreElasticList.toString());

    }

    @Override
    public void nearMeError() {

    }

    @Override
    public void onStoreItemClick(BizStoreElastic item, View view, int pos) {
        Intent in = new Intent(getActivity(), StoreDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("BizStoreElastic", item);
        in.putExtras(bundle);
        startActivity(in);
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
    @OnClick(R.id.tv_near_view_all)
    public void nearClick(){
        Intent intent = new Intent(getActivity(),ViewAllListActivity.class);
        intent.putExtra("list", (Serializable) nearMeData);
        startActivity(intent);

    }

    @OnClick(R.id.tv_recent_view_all)
    public void recentClick(){
        Intent intent = new Intent(getActivity(),ViewAllListActivity.class);
        Bundle bundle = new Bundle();
       // bundle.putSerializable("data", data1);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.btn_type_1)
    public void btn1() {
        Intent intent = new Intent(getActivity(), DoctorProfile1Activity.class);
        startActivity(intent);

//
//        // Extract Bitmap from ImageView drawable
//        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.launcher);
//        if (drawable instanceof BitmapDrawable) {
//            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//
//            // Store image to default external storage directory
//            Uri bitmapUri = null;
//            try {
//                File file = new File(Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
//                file.getParentFile().mkdirs();
//                FileOutputStream out = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
//                out.close();
//                bitmapUri = Uri.fromFile(file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (bitmapUri != null) {
//                Intent shareIntent = new Intent();
//                shareIntent.putExtra(Intent.EXTRA_TEXT, "I am inviting you to join our app. A simple and secure app developed by us. https://www.google.co.in/");
//                shareIntent.setAction(Intent.ACTION_SEND);
//                shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
//                shareIntent.setType("*/*");
//                startActivity(Intent.createChooser(shareIntent, "Share my app"));
//            }
//        }


    }


}
