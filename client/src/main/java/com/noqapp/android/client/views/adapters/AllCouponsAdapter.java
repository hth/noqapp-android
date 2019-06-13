package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.common.beans.JsonCoupon;


import java.util.List;

public class AllCouponsAdapter extends RecyclerView.Adapter {

    private final OnItemClickListener listener;
    private List<JsonCoupon> jsonCoupons;
    private Context context;

    public AllCouponsAdapter(Context context, List<JsonCoupon> JsonCouponList,
                             OnItemClickListener listener) {
        this.jsonCoupons = JsonCouponList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rcv_item_all_coupons, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder Vholder, final int position) {
        ViewHolder holder = (ViewHolder) Vholder;
        final JsonCoupon JsonCoupon = jsonCoupons.get(position);
//        holder.tv_menu_header.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listener.onDiscountItemClick(position, JsonCoupon);
//            }
//        });
//        holder.tv_menu_header.setText(JsonCoupon.getDiscountName());
    }

    @Override
    public int getItemCount() {
        return jsonCoupons.size();
    }


    public interface OnItemClickListener {
        void onDiscountItemClick(int pos, JsonCoupon JsonCoupon);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_menu_header;
        private LinearLayout ll_header;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_menu_header = itemView.findViewById(R.id.tv_menu_header);
            this.ll_header = itemView.findViewById(R.id.ll_header);
        }
    }

}