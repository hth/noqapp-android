package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.DataObj;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SelectItemListAdapter extends ArrayAdapter<DataObj> {
    private List<DataObj> list;
    private LayoutInflater inflator;
    private Activity context;
    private RemoveListItem removeListItem;

    public SelectItemListAdapter(Activity context, List<DataObj> list,RemoveListItem removeListItem) {
        super(context, R.layout.list_item_selected, list);
        this.list = list;
        inflator = context.getLayoutInflater();
        this.context = context;
        this.removeListItem = removeListItem;
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

        holder.title.setText(list.get(position).getShortName());
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
                        if(null != removeListItem)
                            removeListItem.removeItem(position);
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

    public interface RemoveListItem{
        void removeItem(int pos);
    }

}