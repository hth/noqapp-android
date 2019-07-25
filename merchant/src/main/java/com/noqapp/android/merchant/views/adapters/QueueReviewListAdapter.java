package com.noqapp.android.merchant.views.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.JsonReview;
import com.noqapp.android.common.beans.JsonReviewList;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.squareup.picasso.Picasso;

public class QueueReviewListAdapter extends RecyclerView.Adapter {
    private final QueueReviewListAdapter.OnItemClickListener listener;
    private JsonReviewList jsonReviewList;

    public QueueReviewListAdapter(JsonReviewList jsonReviewList, QueueReviewListAdapter.OnItemClickListener listener) {
        this.jsonReviewList = jsonReviewList;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item_show_all_review, parent, false);
        return new QueueReviewListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        final JsonReview jsonReview = jsonReviewList.getJsonReviews().get(listPosition);
        holder.tv_name.setText(TextUtils.isEmpty(jsonReview.getName()) ? "Customer Name - N/A" : jsonReview.getName());
        holder.tv_review_detail.setText(jsonReview.getReview());
        holder.tv_review_detail.setVisibility(jsonReview.isReviewShow() ? View.VISIBLE : View.GONE);
        holder.tv_rating.setText(String.valueOf(jsonReview.getRatingCount()));
        holder.tv_date.setText(CommonHelper.formatStringDate(CommonHelper.SDF_DOB_FROM_UI, jsonReview.getCreated()));
        // Picasso.get().load(ImageUtils.getProfilePlaceholder()).into(holder.iv_main);
        try {
            if (!TextUtils.isEmpty(jsonReview.getProfileImage())) {
                Picasso.get()
                        .load(AppUtils.getImageUrls(BuildConfig.PROFILE_BUCKET, jsonReview.getProfileImage()))
                        // .placeholder(ImageUtils.getProfilePlaceholder(context))
                        //  .error(ImageUtils.getProfileErrorPlaceholder(context))
                        .into(holder.iv_main);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.iv_flag.setVisibility(jsonReview.isReviewShow() ? View.VISIBLE : View.INVISIBLE);
        holder.iv_flag.setOnClickListener(v -> {
            if (null != listener) {
                listener.reviewItemListClick(jsonReviewList.getCodeQR(), jsonReview);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jsonReviewList.getJsonReviews().size();
    }

    public interface OnItemClickListener {
        void reviewItemListClick(String codeQR, JsonReview jsonReview);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private TextView tv_review_detail;
        private TextView tv_rating;
        private TextView tv_date;
        private ImageView iv_main;
        private ImageView iv_flag;
        private CardView card_view;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_review_detail = itemView.findViewById(R.id.tv_review_detail);
            this.tv_rating = itemView.findViewById(R.id.tv_rating);
            this.tv_date = itemView.findViewById(R.id.tv_date);
            this.iv_main = itemView.findViewById(R.id.iv_main);
            this.iv_flag = itemView.findViewById(R.id.iv_flag);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
