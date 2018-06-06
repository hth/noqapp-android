package com.noqapp.android.merchant.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.AutocompleteAdapter;
import com.noqapp.android.merchant.views.adapters.MerchantChartListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MerchantChartListFragment extends Fragment implements    MerchantChartViewPagerFragment.UpdateListColorCallBack {

    public static int selected_pos = 0;
    public MerchantChartViewPagerFragment merchantChartViewPagerFragment;

    private MerchantChartListAdapter adapter;
    private AutocompleteAdapter temp_adapter;

    private Runnable  run;

    private ArrayList<JsonTopic> topics = new ArrayList<>();
    private ListView listview;
    private AutoCompleteTextView auto_complete_search;

    public MerchantChartListFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchant_chart_list, container, false);
        listview = (ListView) view.findViewById(R.id.listview);
        auto_complete_search = (AutoCompleteTextView) view.findViewById(R.id.auto_complete_search);
        auto_complete_search.setThreshold(1);
        auto_complete_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (auto_complete_search.getRight() - auto_complete_search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        hideAndReset();
                        return true;
                    }
                }
                return false;
            }
        });

        auto_complete_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                String selectedQRcode = temp_adapter.getQRCode(pos);
                for (int j = 0; j < topics.size(); j++) {
                    JsonTopic jt = topics.get(j);
                    if (selectedQRcode.equalsIgnoreCase(jt.getCodeQR())) {
                        hideAndReset();
                        listview.performItemClick(
                                listview.getAdapter().getView(j, null, null),
                                j,
                                listview.getAdapter().getItemId(j));
                        break;
                    }
                }
            }
        });

        Bundle bundle = getArguments();
        MerchantChartViewPagerFragment.setUpdateListColorCallBack(this);
        run = new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
                listview.invalidateViews();
                listview.refreshDrawableState();
            }
        };
        if (null != bundle) {
            ArrayList<JsonTopic> jsonTopics = (ArrayList<JsonTopic>) bundle.getSerializable("jsonTopic");
            updateListData(jsonTopics);
            initListView();
        }
        return view;
    }

    private void hideAndReset() {
        auto_complete_search.setText("");
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(auto_complete_search.getWindowToken(), 0);
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
        adapter = new MerchantChartListAdapter(getActivity(), topics);
        listview.setAdapter(adapter);
        temp_adapter = new AutocompleteAdapter(getActivity(),
                R.layout.auto_text_item, topics);
        auto_complete_search.setAdapter(temp_adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!new AppUtils().isTablet(getActivity())) {
                    merchantChartViewPagerFragment = new MerchantChartViewPagerFragment();
                    Bundle b = new Bundle();
                    b.putSerializable("jsonMerchant", topics);
                    b.putInt("position", position);
                    merchantChartViewPagerFragment.setArguments(b);
                    LaunchActivity.getLaunchActivity().replaceFragmentWithBackStack(R.id.frame_layout, merchantChartViewPagerFragment, "MerchantViewPagerFragment");
                } else {
                    merchantChartViewPagerFragment.setPage(position);
                    //set page for view pager
                }
                selected_pos = position;
                // to set the selected cell color
                getActivity().runOnUiThread(run);
            }
        });



        if (new AppUtils().isTablet(getActivity())) {
            merchantChartViewPagerFragment = new MerchantChartViewPagerFragment();
            Bundle b = new Bundle();
            b.putSerializable("jsonMerchant", topics);
            b.putInt("position", selected_pos);
            merchantChartViewPagerFragment.setArguments(b);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.list_detail_fragment, merchantChartViewPagerFragment);
            //  fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    public void updateListData(List<JsonTopic> jsonTopics) {
        topics = new ArrayList<>();
        topics.addAll(jsonTopics);
    }


    @Override
    public void onUpdateListColorCallBack(int pos) {
        selected_pos = pos;
        // to set the selected cell color
        getActivity().runOnUiThread(run);
    }
}
