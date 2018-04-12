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
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.types.BusinessTypeEnum;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.views.adapters.CategoryListAdapter;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryListActivity extends AppCompatActivity implements CategoryListAdapter.OnItemClickListener {


    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
    private ArrayList<BizStoreElastic> jsonQueues ;
    private CategoryListAdapter categoryListAdapter1;
    @BindView(R.id.rv_category_list)
    protected RecyclerView rv_category_list;
    private CategoryListAdapter.OnItemClickListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        ButterKnife.bind(this);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listener = this;
        //getString(R.string.medical_history));
        String categoryName = getIntent().getStringExtra("categoryName");

        tv_toolbar_title.setText(categoryName);
        try {
          jsonQueues = (ArrayList<BizStoreElastic>) getIntent().getExtras().getSerializable("list");
        }catch (Exception e){
            e.printStackTrace();
            jsonQueues = new ArrayList<>();
        }

        categoryListAdapter1 = new CategoryListAdapter(jsonQueues, this, listener);
        rv_category_list.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_category_list.setLayoutManager(horizontalLayoutManagaer);
        rv_category_list.setItemAnimator(new DefaultItemAnimator());
       // rv_merchant_around_you.addItemDecoration(new VerticalSpaceItemDecoration(2));
        rv_category_list.setAdapter(categoryListAdapter1);
    }


       @Override
    public void onCategoryItemClick(BizStoreElastic item, View view, int pos) {
        if (item.getBusinessType().equalsIgnoreCase(BusinessTypeEnum.DO.toString()) ||
                item.getBusinessType().equalsIgnoreCase(BusinessTypeEnum.HO.toString())) {
            // open hospital profile
            Intent in = new Intent(this, JoinActivity.class);
            in.putExtra(NoQueueBaseFragment.KEY_CODE_QR, item.getCodeQR());
            in.putExtra(NoQueueBaseFragment.KEY_FROM_LIST, false);
            in.putExtra(NoQueueBaseFragment.KEY_IS_HISTORY, false);
            in.putExtra("isCategoryData", false);
            startActivity(in);
        } else {
            // open order screen
            Intent in = new Intent(this, StoreDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("BizStoreElastic", item);
            in.putExtras(bundle);
            startActivity(in);
        }
    }

}
