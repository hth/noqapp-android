package com.noqapp.android.client.views.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.views.adapters.CategoryListAdapter;
import com.noqapp.android.client.views.adapters.CategoryPagerAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CategoryListFragment extends Fragment {

    @BindView(R.id.iv_list)
    protected ListView iv_list;

    private List<JsonQueue> arrayList;
    private String categoryName;
    private String color;

    public CategoryListFragment(List<JsonQueue> arrayList,String categoryName,String color){
        this.arrayList =arrayList;
        this.categoryName = categoryName;
        this.color =color;
    }

    public CategoryListFragment(){}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        ButterKnife.bind(this, view);
        LinearLayout ll_top = (LinearLayout) view.findViewById(R.id.ll_top);
        CardView card_view = (CardView) view.findViewById(R.id.card_view);
        CategoryListAdapter adapter = new CategoryListAdapter(getActivity(), arrayList);
        if(iv_list.getHeaderViewsCount() ==0) {
            View footer = (View) inflater.inflate(R.layout.category_lv_header, iv_list, false);
            TextView tv = (TextView)footer.findViewById(R.id.tv_category);
            tv.setBackgroundColor(Color.parseColor(color));
            tv.setText(categoryName);
            iv_list.addHeaderView(footer);
        }
        card_view.setCardBackgroundColor(Color.parseColor(color));
        iv_list.setAdapter(adapter);
        return view;
    }

}
