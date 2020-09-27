package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.views.adapters.EventsAdapter;
import com.noqapp.android.common.beans.JsonAdvertisement;

import java.util.ArrayList;

public class AllEventsActivity extends BaseActivity implements EventsAdapter.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        initActionsViews(true);
        RecyclerView rv_feed = findViewById(R.id.rv_merchant_around_you);
        rv_feed.setHasFixedSize(true);
        rv_feed.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv_feed.setItemAnimator(new DefaultItemAnimator());
        tv_toolbar_title.setText(getString(R.string.screen_all_events));
        ArrayList<JsonAdvertisement> listData = (ArrayList<JsonAdvertisement>) getIntent().getExtras().getSerializable("list");
        if (null == listData) {
            listData = new ArrayList<>();
        }
        EventsAdapter eventsAdapter = new EventsAdapter(listData, this, true);
        rv_feed.setAdapter(eventsAdapter);
    }

    @Override
    public void onEventItemClick(JsonAdvertisement item ) {
        switch (item.getAdvertisementViewerType()) {
            case JBA: {
                Intent in = new Intent(this, ImageViewerActivity.class);
                in.putExtra(IBConstant.KEY_URL, AppUtils.getImageUrls(BuildConfig.ADVERTISEMENT_BUCKET, item.createAdvertisementImageURL()));
                startActivity(in);
                break;
            }
            default: {
                Intent in = new Intent(this, EventsDetailActivity.class);
                in.putExtra(IBConstant.KEY_DATA_OBJECT, item);
                startActivity(in);
            }
        }

    }
}
