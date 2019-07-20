package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.ImmuneObjList;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HospitalVisitScheduleAdapter extends RecyclerView.Adapter {

    private final OnItemClickListener listener;
    private List<ImmuneObjList> categories;
    private Context context;

    public HospitalVisitScheduleAdapter(Context context, List<ImmuneObjList> categories, OnItemClickListener listener) {
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
        holder.tv_menu_header.setText(categories.get(position).getHeaderTitle());
        // holder.ll_header.removeAllViews();
        for (int i = 0; i < categories.get(position).getImmuneObjs().size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.rcv_hvs_item, null, false);
            TextView tv_hvs_title = view.findViewById(R.id.tv_hvs_name);
            TextView tv_hvs_date = view.findViewById(R.id.tv_hvs_visitedDate);
            CardView card_view = view.findViewById(R.id.card_view);
            tv_hvs_title.setText(categories.get(position).getImmuneObjs().get(i).getName());
            tv_hvs_date.setText(categories.get(position).getImmuneObjs().get(i).getExpectedDate());
            if (TextUtils.isEmpty(categories.get(position).getImmuneObjs().get(i).getVisitedDate())) {
                card_view.setCardBackgroundColor(ContextCompat.getColor(context, R.color.pressed_color));
            } else {
                card_view.setCardBackgroundColor(Color.parseColor("#008080"));
            }
            holder.ll_child.addView(view);
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