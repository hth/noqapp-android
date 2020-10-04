package com.noqapp.android.client.views.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.common.beans.JsonCoupon;
import com.noqapp.android.common.model.types.DiscountTypeEnum;
import com.noqapp.android.common.utils.CommonHelper;

import java.util.List;

public class MyCouponsAdapter extends RecyclerView.Adapter {

    private final OnItemClickListener listener;
    private List<JsonCoupon> jsonCoupons;

    public MyCouponsAdapter(List<JsonCoupon> jsonCoupons, OnItemClickListener listener) {
        this.jsonCoupons = jsonCoupons;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item_mycoupons, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder Vholder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) Vholder;
        final JsonCoupon jsonCoupon = jsonCoupons.get(listPosition);
        holder.tv_discount_name.setText(jsonCoupon.getDiscountName());
        holder.tv_discount_description.setText(jsonCoupon.getDiscountDescription());

        if (jsonCoupon.getDiscountType() == DiscountTypeEnum.F) {
            holder.tv_discount_amount.setText("Rs " + CommonHelper.displayPrice(jsonCoupon.getDiscountAmount()));
        } else {
            holder.tv_discount_amount.setText(String.valueOf(jsonCoupon.getDiscountAmount()) + "% off");
        }
        holder.tv_offer_validity.setText("Validity period: " +
                CommonHelper.formatStringDate(CommonHelper.SDF_YYYY_MM_DD, jsonCoupon.getCouponEndDate()));
        holder.card_view.setCardBackgroundColor(Color.parseColor("#CA705F"));
        holder.tv_apply_coupon.setTextColor(Color.parseColor("#CA705F"));
        holder.tv_apply_coupon.setOnClickListener((View v) -> {
            if (null != listener) {
                listener.discountItemClick(jsonCoupon);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jsonCoupons.size();
    }

    public interface OnItemClickListener {
        void discountItemClick(JsonCoupon jsonCoupon);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_discount_name;
        private TextView tv_discount_description;
        private TextView tv_discount_amount;
        private TextView tv_apply_coupon;
        private TextView tv_offer_validity;
        private CardView card_view;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_discount_name = itemView.findViewById(R.id.tv_discount_name);
            this.tv_discount_description = itemView.findViewById(R.id.tv_discount_description);
            this.tv_discount_amount = itemView.findViewById(R.id.tv_discount_amount);
            this.tv_apply_coupon = itemView.findViewById(R.id.tv_apply_coupon);
            this.tv_offer_validity = itemView.findViewById(R.id.tv_offer_validity);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
