package com.noqapp.android.merchant.views.fragments;


import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.utils.UserUtils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MerchantDetailFragment extends BaseMerchantDetailFragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater,container,savedInstanceState);
    }

    @Override
    public void getAllPeopleInQ(JsonTopic jsonTopic) {
        manageQueueModel.setQueuePersonListPresenter(this);
         manageQueueModel.getAllQueuePersonList(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonTopic.getCodeQR());
    }
}
