package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.HCSMenuObject;

import android.content.Context;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class HCSMenuAdapter extends RecyclerView.Adapter<HCSMenuAdapter.MyViewHolder> {

    private ArrayList<HCSMenuObject> hcsMenuObjects;
    private Context context;
    private StaggeredClick staggeredClick;
    private boolean isRemove;
    public interface StaggeredClick {
        void staggeredClick(boolean isEdit, HCSMenuObject dataObj, int pos);
    }

    public HCSMenuAdapter(Context context, ArrayList<HCSMenuObject> hcsMenuObjects, StaggeredClick staggeredClick, boolean isRemove) {
        this.context = context;
        this.hcsMenuObjects = hcsMenuObjects;
        this.staggeredClick = staggeredClick;
        this.isRemove = isRemove;
        Collections.sort(this.hcsMenuObjects);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.name.setText(hcsMenuObjects.get(position).getSortName());
        if (hcsMenuObjects.get(position).isSelect()) {
            holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect));
            holder.name.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect));
            holder.name.setTextColor(Color.parseColor("#FFFFFF"));
        }
        holder.name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hcsMenuObjects.get(position).setSelect(isChecked);

                if(isRemove){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    builder.setTitle(null);
                    View customDialogView = inflater.inflate(R.layout.dialog_logout, null, false);
                    builder.setView(customDialogView);
                    final AlertDialog mAlertDialog = builder.create();
                    mAlertDialog.setCanceledOnTouchOutside(false);
                    TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
                    TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
                    tvtitle.setText("Delete Test Item");
                    tv_msg.setText("Do you want to delete it from Test List?");
                    Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
                    Button btn_no = customDialogView.findViewById(R.id.btn_no);
                    btn_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAlertDialog.dismiss();
                        }
                    });
                    btn_yes.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (null != staggeredClick) {
                                staggeredClick.staggeredClick(isRemove, hcsMenuObjects.get(position), position);
                                Toast.makeText(context, "Deleted from Test List", Toast.LENGTH_LONG).show();
                            }
                            mAlertDialog.dismiss();
                        }
                    });
                    mAlertDialog.show();
                }else {
                    if (isChecked) {
                        holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect));
                        holder.name.setTextColor(Color.parseColor("#FFFFFF"));
                        if (null != staggeredClick)
                            staggeredClick.staggeredClick(isRemove, hcsMenuObjects.get(position), position);
                    } else {
                        holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect));
                        holder.name.setTextColor(Color.parseColor("#FFFFFF"));
                        if (null != staggeredClick)
                            staggeredClick.staggeredClick(isRemove, hcsMenuObjects.get(position), position);
                    }
                }

            }
        });


    }


    @Override
    public int getItemCount() {
        return hcsMenuObjects.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox name;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
        }
    }

    //    public String getSelectedData() {
//        String data = "";
//        for (int i = 0; i < hcsMenuObjects.size(); i++) {
//            if (hcsMenuObjects.get(i).isSelect()) {
//                data += hcsMenuObjects.get(i).getShortName() + ", ";
//            }
//        }
//        if (data.endsWith(", "))
//            data = data.substring(0, data.length() - 2);
//        return data;
//    }
//
//    public ArrayList<String> getSelectedDataList() {
//        ArrayList<String> temp = new ArrayList<>();
//        for (int i = 0; i < hcsMenuObjects.size(); i++) {
//            if (hcsMenuObjects.get(i).isSelect()) {
//                temp.add(hcsMenuObjects.get(i).getShortName());
//            }
//        }
//        return temp;
//    }
//
//    public void updateSelectList(List<JsonMedicalRadiology> temp) {
//        for (JsonMedicalRadiology d :
//                temp) {
//            for (int i = 0; i < hcsMenuObjects.size(); i++) {
//                if (d.getName().equals(hcsMenuObjects.get(i).getShortName())) {
//                    hcsMenuObjects.get(i).setSelect(true);
//                    break;
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }
//
//    public void updatePathSelectList(List<JsonMedicalPathology> temp) {
//        for (JsonMedicalPathology d :
//                temp) {
//            for (int i = 0; i < hcsMenuObjects.size(); i++) {
//                if (d.getName().equals(hcsMenuObjects.get(i).getShortName())) {
//                    hcsMenuObjects.get(i).setSelect(true);
//                    break;
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }
//
//    public void updateSelection(String[] temp) {
//        for (String d :
//                temp) {
//            for (int i = 0; i < hcsMenuObjects.size(); i++) {
//                if (d.trim().equals(hcsMenuObjects.get(i).getShortName())) {
//                    hcsMenuObjects.get(i).setSelect(true);
//                    break;
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }
//
    public void selectItem(HCSMenuObject hcsMenuObject) {
        hcsMenuObjects.get(hcsMenuObjects.indexOf(hcsMenuObject)).setSelect(true);
    }
}
