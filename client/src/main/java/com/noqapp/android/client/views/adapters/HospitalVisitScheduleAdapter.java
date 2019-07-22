package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.model.types.BooleanReplacementEnum;
import com.noqapp.android.common.utils.CommonHelper;


import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HospitalVisitScheduleAdapter extends RecyclerView.Adapter {

    private final OnItemClickListener listener;
    private List<JsonHospitalVisitSchedule> categories;
    private Context context;

    public HospitalVisitScheduleAdapter(Context context, List<JsonHospitalVisitSchedule> categories,
                                        OnItemClickListener listener) {
        this.categories = categories;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rcv_hvs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder Vholder, final int position) {
        ViewHolder holder = (ViewHolder) Vholder;
        holder.tv_menu_header.setOnClickListener((View v) -> {
            //listener.onCategoryItemClick(position, jsonCategory);
        });
        holder.tv_menu_header.setText(categories.get(position).getHeader());
        // holder.ll_header.removeAllViews();
        Map<String, BooleanReplacementEnum> visitingFor = categories.get(position).getVisitingFor();
        Iterator it = visitingFor.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            BooleanReplacementEnum booleanReplacementEnum = (BooleanReplacementEnum) pair.getValue();

            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.rcv_hvs_item, null, false);
            TextView tv_hvs_name = view.findViewById(R.id.tv_hvs_name);
            TextView tv_hvs_visitedDate = view.findViewById(R.id.tv_hvs_visitedDate);
            TextView tv_hvs_status = view.findViewById(R.id.tv_hvs_status);
            CardView card_view = view.findViewById(R.id.card_view);
            tv_hvs_name.setText(pair.getKey().toString());
            String date = CommonHelper.formatStringDate(CommonHelper.SDF_DOB_FROM_UI,
                    categories.get(position).getVisitedDate());
            tv_hvs_visitedDate.setText(date);
            tv_hvs_status.setText("Status- "+booleanReplacementEnum.getDescription());
            card_view.setCardBackgroundColor(Color.parseColor(booleanReplacementEnum.getColor()));
            holder.ll_child.addView(view);
            it.remove(); // avoids a ConcurrentModificationException
        }
        holder.ll_header.setBackgroundColor(Color.WHITE);
        holder.tv_menu_header.setTextColor(Color.BLACK);

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public interface OnItemClickListener {
        void onImmuneItemClick(JsonHospitalVisitSchedule jsonHospitalVisitSchedule);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_menu_header;
        private LinearLayout ll_header;
        private LinearLayout ll_child;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_menu_header = itemView.findViewById(R.id.tv_menu_header);
            this.ll_header = itemView.findViewById(R.id.ll_header);
            this.ll_child = itemView.findViewById(R.id.ll_child);
        }
    }

}