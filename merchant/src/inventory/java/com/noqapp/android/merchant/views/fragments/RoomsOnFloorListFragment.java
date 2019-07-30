package com.noqapp.android.merchant.views.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.CheckAssetPresenter;
import com.noqapp.android.merchant.model.CheckAssetApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAsset;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAssetList;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CheckAsset;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.RoomsOnFloorAdapter;

import java.util.ArrayList;


public class RoomsOnFloorListFragment extends BaseFragment implements
        RoomsOnFloorAdapter.OnItemClickListener, CheckAssetPresenter {
    private RecyclerView rv_floors;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_inventory_home, container, false);
        rv_floors = view.findViewById(R.id.rv_floors);
        rv_floors.setHasFixedSize(true);
        rv_floors.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rv_floors.setItemAnimator(new DefaultItemAnimator());
        JsonCheckAsset jsonCheckAsset = (JsonCheckAsset) getArguments().getSerializable("data");
        ArrayList<String> floorList = new ArrayList<>();
        floorList.add("Room No 1");
        floorList.add("Room No 2");
        floorList.add("Room No 3");
        floorList.add("Room No 4");
        floorList.add("Room No 5");
        floorList.add("Room No 6");
        floorList.add("Room No 7");
        floorList.add("Room No 8");


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
            checkAssetApiCalls.rooms(UserUtils.getDeviceId(), UserUtils.getEmail(),
                    UserUtils.getAuth(), checkAsset);
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
        return view;
    }

    @Override
    public void onRoomsOnFloorItemClick(JsonCheckAsset item) {
        InventoryInRoomFragment inventoryInRoomFragment = new InventoryInRoomFragment();
        Bundle b = new Bundle();
        b.putSerializable("data",item);
        inventoryInRoomFragment.setArguments(b);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, inventoryInRoomFragment, "inventoryInRoomFragment");
        fragmentTransaction.addToBackStack("inventoryInRoomFragment");
        fragmentTransaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("Rooms on floor");
        LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.VISIBLE);
        LaunchActivity.getLaunchActivity().enableDisableBack(false);
    }

    @Override
    public void jsonCheckAssetListResponse(JsonCheckAssetList jsonCheckAssetList) {
        dismissProgress();
        if(null != jsonCheckAssetList.getJsonCheckAssets()) {
            RoomsOnFloorAdapter floorAdapter = new RoomsOnFloorAdapter(jsonCheckAssetList.getJsonCheckAssets(), getActivity(), this);
            rv_floors.setAdapter(floorAdapter);
        }
    }
}