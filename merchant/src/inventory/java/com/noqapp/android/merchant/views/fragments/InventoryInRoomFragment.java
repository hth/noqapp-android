package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.CheckAssetPresenter;
import com.noqapp.android.merchant.model.CheckAssetApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAsset;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAssetList;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CheckAsset;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.InventoryAdapter;

import java.util.List;

public class InventoryInRoomFragment extends BaseFragment implements
        CheckAssetPresenter, InventoryAdapter.OnItemClickListener {
    private String room = "";
    private String floor = "";
    private RecyclerView rv_inventory_items;
    private InventoryAdapter inventoryAdapter;
    private LinearLayout ll_bottom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        super.onCreateView(inflater, container, args);
        View view = inflater.inflate(R.layout.frag_inventory_in_rooms, container, false);
        rv_inventory_items = view.findViewById(R.id.rv_inventory_items);
        ll_bottom = view.findViewById(R.id.ll_bottom);
        rv_inventory_items.setHasFixedSize(true);
        rv_inventory_items.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rv_inventory_items.setItemAnimator(new DefaultItemAnimator());
        JsonCheckAsset jsonCheckAsset = (JsonCheckAsset) getArguments().getSerializable("data");
        String bizNameId = getArguments().getString("bizNameId", "");
        room = getArguments().getString("room", "");
        floor = getArguments().getString("floor", "");
        LaunchActivity.getLaunchActivity().setActionBarTitle(floor+" : "+room);
        Button btn_check_all = view.findViewById(R.id.btn_check_all);
        btn_check_all.setOnClickListener(v -> {
            if (null != inventoryAdapter)
                inventoryAdapter.selectUnselectAllData(true);

        });
        Button btn_uncheck_all = view.findViewById(R.id.btn_uncheck_all);
        btn_uncheck_all.setOnClickListener(v -> {
            if (null != inventoryAdapter)
                inventoryAdapter.selectUnselectAllData(false);
        });
        if (new NetworkUtil(getActivity()).isOnline()) {
            setProgressMessage("fetching info...");
            showProgress();
            CheckAssetApiCalls checkAssetApiCalls = new CheckAssetApiCalls();
            checkAssetApiCalls.setCheckAssetPresenter(this);
            CheckAsset checkAsset = new CheckAsset();
            checkAsset.setAssetName(jsonCheckAsset.getAssetName());
            checkAsset.setBizNameId(bizNameId);
            checkAsset.setRoomNumber(room);
            checkAsset.setFloor(floor);
            checkAssetApiCalls.assetsInRoom(UserUtils.getDeviceId(), UserUtils.getEmail(),
                    UserUtils.getAuth(), checkAsset);
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle(floor+" : "+room);
        LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.VISIBLE);
        LaunchActivity.getLaunchActivity().enableDisableBack(false);
    }

    @Override
    public void jsonCheckAssetListResponse(JsonCheckAssetList jsonCheckAssetList) {
        dismissProgress();
        Log.e("data", jsonCheckAssetList.toString());
        if (null != jsonCheckAssetList.getJsonCheckAssets()) {
            List<JsonCheckAsset> jsonCheckAssets = jsonCheckAssetList.getJsonCheckAssets();
            List<JsonCheckAsset> temp = InventoryHomeFragment.prefList.get("Floor No- " + floor + "  >>  " + "Room No- " + room);
            if (temp != null && temp.size() > 0 && jsonCheckAssets.size() > 0) {
                for (JsonCheckAsset d : temp) {
                    for (int i = 0; i < jsonCheckAssets.size(); i++) {
                        if (jsonCheckAssets.get(i).getAssetName().equals(d.getAssetName())) {
                            jsonCheckAssets.get(i).setInventoryStateEnum(d.getInventoryStateEnum());
                            break;
                        }
                    }
                }
            } else {
                // No such key do nothing
            }
            ll_bottom.setVisibility(jsonCheckAssets.size() > 0 ? View.VISIBLE : View.INVISIBLE);
            inventoryAdapter = new InventoryAdapter(jsonCheckAssets, getActivity(), this);
            rv_inventory_items.setAdapter(inventoryAdapter);
        }
    }

    @Override
    public void onInventoryItemClick(JsonCheckAsset item) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != inventoryAdapter)
            InventoryHomeFragment.tempList.put("Floor No- " + floor + "  >>  " + "Room No- " + room, inventoryAdapter.getDataSet());
    }
}
