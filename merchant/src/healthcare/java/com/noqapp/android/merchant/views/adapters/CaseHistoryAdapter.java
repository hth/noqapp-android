package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.medical.MedicalRecordFieldFilterEnum;
import com.noqapp.android.merchant.R;

import java.util.List;

public class CaseHistoryAdapter extends BaseAdapter {
    private final Context context;
    private List<JsonMedicalRecord> dataSet;
    private MedicalRecordFieldFilterEnum medicalRecordFieldFilterEnum;

    public CaseHistoryAdapter(List<JsonMedicalRecord> data, Context context,
                              MedicalRecordFieldFilterEnum medicalRecordFieldFilterEnum) {
        this.dataSet = data;
        this.context = context;
        this.medicalRecordFieldFilterEnum = medicalRecordFieldFilterEnum;
    }


    public View getView(int position, View view, ViewGroup viewGroup) {
        MyViewHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            recordHolder = new MyViewHolder();
            view = layoutInflater.inflate(R.layout.list_item_case_history, viewGroup, false);
            recordHolder.tv_patient_name = view.findViewById(R.id.tv_patient_name);
            recordHolder.tv_patient_details = view.findViewById(R.id.tv_patient_details);
            view.setTag(recordHolder);
        } else {
            recordHolder = (MyViewHolder) view.getTag();
        }
        recordHolder.tv_patient_name.setText(dataSet.get(position).getBusinessName());
        switch (medicalRecordFieldFilterEnum) {
            case ND:
                recordHolder.tv_patient_details.setText(Html.fromHtml(parseWorkDone(dataSet.get(position).getNoteToDiagnoser())));
                break;
            case NP:
                recordHolder.tv_patient_details.setText(Html.fromHtml(parseWorkDone(dataSet.get(position).getNoteForPatient())));
                break;
            case CC:
                recordHolder.tv_patient_details.setText(checkForNullOrEmpty(dataSet.get(position).getChiefComplain()));
                break;
            case DI:
                recordHolder.tv_patient_details.setText(checkForNullOrEmpty(dataSet.get(position).getDiagnosis()));
                break;
            case PP:
                recordHolder.tv_patient_details.setText(checkForNullOrEmpty(dataSet.get(position).getPlanToPatient()));
                break;
        }


        return view;
    }

    public int getCount() {
        return this.dataSet.size();
    }

    public Object getItem(int n) {
        return null;
    }

    public long getItemId(int n) {
        return 0;
    }


    public static class MyViewHolder {
        private TextView tv_patient_name;
        private TextView tv_patient_details;
        private CardView card_view;
    }

    private String checkForNullOrEmpty(String str) {
        return TextUtils.isEmpty(str) ? "N/A" : str;
    }

    private String parseWorkDone(String str) {
        if (TextUtils.isEmpty(str))
            return "N/A";
        else {
            try {
                String[] temp = str.split("\\|", -1);
                if (temp.length > 0) {
                    String output = "";
                    for (String act : temp) {
                        if (act.contains(":")) {
                            String[] strArray = act.split(":", -1);
                            String toothNum = strArray[0].trim();
                            String procedure = strArray[1];
                            String summary = strArray.length >= 3 ? strArray[2] : "";
                            if (strArray.length > 3) {
                                String status = strArray[3].trim();
                                String unit = strArray[4];
                                String period = strArray[5];
                                output += "<b> Tooth Number:  " + toothNum + "</b><b> Procedure: </b> " + procedure + "<br> <b> Summary: </b> " + summary + " <br>" +
                                        "<b> Unit: </b> " + unit + "   <b> Period: </b> " + period + "   " + "<b> Status: </b> " + status + "<br><br>";
                            }
                            if (strArray.length == 2)
                                output += "<b> Tooth Number: </b> " + toothNum + "<br> <b> Procedure: </b> " + procedure + "<br><br>";
                        }
                    }
                    if(output.endsWith("<br><br>")){
                        return output.substring(0, output.length()-8);
                    }else {
                        return output;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "N/A";
            }
            return "N/A";
        }
    }
}
