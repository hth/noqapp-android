package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonCategory;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.views.adapters.CategoryHeaderAdapter;
import com.noqapp.android.client.views.adapters.LevelUpQueueAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class QueueListActivity extends BaseActivity implements
        CategoryHeaderAdapter.OnItemClickListener, LevelUpQueueAdapter.OnItemClickListener {

    private List<JsonCategory> categoryMap;
    private Map<String, ArrayList<BizStoreElastic>> queueMap;
    private CategoryHeaderAdapter categoryHeaderAdapter;
    private ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_list);
        initActionsViews(true);
        try {
            categoryMap = (List<JsonCategory>) getIntent().getExtras().getSerializable("list");
            queueMap = (Map<String, ArrayList<BizStoreElastic>>) getIntent().getExtras().getSerializable("hashmap");
            String title = getIntent().getExtras().getString("title", "Select Queue");
            tv_toolbar_title.setText(title);
        } catch (Exception e) {
            e.printStackTrace();

        }
        final RecyclerView rcv_header = findViewById(R.id.rcv_header);
        rcv_header.setHasFixedSize(true);
        rcv_header.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcv_header.setItemAnimator(new DefaultItemAnimator());
        categoryHeaderAdapter = new CategoryHeaderAdapter(this, categoryMap, this);
        rcv_header.setAdapter(categoryHeaderAdapter);

        expandableListView = findViewById(R.id.expandableListView);
        LevelUpQueueAdapter expandableListAdapter = new LevelUpQueueAdapter(this, categoryMap, queueMap, this);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int position = new AppUtils().getFirstVisibleGroup(expandableListView);
                rcv_header.smoothScrollToPosition(position);
                categoryHeaderAdapter.setSelectedPosition(position);
                categoryHeaderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    public void onCategoryItemClick(int pos, JsonCategory jsonCategory) {
        expandableListView.setSelectedGroup(pos);
        categoryHeaderAdapter.setSelectedPosition(pos);
        categoryHeaderAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCategoryItemClick(BizStoreElastic item) {
        Intent in;
        Bundle b = new Bundle();
        switch (item.getBusinessType()) {
            case DO:
            case BK:
                // open hospital profile
                in = new Intent(this, BeforeJoinActivity.class);
                in.putExtra(IBConstant.KEY_CODE_QR, item.getCodeQR());
                in.putExtra(IBConstant.KEY_FROM_LIST, false);
                in.putExtra(IBConstant.KEY_IS_CATEGORY, false);
                in.putExtra(IBConstant.KEY_IMAGE_URL, AppUtils.getImageUrls(BuildConfig.PROFILE_BUCKET, item.getDisplayImage()));
                startActivity(in);
                break;
            case HS:
            case PH: {
                // open order screen
                in = new Intent(this, StoreDetailActivity.class);
                b.putSerializable("BizStoreElastic", item);
                in.putExtras(b);
                startActivity(in);
            }
            break;
            default: {
                // open order screen
                in = new Intent(this, StoreWithMenuActivity.class);
                b.putSerializable("BizStoreElastic", item);
                in.putExtras(b);
                startActivity(in);
            }
        }
    }
}
