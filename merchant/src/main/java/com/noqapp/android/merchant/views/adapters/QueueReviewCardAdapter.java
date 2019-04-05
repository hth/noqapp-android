package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.JsonReviewList;
import com.noqapp.android.merchant.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QueueReviewCardAdapter extends RecyclerView.Adapter<QueueReviewCardAdapter.MyViewHolder> {
    private final Context context;
    private final QueueReviewCardAdapter.OnItemClickListener listener;
    private List<JsonReviewList> reviews;

    public QueueReviewCardAdapter(List<JsonReviewList> reviews, Context context, QueueReviewCardAdapter.OnItemClickListener listener) {
        this.reviews = reviews;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public QueueReviewCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_q_review_item, parent, false);
        QueueReviewCardAdapter.MyViewHolder myViewHolder = new QueueReviewCardAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final QueueReviewCardAdapter.MyViewHolder holder, final int listPosition) {
        holder.tv_customer_name.setText(TextUtils.isEmpty(reviews.get(listPosition).getDisplayName()) ? "N/A" : reviews.get(listPosition).getDisplayName());
        float f = reviews.get(listPosition).getAggregateRatingCount() * 1.0f / reviews.get(listPosition).getJsonReviews().size();
        holder.tv_business_customer_id.setText("Rating " + String.format("%.01f", f));

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.currentItemClick(reviews.get(listPosition));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public interface OnItemClickListener {
        void currentItemClick(JsonReviewList jsonReviewList);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_customer_name;
        private TextView tv_business_customer_id;
        private CardView card_view;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_customer_name = itemView.findViewById(R.id.tv_customer_name);
            this.tv_business_customer_id = itemView.findViewById(R.id.tv_business_customer_id);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
