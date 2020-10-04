package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.views.activities.ProductSliderActivity;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class DisplayCaseAdapter extends RecyclerView.Adapter {
    private final int baseVisibleCount = 4;
    private List<JsonStoreProduct> jsonStoreProducts;
    private Context context;

    public DisplayCaseAdapter(Context context, List<JsonStoreProduct> jsonStoreProducts) {
        this.context = context;
        this.jsonStoreProducts = jsonStoreProducts;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.layout_image_thumb_product, parent, false);
        return new MyViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder Vholder, int position) {
        MyViewHolder holder = (MyViewHolder) Vholder;
        Picasso.get()
            .load(AppUtils.getImageUrls(BuildConfig.PRODUCT_BUCKET, jsonStoreProducts.get(position).getProductImage()))
            .placeholder(ImageUtils.getThumbPlaceholder(context))
            .error(ImageUtils.getThumbErrorPlaceholder(context))
            .into(holder.iv_photo);
        holder.tv_product_name.setText(jsonStoreProducts.get(position).getProductName());
        if (position < 3 || jsonStoreProducts.size() == 4) {
            holder.tv_more.setVisibility(View.GONE);
            holder.tv_product_name.setVisibility(View.VISIBLE);
        } else {
            holder.tv_more.setVisibility(View.VISIBLE);
            holder.tv_product_name.setVisibility(View.GONE);
            holder.tv_more.setText("+" + (jsonStoreProducts.size() - baseVisibleCount));
        }
    }

    @Override
    public int getItemCount() {
        return (jsonStoreProducts.size() > baseVisibleCount ? baseVisibleCount : jsonStoreProducts.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView iv_photo;
        public TextView tv_more;
        private TextView tv_product_name;

        private MyViewHolder(View itemView) {
            super(itemView);
            iv_photo = itemView.findViewById(R.id.iv_photo);
            tv_more = itemView.findViewById(R.id.tv_more);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, ProductSliderActivity.class);
            intent.putExtra("pos", getAdapterPosition());
            intent.putExtra("storeProduct", (Serializable) jsonStoreProducts);
            context.startActivity(intent);
        }
    }
}
