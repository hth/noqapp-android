package com.noqapp.android.client.views.fragments;

/**
 * Created by chandra on 10/4/18.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonStore;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.common.beans.JsonHour;
import com.noqapp.android.common.utils.Formatter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserProfileFragment extends Fragment {

    @BindView(R.id.ll_multiple_store)
    protected LinearLayout ll_multiple_store;

    private String[] days = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void updateUI(List<JsonStore> stores) {
        if (null != stores && stores.size() > 0) {
            for (int i = 0; i < stores.size(); i++) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View inflatedLayout = inflater.inflate(R.layout.store_items, null, false);
                TextView tv_name = inflatedLayout.findViewById(R.id.tv_name);
                TextView tv_address = inflatedLayout.findViewById(R.id.tv_address);
                TextView tv_opening_date = inflatedLayout.findViewById(R.id.tv_opening_date);
                tv_name.setText(stores.get(i).getJsonQueue().getBusinessName());
                tv_address.setText(stores.get(i).getJsonQueue().getStoreAddress());
                String timing = new AppUtilities().orderTheTimings(getActivity(),stores.get(i).getJsonHours());
                tv_opening_date.setText(Html.fromHtml(timing));
                ll_multiple_store.addView(inflatedLayout);
            }
        }
    }
}
