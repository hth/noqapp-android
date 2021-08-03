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
import com.noqapp.android.common.model.types.BusinessCustomerAttributeEnum;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.category.CanteenStoreDepartmentEnum;

import java.util.ArrayList;
import java.util.HashMap;
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
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_list);
        initActionsViews(true);
        try {
            List<JsonCategory> tempCategoryMap = (List<JsonCategory>) getIntent().getExtras().getSerializable("list");
            Map<String, ArrayList<BizStoreElastic>> tempQueueMap = (Map<String, ArrayList<BizStoreElastic>>) getIntent().getExtras().getSerializable("hashmap");
            if (getIntent().getExtras().getBoolean("isCanteenStore", false)) {
                categoryMap = new ArrayList<>();
                queueMap = new HashMap<>();
                Map<String, JsonCategory> keys = new HashMap();
                for (int i = 0; i < tempCategoryMap.size(); i++) {
                    BusinessCustomerAttributeEnum businessCustomerAttribute = CanteenStoreDepartmentEnum.
                        getBusinessCustomerAttribute(tempCategoryMap.get(i).getBizCategoryId());
                    if (!keys.containsKey(businessCustomerAttribute.getName())) {
                        keys.put(businessCustomerAttribute.getName(), null);
                        categoryMap.add(new JsonCategory().setCategoryName(businessCustomerAttribute.getDescription()).setBizCategoryId(businessCustomerAttribute.getName()));
                    }
                }
                for (Map.Entry<String, ArrayList<BizStoreElastic>> entry : tempQueueMap.entrySet()) {
                    ArrayList<BizStoreElastic> innerList = entry.getValue();
                    for (int i = 0; i < innerList.size(); i++) {
                        BusinessCustomerAttributeEnum businessCustomerAttribute = CanteenStoreDepartmentEnum.
                            getBusinessCustomerAttribute(innerList.get(i).getBizCategoryId());
                        if (queueMap.containsKey(businessCustomerAttribute.getName())) {
                            queueMap.get(businessCustomerAttribute.getName()).add(innerList.get(i));
                        } else {
                            queueMap.put(businessCustomerAttribute.getName(), new ArrayList<>());
                            queueMap.get(businessCustomerAttribute.getName()).add(innerList.get(i));
                        }
                    }

                }
            } else {
                categoryMap = tempCategoryMap;
                queueMap = tempQueueMap;
            }
            String title = getIntent().getExtras().getString("title", "Select Queue");
            tv_toolbar_title.setText(title);
        } catch (Exception e) {
            e.printStackTrace();

        }
        final RecyclerView rcv_header = findViewById(R.id.rcv_header);
        rcv_header.setHasFixedSize(true);
        rcv_header.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcv_header.setItemAnimator(new DefaultItemAnimator());
        int pos = getIntent().getIntExtra("position", 0);
        categoryHeaderAdapter = new CategoryHeaderAdapter(this, categoryMap, this, pos);
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
        for (int i = 0; i < expandableListAdapter.getGroupCount(); i++)
            expandableListView.expandGroup(i);

        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> true);
        if (pos > 0) {
            rcv_header.scrollToPosition(pos);
            onCategoryItemClick(pos, null);
        }
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
            case CD:
            case CDQ:
            case BK:
            case PW:
                // open hospital profile
                if (AppInitialize.isLockMode) {
                    in = new Intent(this, KioskActivity.class);
                } else {
                    in = new Intent(this, BeforeJoinActivity.class);
                }
                b.putString(IBConstant.KEY_CODE_QR, item.getCodeQR());
                b.putString(IBConstant.KEY_IMAGE_URL, AppUtils.getImageUrls(BuildConfig.PROFILE_BUCKET, item.getDisplayImage()));
                b.putBoolean(IBConstant.KEY_IS_DO, item.getBusinessType() == BusinessTypeEnum.DO);
                in.putExtras(b);
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
