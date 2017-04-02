package com.noqapp.client.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noqapp.client.R;
import com.noqapp.client.presenter.beans.JsonToken;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;

import org.w3c.dom.Text;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by omkar on 4/2/17.
 */

public class ListqueueAdapter extends RecyclerView.Adapter<ListqueueAdapter.ListQueueVH> {


    private  List<JsonTokenAndQueue> list;
    private  Context context;

    public ListqueueAdapter (Context context , List<JsonTokenAndQueue> list)
    {
        this.context = context;
        this.list = list;
    }

    @Override
    public ListQueueVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currentqueue,parent,false);
        return new ListQueueVH(view);
    }

    @Override
    public void onBindViewHolder(ListQueueVH holder, int position) {

        JsonTokenAndQueue queue = list.get(position);
        holder.txtnumber.setText(String.valueOf(position));
        holder.txtStoreName.setText(queue.getBusinessName());
        holder.txtStorePhoneNumber.setText(queue.getStorePhone());
        holder.txtToken.setText(String.valueOf(queue.getToken()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ListQueueVH extends RecyclerView.ViewHolder
    {
       public TextView txtnumber, txtStoreName , txtStorePhoneNumber , txtToken;

        public ListQueueVH(View itemView) {
            super(itemView);
            txtnumber = (TextView) itemView.findViewById(R.id.txtNumber);
            txtStoreName = (TextView) itemView.findViewById(R.id.txtStoreName);
            txtStorePhoneNumber = (TextView) itemView.findViewById(R.id.txtStorePhoneNo);
            txtToken = (TextView) itemView.findViewById(R.id.txtToken);
        }

    }



}
