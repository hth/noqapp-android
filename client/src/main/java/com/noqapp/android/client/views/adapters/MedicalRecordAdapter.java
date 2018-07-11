package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;

import java.util.List;

public class MedicalRecordAdapter extends BaseAdapter {

    private Context context;
    private List<JsonMedicalMedicine> medicalRecordList;

    public MedicalRecordAdapter(Context context, List<JsonMedicalMedicine> medicalRecordList) {
        this.context = context;
        this.medicalRecordList = medicalRecordList;
    }

    public int getCount() {
        return this.medicalRecordList.size();
    }

    public Object getItem(int n) {
        return null;
    }

    public long getItemId(int n) {
        return 0;
    }

    public int getItemViewType(int position) {
        return position;
    }

    public View getView(int pos, View view, ViewGroup viewGroup) {
        final int position = pos;
        final RecordHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            recordHolder = new RecordHolder();
            view = layoutInflater.inflate(R.layout.medical_item, null);
            recordHolder.tv_medicine_name = view.findViewById(R.id.tv_medicine_name);
            recordHolder.tv_medication = view.findViewById(R.id.tv_medication);
            recordHolder.tv_dose = view.findViewById(R.id.tv_dose);
            recordHolder.tv_frequency = view.findViewById(R.id.tv_frequency);
            recordHolder.tv_dose_timing = view.findViewById(R.id.tv_dose_timing);
            recordHolder.tv_course = view.findViewById(R.id.tv_course);
            recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }

        final JsonMedicalMedicine medicalRecord = medicalRecordList.get(position);
        recordHolder.tv_medication.setText(medicalRecord.getMedicationType().getDescription());
        recordHolder.tv_dose.setText(medicalRecord.getStrength());
        recordHolder.tv_frequency.setText(medicalRecord.getDailyFrequency());
        recordHolder.tv_dose_timing.setText(medicalRecord.getMedicationWithFood().getDescription());
        recordHolder.tv_course.setText(medicalRecord.getCourse());
        recordHolder.tv_medicine_name.setText(medicalRecord.getName());
        return view;
    }

    static class RecordHolder {
        TextView tv_medicine_name;
        TextView tv_medication;
        TextView tv_frequency;
        TextView tv_dose_timing;
        TextView tv_course;
        TextView tv_dose;
        CardView cardview;

        RecordHolder() {
        }
    }

}
