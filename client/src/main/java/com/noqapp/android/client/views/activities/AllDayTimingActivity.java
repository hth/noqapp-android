package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.views.adapters.AllDayTimingAdapter;

import java.util.List;

public class AllDayTimingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_day_timing);
        ListView listview = findViewById(R.id.listview);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.weekly_hours));
        Intent bundle = getIntent();
        if (null != bundle && null != bundle.getExtras()) {
            List<StoreHourElastic> storeHourElastics = (List<StoreHourElastic>) bundle.getExtras().getSerializable(IBConstant.KEY_STORE_TIMING);
            AllDayTimingAdapter adapter = new AllDayTimingAdapter(this, storeHourElastics);
            listview.setAdapter(adapter);
        }
    }
}
