package com.noqapp.android.merchant.views.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.views.pojos.DataObj;

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
        holder.iv_delete.setOnClickListener(v -> {
            ShowCustomDialog showDialog = new ShowCustomDialog(context);
            showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                @Override
                public void btnPositiveClick() {
                    if(null != removeListItem)
                        removeListItem.removeItem(position);
                }
                @Override
                public void btnNegativeClick() {
                    //Do nothing
                }
            });
            showDialog.displayDialog("Delete from list", "Do you want to delete it from selected list?");
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