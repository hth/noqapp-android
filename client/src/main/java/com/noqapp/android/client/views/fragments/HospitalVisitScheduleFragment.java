package com.noqapp.android.client.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.adapters.HospitalVisitScheduleAdapter;
import com.noqapp.android.client.views.adapters.HospitalVisitScheduleListAdapter;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;

import java.util.List;

public class HospitalVisitScheduleFragment extends BaseFragment
    implements HospitalVisitScheduleListAdapter.OnItemClickListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_hospital_visit, container, false);
        RecyclerView rcv_order_history = view.findViewById(R.id.rcv_order_history);
        RelativeLayout rl_empty = view.findViewById(R.id.rl_empty);
        rcv_order_history.setHasFixedSize(true);
        rcv_order_history.setLayoutManager(new LinearLayoutManager(getActivity(),
            RecyclerView.HORIZONTAL, false));
        rcv_order_history.setItemAnimator(new DefaultItemAnimator());


        List<JsonHospitalVisitSchedule> listData = (List<JsonHospitalVisitSchedule>) getArguments().getSerializable("data");
        HospitalVisitScheduleAdapter hospitalVisitScheduleAdapter = new HospitalVisitScheduleAdapter(getActivity(), listData,
            this);
        rcv_order_history.setAdapter(hospitalVisitScheduleAdapter);

        if (null != listData && listData.size() == 0 && null != getActivity()) {
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            rl_empty.setVisibility(View.GONE);
        }
        return view;
    }


    @Override
    public void onImmuneItemClick() {
        // new CustomToast().showToast(getActivity(), "Do some action");
    }

}
