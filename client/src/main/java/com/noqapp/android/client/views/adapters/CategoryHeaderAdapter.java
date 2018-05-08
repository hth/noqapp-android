package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonCategory;

import java.util.List;

public class CategoryHeaderAdapter extends RecyclerView.Adapter<CategoryHeaderAdapter.ViewHolder> {

    private final OnItemClickListener listener;
    private List<JsonCategory> categories;
    private Context context;
    private int selected_pos = 0;
    public interface OnItemClickListener {
        void onCategoryItemClick(int pos, JsonCategory jsonCategory);
    }

    public CategoryHeaderAdapter(Context context, List<JsonCategory> categories,
                                 OnItemClickListener listener) {
        this.categories = categories;
        this.context = context;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_menu_header;
        private LinearLayout ll_header;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_menu_header = itemView.findViewById(R.id.tv_menu_header);
            this.ll_header = itemView.findViewById(R.id.ll_header);
        }
    }

    @Override
    public CategoryHeaderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.menu_header_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder Vholder, final int position) {
        final JsonCategory jsonCategory = categories.get(position);
        Vholder.tv_menu_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCategoryItemClick(position, jsonCategory);
            }
        });
        Vholder.tv_menu_header.setText(jsonCategory.getCategoryName());
        if (selected_pos == position) {
            Vholder.ll_header.setBackgroundColor(ContextCompat.getColor(context, R.color.colorActionbar));
            Vholder.tv_menu_header.setTextColor(ContextCompat.getColor(context, R.color.colorActionbar));
        } else {
            Vholder.ll_header.setBackgroundColor(Color.WHITE);
            Vholder.tv_menu_header.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public CategoryHeaderAdapter setSelected_pos(int selected_pos) {
        this.selected_pos = selected_pos;
        return this;
    }

}