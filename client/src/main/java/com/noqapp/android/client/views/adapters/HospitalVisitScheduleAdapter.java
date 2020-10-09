package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.customviews.FixedHeightListView;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;

import java.util.List;

public class HospitalVisitScheduleAdapter extends RecyclerView.Adapter {

    private final HospitalVisitScheduleListAdapter.OnItemClickListener listener;
    private List<JsonHospitalVisitSchedule> categories;
    private Context context;

    public HospitalVisitScheduleAdapter(
        Context context,
        List<JsonHospitalVisitSchedule> categories,
        HospitalVisitScheduleListAdapter.OnItemClickListener listener
    ) {
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
        holder.tv_menu_header.setText(categories.get(position).getHeader());
        HospitalVisitScheduleListAdapter adapter = new HospitalVisitScheduleListAdapter(
            context,
            categories.get(position).getVisitingFor(),
            listener,
            categories.get(position));
        holder.fh_list_view.setAdapter(adapter);
        holder.ll_header.setBackgroundColor(Color.WHITE);
        holder.tv_menu_header.setTextColor(Color.BLACK);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_menu_header;
        private LinearLayout ll_header;
        private FixedHeightListView fh_list_view;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_menu_header = itemView.findViewById(R.id.tv_menu_header);
            this.ll_header = itemView.findViewById(R.id.ll_header);
            this.fh_list_view = itemView.findViewById(R.id.fh_list_view);
        }
    }
}