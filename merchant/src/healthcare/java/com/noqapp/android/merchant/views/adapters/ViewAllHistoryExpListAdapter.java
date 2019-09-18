package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.common.model.types.medical.MedicalRecordFieldFilterEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.customviews.FixedHeightListView;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by chandra on 3/28/18.
 */
public class ViewAllHistoryExpListAdapter extends BaseAdapter {

    private Context context;
    private List<Date> listDataHeader; // header titles
    // child data in format of header title, child title
    private Map<Date, List<JsonMedicalRecordList>> listDataChild;
    private MedicalRecordFieldFilterEnum medicalRecordFieldFilterEnum;

    public ViewAllHistoryExpListAdapter(Context context, List<Date> listDataHeader,
                                        Map<Date, List<JsonMedicalRecordList>> listChildData,
                                        MedicalRecordFieldFilterEnum medicalRecordFieldFilterEnum) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
        this.medicalRecordFieldFilterEnum = medicalRecordFieldFilterEnum;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ChildViewHolder childViewHolder;
        final JsonMedicalRecordList childData = listDataChild.get(listDataHeader.get(position)).get(0);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_history, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.fh_list_view = convertView.findViewById(R.id.fh_list_view);
            childViewHolder.tv_title = convertView.findViewById(R.id.tv_title);
            convertView.setTag(R.layout.list_item_history, childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag(R.layout.list_item_history);
        }
        Date headerTitle = listDataHeader.get(position);
        childViewHolder.tv_title.setText(CommonHelper.SDF_DOB_FROM_UI.format(headerTitle));
        CaseHistoryAdapter caseHistoryAdapter = new CaseHistoryAdapter(childData.getJsonMedicalRecords(), context,medicalRecordFieldFilterEnum);
        childViewHolder.fh_list_view.setAdapter(caseHistoryAdapter);
        caseHistoryAdapter.notifyDataSetChanged();
        return convertView;
    }

    public int getCount() {
        return this.listDataHeader.size();
    }

    public Object getItem(int n) {
        return null;
    }

    public long getItemId(int n) {
        return 0;
    }


    public final class ChildViewHolder {
        private FixedHeightListView fh_list_view;
        private TextView tv_title;
    }
}