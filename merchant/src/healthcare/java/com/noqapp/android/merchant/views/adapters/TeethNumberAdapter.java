package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.merchant.R;

import java.util.List;


public class TeethNumberAdapter extends RecyclerView.Adapter {

    private List<String> dataObjArrayList;
    private Context context;
    private int selectedPos = -1;

    public TeethNumberAdapter(Context context, List<String> dataObjArrayList) {
        this.context = context;
        this.dataObjArrayList = dataObjArrayList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayoutdental, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        holder.name.setText(dataObjArrayList.get(position));
        if (selectedPos == position) {
            holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_select));
            holder.name.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect));
            holder.name.setTextColor(Color.parseColor("#FFFFFF"));
        }
        holder.name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedPos = position;
                notifyDataSetChanged();
                if (isChecked) {
                    holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_select));
                    holder.name.setTextColor(Color.parseColor("#FFFFFF"));

                } else {
                    holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect));
                    holder.name.setTextColor(Color.parseColor("#FFFFFF"));
                }

            }
        });
    }


    @Override
    public int getItemCount() {
        return dataObjArrayList.size();
    }

    public String getSelectedItem() {
        return selectedPos == -1 ? "" : dataObjArrayList.get(selectedPos);
    }

    public void clearSelection() {
        selectedPos = -1;
        notifyDataSetChanged();
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

}
