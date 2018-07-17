package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;

import java.util.List;

public class MedicalHistoryAdapter extends BaseAdapter {
    private static final String TAG = MedicalHistoryAdapter.class.getSimpleName();
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
            view = layoutInflater.inflate(R.layout.listitem_medical_history, null);

            recordHolder.tv_diagnosed_by = view.findViewById(R.id.tv_diagnosed_by);
            recordHolder.tv_business_name = view.findViewById(R.id.tv_business_name);
            recordHolder.tv_business_category_name = view.findViewById(R.id.tv_business_category_name);
            recordHolder.tv_complaints = view.findViewById(R.id.tv_complaints);
            recordHolder.tv_create = view.findViewById(R.id.tv_create);
            recordHolder.tv_no_of_time_access = view.findViewById(R.id.tv_no_of_time_access);
            recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        recordHolder.tv_diagnosed_by.setText(jsonMedicalRecordList.get(position).getDiagnosedById());
        recordHolder.tv_business_name.setText(jsonMedicalRecordList.get(position).getBusinessName());
        recordHolder.tv_business_category_name.setText(jsonMedicalRecordList.get(position).getBizCategoryName());
        recordHolder.tv_complaints.setText(jsonMedicalRecordList.get(position).getChiefComplain());
        recordHolder.tv_create.setText("Visited: " + jsonMedicalRecordList.get(position).getCreateDate());
        recordHolder.tv_no_of_time_access.setText("No of times record view: " + jsonMedicalRecordList.get(position).getRecordAccess().size());
        return view;
    }

    static class RecordHolder {
        TextView tv_diagnosed_by;
        TextView tv_complaints;
        TextView tv_create;
        TextView tv_business_name;
        TextView tv_business_category_name;
        TextView tv_no_of_time_access;
        CardView cardview;

        RecordHolder() {
        }
    }
}
