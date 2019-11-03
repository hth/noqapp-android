package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.JsonCoupon;
import com.noqapp.android.common.model.types.DiscountTypeEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter {
    private final CouponAdapter.OnItemClickListener listener;
    private List<JsonCoupon> jsonCoupons;
    private Context context;

    public CouponAdapter(Context context, List<JsonCoupon> jsonCoupons, CouponAdapter.OnItemClickListener listener) {
        this.jsonCoupons = jsonCoupons;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item_discount, parent, false);
        return new CouponAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vholder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) vholder;
        final JsonCoupon jsonCoupon = jsonCoupons.get(listPosition);
        holder.tv_discount_name.setText(jsonCoupon.getDiscountName());
        holder.tv_discount_description.setText(jsonCoupon.getDiscountDescription());

        if (jsonCoupon.getDiscountType() == DiscountTypeEnum.F) {
            holder.tv_discount_amount.setText(LaunchActivity.getCurrencySymbol() + CommonHelper.displayPrice(jsonCoupon.getDiscountAmount()));
        } else {
            holder.tv_discount_amount.setText(jsonCoupon.getDiscountAmount() + "% off");
        }
        holder.tv_offer_validity.setText("Validity period: " + CommonHelper.formatStringDate(CommonHelper.SDF_YYYY_MM_DD, jsonCoupon.getCouponEndDate()));
        holder.card_view.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        holder.tv_apply_coupon.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        holder.tv_apply_coupon.setOnClickListener(v -> {
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
