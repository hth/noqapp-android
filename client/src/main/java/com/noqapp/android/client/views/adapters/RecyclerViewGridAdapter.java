package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonCategory;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.utils.Formatter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.noqapp.android.client.utils.AppUtilities.getTimeIn24HourFormat;

public class RecyclerViewGridAdapter extends RecyclerView.Adapter<RecyclerViewGridAdapter.ViewHolder> {

    private final OnItemClickListener listener;
    private List<JsonCategory> categories;
    private Map<String, ArrayList<BizStoreElastic>> queueMap;
    private Context context;

    public interface OnItemClickListener {
        void onCategoryItemClick(int pos, JsonCategory jsonCategory);
    }

    public RecyclerViewGridAdapter(Context context, List<JsonCategory> categories,
                                   Map<String, ArrayList<BizStoreElastic>> queueMap, OnItemClickListener listener) {
        this.categories = categories;
        this.queueMap = queueMap;
        this.context = context;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private TextView tv_detail;
        private TextView tv_noinq;
        private ImageView iv_main;
        private CardView card_view;

        public ViewHolder(View v) {
            super(v);
            tv_title = v.findViewById(R.id.tv_title);
            tv_detail = v.findViewById(R.id.tv_detail);
            tv_noinq = v.findViewById(R.id.tv_noinq);
            iv_main = v.findViewById(R.id.iv_main);
            card_view = v.findViewById(R.id.card_view);
        }
    }

    @Override
    public RecyclerViewGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_category, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder Vholder, final int position) {
        final JsonCategory jsonCategory = categories.get(position);
        List<BizStoreElastic> jsonQueues = queueMap.get(jsonCategory.getBizCategoryId());
        BizStoreElastic jsonQueue = null;
        if (!jsonQueues.isEmpty()) {
            jsonQueue = jsonQueues.get(0);
        }
        Vholder.tv_title.setText(jsonCategory.getCategoryName());
        // Vholder.tv_detail.setText(getAdditionalCardText(jsonQueue));
        Vholder.tv_noinq.setText(String.valueOf(jsonQueues.size()));

        Picasso.with(context)
                .load("https://noqapp.com/imgs/240x120/a.jpeg")
                .into(Vholder.iv_main);
        Vholder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCategoryItemClick(position, jsonCategory);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    private String getAdditionalCardText(JsonQueue jsonQueue) {
        String additionalText;
        if (jsonQueue.isDayClosed()) {
            //Fetch for tomorrow when closed
            additionalText = jsonQueue.getDisplayName() + " is closed today.";
        } else if (getTimeIn24HourFormat() >= jsonQueue.getStartHour() && getTimeIn24HourFormat() < jsonQueue.getEndHour()) {
            //Based on location let them know in how much time they will reach or suggest the next queue.
            additionalText = jsonQueue.getDisplayName()
                    + " is open & can service you now. Click to join the queue.";
        } else {
            if (getTimeIn24HourFormat() >= jsonQueue.getTokenAvailableFrom()) {
                additionalText = jsonQueue.getDisplayName()
                        + " opens at "
                        + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getStartHour())
                        + ". Join queue now to save time.";
            } else {
                additionalText = jsonQueue.getDisplayName()
                        + " can service you at "
                        + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getStartHour())
                        + ". You can join this queue at "
                        + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getTokenAvailableFrom());
            }
        }

        return additionalText;
    }
}