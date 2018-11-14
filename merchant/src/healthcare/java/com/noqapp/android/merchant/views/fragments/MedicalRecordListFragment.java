package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalHistoryModel;
import com.noqapp.android.merchant.presenter.beans.MedicalRecordListPresenter;
import com.noqapp.android.merchant.presenter.beans.body.FindMedicalProfile;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.MedicalHistoryAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedicalRecordListFragment extends Fragment implements MedicalRecordListPresenter {


    private ListView listview;
    private TextView tv_empty;
    private List<JsonMedicalRecord> jsonMedicalRecords = new ArrayList<>();
    private MedicalHistoryModel medicalHistoryModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_medical_record_list, container, false);
        listview = view.findViewById(R.id.listview);
        tv_empty = view.findViewById(R.id.tv_empty);
        FrameLayout frame_layout = view.findViewById(R.id.frame_layout);
        if (jsonMedicalRecords.size() <= 0) {
            listview.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
        } else {
            listview.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
        }
        medicalHistoryModel = new MedicalHistoryModel(this);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            //  progressDialog.show();
            medicalHistoryModel.fetch(BaseLaunchActivity.getDeviceID(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(), new FindMedicalProfile().setCodeQR(getArguments().getString("qCodeQR")).setQueueUserId(getArguments().getString("qUserId")));
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }

        return view;
    }


    @Override
    public void medicalRecordListResponse(JsonMedicalRecordList jsonMedicalRecordList) {
        Log.d("data", jsonMedicalRecordList.toString());
        if (!jsonMedicalRecordList.getJsonMedicalRecords().isEmpty()) {
            jsonMedicalRecords = jsonMedicalRecordList.getJsonMedicalRecords();
        }
        Collections.reverse(jsonMedicalRecords);
        MedicalHistoryAdapter adapter = new MedicalHistoryAdapter(getActivity(), jsonMedicalRecords);
        listview.setAdapter(adapter);
        if (jsonMedicalRecords.size() <= 0) {
            listview.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
        } else {
            listview.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent in = new Intent(MedicalHistoryActivity.this, MedicalHistoryDetailActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("data", jsonMedicalRecords.get(i));
//                in.putExtras(bundle);
//                startActivity(in);
            }
        });
        // dismissProgress();
    }

    @Override
    public void medicalRecordListError() {

    }

    @Override
    public void authenticationFailure() {
        AppUtils.authenticationProcessing();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        // dismissProgress();
        new ErrorResponseHandler().processError(getActivity(), eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        //dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
    }
}
