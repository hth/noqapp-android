package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;

import java.util.List;

public class MedicalHistoryDentalAdapter extends BaseAdapter {
    private Context context;
    private List<JsonMedicalRecord> jsonMedicalRecordList;

    public MedicalHistoryDentalAdapter(Context context, List<JsonMedicalRecord> jsonMedicalRecordList) {
        this.context = context;
        this.jsonMedicalRecordList = jsonMedicalRecordList;
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
            recordHolder.tv_dental_treatment = view.findViewById(R.id.tv_dental_treatment);
            recordHolder.tv_dental_work_done = view.findViewById(R.id.tv_dental_work_done);
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
        try {
            recordHolder.tv_create.setText("Visited: " + CommonHelper.SDF_YYYY_MM_DD_HH_MM_A.format(CommonHelper.SDF_ISO8601_FMT.parse(jsonMedicalRecord.getCreateDate())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        recordHolder.tv_dental_treatment.setText(beautifyString(jsonMedicalRecord.getNoteForPatient()));
        recordHolder.tv_dental_work_done.setText(beautifyString(jsonMedicalRecord.getNoteToDiagnoser()));

        return view;
    }


    static class RecordHolder {
        private TextView tv_diagnosed_by;
        private TextView tv_create;
        private TextView tv_business_name;
        private TextView tv_business_category_name;
        private TextView tv_dental_treatment;
        private TextView tv_dental_work_done;

        private CardView cardview;

        RecordHolder() {
        }
    }

    private String beautifyString(String str){
        if(TextUtils.isEmpty(str)){
            return str;
        }else{
            if(str.contains("|")){
                String temp = str.replaceAll("\\|",", ");
                return temp;
            }else {
                return str;
            }
        }

    }

}
