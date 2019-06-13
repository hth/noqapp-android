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
import com.noqapp.android.client.presenter.beans.JsonDiscount;

import java.util.List;

public class MyCouponsAdapter extends RecyclerView.Adapter {

    private final OnItemClickListener listener;
    private List<JsonDiscount> jsonDiscountList;
    private Context context;

    public MyCouponsAdapter(Context context, List<JsonDiscount> jsonDiscountList,
                            OnItemClickListener listener) {
        this.jsonDiscountList = jsonDiscountList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rcv_item_mycoupons, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder Vholder, final int position) {
        ViewHolder holder = (ViewHolder) Vholder;
        final JsonDiscount jsonDiscount = jsonDiscountList.get(position);
//        holder.tv_menu_header.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listener.onDiscountItemClick(position, jsonDiscount);
//            }
//        });
//        holder.tv_menu_header.setText(jsonDiscount.getDiscountName());
        holder.card_view.setCardBackgroundColor(Color.parseColor("#CA705F"));
        holder.tv_apply.setTextColor(Color.parseColor("#CA705F"));
    }

    @Override
    public int getItemCount() {
        return jsonDiscountList.size();
    }


    public interface OnItemClickListener {
        void onDiscountItemClick(int pos, JsonDiscount jsonDiscount);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_apply;
        private LinearLayout ll_header;
        private CardView card_view;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_apply = itemView.findViewById(R.id.tv_apply);
            this.ll_header = itemView.findViewById(R.id.ll_header);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }

}