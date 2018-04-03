package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonStoreCategory;
import com.noqapp.android.client.views.toremove.ChildData;

import java.util.HashMap;
import java.util.List;

/**
 * Created by chandra on 3/28/18.
 */

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;

    private List<JsonStoreCategory> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<ChildData>> listDataChild;

    public CustomExpandableListAdapter(Context context, List<JsonStoreCategory> listDataHeader,
                                       HashMap<String, List<ChildData>> listDataChild) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition).getCategoryId())
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildViewHolder childViewHolder;
        final ChildData childData = (ChildData) getChild(groupPosition, childPosition);


        if (convertView == null) {

            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_filter, null);

            childViewHolder = new ChildViewHolder();

            childViewHolder.tv_child_title = (TextView) convertView
                    .findViewById(R.id.tv_child_title);
            childViewHolder.tv_value = (TextView) convertView
                    .findViewById(R.id.tv_value);
            childViewHolder.btn_increase = (Button) convertView.findViewById(R.id.btn_increase);
            childViewHolder.btn_decrease = (Button) convertView.findViewById(R.id.btn_decrease);
            convertView.setTag(R.layout.list_item_filter, childViewHolder);

        } else {

            childViewHolder = (ChildViewHolder) convertView
                    .getTag(R.layout.list_item_filter);
        }

        childViewHolder.tv_child_title.setText(childData.getJsonStoreProduct().getProductName());
        childViewHolder.tv_value.setText(childData.getChildInput());
        childViewHolder.btn_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = childViewHolder.tv_value.getText().toString();
                int number = 1 + (TextUtils.isEmpty(val) ? 0 : Integer.parseInt(val));
                childViewHolder.tv_value.setText("" + number);
                listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                        .get(childPosition).setChildInput("" + number);
                notifyDataSetChanged();
            }
        });
        childViewHolder.btn_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = childViewHolder.tv_value.getText().toString();
                int number = (TextUtils.isEmpty(val) ? 0 : (val.equals("0") ? 0 : Integer.parseInt(val) - 1));
                childViewHolder.tv_value.setText("" + number);
                listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                        .get(childPosition).setChildInput("" + number);
                notifyDataSetChanged();
            }
        });


        return convertView;

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition).getCategoryId())
                .size();
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
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = ((JsonStoreCategory) getGroup(groupPosition)).getCategoryName();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_filter, null);
        }

        TextView tv_list_header = (TextView) convertView
                .findViewById(R.id.tv_list_header);
        tv_list_header.setTypeface(null, Typeface.BOLD);
        tv_list_header.setText(headerTitle);
        ImageView ivGroupIndicator = (ImageView) convertView.findViewById(R.id.ivGroupIndicator);
        ivGroupIndicator.setSelected(isExpanded);
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


    public final class ChildViewHolder {

        TextView tv_child_title;
        TextView tv_value;
        Button btn_decrease;
        Button btn_increase;
    }
}