package com.noqapp.android.merchant.views.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.ShowPersonInQAdapter;
import com.noqapp.android.merchant.views.interfaces.AdapterCallback;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MerchantDetailFragment extends BaseFragment implements QueuePersonListPresenter {

    protected Context context;
   // protected ImageView iv_banner;
    protected TextView tvcount;
    private ShowPersonInQAdapter peopleInQAdapter;

    public List<JsonQueuedPerson> getJsonQueuedPersonArrayList() {
        return jsonQueuedPersonArrayList;
    }

    private List<JsonQueuedPerson> jsonQueuedPersonArrayList = new ArrayList<>();
    protected EditText edt_mobile;
    protected RecyclerView rv_queue_people;
   // protected ProgressBar progressDialog;
    protected JsonTopic jsonTopic = null;
    protected TextView tv_title, tv_current_value, tv_timing;
    protected int currrentpos = 0;
    protected static AdapterCallback mAdapterCallback;
    protected boolean queueStatusOuter = false;
    protected ManageQueueApiCalls manageQueueApiCalls;
    protected ArrayList<JsonTopic> topicsList;


    public static void setAdapterCallBack(AdapterCallback adapterCallback) {
        mAdapterCallback = adapterCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Bundle bundle = getArguments();
        if (null != bundle) {
            topicsList = (ArrayList<JsonTopic>) bundle.getSerializable("jsonMerchant");
            currrentpos = bundle.getInt("position");
        }

        View itemView = inflater.inflate(R.layout.merchant_detail_page, container, false);
        context = getActivity();
        manageQueueApiCalls = new ManageQueueApiCalls();
        jsonTopic = topicsList.get(currrentpos);
        //progressDialog = itemView.findViewById(R.id.progress_bar);
        tv_current_value = itemView.findViewById(R.id.tv_current_value);
        tv_title = itemView.findViewById(R.id.tv_title);
        tv_timing = itemView.findViewById(R.id.tv_timing);
        rv_queue_people = itemView.findViewById(R.id.rv_queue_people);
        rv_queue_people.setHasFixedSize(true);
        rv_queue_people.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        rv_queue_people.setItemAnimator(new DefaultItemAnimator());

        TextView tv_deviceId = itemView.findViewById(R.id.tv_deviceId);
        tv_deviceId.setText(UserUtils.getDeviceId());
        tv_deviceId.setVisibility(BuildConfig.BUILD_TYPE.equals("debug") ? View.VISIBLE : View.GONE);
        updateUI();
        return itemView;
    }


    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.screen_queue_detail));
        LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.VISIBLE);
        LaunchActivity.getLaunchActivity().enableDisableBack(false);
    }

    public void updateListData(final ArrayList<JsonTopic> jsonTopics) {
        try {
            topicsList = jsonTopics;
            resetList();
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPage(int pos) {
        //update UI
        currrentpos = pos;
        jsonTopic = topicsList.get(pos);
        resetList();
        updateUI();
    }

    @Override
    public void queuePersonListResponse(JsonQueuePersonList jsonQueuePersonList) {
        if (null != jsonQueuePersonList) {
            jsonQueuedPersonArrayList = jsonQueuePersonList.getQueuedPeople();
            Collections.sort(jsonQueuedPersonArrayList, (lhs, rhs) -> Integer.compare(lhs.getToken(), rhs.getToken()));
            peopleInQAdapter = new ShowPersonInQAdapter(jsonQueuedPersonArrayList, context, jsonTopic.getServingNumber(), jsonTopic.getQueueStatus());
            rv_queue_people.setAdapter(peopleInQAdapter);
            if (jsonTopic.getServingNumber() > 0) {
                rv_queue_people.getLayoutManager().scrollToPosition(jsonTopic.getServingNumber() - 1);
            }
        }
        dismissProgress();
    }

    @Override
    public void queuePersonListError() {
        dismissProgress();
    }


    protected void updateUI() {
        final QueueStatusEnum queueStatus = jsonTopic.getQueueStatus();
        queueStatusOuter = queueStatus == QueueStatusEnum.N;
        tv_timing.setText("Timing: " + Formatter.convertMilitaryTo12HourFormat(jsonTopic.getHour().getStartHour())
                + " - " + Formatter.convertMilitaryTo12HourFormat(jsonTopic.getHour().getEndHour()));
        tv_current_value.setText(String.valueOf(jsonTopic.getServingNumber()));
        tv_title.setText(jsonTopic.getDisplayName());
        if (new NetworkUtil(getActivity()).isOnline()) {
            showProgress();
            getAllPeopleInQ(jsonTopic);

        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    protected void resetList() {
        jsonQueuedPersonArrayList = new ArrayList<>();
        peopleInQAdapter = new ShowPersonInQAdapter(jsonQueuedPersonArrayList, context);
        rv_queue_people.setAdapter(peopleInQAdapter);
    }

    public void getAllPeopleInQ(JsonTopic jsonTopic) {
        manageQueueApiCalls.setQueuePersonListPresenter(this);
        manageQueueApiCalls.getAllQueuePersonList(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonTopic.getCodeQR());
    }
}
