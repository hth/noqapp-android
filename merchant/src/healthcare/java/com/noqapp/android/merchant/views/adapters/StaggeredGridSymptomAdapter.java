package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.DataObj;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;


public class StaggeredGridSymptomAdapter extends RecyclerView.Adapter<StaggeredGridSymptomAdapter.MyViewHolder> {

    private ArrayList<DataObj> dataObjArrayList;
    private Context context;
    private StaggeredClick staggeredClick;
    private boolean isEdit;

    public interface StaggeredClick {
        void staggeredClick(boolean isOpen, boolean isEdit, DataObj dataObj, int pos);
    }



    public StaggeredGridSymptomAdapter(Context context, ArrayList<DataObj> dataObjArrayList, StaggeredClick staggeredClick, boolean isEdit) {
        this.context = context;
        this.dataObjArrayList = dataObjArrayList;
        this.staggeredClick = staggeredClick;
        this.isEdit = isEdit;
        Collections.sort(dataObjArrayList);
    }




    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.name.setText(dataObjArrayList.get(position).getShortName());
        if (dataObjArrayList.get(position).isSelect()) {
            holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect));
        } else {
            holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect));
        }
        holder.name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                  dataObjArrayList.get(position).setSelect(isChecked);
                if (isChecked) {
                    holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect));
                    if (null != staggeredClick)
                        staggeredClick.staggeredClick(true,isEdit, dataObjArrayList.get(position),position);
                } else {
                    holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect));
                    if (null != staggeredClick)
                        staggeredClick.staggeredClick(true,isEdit, dataObjArrayList.get(position),position);
                }

            }
        });
        if(dataObjArrayList.get(position).isNewlyAdded()){
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(150); //You can manage the blinking time with this parameter
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(20);
            holder.name.startAnimation(anim);

        }else{
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
                data +=   "Having " + dataObjArrayList.get(i).getShortName() + " since last " + dataObjArrayList.get(i).getAdditionalNotes()   + "\n";
            }
        }
        if (data.endsWith(", "))
            data = data.substring(0, data.length() - 2);
        return data;
    }
}
