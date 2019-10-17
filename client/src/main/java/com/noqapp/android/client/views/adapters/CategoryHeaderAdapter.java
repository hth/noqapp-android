package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonCategory;

import java.util.List;

public class CategoryHeaderAdapter extends RecyclerView.Adapter {

    private final OnItemClickListener listener;
    private List<JsonCategory> categories;
    private Context context;
    private int selected_pos = 0;

    public CategoryHeaderAdapter(Context context, List<JsonCategory> categories,
                                 OnItemClickListener listener, int selected_pos) {
        this.categories = categories;
        this.context = context;
        this.listener = listener;
        this.selected_pos = selected_pos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_header_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder Vholder, final int position) {
        ViewHolder holder = (ViewHolder) Vholder;
        final JsonCategory jsonCategory = categories.get(position);
        holder.ll_header.setOnClickListener((View v) -> {
            listener.onCategoryItemClick(position, jsonCategory);
        });
        holder.tv_menu_header.setText(jsonCategory.getCategoryName());
        if (selected_pos == position) {
            holder.ll_header.setBackgroundColor(ContextCompat.getColor(context, R.color.theme_color_dark));
            holder.tv_menu_header.setTextColor(ContextCompat.getColor(context, R.color.theme_color_dark));
        } else {
            holder.ll_header.setBackgroundColor(Color.WHITE);
            holder.tv_menu_header.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public CategoryHeaderAdapter setSelectedPosition(int selected_pos) {
        this.selected_pos = selected_pos;
        return this;
    }

    public interface OnItemClickListener {
        void onCategoryItemClick(int pos, JsonCategory jsonCategory);
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