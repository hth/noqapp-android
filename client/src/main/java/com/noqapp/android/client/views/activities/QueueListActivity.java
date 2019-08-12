package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonCategory;
import com.noqapp.android.client.utils.AppUtilities;
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
        expandableListView = findViewById(R.id.expandableListView);
        LevelUpQueueAdapter expandableListAdapter = new LevelUpQueueAdapter(this, categoryMap, queueMap, this);
        expandableListView.setAdapter(expandableListAdapter);

        rcv_header.setHasFixedSize(true);
        rcv_header.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcv_header.setItemAnimator(new DefaultItemAnimator());

        categoryHeaderAdapter = new CategoryHeaderAdapter(this, categoryMap, this);
        rcv_header.setAdapter(categoryHeaderAdapter);

    }

    @Override
    public void onCategoryItemClick(int pos, JsonCategory jsonCategory) {
        expandableListView.setSelectedGroup(pos);
        categoryHeaderAdapter.setSelectedPosition(pos);
        categoryHeaderAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCategoryItemClick(BizStoreElastic item) {
        switch (item.getBusinessType()) {
            case DO:
            case BK:
            case HS:
                // open hospital profile
                Intent in = new Intent(this, BeforeJoinActivity.class);
                in.putExtra(IBConstant.KEY_CODE_QR, item.getCodeQR());
                in.putExtra(IBConstant.KEY_FROM_LIST, false);
                in.putExtra(IBConstant.KEY_IS_CATEGORY, false);
                in.putExtra(IBConstant.KEY_IMAGE_URL, AppUtilities.getImageUrls(BuildConfig.PROFILE_BUCKET, item.getDisplayImage()));
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
