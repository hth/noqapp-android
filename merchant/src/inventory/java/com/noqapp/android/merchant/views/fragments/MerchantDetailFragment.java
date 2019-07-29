package com.noqapp.android.merchant.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.views.interfaces.AdapterCallback;

import java.util.ArrayList;

// Not required here Only added for build
public class MerchantDetailFragment extends BaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        super.onCreateView(inflater, container, args);
        View view = inflater.inflate(R.layout.frag_inventory_home, container, false);
        return view;
    }


    public static void setAdapterCallBack(AdapterCallback adapterCallback){}

    public void setPage(int position){}

    public void updateListData(ArrayList<JsonTopic> topics) {
    }
}
