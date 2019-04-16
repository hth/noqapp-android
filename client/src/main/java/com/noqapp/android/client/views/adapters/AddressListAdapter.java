package com.noqapp.android.client.views.adapters;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.common.beans.JsonUserAddress;

import java.util.List;

public class AddressListAdapter extends ArrayAdapter<JsonUserAddress> {
    private List<JsonUserAddress> list;
    private LayoutInflater inflator;
    private Activity context;
    private RemoveListItem removeListItem;

    public AddressListAdapter(Activity context, List<JsonUserAddress> list, RemoveListItem removeListItem) {
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

        holder.title.setText(list.get(position).getAddress());
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ShowCustomDialog showDialog = new ShowCustomDialog(context);
//                showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
//                    @Override
//                    public void btnPositiveClick() {
//                        if(null != removeListItem)
//                            removeListItem.removeItem(position);
//                    }
//                    @Override
//                    public void btnNegativeClick() {
//                        //Do nothing
//                    }
//                });
//                showDialog.displayDialog("Delete from list", "Do you want to delete it from selected list?");
            }
        });

        return convertView;
    }

    static class ViewHolder {
        private TextView title;
        private ImageView iv_delete;
    }

    public interface RemoveListItem {
        void removeItem(int pos);
    }

}