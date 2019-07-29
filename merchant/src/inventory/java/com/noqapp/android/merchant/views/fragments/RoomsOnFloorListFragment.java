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
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.RoomsOnFloorAdapter;

import java.util.ArrayList;


public class RoomsOnFloorListFragment extends BaseFragment implements
        RoomsOnFloorAdapter.OnItemClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_inventory_home, container, false);
        RecyclerView rv_floors = view.findViewById(R.id.rv_floors);
        rv_floors.setHasFixedSize(true);
        rv_floors.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rv_floors.setItemAnimator(new DefaultItemAnimator());
        ArrayList<String> floorList = new ArrayList<>();
        floorList.add("Room No 1");
        floorList.add("Room No 2");
        floorList.add("Room No 3");
        floorList.add("Room No 4");
        floorList.add("Room No 5");
        floorList.add("Room No 6");
        floorList.add("Room No 7");
        floorList.add("Room No 8");
        RoomsOnFloorAdapter roomsOnFloorAdapter = new RoomsOnFloorAdapter(floorList, getActivity(), this);
        rv_floors.setAdapter(roomsOnFloorAdapter);
        return view;
    }

    @Override
    public void onRoomsOnFloorItemClick(String item) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new InventoryInRoomFragment(), "inventoryInRoomFragment");
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
}