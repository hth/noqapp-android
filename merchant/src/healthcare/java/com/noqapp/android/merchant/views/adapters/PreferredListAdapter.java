package com.noqapp.android.merchant.views.adapters;


import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.ParentCheckBoxObj;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PreferredListAdapter extends RecyclerView.Adapter<PreferredListAdapter.MyViewHolder> implements RadioRecyclerAdapter.UpdateGrid {

    private Context context;

    public List<ParentCheckBoxObj> getParentCheckBoxObjs() {
        return parentCheckBoxObjs;
    }

    private List<ParentCheckBoxObj> parentCheckBoxObjs;

    public PreferredListAdapter(Context context, List<ParentCheckBoxObj> parentCheckBoxObjs) {
        this.context = context;
        this.parentCheckBoxObjs = parentCheckBoxObjs;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_prefered_store, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tv_title.setText(parentCheckBoxObjs.get(position).getJsonTopic().getDisplayName());
        holder.cardview.setCardBackgroundColor(Color.TRANSPARENT);


        RadioRecyclerAdapter rca_xray = new RadioRecyclerAdapter(context, parentCheckBoxObjs.get(position).getCheckBoxObjList(), position, this, parentCheckBoxObjs.get(position).getSelectedPos());
        holder.rcv.setLayoutManager(new GridLayoutManager(context, 2));
        holder.rcv.setAdapter(rca_xray);

    }


    @Override
    public int getItemCount() {
        return parentCheckBoxObjs.size();
    }

    @Override
    public void notifyView(int parentPos, int checkedPos) {
        parentCheckBoxObjs.get(parentPos).setSelectedPos(checkedPos);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        CardView cardview;
        RecyclerView rcv;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            cardview = itemView.findViewById(R.id.cardview);
            rcv = itemView.findViewById(R.id.rcv);
        }
    }

}

