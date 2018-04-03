package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.views.adapters.StoreInfoViewAllAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewAllListActivity extends AppCompatActivity implements StoreInfoViewAllAdapter.OnItemClickListener {


    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.fl_notification)
    protected FrameLayout fl_notification;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
    private ArrayList<BizStoreElastic> listData;
    private StoreInfoViewAllAdapter storeInfoViewAllAdapter;
    @BindView(R.id.rv_merchant_around_you)
    protected RecyclerView rv_merchant_around_you;
    private StoreInfoViewAllAdapter.OnItemClickListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        ButterKnife.bind(this);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setVisibility(View.VISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText("View All");
        listener = this;
        //getString(R.string.medical_history));

        listData = (ArrayList<BizStoreElastic>) getIntent().getExtras().getSerializable("list");
        storeInfoViewAllAdapter = new StoreInfoViewAllAdapter(listData, this, listener);
        rv_merchant_around_you.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_merchant_around_you.setLayoutManager(horizontalLayoutManagaer);
        rv_merchant_around_you.setItemAnimator(new DefaultItemAnimator());
       // rv_merchant_around_you.addItemDecoration(new VerticalSpaceItemDecoration(2));
        rv_merchant_around_you.setAdapter(storeInfoViewAllAdapter);
    }


    @Override
    public void onStoreItemClick(BizStoreElastic item, View view, int pos) {
        Intent in = new Intent(this, StoreDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("BizStoreElastic", item);
        in.putExtras(bundle);
        startActivity(in);
    }
}
