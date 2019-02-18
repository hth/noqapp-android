package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.common.beans.medical.JsonMedicalRadiologyList;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.medical.LabCategoryEnum;
import com.noqapp.android.common.model.types.medical.PharmacyCategoryEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.SliderActivity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
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

    public View getView(final int position, View view, ViewGroup viewGroup) {
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
            recordHolder.tv_attachment = view.findViewById(R.id.tv_attachment);

            recordHolder.ll_xray = view.findViewById(R.id.ll_xray);
            recordHolder.ll_sono = view.findViewById(R.id.ll_sono);
            recordHolder.ll_scan = view.findViewById(R.id.ll_scan);
            recordHolder.ll_pathology = view.findViewById(R.id.ll_pathology);
            recordHolder.ll_spec = view.findViewById(R.id.ll_spec);
            recordHolder.ll_mri = view.findViewById(R.id.ll_mri);

            recordHolder.tv_attachment_xray = view.findViewById(R.id.tv_attachment_xray);
            recordHolder.tv_attachment_sono = view.findViewById(R.id.tv_attachment_sono);
            recordHolder.tv_attachment_scan = view.findViewById(R.id.tv_attachment_scan);
            recordHolder.tv_attachment_pathology = view.findViewById(R.id.tv_attachment_pathology);
            recordHolder.tv_attachment_spec = view.findViewById(R.id.tv_attachment_spec);
            recordHolder.tv_attachment_mri = view.findViewById(R.id.tv_attachment_mri);

            recordHolder.tv_observation_xray_value = view.findViewById(R.id.tv_observation_xray_value);
            recordHolder.tv_observation_sono_value = view.findViewById(R.id.tv_observation_sono_value);
            recordHolder.tv_observation_scan_value = view.findViewById(R.id.tv_observation_scan_value);
            recordHolder.tv_observation_pathology_value = view.findViewById(R.id.tv_observation_pathology_value);
            recordHolder.tv_observation_spec_value = view.findViewById(R.id.tv_observation_spec_value);
            recordHolder.tv_observation_mri_value = view.findViewById(R.id.tv_observation_mri_value);


            recordHolder.view_seperator = view.findViewById(R.id.view_seperator);
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
        recordHolder.tv_business_category_name.setText("(" + jsonMedicalRecord.getBizCategoryName() + ")");
        recordHolder.tv_complaints.setText(parseSymptoms(jsonMedicalRecord.getChiefComplain()));
        recordHolder.tv_create.setText("Visited: " + jsonMedicalRecord.getCreateDate());
        recordHolder.tv_examination.setText(jsonMedicalRecord.getExamination());
        recordHolder.tv_medicine.setText(getMedicineFormList(jsonMedicalRecord.getMedicalMedicines()));
        if (null != jsonMedicalRecord.getImages() && jsonMedicalRecord.getImages().size() > 0) {
            recordHolder.tv_attachment.setText("Attachment Available : " + jsonMedicalRecord.getImages().size());
            recordHolder.tv_attachment.setVisibility(View.VISIBLE);
            recordHolder.view_seperator.setVisibility(View.VISIBLE);
            recordHolder.tv_attachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SliderActivity.class);
                    intent.putExtra("imageurls", (ArrayList<String>) jsonMedicalRecord.getImages());
                    intent.putExtra("isDocument", true);
                    intent.putExtra("recordReferenceId", jsonMedicalRecord.getRecordReferenceId());
                    context.startActivity(intent);
                }
            });
        } else {
            recordHolder.tv_attachment.setText("No Attachment Available");
            recordHolder.tv_attachment.setVisibility(View.GONE);
            recordHolder.view_seperator.setVisibility(View.GONE);
        }
        if (null != jsonMedicalRecord.getMedicalPathologiesLists() && jsonMedicalRecord.getMedicalPathologiesLists().size() > 0
                && jsonMedicalRecord.getMedicalPathologiesLists().get(0).getImages() != null &&
                jsonMedicalRecord.getMedicalPathologiesLists().get(0).getImages().size() > 0) {
            recordHolder.tv_attachment_pathology.setText("Attachment Available : " + jsonMedicalRecord.getMedicalPathologiesLists().get(0).getImages().size());
            recordHolder.tv_observation_pathology_value.setText(jsonMedicalRecord.getMedicalPathologiesLists().get(0).getObservation());
            recordHolder.ll_pathology.setVisibility(View.VISIBLE);
        } else {
            recordHolder.tv_attachment_pathology.setText("No Attachment Available");
            recordHolder.tv_observation_pathology_value.setText("N/A");
            recordHolder.ll_pathology.setVisibility(View.GONE);
        }
        if (null != jsonMedicalRecord.getMedicalRadiologyLists() && jsonMedicalRecord.getMedicalRadiologyLists().size() > 0) {
            for (int i = 0; i < jsonMedicalRecord.getMedicalRadiologyLists().size(); i++) {
                final JsonMedicalRadiologyList jsonMedicalRadiologyList = jsonMedicalRecord.getMedicalRadiologyLists().get(i);
                LabCategoryEnum labCategory = jsonMedicalRadiologyList.getLabCategory();
                String value, observation;
                boolean showLayout;
                if (null != jsonMedicalRadiologyList.getImages() && jsonMedicalRadiologyList.getImages().size() > 0) {
                    value = "Attachment Available : " + jsonMedicalRadiologyList.getImages().size();
                    showLayout = true;
                } else {
                    value = "No Attachment Available";
                    showLayout = false;
                }
                if (TextUtils.isEmpty(jsonMedicalRadiologyList.getObservation())) {
                    observation = "N/A";
                } else {
                    observation = jsonMedicalRadiologyList.getObservation();
                }
                switch (labCategory) {
                    case SPEC:
                        recordHolder.tv_attachment_spec.setText(value);
                        recordHolder.tv_attachment_spec.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, SliderActivity.class);
                                intent.putExtra("imageurls", (ArrayList<String>) jsonMedicalRadiologyList.getImages());
                                intent.putExtra("isDocument", true);
                                intent.putExtra("recordReferenceId", jsonMedicalRadiologyList.getRecordReferenceId());
                                context.startActivity(intent);
                            }
                        });
                        recordHolder.tv_observation_spec_value.setText(observation);
                        recordHolder.ll_spec.setVisibility(showLayout ? View.VISIBLE : View.GONE);
                        break;
                    case SONO:
                        recordHolder.tv_attachment_sono.setText(value);
                        recordHolder.tv_attachment_sono.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, SliderActivity.class);
                                intent.putExtra("imageurls", (ArrayList<String>) jsonMedicalRadiologyList.getImages());
                                intent.putExtra("isDocument", true);
                                intent.putExtra("recordReferenceId", jsonMedicalRadiologyList.getRecordReferenceId());
                                context.startActivity(intent);
                            }
                        });
                        recordHolder.tv_observation_sono_value.setText(observation);
                        recordHolder.ll_sono.setVisibility(showLayout ? View.VISIBLE : View.GONE);
                        break;
                    case XRAY:
                        recordHolder.tv_attachment_xray.setText(value);
                        recordHolder.tv_attachment_xray.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, SliderActivity.class);
                                intent.putExtra("imageurls", (ArrayList<String>) jsonMedicalRadiologyList.getImages());
                                intent.putExtra("isDocument", true);
                                intent.putExtra("recordReferenceId", jsonMedicalRadiologyList.getRecordReferenceId());
                                context.startActivity(intent);
                            }
                        });
                        recordHolder.tv_observation_xray_value.setText(observation);
                        recordHolder.ll_xray.setVisibility(showLayout ? View.VISIBLE : View.GONE);
                        break;
                    case PATH:
                        recordHolder.tv_attachment_pathology.setText(value);
                        recordHolder.tv_attachment_pathology.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, SliderActivity.class);
                                intent.putExtra("imageurls", (ArrayList<String>) jsonMedicalRadiologyList.getImages());
                                intent.putExtra("isDocument", true);
                                intent.putExtra("recordReferenceId", jsonMedicalRadiologyList.getRecordReferenceId());
                                context.startActivity(intent);
                            }
                        });
                        recordHolder.tv_observation_pathology_value.setText(observation);
                        recordHolder.ll_pathology.setVisibility(showLayout ? View.VISIBLE : View.GONE);
                        break;
                    case MRI:
                        recordHolder.tv_attachment_mri.setText(value);
                        recordHolder.tv_attachment_mri.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, SliderActivity.class);
                                intent.putExtra("imageurls", (ArrayList<String>) jsonMedicalRadiologyList.getImages());
                                intent.putExtra("isDocument", true);
                                intent.putExtra("recordReferenceId", jsonMedicalRadiologyList.getRecordReferenceId());
                                context.startActivity(intent);
                            }
                        });
                        recordHolder.tv_observation_mri_value.setText(observation);
                        recordHolder.ll_mri.setVisibility(showLayout ? View.VISIBLE : View.GONE);
                        break;
                    case SCAN:
                        recordHolder.tv_attachment_scan.setText(value);
                        recordHolder.tv_attachment_scan.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, SliderActivity.class);
                                intent.putExtra("imageurls", (ArrayList<String>) jsonMedicalRadiologyList.getImages());
                                intent.putExtra("isDocument", true);
                                intent.putExtra("recordReferenceId", jsonMedicalRadiologyList.getRecordReferenceId());
                                context.startActivity(intent);
                            }
                        });
                        recordHolder.tv_observation_scan_value.setText(observation);
                        recordHolder.ll_scan.setVisibility(showLayout ? View.VISIBLE : View.GONE);
                        break;
                    default:
                }
            }
        }else{
            recordHolder.ll_scan.setVisibility(View.GONE);
            recordHolder.ll_spec.setVisibility(View.GONE);
            recordHolder.ll_xray.setVisibility(View.GONE);
            recordHolder.ll_pathology.setVisibility(View.GONE);
            recordHolder.ll_mri.setVisibility(View.GONE);
            recordHolder.ll_sono.setVisibility(View.GONE);
        }
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
        TextView tv_attachment;

        TextView tv_attachment_xray;
        TextView tv_attachment_sono;
        TextView tv_attachment_scan;
        TextView tv_attachment_pathology;
        TextView tv_attachment_spec;
        TextView tv_attachment_mri;

        TextView tv_observation_xray_value;
        TextView tv_observation_sono_value;
        TextView tv_observation_scan_value;
        TextView tv_observation_pathology_value;
        TextView tv_observation_spec_value;
        TextView tv_observation_mri_value;

        LinearLayout ll_xray;
        LinearLayout ll_sono;
        LinearLayout ll_scan;
        LinearLayout ll_pathology;
        LinearLayout ll_spec;
        LinearLayout ll_mri;

        View view_seperator;
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

    public String parseSymptoms(String str) {
        String data = "";
        try {
            String[] temp = str.split("\\r?\\n");
            if (null != temp && temp.length > 0) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (data.endsWith(", "))
            data = data.substring(0, data.length() - 2);
        return data;
    }
}
