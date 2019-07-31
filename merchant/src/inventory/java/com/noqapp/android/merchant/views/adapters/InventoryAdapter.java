package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAsset;

import java.util.ArrayList;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

public class InventoryAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final OnItemClickListener listener;
    private List<JsonCheckAsset> dataSet;
    private String YES = "Yes";
    private String NO = "No";
    private List<String> yes_no_list = new ArrayList<>();

    public List<JsonCheckAsset> getDataSet() {
        return dataSet;
    }

    public InventoryAdapter(List<JsonCheckAsset> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
        yes_no_list.clear();
        yes_no_list.add(YES);
        yes_no_list.add(NO);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_item_inventory, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder Vholder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) Vholder;
        holder.tv_title.setText(dataSet.get(listPosition).getAssetName());
        holder.sc_status.addSegments(yes_no_list);
        holder.sc_status.setSelectedSegment(dataSet.get(listPosition).isStatus() ? 0 : 1);
        holder.sc_status.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                dataSet.get(listPosition).setStatus(holder.sc_status.getSelectedAbsolutePosition() == 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void onInventoryItemClick(JsonCheckAsset item);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_title;
        private SegmentedControl sc_status;
        private CardView card_view;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.sc_status = itemView.findViewById(R.id.sc_status);
            this.card_view = itemView.findViewById(R.id.card_view);

        }
    }


}
