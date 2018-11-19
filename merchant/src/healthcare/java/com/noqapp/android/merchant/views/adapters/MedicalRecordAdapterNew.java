package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.common.model.types.medical.DailyFrequencyEnum;
import com.noqapp.android.common.model.types.medical.PharmacyCategoryEnum;
import com.noqapp.android.merchant.R;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import info.hoang8f.android.segmented.SegmentedGroup;

import java.util.ArrayList;
import java.util.List;

public class MedicalRecordAdapterNew extends BaseAdapter {

    private Context context;
    private List<JsonMedicalMedicine> medicalRecordList;

    public MedicalRecordAdapterNew(Context context, List<JsonMedicalMedicine> medicalRecordList) {
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

    public View getView(final int pos, View view, ViewGroup viewGroup) {
        final int position = pos;
        final RecordHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            recordHolder = new RecordHolder();
            view = layoutInflater.inflate(R.layout.medical_item_new, viewGroup, false);
            recordHolder.tv_medicine_name = view.findViewById(R.id.tv_medicine_name);
            recordHolder.tv_medication = view.findViewById(R.id.tv_medication);
            recordHolder.tv_dose = view.findViewById(R.id.tv_dose);
            recordHolder.sp_course = view.findViewById(R.id.sp_course);
            recordHolder.sg_daily_frequency = view.findViewById(R.id.sg_daily_frequency);
            recordHolder.sg_medication_food = view.findViewById(R.id.sg_medication_food);
            recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }

        final JsonMedicalMedicine medicalRecord = medicalRecordList.get(position);
        recordHolder.tv_medication.setText(medicalRecord.getPharmacyCategory());
        recordHolder.tv_dose.setText(medicalRecord.getStrength());
        //recordHolder.tv_frequency.setText(medicalRecord.getDailyFrequency());
        //recordHolder.tv_dose_timing.setText(medicalRecord.getMedicationWithFood());
        //recordHolder.tv_course.setText(medicalRecord.getCourse());
        recordHolder.tv_medicine_name.setText(medicalRecord.getName());
        recordHolder.sp_course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int sp_pos, long id) {
                medicalRecordList.get(position).setCourse(recordHolder.sp_course.getItemAtPosition(sp_pos).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        recordHolder.sg_daily_frequency.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton radioButton = group.findViewById(checkedId);
                        medicalRecordList.get(position).setDailyFrequency(radioButton.getText().toString());
                    }
                }
        );
        recordHolder.sg_medication_food.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton radioButton = group.findViewById(checkedId);
                        medicalRecordList.get(position).setMedicationWithFood(radioButton.getText().toString());
                    }
                }
        );
        return view;
    }

    public List<JsonMedicalMedicine> getJsonMedicineList() {
        return medicalRecordList;
    }

    public List<JsonMedicalMedicine> getJsonMedicineListWithEnum() {
        if (medicalRecordList.size() == 0)
            return medicalRecordList;
        else {
            List<JsonMedicalMedicine> temp = new ArrayList<>();
            for (int i = 0; i < medicalRecordList.size(); i++) {
                JsonMedicalMedicine jsonMedicalMedicine = medicalRecordList.get(i);
                jsonMedicalMedicine.setPharmacyCategory(PharmacyCategoryEnum.getValue(jsonMedicalMedicine.getPharmacyCategory()));
                jsonMedicalMedicine.setDailyFrequency(DailyFrequencyEnum.getValueFromTimes(jsonMedicalMedicine.getDailyFrequency()));
                temp.add(jsonMedicalMedicine);
            }
            return temp;
        }
    }

    static class RecordHolder {
        TextView tv_medicine_name;
        TextView tv_medication;
        TextView tv_dose;
        Spinner sp_course;
        SegmentedGroup sg_daily_frequency;
        SegmentedGroup sg_medication_food;
        CardView cardview;

        RecordHolder() {
        }
    }
}
