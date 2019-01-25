package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.CheckBoxObj;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.List;

public class RadioRecyclerAdapter extends RecyclerView.Adapter<RadioRecyclerAdapter.MyViewHolder> {

    private int lastCheckedPos = -1;
    private Context context;
    private List<CheckBoxObj> checkBoxObjList;
    private int parentPos;
    private UpdateGrid updateGrid;
    private int selectPos;

    interface UpdateGrid {
        void notifyView(int parentPos, int checkedPos);
    }

    public RadioRecyclerAdapter(Context context, List<CheckBoxObj> checkBoxObjList, int parentPos, UpdateGrid updateGrid, int selectPos) {
        this.context = context;
        this.checkBoxObjList = checkBoxObjList;
        this.parentPos = parentPos;
        this.updateGrid = updateGrid;
        this.selectPos = selectPos;
        lastCheckedPos = selectPos;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_checkbox, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.checkBox.setChecked(false);
        holder.checkBox.setText(checkBoxObjList.get(position).getJsonPreferredBusiness().getDisplayName());
        if (selectPos != -1 && selectPos == position) {
            holder.checkBox.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_select_square));
            holder.checkBox.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.checkBox.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect_square));
            holder.checkBox.setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark));
        }
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if(lastCheckedPos == -1){
                            lastCheckedPos = position;
                        }else if(lastCheckedPos > -1 && lastCheckedPos == position){
                            lastCheckedPos = -1;
                        }else if(lastCheckedPos > -1 && lastCheckedPos != position){
                            lastCheckedPos = position;
                        }

//                        if (holder.checkBox.isChecked()) {
//                            lastCheckedPos = position;
//                        } else {
//                            lastCheckedPos = -1;
//                        }
                        if (null != updateGrid) {
                            updateGrid.notifyView(parentPos, lastCheckedPos);
                        }

                    }
                }
        );

    }


    @Override
    public int getItemCount() {
        return checkBoxObjList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkbox);
            this.setIsRecyclable(false);

        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
