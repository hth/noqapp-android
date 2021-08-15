package com.noqapp.android.client.views.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.api.ClientCouponApiImpl;
import com.noqapp.android.client.presenter.beans.body.Location;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.AppInitialize;
import com.noqapp.android.client.views.adapters.AllCouponsAdapter;
import com.noqapp.android.client.views.pojos.LocationPref;
import com.noqapp.android.common.beans.JsonCoupon;
import com.noqapp.android.common.beans.JsonCouponList;
import com.noqapp.android.common.presenter.CouponPresenter;
import com.noqapp.android.common.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

public class AllCouponsFragment
    extends BaseFragment
    implements CouponPresenter, AllCouponsAdapter.OnItemClickListener {
    private RecyclerView rcv_appointments;
    private RelativeLayout rl_empty;
    private List<JsonCoupon> jsonCoupons = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_all_coupons, container, false);
        rcv_appointments = view.findViewById(R.id.rcv_appointments);
        rl_empty = view.findViewById(R.id.rl_empty);
        rcv_appointments.setHasFixedSize(true);
        rcv_appointments.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rcv_appointments.setItemAnimator(new DefaultItemAnimator());

        if (jsonCoupons.size() <= 0) {
            rcv_appointments.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            rcv_appointments.setVisibility(View.VISIBLE);
            rl_empty.setVisibility(View.GONE);
        }
        TextView tv_location_enable = view.findViewById(R.id.tv_location_enable);
        if (new NetworkUtil(getActivity()).isOnline()) {
            setProgressMessage("Fetching all coupons...");
            showProgress();
            ClientCouponApiImpl clientCouponApiImpl = new ClientCouponApiImpl();
            clientCouponApiImpl.setCouponPresenter(this);
            Location location = new Location();
            if (TextUtils.isEmpty(AppInitialize.cityName)) {
                LocationPref locationPref = AppInitialize.getLocationPreference();
                location.setLatitude(String.valueOf(locationPref.getLatitude()))
                    .setLongitude(String.valueOf(locationPref.getLongitude()))
                    .setCityName(locationPref.getLocationAsString());
                tv_location_enable.setVisibility(View.VISIBLE);
            } else {
                location.setLatitude(String.valueOf(AppInitialize.location.getLatitude()))
                    .setLongitude(String.valueOf(AppInitialize.location.getLongitude()))
                    .setCityName(AppInitialize.cityName);
                tv_location_enable.setVisibility(View.GONE);
            }
            String codeQR = getArguments().getString(IBConstant.KEY_CODE_QR, null);
            if (TextUtils.isEmpty(codeQR)) {
                clientCouponApiImpl.globalCoupon(
                    UserUtils.getDeviceId(),
                    UserUtils.getEmail(),
                    UserUtils.getAuth(),
                    location);
            } else {
                clientCouponApiImpl.filterCoupon(
                    UserUtils.getDeviceId(),
                    UserUtils.getEmail(),
                    UserUtils.getAuth(),
                    codeQR);
            }
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
        return view;
    }

    @Override
    public void discountItemClick(JsonCoupon jsonCoupon) {
        if (null != getActivity().getCallingActivity()) {
            Intent intent = new Intent();
            intent.putExtra(IBConstant.KEY_DATA_OBJECT, jsonCoupon);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        } else {
            // Do nothing right now
        }
    }

    @Override
    public void couponResponse(JsonCouponList jsonCouponList) {
        Log.e("all coupons", jsonCouponList.toString());
        jsonCoupons.clear();
        jsonCoupons.addAll(jsonCouponList.getCoupons());
        if (jsonCoupons.size() <= 0) {
            rcv_appointments.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            rcv_appointments.setVisibility(View.VISIBLE);
            rl_empty.setVisibility(View.GONE);
        }
        AllCouponsAdapter offersAdapter = new AllCouponsAdapter(jsonCoupons, this);
        rcv_appointments.setAdapter(offersAdapter);
        dismissProgress();
    }
}
