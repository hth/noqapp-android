package com.noqapp.android.client.views.activities;

import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonCategory;
import com.noqapp.android.client.views.adapters.CategoryHeaderAdapter;
import com.noqapp.android.client.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.client.views.fragments.CategoryListFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chandra on 5/7/17.
 */
public class CategoryPagerActivity extends BaseActivity implements CategoryHeaderAdapter.OnItemClickListener {


    private ViewPager viewPager;
    private List<JsonCategory> categoryMap;
    private Map<String, ArrayList<BizStoreElastic>> queueMap;
    private CategoryHeaderAdapter categoryHeaderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_pager);
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
        viewPager = findViewById(R.id.pager);
        rcv_header.setHasFixedSize(true);
        rcv_header.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcv_header.setItemAnimator(new DefaultItemAnimator());

        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < categoryMap.size(); i++) {
            adapter.addFragment(new CategoryListFragment(queueMap.get(categoryMap.get(i).getBizCategoryId())), "FRAG" + i);
        }
        viewPager.setAdapter(adapter);
        categoryHeaderAdapter = new CategoryHeaderAdapter(this, categoryMap,
                this);
        rcv_header.setAdapter(categoryHeaderAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                rcv_header.smoothScrollToPosition(position);
                categoryHeaderAdapter.setSelected_pos(position);
                categoryHeaderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onCategoryItemClick(int pos, JsonCategory jsonCategory) {
        viewPager.setCurrentItem(pos);
    }

//    @Override
//    public void onCategoryItemClick(BizStoreElastic item, View view, int pos) {
//        switch (item.getBusinessType()) {
//            case DO:
//            case HO:
//                // open hospital profile
//                Intent in = new Intent(this, JoinActivity.class);
//                in.putExtra(NoQueueBaseFragment.KEY_CODE_QR, item.getCodeQR());
//                in.putExtra(NoQueueBaseFragment.KEY_FROM_LIST, false);
//                in.putExtra(NoQueueBaseFragment.KEY_IS_HISTORY, false);
//                in.putExtra("isCategoryData", false);
//                startActivity(in);
//                break;
//            default:
//                // open order screen
//                in = new Intent(this, StoreDetailActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("BizStoreElastic", item);
//                in.putExtras(bundle);
//                startActivity(in);
//        }
//    }

}
