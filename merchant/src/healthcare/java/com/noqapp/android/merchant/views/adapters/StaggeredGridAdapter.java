package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.medical.JsonMedicalPathology;
import com.noqapp.android.common.beans.medical.JsonMedicalRadiology;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.DataObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class StaggeredGridAdapter extends RecyclerView.Adapter {

    private ArrayList<DataObj> dataObjArrayList;
    private Context context;
    private int drawableSelect;
    private int drawableUnSelect;
    private StaggeredClick staggeredClick;

    public interface StaggeredClick {
        void staggeredClick(boolean isOpen, String medicineName);
    }

    public StaggeredGridAdapter(Context context, ArrayList<DataObj> dataObjArrayList) {
        this.context = context;
        this.dataObjArrayList = dataObjArrayList;
        drawableSelect = R.drawable.bg_select;
        drawableUnSelect = R.drawable.bg_unselect;
        Collections.sort(dataObjArrayList);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        holder.name.setText(dataObjArrayList.get(position).getShortName());
        if (dataObjArrayList.get(position).isSelect()) {
            holder.name.setBackground(ContextCompat.getDrawable(context, drawableSelect));
            holder.name.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.name.setBackground(ContextCompat.getDrawable(context, drawableUnSelect));
            holder.name.setTextColor(Color.parseColor("#FFFFFF"));
        }
        holder.name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataObjArrayList.get(position).setSelect(isChecked);
                if (isChecked) {
                    holder.name.setBackground(ContextCompat.getDrawable(context, drawableSelect));
                    holder.name.setTextColor(Color.parseColor("#FFFFFF"));
                    if (null != staggeredClick)
                        staggeredClick.staggeredClick(true, dataObjArrayList.get(position).getShortName());
                } else {
                    holder.name.setBackground(ContextCompat.getDrawable(context, drawableUnSelect));
                    holder.name.setTextColor(Color.parseColor("#FFFFFF"));
                    if (null != staggeredClick)
                        staggeredClick.staggeredClick(false, dataObjArrayList.get(position).getShortName());
                }

            }
        });

        if (dataObjArrayList.get(position).isNewlyAdded()) {
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(150); //You can manage the blinking time with this parameter
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(20);
            holder.name.startAnimation(anim);


        } else {
            holder.name.clearAnimation();
        }
    }


    @Override
    public int getItemCount() {
        return dataObjArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox name;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
        }
    }

    public String getSelectedData() {
        String data = "";
        for (int i = 0; i < dataObjArrayList.size(); i++) {
            if (dataObjArrayList.get(i).isSelect()) {
                data += dataObjArrayList.get(i).getShortName() + ", ";
            }
        }
        if (data.endsWith(", "))
            data = data.substring(0, data.length() - 2);
        return data;
    }

    public ArrayList<String> getSelectedDataList() {
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < dataObjArrayList.size(); i++) {
            if (dataObjArrayList.get(i).isSelect()) {
                temp.add(dataObjArrayList.get(i).getShortName());
            }
        }
        return temp;
    }

    public void updateSelectList(List<JsonMedicalRadiology> temp) {
        for (JsonMedicalRadiology d :
                temp) {
            for (int i = 0; i < dataObjArrayList.size(); i++) {
                if (d.getName().equals(dataObjArrayList.get(i).getShortName())) {
                    dataObjArrayList.get(i).setSelect(true);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updatePathSelectList(List<JsonMedicalPathology> temp) {
        for (JsonMedicalPathology d :
                temp) {
            for (int i = 0; i < dataObjArrayList.size(); i++) {
                if (d.getName().equals(dataObjArrayList.get(i).getShortName())) {
                    dataObjArrayList.get(i).setSelect(true);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateSelection(String[] temp) {
        for (String d :
                temp) {
            for (int i = 0; i < dataObjArrayList.size(); i++) {
                if (d.trim().equals(dataObjArrayList.get(i).getShortName())) {
                    dataObjArrayList.get(i).setSelect(true);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void selectItem(DataObj dataObj) {
        dataObjArrayList.get(dataObjArrayList.indexOf(dataObj)).setSelect(true);
    }
}
