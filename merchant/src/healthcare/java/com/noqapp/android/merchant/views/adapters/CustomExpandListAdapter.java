package com.noqapp.android.merchant.views.adapters;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.DataObj;

public class CustomExpandListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<DataObj>> listDataChild;
    private RemoveChild removeChild;

    public CustomExpandListAdapter(Context context, List<String> listDataHeader,
                                   HashMap<String, List<DataObj>> listChildData,RemoveChild removeChild) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
        this.removeChild = removeChild;
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
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final DataObj childText = (DataObj) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        TextView tv_child = convertView
                .findViewById(R.id.tv_child);
        ImageView iv_delete = convertView.findViewById(R.id.iv_delete);
        tv_child.setText(childText.getShortName());
        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                builder.setTitle(null);
                View customDialogView = inflater.inflate(R.layout.dialog_logout, null, false);
                builder.setView(customDialogView);
                final AlertDialog mAlertDialog = builder.create();
                mAlertDialog.setCanceledOnTouchOutside(false);
                TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
                TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
                tvtitle.setText("Delete from list");
                tv_msg.setText("Do you want to delete it from selected list?");
                Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
                Button btn_no = customDialogView.findViewById(R.id.btn_no);
                btn_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialog.dismiss();
                    }
                });
                btn_yes.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(null != removeChild)
                            removeChild.removeChildAtPos(childPosition, childText);
                        mAlertDialog.dismiss();
                    }
                });
                mAlertDialog.show();

            }
        });
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
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group_item, null);
        }

        TextView lblListHeader = convertView
                .findViewById(R.id.lblListHeader);
        TextView tv_child_count = convertView
                .findViewById(R.id.tv_child_count);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        tv_child_count.setText(" ("+getChildrenCount(groupPosition)+")");

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

    public interface RemoveChild{
        void removeChildAtPos(int pos, DataObj dataObj);
    }
}
