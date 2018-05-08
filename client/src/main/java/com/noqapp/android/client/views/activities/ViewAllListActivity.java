package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.views.adapters.StoreInfoViewAllAdapter;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chandra on 5/7/17.
 */
public class ViewAllListActivity extends AppCompatActivity implements StoreInfoViewAllAdapter.OnItemClickListener {


    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
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
        switch (item.getBusinessType()) {
            case DO:
            case BK:
                // open hospital/Bank profile
                Bundle b = new Bundle();
                b.putString(NoQueueBaseFragment.KEY_CODE_QR, item.getCodeQR());
                b.putBoolean(NoQueueBaseFragment.KEY_FROM_LIST, false);
                b.putBoolean(NoQueueBaseFragment.KEY_IS_HISTORY, false);
                b.putBoolean("CallCategory", true);
                b.putBoolean("isCategoryData", false);
                Intent in = new Intent(this, CategoryInfoActivity.class);
                in.putExtra("bundle", b);
                startActivity(in);
                break;
            default:
                // open order screen
                Intent intent = new Intent(this, StoreDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("BizStoreElastic", item);
                intent.putExtras(bundle);
                startActivity(intent);
        }
    }
}
