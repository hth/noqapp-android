package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chandra on 3/28/18.
 */

public class ViewAllExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Date> listDataHeader; // header titles
    // child data in format of header title, child title
    private Map<Date, List<JsonQueuePersonList>> listDataChild;

    public ViewAllExpandableListAdapter(Context context, List<Date> listDataHeader,
                                        Map<Date, List<JsonQueuePersonList>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
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
        final JsonQueuePersonList childData = (JsonQueuePersonList) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_child, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.rv = convertView.findViewById(R.id.rv);
            convertView.setTag(R.layout.list_item_child, childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView
                    .getTag(R.layout.list_item_child);
        }
        childViewHolder.rv.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        childViewHolder.rv.setLayoutManager(new GridLayoutManager(context, 7));
        childViewHolder.rv.setItemAnimator(new DefaultItemAnimator());
        ViewAllPeopleInQAdapter currentActivityAdapter = new ViewAllPeopleInQAdapter(childData.getQueuedPeople(), context, null);
        childViewHolder.rv.setAdapter(currentActivityAdapter);
        currentActivityAdapter.notifyDataSetChanged();
        return convertView;

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
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
        Date headerTitle = (Date) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_header, null);
        }
        TextView tv_date = convertView.findViewById(R.id.tv_date);
        TextView tv_count = convertView.findViewById(R.id.tv_count);
        tv_date.setText(CommonHelper.SDF_DOB_FROM_UI.format(headerTitle));
        tv_count.setText(String.valueOf(listDataChild.get(listDataHeader.get(groupPosition)).get(0).getQueuedPeople().size()));
        ImageView ivGroupIndicator = convertView.findViewById(R.id.ivGroupIndicator);
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
        RecyclerView rv;
    }
}