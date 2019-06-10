package com.noqapp.android.merchant.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.model.types.DiscountTypeEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonDiscount;

import java.util.List;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.MyViewHolder> {
    private final DiscountAdapter.OnItemClickListener listener;
    private List<JsonDiscount> jsonDiscounts;

    public DiscountAdapter(List<JsonDiscount> jsonDiscounts, DiscountAdapter.OnItemClickListener listener) {
        this.jsonDiscounts = jsonDiscounts;
        this.listener = listener;
    }

    @Override
    public DiscountAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item_discount, parent, false);
        return new DiscountAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DiscountAdapter.MyViewHolder holder, final int listPosition) {
        final JsonDiscount jsonDiscount = jsonDiscounts.get(listPosition);
        holder.tv_discount_name.setText(jsonDiscount.getDiscountName());
        holder.tv_discount_description.setText(jsonDiscount.getDiscountDescription());

        if(jsonDiscount.getDiscountType() == DiscountTypeEnum.F){
            holder.tv_discount_amount.setText("Rs "+String.valueOf(jsonDiscount.getDiscountAmount())+"available");
        }else{
            holder.tv_discount_amount.setText(String.valueOf(jsonDiscount.getDiscountAmount())+"% dicount available");
        }
        holder.tv_apply_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != listener){
                    listener.discountItemClick(jsonDiscount);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return jsonDiscounts.size();
    }

    public interface OnItemClickListener {
        void discountItemClick(JsonDiscount jsonDiscount);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_discount_name;
        private TextView tv_discount_description;
        private TextView tv_discount_amount;
        private TextView tv_apply_coupon;
        private CardView card_view;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_discount_name = itemView.findViewById(R.id.tv_discount_name);
            this.tv_discount_description = itemView.findViewById(R.id.tv_discount_description);
            this.tv_discount_amount = itemView.findViewById(R.id.tv_discount_amount);
            this.tv_apply_coupon = itemView.findViewById(R.id.tv_apply_coupon);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
