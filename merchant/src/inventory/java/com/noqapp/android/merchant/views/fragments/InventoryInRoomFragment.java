package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.CheckAssetPresenter;
import com.noqapp.android.merchant.model.CheckAssetApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAsset;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAssetList;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CheckAsset;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

public class InventoryInRoomFragment extends BaseFragment implements CheckAssetPresenter {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        super.onCreateView(inflater, container, args);
        View view = inflater.inflate(R.layout.frag_inventory_in_rooms, container, false);
        JsonCheckAsset jsonCheckAsset = (JsonCheckAsset) getArguments().getSerializable("data");
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            setProgressMessage("fetching info...");
            showProgress();
            CheckAssetApiCalls checkAssetApiCalls = new CheckAssetApiCalls();
            checkAssetApiCalls.setCheckAssetPresenter(this);
            CheckAsset checkAsset = new CheckAsset();
            checkAsset.setAssetName(jsonCheckAsset.getAssetName());
            checkAsset.setBizNameId(jsonCheckAsset.getId());
            checkAsset.setRoomNumber(jsonCheckAsset.getRoomNumber());
            checkAsset.setFloor(jsonCheckAsset.getFloor());
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
        LaunchActivity.getLaunchActivity().setActionBarTitle("Inventory in room");
        LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.VISIBLE);
        LaunchActivity.getLaunchActivity().enableDisableBack(false);
    }

    @Override
    public void jsonCheckAssetListResponse(JsonCheckAssetList jsonCheckAssetList) {
        dismissProgress();
        Log.e("data", jsonCheckAssetList.toString());
    }
}
