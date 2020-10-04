package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonFeed;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.views.adapters.FeedAdapter;

import java.util.ArrayList;

public class AllFeedsActivity extends BaseActivity implements FeedAdapter.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        initActionsViews(false);
        RecyclerView rv_feed = findViewById(R.id.rv_merchant_around_you);
        rv_feed.setHasFixedSize(true);
        rv_feed.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv_feed.setItemAnimator(new DefaultItemAnimator());
        tv_toolbar_title.setText(getString(R.string.screen_all_feeds));
        ArrayList<JsonFeed> listData = getIntent().getExtras().getParcelableArrayList("list");
        if (null == listData)
            listData = new ArrayList<>();
        FeedAdapter feedAdapter = new FeedAdapter(listData,this, true);
        rv_feed.setAdapter(feedAdapter);
    }

    @Override
    public void onFeedItemClick(JsonFeed item) {
        Intent in = new Intent(this, FeedActivity.class);
        in.putExtra(IBConstant.KEY_DATA_OBJECT, item);
        startActivity(in);
    }
}
