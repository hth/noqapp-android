package com.noqapp.android.client.views.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.views.adapters.CategoryListAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryListFragment extends NoQueueBaseFragment {

    public static CategoryListFragment newInstance(ArrayList<JsonQueue> jsonQueues, String categoryName, String color) {
        CategoryListFragment fragmentFirst = new CategoryListFragment();
        Bundle args = new Bundle();
        args.putString("color", color);
        args.putString("categoryName", categoryName);
        args.putSerializable("jsonQueues", jsonQueues);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public CategoryListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);

        LinearLayout ll_top = (LinearLayout) view.findViewById(R.id.ll_top);
        CardView card_view = (CardView) view.findViewById(R.id.card_view);
        ListView iv_list = (ListView) view.findViewById(R.id.iv_list);
        String categoryName = getArguments().getString("categoryName");
        String color = getArguments().getString("color");
        TextView tv = (TextView) view.findViewById(R.id.tv_category);
        tv.setBackgroundColor(Color.parseColor(color));
        tv.setText(categoryName);
        final List<JsonQueue> jsonQueues = (ArrayList<JsonQueue>) getArguments().getSerializable("jsonQueues");
        CategoryListAdapter adapter = new CategoryListAdapter(getActivity(), jsonQueues);
        card_view.setCardBackgroundColor(Color.parseColor(color));
        iv_list.setAdapter(adapter);
        return view;
    }

}
