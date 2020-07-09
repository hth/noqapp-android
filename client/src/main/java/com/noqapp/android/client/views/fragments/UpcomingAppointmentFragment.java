package com.noqapp.android.client.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.AppointmentApiCalls;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.AppointmentDetailActivity;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.adapters.MyAppointmentAdapter;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.common.presenter.AppointmentPresenter;
import com.noqapp.android.common.utils.CommonHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UpcomingAppointmentFragment extends BaseFragment implements AppointmentPresenter,
        MyAppointmentAdapter.OnItemClickListener {
    private RecyclerView rcv_appointments;
    private RelativeLayout rl_empty;
    private List<JsonSchedule> jsonSchedules = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_future_appointment, container, false);
        rcv_appointments = view.findViewById(R.id.rcv_appointments);
        rl_empty = view.findViewById(R.id.rl_empty);
        rcv_appointments.setHasFixedSize(true);
        rcv_appointments.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false));
        rcv_appointments.setItemAnimator(new DefaultItemAnimator());
        if (jsonSchedules.size() <= 0) {
            rcv_appointments.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            rcv_appointments.setVisibility(View.VISIBLE);
            rl_empty.setVisibility(View.GONE);
        }
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            setProgressMessage("Fetching appointments...");
            showProgress();
            AppointmentApiCalls appointmentApiCalls = new AppointmentApiCalls();
            appointmentApiCalls.setAppointmentPresenter(this);
            appointmentApiCalls.allAppointments(
                    UserUtils.getDeviceId(),
                    UserUtils.getEmail(),
                    UserUtils.getAuth());
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
        return view;
    }

    @Override
    public void appointmentResponse(JsonScheduleList jsonScheduleList) {
        Log.e("all appointments", jsonScheduleList.toString());
        jsonSchedules = jsonScheduleList.getJsonSchedules();
        if (jsonSchedules.size() <= 0) {
            rcv_appointments.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            rcv_appointments.setVisibility(View.VISIBLE);
            rl_empty.setVisibility(View.GONE);
        }

        Collections.sort(jsonSchedules, new Comparator<JsonSchedule>() {
            public int compare(JsonSchedule o1, JsonSchedule o2) {
                try {
                    return CommonHelper.SDF_YYYY_MM_DD.parse(o1.getScheduleDate()).
                            compareTo(CommonHelper.SDF_YYYY_MM_DD.parse(o2.getScheduleDate()));
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        MyAppointmentAdapter appointmentListAdapter = new MyAppointmentAdapter(
                jsonSchedules, getActivity(), this);
        rcv_appointments.setAdapter(appointmentListAdapter);
        dismissProgress();
    }

    @Override
    public void appointmentBookingResponse(JsonSchedule jsonSchedule) {
        dismissProgress();
    }

    @Override
    public void appointmentAcceptRejectResponse(JsonSchedule jsonSchedule) {
        dismissProgress();
    }

    @Override
    public void appointmentCancelResponse(JsonResponse jsonResponse) {
        dismissProgress();
    }

    @Override
    public void appointmentDetails(JsonSchedule jsonSchedule) {
        Intent intent = new Intent(getActivity(), AppointmentDetailActivity.class);
        intent.putExtra(IBConstant.KEY_DATA_OBJECT, jsonSchedule);
        intent.putExtra(IBConstant.KEY_FROM_LIST, true);
        startActivity(intent);
    }
}
