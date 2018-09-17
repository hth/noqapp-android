package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.views.adapters.CategoryListAdapter;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by chandra on 5/7/17.
 */
public class CategoryListActivity extends BaseActivity implements CategoryListAdapter.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        initActionsViews(true);
        CategoryListAdapter.OnItemClickListener listener = this;
        String categoryName = getIntent().getStringExtra("categoryName");
        RecyclerView rv_category_list = findViewById(R.id.rv_category_list);
        tv_toolbar_title.setText(categoryName);
        ArrayList<BizStoreElastic> jsonQueues = null;
        try {
            jsonQueues = (ArrayList<BizStoreElastic>) getIntent().getExtras().getSerializable("list");
        } catch (Exception e) {
            e.printStackTrace();
            jsonQueues = new ArrayList<>();
        }

        CategoryListAdapter categoryListAdapter = new CategoryListAdapter(jsonQueues, this, listener);
        rv_category_list.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_category_list.setLayoutManager(horizontalLayoutManagaer);
        rv_category_list.setItemAnimator(new DefaultItemAnimator());
        rv_category_list.setAdapter(categoryListAdapter);
    }

    @Override
    public void onCategoryItemClick(BizStoreElastic item, View view, int pos) {
        switch (item.getBusinessType()) {
            case DO:
                // open hospital profile
                Intent in = new Intent(this, JoinActivity.class);
                in.putExtra(NoQueueBaseFragment.KEY_CODE_QR, item.getCodeQR());
                in.putExtra(NoQueueBaseFragment.KEY_FROM_LIST, false);
                in.putExtra(NoQueueBaseFragment.KEY_IS_HISTORY, false);
                in.putExtra("imageUrl",AppUtilities.getImageUrls(BuildConfig.PROFILE_BUCKET, item.getDisplayImage()));
                in.putExtra("isCategoryData", false);
                startActivity(in);
                break;
            default:
                // open order screen
                in = new Intent(this, StoreDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("BizStoreElastic", item);
                in.putExtras(bundle);
                startActivity(in);
        }
    }
}
