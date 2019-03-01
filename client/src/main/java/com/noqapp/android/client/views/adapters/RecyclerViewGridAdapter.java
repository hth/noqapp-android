package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonCategory;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ImageUtils;

import com.squareup.picasso.Picasso;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecyclerViewGridAdapter extends RecyclerView.Adapter<RecyclerViewGridAdapter.ViewHolder> {

    private final OnItemClickListener listener;
    private List<JsonCategory> categories;
    private Map<String, ArrayList<BizStoreElastic>> queueMap;
    private Context context;

    public RecyclerViewGridAdapter(Context context, List<JsonCategory> categories,
                                   Map<String, ArrayList<BizStoreElastic>> queueMap, OnItemClickListener listener) {
        this.categories = categories;
        this.queueMap = queueMap;
        this.context = context;
        this.listener = listener;
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
            switch (jsonQueue.getBusinessType()) {
                case BK:
                    Vholder.iv_main.setBackground(ContextCompat.getDrawable(context, R.drawable.bank_bg));
                    break;
                default:
                    Vholder.iv_main.setBackground(ContextCompat.getDrawable(context, R.drawable.dep_bg));
            }

        }
        Vholder.tv_title.setText(jsonCategory.getCategoryName());
        if (!TextUtils.isEmpty(jsonCategory.getDisplayImage())) {
            Picasso.with(context)
                    .load(AppUtilities.getImageUrls(BuildConfig.SERVICE_BUCKET, jsonCategory.getDisplayImage()))
                    .placeholder(ImageUtils.getThumbPlaceholder(context))
                    .error(ImageUtils.getThumbErrorPlaceholder(context))
                    .into(Vholder.iv_main);
        }
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

    public interface OnItemClickListener {
        void onCategoryItemClick(int pos, JsonCategory jsonCategory);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private ImageView iv_main;
        private CardView card_view;

        public ViewHolder(View v) {
            super(v);
            tv_title = v.findViewById(R.id.tv_title);
            iv_main = v.findViewById(R.id.iv_main);
            card_view = v.findViewById(R.id.card_view);
        }
    }


}