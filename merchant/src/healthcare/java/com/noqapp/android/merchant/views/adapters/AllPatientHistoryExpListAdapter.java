package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by chandra on 3/28/18.
 */
public class AllPatientHistoryExpListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Date> listDataHeader;
    private Map<Date, List<JsonQueuePersonList>> listDataChild;
    private JsonTopic jt;

    public AllPatientHistoryExpListAdapter(Context context, List<Date> listDataHeader,
                                           Map<Date, List<JsonQueuePersonList>> listChildData, JsonTopic jt) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
        this.jt = jt;
    }

    public void resetData() {
        listDataHeader.clear();
        listDataChild.clear();
        notifyDataSetChanged();
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
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_child, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.rv = convertView.findViewById(R.id.rv);
            convertView.setTag(R.layout.list_item_child, childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag(R.layout.list_item_child);
        }
        childViewHolder.rv.setHasFixedSize(true);
        int columnCount;
        if (LaunchActivity.isTablet) {
            columnCount = 5;
        } else {
            columnCount = 2;
        }
        childViewHolder.rv.setLayoutManager(new GridLayoutManager(context, columnCount));
        childViewHolder.rv.setItemAnimator(new DefaultItemAnimator());
        AllPatientHistoryAdapter allPatientHistoryAdapter = new AllPatientHistoryAdapter(childData.getQueuedPeople(), context, jt);
        childViewHolder.rv.setAdapter(allPatientHistoryAdapter);
        allPatientHistoryAdapter.notifyDataSetChanged();
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Date headerTitle = (Date) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_header, parent, false);
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