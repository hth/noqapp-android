package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.BizNamePresenter;
import com.noqapp.android.merchant.interfaces.CheckAssetPresenter;
import com.noqapp.android.merchant.model.CheckAssetApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAsset;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAssetList;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CheckAsset;
import com.noqapp.android.merchant.utils.PermissionHelper;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.AppInitialize;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.FloorAdapter;
import com.noqapp.android.merchant.views.utils.PdfInventoryGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InventoryHomeFragment extends BaseFragment implements BizNamePresenter, CheckAssetPresenter,
        FloorAdapter.OnItemClickListener {
    private RecyclerView rv_floors;
    private CheckAssetApiCalls checkAssetApiCalls;
    private String bizNameId = "";
    private String businessName = "";
    private String businessAddress= "";
    public static Map<String, List<JsonCheckAsset>> tempList = new HashMap<>();
    public static Map<String, List<JsonCheckAsset>> prefList = new HashMap<>();
    private Button btn_print;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        super.onCreateView(inflater, container, args);
        View view = inflater.inflate(R.layout.frag_inventory_home, container, false);
        btn_print = view.findViewById(R.id.btn_print);
        PermissionHelper permissionHelper = new PermissionHelper(getActivity());
        prefList = AppInitialize.getInventoryPrefs();
        Log.e("saved info",prefList.toString());
        btn_print.setOnClickListener(v -> {
            if(tempList.size()>0) {
                if (permissionHelper.isStoragePermissionAllowed()) {
                    PdfInventoryGenerator pdfGenerator = new PdfInventoryGenerator(getActivity());
                    pdfGenerator.createPdf(businessName,businessAddress,tempList);
                } else {
                    permissionHelper.requestStoragePermission();
                }
            }else{
                new CustomToast().showToast(getActivity(),"Please visit atleast 1 floor to create pdf");
            }
        });
        rv_floors = view.findViewById(R.id.rv_floors);
        rv_floors.setHasFixedSize(true);
        rv_floors.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rv_floors.setItemAnimator(new DefaultItemAnimator());
        checkAssetApiCalls = new CheckAssetApiCalls();
        checkAssetApiCalls.setBizNamePresenter(this);
        checkAssetApiCalls.setCheckAssetPresenter(this);
        if (new NetworkUtil(getActivity()).isOnline()) {
            setProgressMessage("Fetching info...");
            showProgress();
            checkAssetApiCalls.bizName(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new CheckAsset());
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("Floors");
        LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.VISIBLE);
        LaunchActivity.getLaunchActivity().enableDisableBack(false);
    }

    @Override
    public void onFloorItemClick(JsonCheckAsset item) {
        RoomsOnFloorListFragment roflf = new RoomsOnFloorListFragment();
        Bundle b = new Bundle();
        b.putSerializable("data", item);
        b.putString("floor", item.getFloor());
        b.putString("bizNameId", bizNameId);
        roflf.setArguments(b);
        LaunchActivity.getLaunchActivity().replaceFragmentWithBackStack(R.id.frame_layout, roflf, "roomsOnFloorList");
    }

    @Override
    public void bizNameResponse(CheckAsset checkAsset) {
        bizNameId = checkAsset.getBizNameId();
        businessName = checkAsset.getBusinessName();
        businessAddress = checkAsset.getAreaAndTown();
        dismissProgress();
        if (new NetworkUtil(getActivity()).isOnline()) {
            setProgressMessage("fetching info...");
            showProgress();
            checkAssetApiCalls.floors(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), checkAsset);
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    @Override
    public void jsonCheckAssetListResponse(JsonCheckAssetList jsonCheckAssetList) {
        dismissProgress();
        if (null != jsonCheckAssetList.getJsonCheckAssets()) {
            btn_print.setVisibility(View.VISIBLE);
            FloorAdapter floorAdapter = new FloorAdapter(jsonCheckAssetList.getJsonCheckAssets(), getActivity(), this);
            rv_floors.setAdapter(floorAdapter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppInitialize.setInventoryPrefs(tempList);
    }
}
