package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.DataObj;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

public class MultiSelectListAdapter extends ArrayAdapter<DataObj> {
    private List<DataObj> list;
    private LayoutInflater inflator;

    public MultiSelectListAdapter(Activity context, List<DataObj> list) {
        super(context, R.layout.list_item_multi_select, list);
        this.list = list;
        inflator = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflator.inflate(R.layout.list_item_multi_select, null);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.title);
            holder.chk = convertView.findViewById(R.id.checkbox);
            holder.chk
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton view,
                                                     boolean isChecked) {
                            int getPosition = (Integer) view.getTag();
                            list.get(getPosition).setSelect(view.isChecked());
                        }
                    });
            convertView.setTag(holder);
            convertView.setTag(R.id.title, holder.title);
            convertView.setTag(R.id.checkbox, holder.chk);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.chk.setTag(position);
        holder.title.setText(list.get(position).getShortName());
        holder.chk.setChecked(list.get(position).isSelect());
        return convertView;
    }

    static class ViewHolder {
        private TextView title;
        private CheckBox chk;
    }


    public String getAllSelectedString() {
        StringBuffer sb = new StringBuffer();
        for (DataObj bean : list) {
            if (bean.isSelect()) {
                sb.append(bean.getShortName());
                sb.append(". ");
            }
        }
        String data = sb.toString();
        if(data.endsWith(". "))
            data = data.substring(0,data.length()-2);
        return data;
    }


    public void addData(DataObj dataObj){
        list.add(dataObj);
        notifyDataSetChanged();
    }

    public void updateSelection(String [] temp){
        for (String d :
                temp) {
            for (int i = 0; i < list.size(); i++) {
                if(d.trim().equals(list.get(i).getShortName())){
                    list.get(i).setSelect(true);
                    break;
                }

            }
        }
        notifyDataSetChanged();
    }
}