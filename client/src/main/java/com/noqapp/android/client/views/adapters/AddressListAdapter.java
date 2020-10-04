package com.noqapp.android.client.views.adapters;


import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.ShowCustomDialog;
import com.noqapp.android.common.beans.JsonUserAddress;

import java.util.List;

public class AddressListAdapter extends ArrayAdapter<JsonUserAddress> {
    private List<JsonUserAddress> list;
    private LayoutInflater inflator;
    private Activity context;
    private UpdateAddress updateAddress;
    private String userAddressId;

    public AddressListAdapter(Activity context, List<JsonUserAddress> list, UpdateAddress updateAddress, String userAddressId) {
        super(context, R.layout.list_item_selected, list);
        this.list = list;
        inflator = context.getLayoutInflater();
        this.context = context;
        this.updateAddress = updateAddress;
        this.userAddressId = userAddressId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflator.inflate(R.layout.list_item_selected, null);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.title);
            holder.tv_primary = convertView.findViewById(R.id.tv_primary);
            holder.iv_delete = convertView.findViewById(R.id.iv_delete);
            convertView.setTag(holder);
            convertView.setTag(R.id.title, holder.title);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(list.get(position).getAddress());
        if (list.get(position).getId().equals(userAddressId)) {
            holder.tv_primary.setText("Primary");
            holder.tv_primary.setTextColor(Color.BLACK);
        } else {
            holder.tv_primary.setText("Set Primary");
            holder.tv_primary.setTextColor(Color.LTGRAY);
        }
        holder.tv_primary.setOnClickListener(v -> {
            if (list.get(position).getId().equals(userAddressId)) {
                // do nothing
            } else {
                if (null != updateAddress)
                    updateAddress.setPrimaryAddress(list.get(position));
            }
        });
        holder.iv_delete.setOnClickListener((View v) -> {
            ShowCustomDialog showDialog = new ShowCustomDialog(context, true);
            showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                @Override
                public void btnPositiveClick() {
                    if (null != updateAddress)
                        updateAddress.removeAddress(list.get(position));
                }

                @Override
                public void btnNegativeClick() {
                    //Do nothing
                }
            });
            showDialog.displayDialog("Delete Address", "Do you want to delete address from address list?");
        });

        return convertView;
    }

    static class ViewHolder {
        private TextView title;
        private TextView tv_primary;
        private ImageView iv_delete;
    }

    public interface UpdateAddress {
        void removeAddress(JsonUserAddress jsonUserAddress);

        void setPrimaryAddress(JsonUserAddress jsonUserAddress);
    }

}