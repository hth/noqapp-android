package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.noqapp.android.client.R;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.utils.CommonHelper;

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

    public View getView(int position, View view, ViewGroup parent) {
        RecordHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            recordHolder = new RecordHolder();
            view = layoutInflater.inflate(R.layout.listitem_medical_history, parent, false);

            recordHolder.tv_diagnosed_by = view.findViewById(R.id.tv_diagnosed_by);
            recordHolder.tv_business_name = view.findViewById(R.id.tv_business_name);
            recordHolder.tv_business_category_name = view.findViewById(R.id.tv_business_category_name);
            recordHolder.tv_complaints = view.findViewById(R.id.tv_complaints);
            recordHolder.tv_create = view.findViewById(R.id.tv_create);
            recordHolder.tv_no_of_time_access = view.findViewById(R.id.tv_no_of_time_access);
            recordHolder.iv_profile = view.findViewById(R.id.iv_profile);
            recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        JsonMedicalRecord jmr = jsonMedicalRecordList.get(position);
        if (jmr.getBusinessType() == BusinessTypeEnum.DO) {
            recordHolder.tv_diagnosed_by.setText("Dr. " + jmr.getDiagnosedByDisplayName());
        } else {
            recordHolder.tv_diagnosed_by.setText(jmr.getDiagnosedByDisplayName());
        }
        recordHolder.tv_diagnosed_by.setVisibility(TextUtils.isEmpty(jmr.getDiagnosedByDisplayName()) ? View.GONE : View.VISIBLE);
        recordHolder.tv_business_name.setText(jmr.getBusinessName());
        recordHolder.tv_business_category_name.setText(jmr.getBizCategoryName());
        recordHolder.tv_complaints.setText(jmr.getChiefComplain());
        try {
            recordHolder.tv_create.setText(CommonHelper.SDF_YYYY_MM_DD_HH_MM_A.format(CommonHelper.SDF_ISO8601_FMT.parse(jmr.getCreateDate())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        recordHolder.tv_no_of_time_access.setText("# of times record viewed: " + jmr.getRecordAccess().size());
        if (BusinessTypeEnum.DO == jmr.getBusinessType()) {
            recordHolder.iv_profile.setImageResource(R.drawable.doctor);
        } else {
            recordHolder.iv_profile.setImageResource(R.drawable.lab);
        }
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
        ImageView iv_profile;

        RecordHolder() {
        }
    }
}
