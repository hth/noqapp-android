package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.model.types.InventoryStateEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAsset;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final OnItemClickListener listener;
    private List<JsonCheckAsset> dataSet;
    private String YES = "Yes";
    private String NO = "No";
    private List<String> state_list = new ArrayList<>();

    public List<JsonCheckAsset> getDataSet() {
        return dataSet;
    }

    public InventoryAdapter(List<JsonCheckAsset> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
        state_list.clear();
        state_list = InventoryStateEnum.asListOfDescription();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item_inventory, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder Vholder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) Vholder;
        holder.tv_title.setText(dataSet.get(listPosition).getAssetName());
        holder.sc_status.removeAllSegments();
        holder.sc_status.addSegments(state_list);
        switch (dataSet.get(listPosition).getInventoryStateEnum()) {
            case WK:
                holder.sc_status.setSelectedSegment(0);
                break;
            case NW:
                holder.sc_status.setSelectedSegment(1);
                break;
            case NC:
                holder.sc_status.setSelectedSegment(2);
                break;
        }
        holder.sc_status.addOnSegmentSelectListener((segmentViewHolder, isSelected, isReselected) -> dataSet.get(listPosition).setInventoryStateEnum(InventoryStateEnum.getNameByDescription(state_list.get(holder.sc_status.getLastSelectedAbsolutePosition()))));
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

    public void selectUnselectAllData(boolean isChecked) {
        for (JsonCheckAsset jsonCheckAsset : dataSet) {
            jsonCheckAsset.setInventoryStateEnum(isChecked ? InventoryStateEnum.WK : InventoryStateEnum.NW);
        }
        notifyDataSetChanged();
    }


}
