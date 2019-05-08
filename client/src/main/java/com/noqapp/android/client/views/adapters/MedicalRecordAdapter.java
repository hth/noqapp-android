package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.common.model.types.medical.DailyFrequencyEnum;
import com.noqapp.android.common.model.types.medical.PharmacyCategoryEnum;

import java.util.List;

import androidx.cardview.widget.CardView;

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

    public View getView(int pos, View view, ViewGroup parent) {
        final int position = pos;
        final RecordHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            recordHolder = new RecordHolder();
            view = layoutInflater.inflate(R.layout.medical_item, parent, false);
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
        recordHolder.tv_medication.setText(PharmacyCategoryEnum.getValueOfField(medicalRecord.getPharmacyCategory()));
        recordHolder.tv_dose.setText(medicalRecord.getStrength());
        recordHolder.tv_frequency.setText(DailyFrequencyEnum.getValueOfField(medicalRecord.getDailyFrequency()));
        recordHolder.tv_dose_timing.setText(medicalRecord.getMedicationIntake());
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
