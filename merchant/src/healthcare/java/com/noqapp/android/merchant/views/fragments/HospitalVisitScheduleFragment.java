package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitScheduleList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.BooleanReplacementEnum;
import com.noqapp.android.common.presenter.HospitalVisitSchedulePresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalHistoryApiCalls;
import com.noqapp.android.merchant.presenter.beans.body.merchant.HospitalVisitFor;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.activities.AppInitialize;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.HospitalVisitScheduleAdapter;
import com.noqapp.android.merchant.views.adapters.HospitalVisitScheduleListAdapter;

import java.util.Date;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;

public class HospitalVisitScheduleFragment
    extends BaseFragment
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
        rcv_hospital_visit.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        rcv_hospital_visit.setItemAnimator(new DefaultItemAnimator());
        listData = (List<JsonHospitalVisitSchedule>) getArguments().getSerializable("data");
        HospitalVisitScheduleAdapter hospitalVisitScheduleAdapter = new HospitalVisitScheduleAdapter(getActivity(), listData, this);
        rcv_hospital_visit.setAdapter(hospitalVisitScheduleAdapter);

        if (null != listData && listData.size() == 0 && null != getActivity()) {
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            rl_empty.setVisibility(View.GONE);
        }
        return view;
    }

    private boolean isExpectedDateInFuture(String dateString) {
        try {
            Date date = CommonHelper.SDF_ISO8601_FMT.parse(dateString);
            int date_diff = new Date().compareTo(date);
            if (date_diff < 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onImmuneItemClick(JsonHospitalVisitSchedule jsonHospitalVisitSchedule, String key, String booleanReplacement) {
        if (isExpectedDateInFuture(jsonHospitalVisitSchedule.getExpectedDate())) {
            new CustomToast().showToast(getActivity(), key + " due date is " + CommonHelper.formatStringDate(CommonHelper.SDF_DOB_FROM_UI, jsonHospitalVisitSchedule.getExpectedDate()));
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            builder.setTitle(null);
            View view = inflater.inflate(R.layout.dialog_update_hvs, null, false);
            SegmentedControl sc_hvs_status = view.findViewById(R.id.sc_hvs_status);
            List<String> tempList = BooleanReplacementEnum.asListOfDescription();
            sc_hvs_status.addSegments(tempList);
            sc_hvs_status.setSelectedSegment(tempList.indexOf(booleanReplacement));
            TextView tv_sub_header = view.findViewById(R.id.tv_sub_header);
            tv_sub_header.setText(key);
            ImageView actionbarBack = view.findViewById(R.id.actionbarBack);
            Button btn_update = view.findViewById(R.id.btn_update);
            builder.setView(view);
            final AlertDialog mAlertDialog = builder.create();
            mAlertDialog.setCanceledOnTouchOutside(false);
            btn_update.setOnClickListener(v -> {
                if (new NetworkUtil(getActivity()).isOnline()) {
                    showProgress();
                    HospitalVisitFor hospitalVisitFor = new HospitalVisitFor();
                    hospitalVisitFor.setHospitalVisitScheduleId(jsonHospitalVisitSchedule.getHospitalVisitScheduleId());
                    hospitalVisitFor.setVisitingFor(key);
                    hospitalVisitFor.setBooleanReplacement(BooleanReplacementEnum.getValue(tempList.get(sc_hvs_status.getLastSelectedAbsolutePosition())));
                    hospitalVisitFor.setQid(getArguments().getString("qUserId"));
                    MedicalHistoryApiCalls medicalHistoryApiCalls = new MedicalHistoryApiCalls(this);
                    medicalHistoryApiCalls.modifyVisitingFor(
                        AppInitialize.getDeviceID(),
                        AppInitialize.getEmail(),
                        AppInitialize.getAuth(),
                        hospitalVisitFor);
                    mAlertDialog.dismiss();
                } else {
                    ShowAlertInformation.showNetworkDialog(getActivity());
                }
            });
            actionbarBack.setOnClickListener(v -> mAlertDialog.dismiss());
            mAlertDialog.show();
        }
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
