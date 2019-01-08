package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.common.model.types.medical.DailyFrequencyEnum;
import com.noqapp.android.common.model.types.medical.MedicationIntakeEnum;
import com.noqapp.android.common.model.types.medical.PharmacyCategoryEnum;
import com.noqapp.android.merchant.R;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
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

    public View getView(final int pos, View view, ViewGroup viewGroup) {
        final int position = pos;
        final RecordHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            recordHolder = new RecordHolder();
            view = layoutInflater.inflate(R.layout.medical_item, viewGroup, false);
            recordHolder.tv_medicine_name = view.findViewById(R.id.tv_medicine_name);
            recordHolder.tv_medication = view.findViewById(R.id.tv_medication);
            recordHolder.tv_course = view.findViewById(R.id.tv_course);
            recordHolder.tv_daily_frequency = view.findViewById(R.id.tv_daily_frequency);
            recordHolder.tv_medication_with_food = view.findViewById(R.id.tv_medication_with_food);
            recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }

        final JsonMedicalMedicine medicalRecord = medicalRecordList.get(position);
        recordHolder.tv_medication.setText(medicalRecord.getPharmacyCategory());
        recordHolder.tv_medication_with_food.setText(medicalRecord.getMedicationIntake());
        recordHolder.tv_daily_frequency.setText(medicalRecord.getDailyFrequency());
        recordHolder.tv_course.setText(medicalRecord.getCourse() + " days");
        recordHolder.tv_medicine_name.setText(medicalRecord.getName());
        if (medicalRecord.getPharmacyCategory().equals(PharmacyCategoryEnum.CA.getDescription()))
            recordHolder.tv_medication.setCompoundDrawablesWithIntrinsicBounds(R.drawable.med_capsule, 0, 0, 0);
        else if (medicalRecord.getPharmacyCategory().equals(PharmacyCategoryEnum.TA.getDescription()))
            recordHolder.tv_medication.setCompoundDrawablesWithIntrinsicBounds(R.drawable.med_tablet, 0, 0, 0);
        else if (medicalRecord.getPharmacyCategory().equals(PharmacyCategoryEnum.SY.getDescription()))
            recordHolder.tv_medication.setCompoundDrawablesWithIntrinsicBounds(R.drawable.med_syrup, 0, 0, 0);
        else if (medicalRecord.getPharmacyCategory().equals(PharmacyCategoryEnum.IJ.getDescription()))
            recordHolder.tv_medication.setCompoundDrawablesWithIntrinsicBounds(R.drawable.med_injection, 0, 0, 0);
        else if (medicalRecord.getPharmacyCategory().equals(PharmacyCategoryEnum.LO.getDescription()))
            recordHolder.tv_medication.setCompoundDrawablesWithIntrinsicBounds(R.drawable.med_lotion, 0, 0, 0);
        else if (medicalRecord.getPharmacyCategory().equals(PharmacyCategoryEnum.CR.getDescription()))
            recordHolder.tv_medication.setCompoundDrawablesWithIntrinsicBounds(R.drawable.med_cream, 0, 0, 0);
        else
            recordHolder.tv_medication.setCompoundDrawables(null, null, null, null);
        return view;
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
                jsonMedicalMedicine.setMedicationIntake(MedicationIntakeEnum.getValue(jsonMedicalMedicine.getMedicationIntake()));
                temp.add(jsonMedicalMedicine);
            }
            return temp;
        }
    }

    static class RecordHolder {
        TextView tv_medicine_name;
        TextView tv_medication;
        TextView tv_medication_with_food;
        TextView tv_course;
        TextView tv_daily_frequency;
        CardView cardview;

        RecordHolder() {
        }
    }
}
