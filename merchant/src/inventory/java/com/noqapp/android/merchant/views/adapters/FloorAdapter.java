package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAsset;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAssetList;

import java.util.List;

public class FloorAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final OnItemClickListener listener;
    private List<JsonCheckAsset> dataSet;

    public FloorAdapter(List<JsonCheckAsset> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_rooms_on_floor, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder Vholder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) Vholder;
        //Picasso.get().load(item.getImageUrl()).into(holder.iv_bg);
        holder.tv_title.setText(dataSet.get(listPosition).getFloor());
        holder.card_view.setOnClickListener((View v) -> {
            if (null != listener)
                listener.onFloorItemClick(dataSet.get(listPosition));
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void onFloorItemClick(JsonCheckAsset item);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_title;
        private ImageView iv_bg;
        private CardView card_view;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.iv_bg = itemView.findViewById(R.id.iv_bg);
            this.card_view = itemView.findViewById(R.id.card_view);

        }
    }


}
