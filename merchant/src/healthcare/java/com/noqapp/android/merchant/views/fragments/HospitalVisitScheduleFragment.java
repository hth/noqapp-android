package com.noqapp.android.merchant.views.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitScheduleList;
import com.noqapp.android.common.model.types.BooleanReplacementEnum;
import com.noqapp.android.common.presenter.HospitalVisitSchedulePresenter;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalHistoryApiCalls;
import com.noqapp.android.merchant.presenter.beans.body.merchant.HospitalVisitFor;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.HospitalVisitScheduleAdapter;
import com.noqapp.android.merchant.views.adapters.HospitalVisitScheduleListAdapter;

import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;

public class HospitalVisitScheduleFragment extends BaseFragment
        implements HospitalVisitScheduleListAdapter.OnItemClickListener, HospitalVisitSchedulePresenter {

    private List<JsonHospitalVisitSchedule> listData;
    private RecyclerView rcv_hospital_visit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_hospital_visit, container, false);
        rcv_hospital_visit = view.findViewById(R.id.rcv_hospital_visit);
        RelativeLayout rl_empty = view.findViewById(R.id.rl_empty);
        rcv_hospital_visit.setHasFixedSize(true);
        rcv_hospital_visit.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.HORIZONTAL, false));
        rcv_hospital_visit.setItemAnimator(new DefaultItemAnimator());
        listData = (List<JsonHospitalVisitSchedule>) getArguments().getSerializable("data");
        HospitalVisitScheduleAdapter hospitalVisitScheduleAdapter = new HospitalVisitScheduleAdapter(getActivity(), listData,
                this);
        rcv_hospital_visit.setAdapter(hospitalVisitScheduleAdapter);

        if (null != listData && listData.size() == 0 && null != getActivity()) {
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            rl_empty.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onImmuneItemClick(JsonHospitalVisitSchedule jsonHospitalVisitSchedule, String key, String booleanReplacement) {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_hvs);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        SegmentedControl sc_hvs_status = dialog.findViewById(R.id.sc_hvs_status);
        List<String> tempList = BooleanReplacementEnum.asListOfDescription();
        sc_hvs_status.addSegments(tempList);
        sc_hvs_status.setSelectedSegment(tempList.indexOf(booleanReplacement));
        TextView tv_sub_header = dialog.findViewById(R.id.tv_sub_header);
        tv_sub_header.setText(key);
        Button btnPositive = dialog.findViewById(R.id.btnPositive);
        Button btnNegative = dialog.findViewById(R.id.btnNegative);
        btnPositive.setOnClickListener(v -> {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                showProgress();
                HospitalVisitFor hospitalVisitFor = new HospitalVisitFor();
                hospitalVisitFor.setHospitalVisitScheduleId(jsonHospitalVisitSchedule.getHospitalVisitScheduleId());
                hospitalVisitFor.setVisitingFor(key);
                hospitalVisitFor.setBooleanReplacement(BooleanReplacementEnum.getValue(
                        tempList.get(sc_hvs_status.getSelectedAbsolutePosition())));
                hospitalVisitFor.setQid(getArguments().getString("qUserId"));
                MedicalHistoryApiCalls medicalHistoryApiCalls = new MedicalHistoryApiCalls(this);
                medicalHistoryApiCalls.modifyVisitingFor(BaseLaunchActivity.getDeviceID(),
                        LaunchActivity.getLaunchActivity().getEmail(),
                        LaunchActivity.getLaunchActivity().getAuth(), hospitalVisitFor);
                dialog.dismiss();
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
        });
        btnNegative.setOnClickListener(v -> dialog.dismiss());
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }


    @Override
    public void hospitalVisitScheduleResponse(JsonHospitalVisitScheduleList jsonHospitalVisitScheduleList) {
        dismissProgress();
        // do nothing
    }

    @Override
    public void hospitalVisitScheduleResponse(JsonHospitalVisitSchedule jsonHospitalVisitSchedule) {
        dismissProgress();
        for (int i = 0; i < listData.size(); i++) {
            if (listData.get(i).getHeader().equals(jsonHospitalVisitSchedule.getHeader())) {
                listData.set(i, jsonHospitalVisitSchedule);
                break;
            }
        }
        rcv_hospital_visit.getAdapter().notifyDataSetChanged();
        Log.e("Hospital visit updated", jsonHospitalVisitSchedule.toString());
    }
}
