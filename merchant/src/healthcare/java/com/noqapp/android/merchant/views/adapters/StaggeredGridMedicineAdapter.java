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

import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.common.model.types.medical.DurationDaysEnum;
import com.noqapp.android.common.model.types.medical.MedicationIntakeEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.DataObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class StaggeredGridMedicineAdapter extends RecyclerView.Adapter<StaggeredGridMedicineAdapter.MyViewHolder> {

    private ArrayList<DataObj> dataObjArrayList;
    private Context context;
    private StaggeredMedicineClick staggeredClick;
    private boolean isEdit;

    public interface StaggeredMedicineClick {
        void staggeredMedicineClick(boolean isOpen, boolean isEdit, DataObj dataObj, int pos);
    }



    public StaggeredGridMedicineAdapter(Context context, ArrayList<DataObj> dataObjArrayList, StaggeredMedicineClick staggeredClick,boolean isEdit) {
        this.context = context;
        this.dataObjArrayList = dataObjArrayList;
        this.staggeredClick = staggeredClick;
        this.isEdit = isEdit;
        Collections.sort(dataObjArrayList);
    }




    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
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
                        staggeredClick.staggeredMedicineClick(true,isEdit, dataObjArrayList.get(position),position);
                } else {
                    holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect));
                    holder.name.setTextColor(Color.parseColor("#FFFFFF"));
                    if (null != staggeredClick)
                        staggeredClick.staggeredMedicineClick(true,isEdit, dataObjArrayList.get(position),position);
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


    public ArrayList<JsonMedicalMedicine> getSelectedDataListObject() {
        ArrayList<JsonMedicalMedicine> temp = new ArrayList<>();
        for (int i = 0; i < dataObjArrayList.size(); i++) {
                JsonMedicalMedicine jsonMedicalMedicine = new JsonMedicalMedicine();
                jsonMedicalMedicine.setName(dataObjArrayList.get(i).getShortName());
                jsonMedicalMedicine.setPharmacyCategory(dataObjArrayList.get(i).getCategory());
                jsonMedicalMedicine.setMedicationIntake(dataObjArrayList.get(i).getMedicineTiming());
                jsonMedicalMedicine.setDailyFrequency(dataObjArrayList.get(i).getMedicineFrequency());
                jsonMedicalMedicine.setCourse(dataObjArrayList.get(i).getMedicineDuration());
                temp.add(jsonMedicalMedicine);
        }
        return temp;
    }

    public ArrayList<DataObj> updateMedicineSelectList(List<JsonMedicalMedicine> temp,ArrayList<DataObj> listData) {
        ArrayList<DataObj> tempList = new ArrayList<>();
        for (JsonMedicalMedicine d :
                temp) {
            for (int i = 0; i < listData.size(); i++) {
                if (d.getName().equals(listData.get(i).getShortName())) {
                    DataObj dataObj = listData.get(i);
                    dataObj.setSelect(true);
                    dataObj.setMedicineTiming(MedicationIntakeEnum.valueOf(d.getMedicationIntake()).getDescription());
                    dataObj.setMedicineFrequency(d.getDailyFrequency());
                    dataObj.setMedicineDuration(DurationDaysEnum.getDescFromVal(Integer.parseInt(d.getCourse())));
                    tempList.add(dataObj);
                    break;
                }
            }
        }
       return tempList;
    }
}
