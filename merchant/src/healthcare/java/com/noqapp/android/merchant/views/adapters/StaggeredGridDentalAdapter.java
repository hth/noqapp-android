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
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.DataObj;

import java.util.ArrayList;


public class StaggeredGridDentalAdapter extends RecyclerView.Adapter {

    private ArrayList<DataObj> dataObjArrayList;
    private Context context;
    private StaggeredMedicineClick staggeredClick;
    private boolean isEdit;
    private final String SPLIT_SYMBOL = ":";

    public interface StaggeredMedicineClick {
        void staggeredMedicineClick(boolean isOpen, boolean isEdit, DataObj dataObj, int pos);
    }


    public StaggeredGridDentalAdapter(Context context, ArrayList<DataObj> dataObjArrayList,
                                      StaggeredMedicineClick staggeredClick, boolean isEdit) {
        this.context = context;
        this.dataObjArrayList = dataObjArrayList;
        this.staggeredClick = staggeredClick;
        this.isEdit = isEdit;
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
            holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect));
            holder.name.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect));
            holder.name.setTextColor(Color.parseColor("#FFFFFF"));
        }
        holder.name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataObjArrayList.get(position).setSelect(isChecked);
                if (isChecked) {
                    holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect));
                    holder.name.setTextColor(Color.parseColor("#FFFFFF"));
                    if (null != staggeredClick)
                        staggeredClick.staggeredMedicineClick(true, isEdit, dataObjArrayList.get(position), position);
                } else {
                    holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect));
                    holder.name.setTextColor(Color.parseColor("#FFFFFF"));
                    if (null != staggeredClick)
                        staggeredClick.staggeredMedicineClick(true, isEdit, dataObjArrayList.get(position), position);
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
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
        }
    }


    public String getSelectedData() {
        String data = "";
        for (int i = 0; i < dataObjArrayList.size(); i++) {
            if (dataObjArrayList.get(i).isSelect()) {
                data += dataObjArrayList.get(i).getShortName() + ":" + dataObjArrayList.get(i).getDentalProcedure() + "|";
            }
        }
        if (data.endsWith("| "))
            data = data.substring(0, data.length() - 2);
        return data;
    }


    public ArrayList<DataObj> updateDataObj(String str, ArrayList<DataObj> list) {

        ArrayList<DataObj> dataObjs = new ArrayList<>();
        try {
            String[] temp = str.split("\\|");
            if (null != temp && temp.length > 0) {
                for (int i = 0; i < temp.length; i++) {
                    String act = temp[i];
                    if (act.contains(SPLIT_SYMBOL)) {
                        String[] strArray = act.split(":");
                        String shortName = strArray[0].trim();
                        String val = strArray[1];
                        for (DataObj d : list) {
                            if (d.getShortName().equals(shortName)) {
                                dataObjs.add(d.setDentalProcedure(val).setSelect(true));
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataObjs;
    }
}
