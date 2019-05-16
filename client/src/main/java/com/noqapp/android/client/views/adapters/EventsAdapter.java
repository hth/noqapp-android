package com.noqapp.android.client.views.adapters;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonEvent;
import com.noqapp.android.client.utils.AppUtilities;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> {
    private final OnItemClickListener listener;
    private List<JsonEvent> dataSet;
    private boolean increaseCardWidth;

    public EventsAdapter(List<JsonEvent> data, OnItemClickListener listener) {
        this.dataSet = data;
        this.listener = listener;
    }

    public EventsAdapter(List<JsonEvent> data, OnItemClickListener listener, boolean increaseCardWidth) {
        this.dataSet = data;
        this.listener = listener;
        this.increaseCardWidth = increaseCardWidth;
    }

    @Override
    public EventsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.events_rcv_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        if (increaseCardWidth) {
            holder.card_view.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            LinearLayout.MarginLayoutParams params = (LinearLayout.MarginLayoutParams) holder.card_view.getLayoutParams();
            params.leftMargin = 20;
            params.rightMargin = 20;
        }
        JsonEvent item = dataSet.get(listPosition);
        if (TextUtils.isEmpty(item.getImageUrl())) {
            holder.tv_title.setTextColor(Color.WHITE);
            holder.tv_subtitle.setTextColor(Color.WHITE);
            holder.iv_bg.setBackgroundColor(AppUtilities.generateRandomColor());
        } else {
            Picasso.get().load(item.getImageUrl()).into(holder.iv_bg);
            holder.tv_title.setTextColor(Color.TRANSPARENT);
            holder.tv_subtitle.setTextColor(Color.TRANSPARENT);
        }
        holder.tv_title.setText(item.getTitle());
        holder.tv_subtitle.setText(item.getContent());
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener)
                    listener.onEventItemClick(dataSet.get(listPosition), v, listPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void onEventItemClick(JsonEvent item, View view, int pos);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title, tv_subtitle;
        private ImageView iv_bg;
        private CardView card_view;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_subtitle = itemView.findViewById(R.id.tv_subtitle);
            this.iv_bg = itemView.findViewById(R.id.iv_bg);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }

}
