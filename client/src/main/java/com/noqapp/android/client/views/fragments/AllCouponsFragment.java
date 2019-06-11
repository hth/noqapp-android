package com.noqapp.android.client.views.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.AppointmentApiCalls;
import com.noqapp.android.client.presenter.beans.JsonDiscount;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.adapters.AllCouponsAdapter;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.common.presenter.AppointmentPresenter;

import java.util.ArrayList;
import java.util.List;

public class AllCouponsFragment extends Fragment implements AppointmentPresenter, AllCouponsAdapter.OnItemClickListener {
    private RecyclerView rcv_appointments;
    private RelativeLayout rl_empty;
    private List<JsonDiscount> jsonDiscountList = new ArrayList<>();
    protected ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_all_coupons, container, false);
        rcv_appointments = view.findViewById(R.id.rcv_appointments);
        rl_empty = view.findViewById(R.id.rl_empty);
        rcv_appointments.setHasFixedSize(true);
        rcv_appointments.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rcv_appointments.setItemAnimator(new DefaultItemAnimator());
        if (jsonDiscountList.size() <= 0) {
            rcv_appointments.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            rcv_appointments.setVisibility(View.VISIBLE);
            rl_empty.setVisibility(View.GONE);
        }
        initProgress();
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.setMessage("Fetching offers...");
            progressDialog.show();
            AppointmentApiCalls appointmentApiCalls = new AppointmentApiCalls();
            appointmentApiCalls.setAppointmentPresenter(this);
            appointmentApiCalls.allAppointments(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
        return view;
    }

    @Override
    public void appointmentResponse(JsonScheduleList jsonScheduleList) {
        Log.e("all appointments", jsonScheduleList.toString());
        jsonDiscountList.add(new JsonDiscount());
        jsonDiscountList.add(new JsonDiscount());
        jsonDiscountList.add(new JsonDiscount());
        jsonDiscountList.add(new JsonDiscount());
        jsonDiscountList.add(new JsonDiscount());
        jsonDiscountList.add(new JsonDiscount());
        if (jsonDiscountList.size() <= 0) {
            rcv_appointments.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            rcv_appointments.setVisibility(View.VISIBLE);
            rl_empty.setVisibility(View.GONE);
        }


        AllCouponsAdapter offersAdapter = new AllCouponsAdapter(
                getActivity(), jsonDiscountList, this);
        rcv_appointments.setAdapter(offersAdapter);
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
    public void authenticationFailure() {
        dismissProgress();
        AppUtilities.authenticationProcessing(getActivity());
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej)
            new ErrorResponseHandler().processError(getActivity(), eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading data...");
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onDiscountItemClick(int pos, JsonDiscount jsonDiscount) {

    }
}

