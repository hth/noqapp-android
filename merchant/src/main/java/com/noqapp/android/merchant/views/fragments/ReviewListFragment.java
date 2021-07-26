package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.ReviewListActivity;
import com.noqapp.android.merchant.views.adapters.MerchantReviewListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReviewListFragment extends Fragment {
    public static int selected_pos = 0;
    public ReviewFragment reviewFragment;
    private MerchantReviewListAdapter adapter;
    private Runnable run;
    private ArrayList<JsonTopic> topics = new ArrayList<>();
    private ListView listview;

    public ReviewListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_merchant_chart_list, container, false);
        listview = view.findViewById(R.id.listview);
        Bundle bundle = getArguments();
        run = () -> {
            adapter.notifyDataSetChanged();
            listview.invalidateViews();
            listview.refreshDrawableState();
        };

        if (null != bundle) {
            ArrayList<JsonTopic> jsonTopics = (ArrayList<JsonTopic>) bundle.getSerializable("jsonTopic");
            updateListData(jsonTopics);
            initListView();
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.screen_queue));
        LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.VISIBLE);
        LaunchActivity.getLaunchActivity().enableDisableBack(false);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initListView() {
        listview.setVisibility(View.VISIBLE);
        adapter = new MerchantReviewListAdapter(getActivity(), topics);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener((parent, view, position, id) -> {
            selected_pos = position;
            if (LaunchActivity.isTablet) {
                reviewFragment.updateReviews(topics.get(selected_pos));
                //set page for view pager
            } else {
                reviewFragment = new ReviewFragment();
                Bundle b = new Bundle();
                b.putSerializable("jsonTopic", topics.get(selected_pos));
                reviewFragment.setArguments(b);
                ReviewListActivity.getReviewListActivity().replaceFragmentWithBackStack(R.id.frame_layout, reviewFragment, ReviewListFragment.class.getSimpleName());
            }

            // to set the selected cell color
            getActivity().runOnUiThread(run);
        });

        if (LaunchActivity.isTablet) {
            reviewFragment = new ReviewFragment();
            Bundle b = new Bundle();
            b.putSerializable("jsonTopic", topics.get(selected_pos));
            reviewFragment.setArguments(b);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.list_detail_fragment, reviewFragment);
            fragmentTransaction.commit();
        }
    }

    public void updateListData(List<JsonTopic> jsonTopics) {
        topics = new ArrayList<>();
        topics.addAll(jsonTopics);
    }
}

