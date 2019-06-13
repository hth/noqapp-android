package com.noqapp.android.client.views.fragments;

import android.app.Activity;
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
import com.noqapp.android.client.model.CouponApiCalls;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.adapters.MyCouponsAdapter;
import com.noqapp.android.common.beans.JsonCoupon;
import com.noqapp.android.common.beans.JsonCouponList;
import com.noqapp.android.common.presenter.CouponPresenter;

import java.util.ArrayList;
import java.util.List;

public class MyCouponsFragment extends BaseFragment implements CouponPresenter,
        MyCouponsAdapter.OnItemClickListener {
    private RecyclerView rcv_appointments;
    private RelativeLayout rl_empty;
    private List<JsonCoupon> jsonCoupons = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_mycoupons, container, false);
        rcv_appointments = view.findViewById(R.id.rcv_appointments);
        rl_empty = view.findViewById(R.id.rl_empty);
        rcv_appointments.setHasFixedSize(true);
        rcv_appointments.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false));
        rcv_appointments.setItemAnimator(new DefaultItemAnimator());
        if (jsonCoupons.size() <= 0) {
            rcv_appointments.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            rcv_appointments.setVisibility(View.VISIBLE);
            rl_empty.setVisibility(View.GONE);
        }
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.setMessage("Fetching coupons...");
            progressDialog.show();
            CouponApiCalls couponApiCalls = new CouponApiCalls();
            couponApiCalls.setCouponPresenter(this);
            couponApiCalls.availableCoupon(UserUtils.getDeviceId(),
                    UserUtils.getEmail(), UserUtils.getAuth());
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
        return view;
    }



    @Override
    public void couponResponse(JsonCouponList jsonCouponList) {
        Log.e("all appointments", jsonCouponList.toString());
        jsonCoupons.clear();
        jsonCoupons.addAll(jsonCouponList.getCoupons());
        if (jsonCoupons.size() <= 0) {
            rcv_appointments.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            rcv_appointments.setVisibility(View.VISIBLE);
            rl_empty.setVisibility(View.GONE);
        }
        MyCouponsAdapter myCouponsAdapter = new MyCouponsAdapter(
                getActivity(), jsonCoupons, this);
        rcv_appointments.setAdapter(myCouponsAdapter);
        dismissProgress();
    }

    @Override
    public void discountItemClick(JsonCoupon jsonCoupon) {
        Intent intent = new Intent();
        intent.putExtra(IBConstant.KEY_DATA_OBJECT,jsonCoupon);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}

