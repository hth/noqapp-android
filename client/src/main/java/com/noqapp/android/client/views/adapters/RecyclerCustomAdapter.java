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
import com.noqapp.android.client.views.toremove.DataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class RecyclerCustomAdapter extends RecyclerView.Adapter<RecyclerCustomAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<DataModel> dataSet;

    public interface OnItemClickListener {
        void onItemClick(DataModel item, View view, int pos);
    }

    private final OnItemClickListener listener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_detail;
        private TextView tv_category;
        private ImageView iv_main;
        private CardView card_view;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_detail = (TextView) itemView.findViewById(R.id.tv_detail);
            this.tv_category = (TextView) itemView.findViewById(R.id.tv_category);
            this.iv_main = (ImageView) itemView.findViewById(R.id.iv_main);
            this.card_view = (CardView) itemView.findViewById(R.id.card_view);
        }
    }

    public RecyclerCustomAdapter(ArrayList<DataModel> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {


        holder.tv_name.setText(dataSet.get(listPosition).getName());
       // holder.tv_category.setText();
        Picasso.with(context)
                .load(dataSet.get(listPosition).getImage())
                .into(holder.iv_main);
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(dataSet.get(listPosition), v, listPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
