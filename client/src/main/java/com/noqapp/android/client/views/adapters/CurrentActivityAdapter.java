package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.common.model.types.QueueOrderTypeEnum;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class CurrentActivityAdapter extends RecyclerView.Adapter<CurrentActivityAdapter.MyViewHolder> {
    private final Context context;
    private final OnItemClickListener listener;
    private List<JsonTokenAndQueue> dataSet;

    public CurrentActivityAdapter(List<JsonTokenAndQueue> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item_current_activity, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        final JsonTokenAndQueue jsonTokenAndQueue = dataSet.get(listPosition);
        holder.tv_name.setText(jsonTokenAndQueue.getDisplayName());
        holder.tv_address.setText(AppUtilities.getStoreAddress(jsonTokenAndQueue.getTown(), jsonTokenAndQueue.getArea()));
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.currentItemClick(jsonTokenAndQueue, v, listPosition);
            }
        });

        holder.tv_total_value.setText(String.valueOf(dataSet.get(listPosition).getServingNumber()));
        if (jsonTokenAndQueue.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.Q) {
            holder.tv_current_title.setText(context.getString(R.string.token));
            if (jsonTokenAndQueue.getToken() - jsonTokenAndQueue.getServingNumber() == 0) {
                holder.tv_total.setText("It's your turn!!!");
                holder.tv_total_value.setVisibility(View.GONE);
            } else if (jsonTokenAndQueue.getServingNumber() == 0) {
                holder.tv_total.setText("Queue not yet started");
                holder.tv_total_value.setVisibility(View.GONE);
            } else {
                holder.tv_total.setText(context.getString(R.string.serving_now));
                holder.tv_total_value.setVisibility(View.VISIBLE);
            }
        } else if (jsonTokenAndQueue.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.O) {
            holder.tv_current_title.setText(context.getString(R.string.order));
            if (jsonTokenAndQueue.getToken() - jsonTokenAndQueue.getServingNumber() <= 0) {
                switch (jsonTokenAndQueue.getPurchaseOrderState()) {
                    case OP:
                        holder.tv_total.setText("Order being prepared");
                        break;
                    default:
                        holder.tv_total.setText(jsonTokenAndQueue.getPurchaseOrderState().getDescription());
                        break;
                }
                holder.tv_total_value.setVisibility(View.GONE);
            } else if (jsonTokenAndQueue.getServingNumber() == 0) {
                holder.tv_total.setText("Queue not yet started");
                holder.tv_total_value.setVisibility(View.GONE);
            } else {
                holder.tv_total.setText(context.getString(R.string.serving_now));
                holder.tv_total_value.setVisibility(View.VISIBLE);
            }
        }

        holder.tv_current_value.setText(String.valueOf(jsonTokenAndQueue.getToken()));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public interface OnItemClickListener {
        void currentItemClick(JsonTokenAndQueue item, View view, int pos);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private TextView tv_detail;
        private TextView tv_address;
        private CardView card_view;
        private TextView tv_total_value;
        private TextView tv_current_value;
        private TextView tv_current_title;

        private TextView tv_total;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_detail = itemView.findViewById(R.id.tv_detail);
            this.tv_address = itemView.findViewById(R.id.tv_address);
            this.tv_current_value = itemView.findViewById(R.id.tv_current_value);
            this.tv_current_title = itemView.findViewById(R.id.tv_current);
            this.tv_total_value = itemView.findViewById(R.id.tv_total_value);
            this.tv_total = itemView.findViewById(R.id.tv_total);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }

}
