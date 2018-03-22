package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.toremove.DataModel;

import java.util.ArrayList;


public class RecyclerCustomAdapter extends RecyclerView.Adapter<RecyclerCustomAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<DataModel> dataSet;

    public interface OnItemClickListener {
        void onItemClick(DataModel item, View view, int pos);
    }

    private final OnItemClickListener listener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewVersion;
        ImageView imageViewIcon;
        CardView card_view;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewVersion = (TextView) itemView.findViewById(R.id.textViewVersion);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
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

        TextView textViewName = holder.textViewName;
        TextView textViewVersion = holder.textViewVersion;
        ImageView imageView = holder.imageViewIcon;

        textViewName.setText(dataSet.get(listPosition).getName());
        if(!TextUtils.isEmpty(dataSet.get(listPosition).getVersion()))
           textViewVersion.setText(dataSet.get(listPosition).getVersion());

        // textViewName.setBackgroundColor(Color.parseColor(colorCodes[listPosition%colorCodes.length]));
        imageView.setBackground(ContextCompat.getDrawable(context, dataSet.get(listPosition).getImage()));
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(dataSet.get(listPosition), v,listPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
