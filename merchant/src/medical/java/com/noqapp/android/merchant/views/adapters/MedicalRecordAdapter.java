package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.beans.MedicalRecord;
import com.noqapp.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.common.model.types.MedicationTypeEnum;
import com.noqapp.common.model.types.MedicationWithFoodEnum;

import java.util.ArrayList;
import java.util.List;

public class MedicalRecordAdapter extends BaseAdapter {

    private final String ADD = "add";
    private final String DELETE = "delete";

    private Context context;


    private List<MedicalRecord> medicalRecordList;

    public MedicalRecordAdapter(Context context, List<MedicalRecord> medicalRecordList) {
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

            recordHolder.edt_medicine_name = view.findViewById(R.id.edt_medicine_name);
            recordHolder.edt_medicine_name.setTag(position);
            recordHolder.sp_medication = view.findViewById(R.id.sp_medication);
            recordHolder.sp_dose = view.findViewById(R.id.sp_dose);
            recordHolder.sp_frequency = view.findViewById(R.id.sp_frequency);
            recordHolder.sp_dose_timing = view.findViewById(R.id.sp_dose_timing);
            recordHolder.sp_course = view.findViewById(R.id.sp_course);
            recordHolder.tv_add_delete = view.findViewById(R.id.tv_add_delete);
            recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
      //  int tag_position = (Integer) recordHolder.edt_medicine_name.getTag();
      //  recordHolder.edt_medicine_name.setId(tag_position);
        ArrayAdapter<String> sp_medication_adapter = new ArrayAdapter<String>
                (context, android.R.layout.simple_spinner_item,
                        MedicationTypeEnum.asList());
        sp_medication_adapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        recordHolder.sp_medication.setAdapter(sp_medication_adapter);

        ArrayAdapter<String> sp_dose_timing_adapter = new ArrayAdapter<String>
                (context, android.R.layout.simple_spinner_item,
                        MedicationWithFoodEnum.asList());
        sp_dose_timing_adapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        recordHolder.sp_dose_timing.setAdapter(sp_dose_timing_adapter);
        final MedicalRecord medicalRecord = medicalRecordList.get(position);
        recordHolder.sp_frequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int sp_position, long id) {
                // your code here
                if (sp_position > 0) {
                    medicalRecordList.get(position).setFrequency(sp_position);
                    medicalRecordList.get(position).getJsonMedicalMedicine().setDailyFrequency(recordHolder.sp_frequency.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        recordHolder.sp_dose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int sp_position, long id) {
                // your code here
                if (sp_position > 0) {
                    medicalRecordList.get(position).setDose_size(sp_position);
                    medicalRecordList.get(position).getJsonMedicalMedicine().setStrength(recordHolder.sp_dose.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        recordHolder.sp_course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int sp_position, long id) {
                // your code here
                if (sp_position > 0) {
                    medicalRecordList.get(position).setCourse(sp_position);
                    medicalRecordList.get(position).getJsonMedicalMedicine().setCourse(recordHolder.sp_course.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        recordHolder.sp_dose_timing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int sp_position, long id) {
                // your code here
                medicalRecordList.get(position).setBefore_after_food(sp_position);
                medicalRecordList.get(position).getJsonMedicalMedicine().setMedicationWithFood(MedicationWithFoodEnum.get(recordHolder.sp_dose_timing.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        recordHolder.sp_medication.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int sp_position, long id) {
                // your code here
                medicalRecordList.get(position).setMedication_type(sp_position);
                medicalRecordList.get(position).getJsonMedicalMedicine().setMedicationType(MedicationTypeEnum.get(recordHolder.sp_medication.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        recordHolder.sp_medication.setSelection(medicalRecord.getMedication_type());
        recordHolder.sp_dose.setSelection(medicalRecord.getDose_size());
        recordHolder.sp_frequency.setSelection(medicalRecord.getFrequency());
        recordHolder.sp_dose_timing.setSelection(medicalRecord.getBefore_after_food());
        recordHolder.sp_course.setSelection(medicalRecord.getCourse());
        if (null != medicalRecord && TextUtils.isEmpty(medicalRecord.getMedicName())) {
            recordHolder.edt_medicine_name.setText(medicalRecord.getMedicName());
        }


        recordHolder.edt_medicine_name.setId(position);
        recordHolder.edt_medicine_name.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            final int id = v.getId();
                            final EditText field = ((EditText) v);
                            medicalRecordList.get(id).setMedicName(field.getText().toString());
                        }
                    }
                }
        );
        
        if (null != medicalRecord && TextUtils.isEmpty(medicalRecord.getMedicName())) {
            recordHolder.tv_add_delete.setBackground(context.getResources().getDrawable(R.drawable.add_medic));
            recordHolder.tv_add_delete.setText(ADD);
        } else {
            recordHolder.tv_add_delete.setBackground(context.getResources().getDrawable(R.drawable.delete_medic));
            recordHolder.tv_add_delete.setText(DELETE);
        }
        if (position == medicalRecordList.size() - 1) {
            recordHolder.tv_add_delete.setBackground(context.getResources().getDrawable(R.drawable.add_medic));
            recordHolder.tv_add_delete.setText(ADD);
        }

        recordHolder.tv_add_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recordHolder.tv_add_delete.setFocusable(true);
                recordHolder.tv_add_delete.setFocusableInTouchMode(true);///add this line
                recordHolder.tv_add_delete.requestFocus();
                if (recordHolder.tv_add_delete.getText().equals(DELETE)) {
                    medicalRecordList.remove(position);
                    notifyDataSetChanged();
                } else {
//                    if (null != medicalRecord && TextUtils.isEmpty(medicalRecord.getMedicName())) {
//                        Toast.makeText(context, "please fill the previous medicine field", Toast.LENGTH_LONG).show();
//                    } else {
                        medicalRecordList.add(new MedicalRecord());
                        notifyDataSetChanged();
                 //   }
                }

            }
        });
        return view;
    }

    static class RecordHolder {
        EditText edt_medicine_name;
        ;
        Spinner sp_medication;
        Spinner sp_frequency;
        Spinner sp_dose_timing;
        Spinner sp_course;
        Spinner sp_dose;
        Button tv_add_delete;
        CardView cardview;

        RecordHolder() {
        }
    }


    public List<JsonMedicalMedicine> getJsonMedicineList() {
        ArrayList<JsonMedicalMedicine> jsonMedicalMedicines = new ArrayList<>();
        for (int i = 0; i < medicalRecordList.size(); i++) {
            MedicalRecord medicalRecord = medicalRecordList.get(i);
            JsonMedicalMedicine jsonMedicalMedicine = medicalRecord.getJsonMedicalMedicine();
            jsonMedicalMedicine.setName(medicalRecord.getMedicName());
            jsonMedicalMedicines.add(jsonMedicalMedicine);
        }
        return jsonMedicalMedicines;
    }
}
