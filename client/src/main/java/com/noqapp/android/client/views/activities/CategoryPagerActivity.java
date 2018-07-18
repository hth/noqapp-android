package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonCategory;
import com.noqapp.android.client.views.adapters.CategoryHeaderAdapter;
import com.noqapp.android.client.views.fragments.CategoryListFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chandra on 5/7/17.
 */
public class CategoryPagerActivity extends BaseActivity implements CategoryHeaderAdapter.OnItemClickListener {


    @BindView(R.id.rcv_header)
    protected RecyclerView rcv_header;
    private ViewPager viewPager;
    private List<JsonCategory> categoryMap;
    private Map<String, ArrayList<BizStoreElastic>> queueMap;
    private CategoryHeaderAdapter categoryHeaderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_pager);
        ButterKnife.bind(this);
        initActionsViews(true);
        try {
            categoryMap = (List<JsonCategory>) getIntent().getExtras().getSerializable("list");
            queueMap = (Map<String, ArrayList<BizStoreElastic>>) getIntent().getExtras().getSerializable("hashmap");
            String title = getIntent().getExtras().getString("title", "Select Queue");
            tv_toolbar_title.setText(title);
        } catch (Exception e) {
            e.printStackTrace();

        }
        viewPager = findViewById(R.id.pager);
        rcv_header.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcv_header.setLayoutManager(horizontalLayoutManagaer);
        rcv_header.setItemAnimator(new DefaultItemAnimator());

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
