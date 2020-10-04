package com.noqapp.android.client.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonFeed;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter {
    private final OnItemClickListener listener;
    private List<JsonFeed> dataSet;
    private boolean increaseCardWidth;

    public FeedAdapter(List<JsonFeed> data, OnItemClickListener listener) {
        this.dataSet = data;
        this.listener = listener;
    }

    public FeedAdapter(List<JsonFeed> data, OnItemClickListener listener, boolean increaseCardWidth) {
        this.dataSet = data;
        this.listener = listener;
        this.increaseCardWidth = increaseCardWidth;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_feed, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder Vholder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) Vholder;
        if (increaseCardWidth) {
            holder.card_view.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            LinearLayout.MarginLayoutParams params = (LinearLayout.MarginLayoutParams) holder.card_view.getLayoutParams();
            params.leftMargin = 20;
            params.rightMargin = 20;
        }
        JsonFeed item = dataSet.get(listPosition);
        Picasso.get().load(item.getImageUrl()).into(holder.iv_bg);
        holder.tv_title.setText(item.getTitle());
        holder.card_view.setOnClickListener((View v) -> {
            if (null != listener) {
                listener.onFeedItemClick(dataSet.get(listPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void onFeedItemClick(JsonFeed item);
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
