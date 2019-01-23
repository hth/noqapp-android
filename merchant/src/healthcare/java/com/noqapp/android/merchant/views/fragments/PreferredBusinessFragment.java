package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.merchant.R;

import com.noqapp.android.merchant.interfaces.PreferredBusinessPresenter;
import com.noqapp.android.merchant.model.PreferredBusinessModel;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusiness;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusinessList;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.MerchantHealthListAdapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;


public class PreferredBusinessFragment extends Fragment implements PreferredBusinessPresenter {
    private ListView list_view_1, list_view_2,lv_xray,lv_pathology,lv_medicine,lv_special;
    private MerchantHealthListAdapter merchantListAdapter1, merchantListAdapter2,merchantListAdapterXray,merchantListAdapterPathology ,merchantListAdapterMedicine,merchantListAdapterSpecial ;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_preferred_business, container, false);
        list_view_1 = v.findViewById(R.id.list_view_1);
        list_view_2 = v.findViewById(R.id.list_view_2);
        lv_xray = v.findViewById(R.id.lv_xray);
        lv_pathology = v.findViewById(R.id.lv_pathology);
        lv_medicine = v.findViewById(R.id.lv_medicine);
        lv_special = v.findViewById(R.id.lv_special);

        if(null != LaunchActivity.merchantListFragment && null != LaunchActivity.merchantListFragment.getTopics() && LaunchActivity.merchantListFragment.getTopics().size()>0){
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                new PreferredBusinessModel(this)
                        .getAllPreferredStores(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), LaunchActivity.merchantListFragment.getTopics().get(0).getCodeQR());
            }
        }
        return v;

    }

    @Override
    public void preferredBusinessResponse(JsonPreferredBusinessList jsonPreferredBusinessList) {
        // this.jsonPreferredBusinessList = jsonPreferredBusinessList;
      //
        if(null != jsonPreferredBusinessList && jsonPreferredBusinessList.getPreferredBusinesses() != null && jsonPreferredBusinessList.getPreferredBusinesses().size()>0){
            jsonPreferredBusinessList.getPreferredBusinesses().add(0,new JsonPreferredBusiness().setDisplayName("Select"));
            merchantListAdapter1 = new MerchantHealthListAdapter(getActivity(), LaunchActivity.merchantListFragment.getTopics(),jsonPreferredBusinessList.getPreferredBusinesses());
            list_view_1.setAdapter(merchantListAdapter1);
            merchantListAdapter2 = new MerchantHealthListAdapter(getActivity(), LaunchActivity.merchantListFragment.getTopics(),jsonPreferredBusinessList.getPreferredBusinesses());
            list_view_2.setAdapter(merchantListAdapter2);
            merchantListAdapterXray = new MerchantHealthListAdapter(getActivity(), LaunchActivity.merchantListFragment.getTopics(),jsonPreferredBusinessList.getPreferredBusinesses());
            lv_xray.setAdapter(merchantListAdapterXray);
            merchantListAdapterPathology = new MerchantHealthListAdapter(getActivity(), LaunchActivity.merchantListFragment.getTopics(),jsonPreferredBusinessList.getPreferredBusinesses());
            lv_pathology.setAdapter(merchantListAdapterPathology);
            merchantListAdapterMedicine = new MerchantHealthListAdapter(getActivity(), LaunchActivity.merchantListFragment.getTopics(),jsonPreferredBusinessList.getPreferredBusinesses());
            lv_medicine.setAdapter(merchantListAdapterMedicine);
            merchantListAdapterSpecial = new MerchantHealthListAdapter(getActivity(), LaunchActivity.merchantListFragment.getTopics(),jsonPreferredBusinessList.getPreferredBusinesses());
            lv_special.setAdapter(merchantListAdapterSpecial);
            Log.e("Pref business list: ", jsonPreferredBusinessList.toString());
        }
    }

    @Override
    public void preferredBusinessError() {

    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        new ErrorResponseHandler().processError(getActivity(), eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
    }

    @Override
    public void authenticationFailure() {
        AppUtils.authenticationProcessing();
    }


}
