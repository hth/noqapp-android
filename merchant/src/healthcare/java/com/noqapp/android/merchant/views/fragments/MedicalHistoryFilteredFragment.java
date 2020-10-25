package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalHistoryApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.body.merchant.FindMedicalProfile;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.activities.AppInitialize;
import com.noqapp.android.merchant.views.adapters.MedicalHistoryAdapter;
import com.noqapp.android.merchant.views.adapters.MedicalHistoryDentalAdapter;
import com.noqapp.android.merchant.views.interfaces.MedicalRecordListPresenter;

import java.util.Collections;
import java.util.List;

public class MedicalHistoryFilteredFragment
    extends BaseFragment
    implements MedicalRecordListPresenter, MedicalHistoryAdapter.HistoryUpdate {
    private ProgressBar pb_history;
    private ListView listview;
    private TextView tv_empty_list;
    private MedicalHistoryApiCalls medicalHistoryApiCalls;
    private String codeQR = "";
    private String bizCategoryId;
    private JsonQueuedPerson jsonQueuedPerson;
    private UpdateWorkDone updateWorkDone;

    public void setUpdateWorkDone(UpdateWorkDone updateWorkDone) {
        this.updateWorkDone = updateWorkDone;
    }

    public interface UpdateWorkDone {
        void updateWorkDone(List<JsonMedicalRecord> jsonMedicalRecords);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_medical_history_filtered, container, false);
        pb_history = view.findViewById(R.id.pb_history);
        tv_empty_list = view.findViewById(R.id.tv_empty_list);
        listview = view.findViewById(R.id.listview);
        codeQR = getArguments().getString("codeQR");
        bizCategoryId = getArguments().getString("bizCategoryId");
        jsonQueuedPerson = (JsonQueuedPerson) getArguments().getSerializable("jsonQueuedPerson");
        medicalHistoryApiCalls = new MedicalHistoryApiCalls(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (new NetworkUtil(getActivity()).isOnline()) {
            medicalHistoryApiCalls.historicalFiltered(
                AppInitialize.getDeviceID(),
                AppInitialize.getEmail(),
                AppInitialize.getAuth(), bizCategoryId,
                new FindMedicalProfile().setCodeQR(codeQR).setQueueUserId(jsonQueuedPerson.getQueueUserId()));
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    @Override
    public void medicalRecordListResponse(JsonMedicalRecordList jsonMedicalRecordList) {
        pb_history.setVisibility(View.GONE);
    }

    @Override
    public void medicalRecordDentalListResponse(JsonMedicalRecordList jsonMedicalRecordList) {
        Log.d("data", jsonMedicalRecordList.toString());
        if (!jsonMedicalRecordList.getJsonMedicalRecords().isEmpty()) {
            List<JsonMedicalRecord> jsonMedicalRecords = jsonMedicalRecordList.getJsonMedicalRecords();
            Collections.sort(jsonMedicalRecords, (o1, o2) -> {
                try {
                    return CommonHelper.SDF_YYYY_MM_DD.parse(o2.getCreateDate()).compareTo(CommonHelper.SDF_YYYY_MM_DD.parse(o1.getCreateDate()));
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            });
            MedicalHistoryDentalAdapter adapter = new MedicalHistoryDentalAdapter(getActivity(), jsonMedicalRecords);
            listview.setAdapter(adapter);
            if (null == jsonMedicalRecords || jsonMedicalRecords.size() == 0) {
                tv_empty_list.setVisibility(View.VISIBLE);
            } else {
                if (MedicalDepartmentEnum.valueOf(bizCategoryId) == MedicalDepartmentEnum.DNT) {
                    if (null != updateWorkDone) {
                        updateWorkDone.updateWorkDone(jsonMedicalRecords);
                    }
                }
            }
            pb_history.setVisibility(View.GONE);
        }
    }

    @Override
    public void medicalRecordListError() {
        pb_history.setVisibility(View.GONE);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        super.responseErrorPresenter(errorCode);
        pb_history.setVisibility(View.GONE);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        super.responseErrorPresenter(eej);
        pb_history.setVisibility(View.GONE);
    }

    @Override
    public void authenticationFailure() {
        super.authenticationFailure();
        pb_history.setVisibility(View.GONE);
    }

    @Override
    public void showProgress(boolean isShown) {
        pb_history.setVisibility(isShown ? View.VISIBLE : View.GONE);
    }

    public void updateList() {
        medicalHistoryApiCalls.historical(
            AppInitialize.getDeviceID(),
            AppInitialize.getEmail(),
            AppInitialize.getAuth(),
            new FindMedicalProfile().setCodeQR(codeQR).setQueueUserId(jsonQueuedPerson.getQueueUserId()));
    }
}
