package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.common.pojos.MenuDrawer;

import java.util.List;


public class DrawerExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<MenuDrawer> listDataHeader;

    public DrawerExpandableListAdapter(Context context, List<MenuDrawer> listDataHeader) {
        this.context = context;
        this.listDataHeader = listDataHeader;
    }

    @Override
    public MenuDrawer getChild(int groupPosition, int childPosititon) {
        return this.listDataHeader.get(groupPosition).getChildList()
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final MenuDrawer child = getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_child, null);
        }
        TextView tv_child_title = convertView
                .findViewById(R.id.tv_child_title);
        tv_child_title.setText(child.getTitle());
        ImageView iv_icon = convertView.findViewById(R.id.iv_icon);
        iv_icon.setImageResource(child.getIcon());
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (this.listDataHeader.get(groupPosition) == null)
            return 0;
        else
            return this.listDataHeader.get(groupPosition).getChildList()
                    .size();
    }

    @Override
    public MenuDrawer getGroup(int groupPosition) {
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
        MenuDrawer headerItem = getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_header, null);
        }
        TextView tv_header_title = convertView.findViewById(R.id.tv_header_title);
        if (headerItem.isHasChildren()) {
            tv_header_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, isExpanded ?
                    R.drawable.arrow_up_black : R.drawable.arrow_down_black, 0);
        } else {
            tv_header_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        ImageView iv_icon = convertView.findViewById(R.id.iv_icon);
        iv_icon.setImageResource(headerItem.getIcon());
        tv_header_title.setText(headerItem.getTitle());

        if(headerItem.getTitle().equals(context.getString(R.string.merchant_account))){
            convertView.setBackgroundColor(Color.GRAY);
        }else{
            convertView.setBackgroundColor(Color.WHITE);
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