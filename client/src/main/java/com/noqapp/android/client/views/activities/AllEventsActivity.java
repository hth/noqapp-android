package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonEvent;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.views.adapters.EventsAdapter;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AllEventsActivity extends AppCompatActivity implements EventsAdapter.OnItemClickListener {

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
        tv_toolbar_title.setText(getString(R.string.screen_all_events));
        ArrayList<JsonEvent> listData = (ArrayList<JsonEvent>) getIntent().getExtras().getSerializable("list");
        if (null == listData)
            listData = new ArrayList<>();
        EventsAdapter eventsAdapter = new EventsAdapter(listData,  this,true);
        rv_feed.setAdapter(eventsAdapter);
    }

    @Override
    public void onEventItemClick(JsonEvent item, View view, int pos) {
        Intent in = new Intent(this, EventsDetailActivity.class);
        in.putExtra(IBConstant.KEY_DATA_OBJECT, item);
        startActivity(in);
    }
}
