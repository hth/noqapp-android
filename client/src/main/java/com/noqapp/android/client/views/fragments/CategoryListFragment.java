package com.noqapp.android.client.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.views.activities.BeforeJoinActivity;
import com.noqapp.android.client.views.activities.StoreDetailActivity;
import com.noqapp.android.client.views.adapters.CategoryListAdapter;

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
        ArrayList<BizStoreElastic> tempList;
        if(null == jsonQueues)
            tempList = new ArrayList<>();
        else
            tempList = jsonQueues;
        CategoryListAdapter categoryListAdapter = new CategoryListAdapter(tempList, getActivity(), listener);
        RecyclerView rv_category_list = view.findViewById(R.id.rv_category_list);
        rv_category_list.setHasFixedSize(true);
        rv_category_list.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rv_category_list.setItemAnimator(new DefaultItemAnimator());
        rv_category_list.setAdapter(categoryListAdapter);
        return view;
    }

    @Override
    public void onCategoryItemClick(BizStoreElastic item, View view, int pos) {
        switch (item.getBusinessType()) {
            case DO:
            case BK:
            case HS:
                // open hospital profile
                Intent in = new Intent(getActivity(), BeforeJoinActivity.class);
                in.putExtra(IBConstant.KEY_CODE_QR, item.getCodeQR());
                in.putExtra(IBConstant.KEY_FROM_LIST, false);
                in.putExtra(IBConstant.KEY_IS_CATEGORY, false);
                in.putExtra(IBConstant.KEY_IMAGE_URL, AppUtilities.getImageUrls(BuildConfig.PROFILE_BUCKET, item.getDisplayImage()));
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
