package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.common.model.types.medical.MedicalRecordFieldFilterEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.customviews.FixedHeightListView;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WorkHistoryAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Date> listDataHeader; // header titles
    private LinkedHashMap<Date, List<JsonMedicalRecordList>> listDataChild;
    private MedicalRecordFieldFilterEnum medicalRecordFieldFilterEnum;

    public WorkHistoryAdapter(Context context, List<Date> listDataHeader,
                              LinkedHashMap<Date, List<JsonMedicalRecordList>> listChildData,
                              MedicalRecordFieldFilterEnum medicalRecordFieldFilterEnum) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
        this.medicalRecordFieldFilterEnum = medicalRecordFieldFilterEnum;
    }

    public void resetData(){

        listDataHeader.clear();
        listDataChild.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (null != listDataHeader ? listDataHeader.size() : 0);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder mainHolder = (MyViewHolder) viewHolder;
        final JsonMedicalRecordList childData = listDataChild.get(listDataHeader.get(position)).get(0);
        Date headerTitle = listDataHeader.get(position);
        mainHolder.tv_title.setText(CommonHelper.SDF_DOB_FROM_UI.format(headerTitle));
        CaseHistoryAdapter caseHistoryAdapter = new CaseHistoryAdapter(childData.getJsonMedicalRecords(), context, medicalRecordFieldFilterEnum);
        mainHolder.fh_list_view.setAdapter(caseHistoryAdapter);
        caseHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.list_item_history, viewGroup, false);
        return new MyViewHolder(mainGroup);

    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private FixedHeightListView fh_list_view;
        private TextView tv_title;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.fh_list_view = itemView.findViewById(R.id.fh_list_view);
            this.tv_title = itemView.findViewById(R.id.tv_title);
        }
    }

}
