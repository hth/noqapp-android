package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.medical.PharmacyCategoryEnum;
import com.noqapp.android.merchant.R;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MedicalHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<JsonMedicalRecord> jsonMedicalRecordList;

    public MedicalHistoryAdapter(Context context, List<JsonMedicalRecord> jsonMedicalRecordList) {
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

    public View getView(int position, View view, ViewGroup viewGroup) {
        RecordHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            recordHolder = new RecordHolder();
            view = layoutInflater.inflate(R.layout.listitem_medical_history, viewGroup, false);

            recordHolder.tv_diagnosed_by = view.findViewById(R.id.tv_diagnosed_by);
            recordHolder.tv_business_name = view.findViewById(R.id.tv_business_name);
            recordHolder.tv_business_category_name = view.findViewById(R.id.tv_business_category_name);
            recordHolder.tv_complaints = view.findViewById(R.id.tv_complaints);
            recordHolder.tv_create = view.findViewById(R.id.tv_create);
            recordHolder.tv_examination = view.findViewById(R.id.tv_examination);
            recordHolder.tv_medicine = view.findViewById(R.id.tv_medicine);
            recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        JsonMedicalRecord jsonMedicalRecord = jsonMedicalRecordList.get(position);
        if (jsonMedicalRecord.getBusinessType() == BusinessTypeEnum.DO) {
            recordHolder.tv_diagnosed_by.setText("Dr. " + jsonMedicalRecord.getDiagnosedByDisplayName());
        } else {
            recordHolder.tv_diagnosed_by.setText(jsonMedicalRecord.getDiagnosedByDisplayName());
        }
        recordHolder.tv_business_name.setText(jsonMedicalRecord.getBusinessName());
        recordHolder.tv_business_category_name.setText("(" + jsonMedicalRecord.getBizCategoryName() + ")");
        recordHolder.tv_complaints.setText(jsonMedicalRecord.getChiefComplain());
        recordHolder.tv_create.setText("Visited: " + jsonMedicalRecord.getCreateDate());
        recordHolder.tv_examination.setText(jsonMedicalRecord.getExamination());
        recordHolder.tv_medicine.setText(getMedicineFormList(jsonMedicalRecord.getMedicalMedicines()));
        showHideViews(recordHolder.tv_examination, recordHolder.tv_medicine, recordHolder.tv_complaints);
        return view;
    }

    static class RecordHolder {
        TextView tv_diagnosed_by;
        TextView tv_complaints;
        TextView tv_create;
        TextView tv_business_name;
        TextView tv_business_category_name;
        TextView tv_examination;
        TextView tv_medicine;
        CardView cardview;

        RecordHolder() {
        }
    }

    private String getMedicineFormList(List<JsonMedicalMedicine> list) {
        String output = "";
        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                output += PharmacyCategoryEnum.valueOf(list.get(i).getPharmacyCategory()).getDescription() + " " + list.get(i).getName();
                if (i != list.size() - 1)
                    output += "\n";
            }
        }
        return output;
    }

    private void showHideViews(TextView... views) {
        for (TextView v : views) {
            if (v.getText().toString().equals("")) {
                v.setVisibility(View.GONE);
            } else {
                v.setVisibility(View.VISIBLE);
            }
        }
    }
}
