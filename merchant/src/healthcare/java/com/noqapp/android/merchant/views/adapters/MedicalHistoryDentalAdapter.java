package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.UpdateObservationPresenter;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.views.activities.PatientProfileActivity;

import java.util.List;

public class MedicalHistoryDentalAdapter extends BaseAdapter implements UpdateObservationPresenter {
    private Context context;
    private List<JsonMedicalRecord> jsonMedicalRecordList;
    private UpdateObservationPresenter updateObservationPresenter;


    public MedicalHistoryDentalAdapter(Context context, List<JsonMedicalRecord> jsonMedicalRecordList) {
        this.context = context;
        this.jsonMedicalRecordList = jsonMedicalRecordList;
        updateObservationPresenter = this;
    }

    public int getCount() {
        return this.jsonMedicalRecordList.size();
    }

    public Object getItem(int n) {
        return null;
    }

    public long getItemId(int n) {
        return 0;
    }

    public View getView(final int position, View view, ViewGroup viewGroup) {
        RecordHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            recordHolder = new RecordHolder();
            view = layoutInflater.inflate(R.layout.listitem_dental_history, viewGroup, false);

            recordHolder.tv_diagnosed_by = view.findViewById(R.id.tv_diagnosed_by);
            recordHolder.tv_business_name = view.findViewById(R.id.tv_business_name);
            recordHolder.tv_business_category_name = view.findViewById(R.id.tv_business_category_name);
            recordHolder.tv_create = view.findViewById(R.id.tv_create);
            recordHolder.tv_examination = view.findViewById(R.id.tv_examination);
            recordHolder.tv_dental_treatment = view.findViewById(R.id.tv_dental_treatment);
            recordHolder.tv_dental_work_done = view.findViewById(R.id.tv_dental_work_done);
            recordHolder.ll_dental = view.findViewById(R.id.ll_dental);

            recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        final JsonMedicalRecord jsonMedicalRecord = jsonMedicalRecordList.get(position);
        if (jsonMedicalRecord.getBusinessType() == BusinessTypeEnum.DO) {
            recordHolder.tv_diagnosed_by.setText("Dr. " + jsonMedicalRecord.getDiagnosedByDisplayName());
        } else {
            recordHolder.tv_diagnosed_by.setText(jsonMedicalRecord.getDiagnosedByDisplayName());
        }
        recordHolder.tv_business_name.setText(jsonMedicalRecord.getBusinessName());
       // recordHolder.tv_business_category_name.setText("(" + jsonMedicalRecord.getBizCategoryName() + ")");
        // recordHolder.tv_create.setText("Visited: " + jsonMedicalRecord.getCreateDate());
        try {
            recordHolder.tv_create.setText("Visited: " + CommonHelper.SDF_YYYY_MM_DD_HH_MM_A.format(CommonHelper.SDF_ISO8601_FMT.parse(jsonMedicalRecord.getCreateDate())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        recordHolder.ll_dental.setVisibility(View.VISIBLE);
        recordHolder.tv_dental_treatment.setText(jsonMedicalRecord.getNoteForPatient());
        recordHolder.tv_dental_work_done.setText(jsonMedicalRecord.getNoteToDiagnoser());

        return view;
    }

    @Override
    public void updateObservationResponse(JsonResponse jsonResponse) {
        if (null != PatientProfileActivity.getPatientProfileActivity())
            PatientProfileActivity.getPatientProfileActivity().pb_history.setVisibility(View.GONE);
        Log.v(" updateObservation", "" + jsonResponse.getResponse());
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            new CustomToast().showToast(context, "Observation updated successfully");
            PatientProfileActivity.getPatientProfileActivity().updateList();
        } else {
            new CustomToast().showToast(context, "Failed to update Observation");
        }
    }

    @Override
    public void authenticationFailure() {
        AppUtils.authenticationProcessing();
        if (null != PatientProfileActivity.getPatientProfileActivity())
            PatientProfileActivity.getPatientProfileActivity().pb_history.setVisibility(View.GONE);
        ;
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        // dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(context, errorCode);
        if (null != PatientProfileActivity.getPatientProfileActivity())
            PatientProfileActivity.getPatientProfileActivity().pb_history.setVisibility(View.GONE);
        ;
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        if (null != eej) {
            new ErrorResponseHandler().processError(context, eej);
        }
        if (null != PatientProfileActivity.getPatientProfileActivity())
            PatientProfileActivity.getPatientProfileActivity().pb_history.setVisibility(View.GONE);
        ;
    }

    static class RecordHolder {
        private TextView tv_diagnosed_by;
        private TextView tv_create;
        private TextView tv_business_name;
        private TextView tv_business_category_name;
        private TextView tv_examination;
        private TextView tv_dental_treatment;
        private TextView tv_dental_work_done;
        private LinearLayout ll_dental;


        private CardView cardview;

        RecordHolder() {
        }
    }


    private String parseSymptoms(String str) {
        String data = "";
        try {
            if (TextUtils.isEmpty(str)) {
                return "";
            } else {
                String[] temp = str.split("\\r?\\n");
                if (temp.length > 0) {
                    for (int i = 0; i < temp.length; i++) {
                        String act = temp[i];
                        if (act.contains("|")) {
                            String[] strArray = act.split("\\|");
                            String shortName = strArray[0];
                            String val = strArray[1];
                            String desc = "";
                            if (strArray.length == 3)
                                desc = strArray[2];

                            if (TextUtils.isEmpty(desc)) {
                                data += "Having " + shortName + " since last " + val + "." + "\n";
                            } else {
                                data += "Having " + shortName + " since last " + val + ". " + desc + "\n";
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (data.endsWith(", "))
            data = data.substring(0, data.length() - 2);
        return data;
    }

}
