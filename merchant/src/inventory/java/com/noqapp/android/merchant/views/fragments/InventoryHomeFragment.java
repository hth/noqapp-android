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
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.FloorAdapter;

import java.util.ArrayList;

public class InventoryHomeFragment extends BaseFragment implements FloorAdapter.OnItemClickListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        super.onCreateView(inflater, container, args);
        View view = inflater.inflate(R.layout.frag_inventory_home, container, false);
        RecyclerView rv_floors = view.findViewById(R.id.rv_floors);
        rv_floors.setHasFixedSize(true);
        rv_floors.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rv_floors.setItemAnimator(new DefaultItemAnimator());
        ArrayList<String> floorList = new ArrayList<>();
        floorList.add("Ground Floor");
        floorList.add("1st Floor");
        floorList.add("2nd Floor");
        floorList.add("3rd Floor");
        floorList.add("4th Floor");
        floorList.add("5th Floor");
        floorList.add("6th Floor");
        FloorAdapter floorAdapter = new FloorAdapter(floorList, getActivity(), this);
        rv_floors.setAdapter(floorAdapter);
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
    public void onFloorItemClick(String item) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new RoomsOnFloorListFragment(), "roomsOnFloorList");
        fragmentTransaction.addToBackStack("roomsOnFloorList");
        fragmentTransaction.commit();
    }
}
