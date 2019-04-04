package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonFeed;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.views.adapters.FeedAdapter;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AllFeedsActivity extends AppCompatActivity implements FeedAdapter.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        RecyclerView rv_feed = findViewById(R.id.rv_merchant_around_you);
        rv_feed.setHasFixedSize(true);
        rv_feed.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv_feed.setItemAnimator(new DefaultItemAnimator());
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_all_feeds));
        ArrayList<JsonFeed> listData = (ArrayList<JsonFeed>) getIntent().getExtras().getSerializable("list");
        if (null == listData)
            listData = new ArrayList<>();
        FeedAdapter feedAdapter = new FeedAdapter(listData, this, this,true);
        rv_feed.setAdapter(feedAdapter);
    }

    @Override
    public void onFeedItemClick(JsonFeed item, View view, int pos) {
        Intent in = new Intent(this, FeedActivity.class);
        in.putExtra(IBConstant.KEY_DATA_OBJECT, item);
        startActivity(in);
    }
}
