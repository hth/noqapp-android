package com.noqapp.android.merchant.views.fragments;


import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueModel;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.ShowPersonInQAdapter;
import com.noqapp.android.merchant.views.interfaces.AdapterCallback;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MerchantDetailFragment extends Fragment implements QueuePersonListPresenter {

    protected Context context;

    protected ImageView iv_banner;
    protected TextView tvcount;
    private ShowPersonInQAdapter peopleInQAdapter;

    public List<JsonQueuedPerson> getJsonQueuedPersonArrayList() {
        return jsonQueuedPersonArrayList;
    }

    private List<JsonQueuedPerson> jsonQueuedPersonArrayList = new ArrayList<>();
    protected EditText edt_mobile;
    protected RecyclerView rv_queue_people;
    protected ProgressBar progressDialog;
    protected JsonTopic jsonTopic = null;
    protected TextView tv_title, tv_current_value, tv_timing;
    protected int currrentpos = 0;
    protected static AdapterCallback mAdapterCallback;
    protected boolean queueStatusOuter = false;
    protected ManageQueueModel manageQueueModel;
    protected ArrayList<JsonTopic> topicsList;


    public static void setAdapterCallBack(AdapterCallback adapterCallback) {
        mAdapterCallback = adapterCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (null != bundle) {
            topicsList = (ArrayList<JsonTopic>) bundle.getSerializable("jsonMerchant");
            currrentpos = bundle.getInt("position");
        }

        View itemView = inflater.inflate(R.layout.merchant_detail_page, container, false);
        context = getActivity();
        manageQueueModel = new ManageQueueModel();
        jsonTopic = topicsList.get(currrentpos);
        progressDialog = itemView.findViewById(R.id.progress_bar);
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
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        dismissProgress();
        new ErrorResponseHandler().processError(getActivity(), eej);

    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
    }

    @Override
    public void authenticationFailure() {
        LaunchActivity.getLaunchActivity().dismissProgress();
        AppUtils.authenticationProcessing();
    }


    @Override
    public void queuePersonListResponse(JsonQueuePersonList jsonQueuePersonList) {
        if (null != jsonQueuePersonList) {
            jsonQueuedPersonArrayList = jsonQueuePersonList.getQueuedPeople();
            Collections.sort(
                    jsonQueuedPersonArrayList,
                    new Comparator<JsonQueuedPerson>() {
                        public int compare(JsonQueuedPerson lhs, JsonQueuedPerson rhs) {
                            return Integer.compare(lhs.getToken(), rhs.getToken());
                        }
                    }
            );
            peopleInQAdapter = new ShowPersonInQAdapter(jsonQueuedPersonArrayList, context, jsonTopic.getServingNumber(), jsonTopic.getQueueStatus());
            rv_queue_people.setAdapter(peopleInQAdapter);
            if (jsonTopic.getServingNumber() > 0)
                rv_queue_people.getLayoutManager().scrollToPosition(jsonTopic.getServingNumber() - 1);

        }
        dismissProgress();
    }

    @Override
    public void queuePersonListError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
        dismissProgress();
    }

    protected void dismissProgress() {
        if (null != progressDialog) {
            progressDialog.setVisibility(View.GONE);
        }
    }

    protected void updateUI() {

        final QueueStatusEnum queueStatus = jsonTopic.getQueueStatus();
        queueStatusOuter = queueStatus == QueueStatusEnum.N;
        tv_timing.setText("Timing: " + Formatter.convertMilitaryTo12HourFormat(jsonTopic.getHour().getStartHour())
                + " - " + Formatter.convertMilitaryTo12HourFormat(jsonTopic.getHour().getEndHour()));
        tv_current_value.setText(String.valueOf(jsonTopic.getServingNumber()));
        tv_title.setText(jsonTopic.getDisplayName());
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.setVisibility(View.VISIBLE);
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
        manageQueueModel.setQueuePersonListPresenter(this);
        manageQueueModel.getAllQueuePersonList(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonTopic.getCodeQR());
    }


}
