package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.DataObj;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;


import java.util.ArrayList;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<DataObj> personNames;
    private Context context;

    public CustomAdapter(Context context, ArrayList<DataObj> personNames) {
        this.context = context;
        this.personNames = personNames;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.name.setText(personNames.get(position).getName());
        if (personNames.get(position).isSelect()) {
            holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_select));
        } else {
            holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect));
        }
        holder.name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                personNames.get(position).setSelect(isChecked);
                if (isChecked) {
                    holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_select));
                } else {
                    holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect));
                }

            }
        });
    }


    @Override
    public int getItemCount() {
        return personNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox name;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);

        }
    }

    public String getSelectedData() {
        String data = "";
        for (int i = 0; i < personNames.size(); i++) {
            if (personNames.get(i).isSelect()) {
                data += personNames.get(i).getName() + ", ";
            }
        }
        if (data.endsWith(", "))
            data = data.substring(0, data.length() - 2);
        return data;
    }

    public ArrayList<String> getSelectedDataList() {
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < personNames.size(); i++) {
            if (personNames.get(i).isSelect()) {
                temp.add(personNames.get(i).getName());
            }
        }
        return temp;
    }

    public ArrayList<JsonMedicalMedicine> getSelectedDataListObject() {
        ArrayList<JsonMedicalMedicine> temp = new ArrayList<>();
        for (int i = 0; i < personNames.size(); i++) {
            if (personNames.get(i).isSelect()) {
                JsonMedicalMedicine jsonMedicalMedicine = new JsonMedicalMedicine();
                jsonMedicalMedicine.setName(personNames.get(i).getName());
                jsonMedicalMedicine.setPharmacyCategory(personNames.get(i).getCategory());
                temp.add(jsonMedicalMedicine);
            }
        }
        return temp;
    }
}
