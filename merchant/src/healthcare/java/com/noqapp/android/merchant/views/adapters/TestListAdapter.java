package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.interfaces.ListCommunication;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class TestListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> listItems;
    private String key;
    private ListCommunication listCommunication;

    public TestListAdapter(Context context, ArrayList<String> listItems, String key, ListCommunication listCommunication) {
        this.context = context;
        this.listItems = listItems;
        this.key = key;
        this.listCommunication = listCommunication;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final RecordHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (convertView == null) {
            recordHolder = new RecordHolder();
            convertView = layoutInflater.inflate(R.layout.list_item_tests, null);
            recordHolder.iv_delete = convertView.findViewById(R.id.iv_delete);
            recordHolder.tv_sequence = convertView.findViewById(R.id.tv_sequence);
            recordHolder.tv_test_name = convertView.findViewById(R.id.tv_test_name);
            recordHolder.iv_favourite = convertView.findViewById(R.id.iv_favourite);
            convertView.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) convertView.getTag();
        }
        recordHolder.tv_sequence.setText(""+(position+1)+"-");
        recordHolder.tv_test_name.setText(listItems.get(position));
       // recordHolder.iv_favourite.setBackgroundResource(listItems.get(position).isFavourite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        recordHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
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
                tvtitle.setText("Delete Item");
                tv_msg.setText("Do you want to delete it from test list.");
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
                        Toast.makeText(context, "Deleted from test list", Toast.LENGTH_LONG).show();
                        listItems.remove(position);
                        listCommunication.updateList(listItems,key);
                        notifyDataSetChanged();
                        mAlertDialog.dismiss();
                    }
                });
                mAlertDialog.show();

            }
        });
        return convertView;
    }

    static class RecordHolder {
        ImageView iv_favourite;
        TextView tv_test_name;
        TextView tv_sequence;
        ImageView iv_delete;

        RecordHolder() {
        }
    }

}


