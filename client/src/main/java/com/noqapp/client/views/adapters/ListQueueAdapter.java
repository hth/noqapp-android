package com.noqapp.client.views.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.client.R;
import com.noqapp.client.helper.PhoneFormatterUtil;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;

import java.util.HashMap;
import java.util.List;

public class ListQueueAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<JsonTokenAndQueue>> listDataChild;

    public ListQueueAdapter(
            Context context,
            List<String> listDataHeader,
            HashMap<String, List<JsonTokenAndQueue>> listChildData
    ) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        JsonTokenAndQueue queue = (JsonTokenAndQueue) getChild(groupPosition, childPosition);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        if (null == rowLayout) {
//
//            if (groupPosition == 0) {
//                rowLayout = (LinearLayout) inflater.inflate(R.layout.listitem_currentqueue, null,false);
//            } else {
//                rowLayout = (LinearLayout) inflater.inflate(R.layout.listitem_historyqueue, null,false);
//            }
//        }else {
//            rowLayout = (LinearLayout) convertView;
//        }


        switch (groupPosition) {
            case 0:
                convertView = inflater.inflate(R.layout.listitem_currentqueue, null);
                TextView txtnumber = (TextView) convertView.findViewById(R.id.txtNumber);
                TextView txtStoreName = (TextView) convertView.findViewById(R.id.txtStoreName);
                TextView txtStorePhoneNumber = (TextView) convertView.findViewById(R.id.txtStorePhoneNo);
                TextView txtToken = (TextView) convertView.findViewById(R.id.txtToken);
                txtnumber.setText("#" + String.valueOf(childPosition + 1));
                txtStoreName.setText(queue.getBusinessName());
                // show only for current queue not for history
                txtStorePhoneNumber.setText(PhoneFormatterUtil.formatNumber(queue.getCountryShortName(), queue.getStorePhone()));
                txtToken.setText(String.valueOf(queue.getToken()));
                break;

            case 1:
                convertView = inflater.inflate(R.layout.listitem_historyqueue, null);
                TextView txtnumber1 = (TextView) convertView.findViewById(R.id.txtNumber);
                TextView txtStoreName1 = (TextView) convertView.findViewById(R.id.txtStoreName);
                TextView txtStorePhoneNumber1 = (TextView) convertView.findViewById(R.id.txtStorePhoneNo);
                TextView txtToken1 = (TextView) convertView.findViewById(R.id.txtToken);
                txtnumber1.setText("#" + String.valueOf(childPosition + 1));
                txtStoreName1.setText(queue.getBusinessName());
                // show only for current queue not for history
                txtStorePhoneNumber1.setText(queue.getStorePhone());//PhoneFormatterUtil.formatNumber(queue.getCountryShortName(), queue.getStorePhone()));
                txtToken1.setText(String.valueOf(queue.getToken()));
                break;
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        if (rowLayout == null) {
//
//
////            if (groupPosition == 0) {
////                rowLayout = (RelativeLayout) inflater.inflate(R.layout.list_group_blank, null,false);
////            } else {
//                rowLayout = (RelativeLayout) inflater.inflate(R.layout.list_group, null,false);
//          //  }
//        }else{
//            rowLayout=(RelativeLayout)convertView;
//        }

        switch (groupPosition) {


            case 0:
                convertView = inflater.inflate(R.layout.list_group_blank, null);
                break;

            case 1:
                convertView = inflater.inflate(R.layout.list_group, null);
                TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
                ImageView ivGroupIndicator = (ImageView) convertView.findViewById(R.id.ivGroupIndicator);
                lblListHeader.setTypeface(null, Typeface.BOLD);
                lblListHeader.setText(headerTitle);
                ivGroupIndicator.setSelected(isExpanded);
                break;
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
