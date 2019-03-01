package com.noqapp.android.merchant.views.adapters;


import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonMasterLab;

import android.app.Activity;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TestListAdapter extends BaseAdapter {

    private List<JsonMasterLab> list;
    private LayoutInflater inflator;
    private Activity context;
    private FlagListItem flagListItem;

    public TestListAdapter(Activity context, List<JsonMasterLab> list, FlagListItem flagListItem) {
        this.list = list;
        inflator = context.getLayoutInflater();
        this.context = context;
        this.flagListItem = flagListItem;
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int n) {
        return null;
    }

    public long getItemId(int n) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflator.inflate(R.layout.list_item_selected, null);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.title);
            holder.iv_delete = convertView.findViewById(R.id.iv_delete);
            convertView.setTag(holder);
            convertView.setTag(R.id.title, holder.title);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.iv_delete.setBackground(ContextCompat.getDrawable(context,R.drawable.icon_flag));
        holder.title.setText(list.get(position).getProductShortName());
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
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
                tvtitle.setText("Flag as incorrect");
                tv_msg.setText("Flag this data only when there is mistake in spelling, duplicate data");
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
                        if (null != flagListItem)
                            flagListItem.flagItem(position);
                        mAlertDialog.dismiss();
                    }
                });
                mAlertDialog.show();

            }
        });

        return convertView;
    }

    static class ViewHolder {
        private TextView title;
        private ImageView iv_delete;
    }

    public interface FlagListItem {
        void flagItem(int pos);
    }

}