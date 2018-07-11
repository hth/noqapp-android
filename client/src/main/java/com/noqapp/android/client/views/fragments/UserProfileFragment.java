package com.noqapp.android.client.views.fragments;

/**
 * Created by chandra on 10/4/18.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonStore;
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
                TextView tv_name = (TextView) inflatedLayout.findViewById(R.id.tv_name);
                TextView tv_address = (TextView) inflatedLayout.findViewById(R.id.tv_address);
                TextView tv_opening_date = (TextView) inflatedLayout.findViewById(R.id.tv_opening_date);
                tv_name.setText(stores.get(i).getJsonQueue().getBusinessName());
                tv_address.setText(stores.get(i).getJsonQueue().getStoreAddress());
                String timing = "";
                for (int j = 0; j < 7; j++) {
                    JsonHour jsonHour = stores.get(i).getJsonHours().get(j);
                    if (Formatter.convertMilitaryTo12HourFormat(jsonHour.getStartHour()).equals("12:01 AM") &&
                            Formatter.convertMilitaryTo12HourFormat(jsonHour.getEndHour()).equals("11:59 PM")) {
                        timing += days[j] + " - "
                                + getString(R.string.whole_day) + "\n";
                    } else {
                        timing += days[j] + " - " + Formatter.convertMilitaryTo12HourFormat(jsonHour.getStartHour()) + " - "
                                + Formatter.convertMilitaryTo12HourFormat(jsonHour.getEndHour()) + "\n";
                    }
                }
                tv_opening_date.setText(timing);
                ll_multiple_store.addView(inflatedLayout);
            }
        }
    }
}
