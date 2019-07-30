package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.BizNamePresenter;
import com.noqapp.android.merchant.interfaces.CheckAssetPresenter;
import com.noqapp.android.merchant.model.CheckAssetApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAsset;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAssetList;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CheckAsset;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.FloorAdapter;


public class InventoryHomeFragment extends BaseFragment implements BizNamePresenter, CheckAssetPresenter,
        FloorAdapter.OnItemClickListener {
    private RecyclerView rv_floors;
    private CheckAssetApiCalls checkAssetApiCalls;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        super.onCreateView(inflater, container, args);
        View view = inflater.inflate(R.layout.frag_inventory_home, container, false);
        rv_floors = view.findViewById(R.id.rv_floors);
        rv_floors.setHasFixedSize(true);
        rv_floors.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rv_floors.setItemAnimator(new DefaultItemAnimator());
        checkAssetApiCalls = new CheckAssetApiCalls();
        checkAssetApiCalls.setBizNamePresenter(this);
        checkAssetApiCalls.setCheckAssetPresenter(this);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            setProgressMessage("Fetching info...");
            showProgress();
            checkAssetApiCalls.bizName(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new CheckAsset());
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
//        ArrayList<String> floorList = new ArrayList<>();
//        floorList.add("Ground Floor");
//        floorList.add("1st Floor");
//        floorList.add("2nd Floor");
//        floorList.add("3rd Floor");
//        floorList.add("4th Floor");
//        floorList.add("5th Floor");
//        floorList.add("6th Floor");
//        FloorAdapter floorAdapter = new FloorAdapter(floorList, getActivity(), this);
      //  rv_floors.setAdapter(floorAdapter);
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
        b.putSerializable("data",item);
        roflf.setArguments(b);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, roflf, "roomsOnFloorList");
        fragmentTransaction.addToBackStack("roomsOnFloorList");
        fragmentTransaction.commit();
    }

    @Override
    public void bizNameResponse(CheckAsset checkAsset) {
        dismissProgress();
        if (LaunchActivity.getLaunchActivity().isOnline()) {
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
        if(null != jsonCheckAssetList.getJsonCheckAssets()) {
            FloorAdapter floorAdapter = new FloorAdapter(jsonCheckAssetList.getJsonCheckAssets(), getActivity(), this);
            rv_floors.setAdapter(floorAdapter);
        }
    }
}
