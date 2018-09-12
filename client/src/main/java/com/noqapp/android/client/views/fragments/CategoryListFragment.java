package com.noqapp.android.client.views.fragments;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.views.activities.JoinActivity;
import com.noqapp.android.client.views.activities.StoreDetailActivity;
import com.noqapp.android.client.views.adapters.CategoryListAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by chandra on 5/7/17.
 */
public class CategoryListFragment extends Fragment implements CategoryListAdapter.OnItemClickListener {
    private ArrayList<BizStoreElastic> jsonQueues;

    public CategoryListFragment() {
    }

    public CategoryListFragment(ArrayList<BizStoreElastic> jsonQueues) {
        this.jsonQueues = jsonQueues;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        CategoryListAdapter.OnItemClickListener listener = this;
        CategoryListAdapter categoryListAdapter = new CategoryListAdapter(jsonQueues, getActivity(), listener);
        RecyclerView rv_category_list = view.findViewById(R.id.rv_category_list);
        rv_category_list.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_category_list.setLayoutManager(horizontalLayoutManagaer);
        rv_category_list.setItemAnimator(new DefaultItemAnimator());
        rv_category_list.setAdapter(categoryListAdapter);
        return view;
    }

    @Override
    public void onCategoryItemClick(BizStoreElastic item, View view, int pos) {
        switch (item.getBusinessType()) {
            case DO:
            case BK:
                // open hospital profile
                Intent in = new Intent(getActivity(), JoinActivity.class);
                in.putExtra(NoQueueBaseFragment.KEY_CODE_QR, item.getCodeQR());
                in.putExtra(NoQueueBaseFragment.KEY_FROM_LIST, false);
                in.putExtra(NoQueueBaseFragment.KEY_IS_HISTORY, false);
                in.putExtra("isCategoryData", false);
                startActivity(in);
                break;
            default:
                // open order screen
                in = new Intent(getActivity(), StoreDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("BizStoreElastic", item);
                in.putExtras(bundle);
                startActivity(in);
        }
    }
}
