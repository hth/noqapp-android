package com.noqapp.client.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noqapp.client.R;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;

import java.util.List;

/**
 * Created by omkar on 4/2/17.
 */

public class ListqueueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TYPE_HISTORY = 1;
    private static final int TYPE_CURRENT = 0;
    private List<JsonTokenAndQueue> list;
    private List<JsonTokenAndQueue> historyList;
    private Context context;
    private int historyCount_row = 0;

    public ListqueueAdapter(Context context, List<JsonTokenAndQueue> list, List<JsonTokenAndQueue> historylist) {
        this.context = context;
        this.list = list;
        this.historyList = historylist;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CURRENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_currentqueue, parent, false);
            return new ListQueueVH(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_historyqueue, parent, false);
            return new ListHistoryQueueVH(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ListQueueVH) {
            ListQueueVH mholder = (ListQueueVH) holder;
            JsonTokenAndQueue queue = list.get(position);
            mholder.txtnumber.setText(String.valueOf(position));
            mholder.txtStoreName.setText(queue.getBusinessName());
            mholder.txtStorePhoneNumber.setText(queue.getStorePhone());
            mholder.txtToken.setText(String.valueOf(queue.getToken()));
        } else {
            ListHistoryQueueVH mholder = (ListHistoryQueueVH) holder;
            JsonTokenAndQueue queue = historyList.get(historyCount_row);
            mholder.txtStoreName.setText(queue.getBusinessName());
            mholder.txtStorePhoneNumber.setText(queue.getStorePhone());
            mholder.txtToken.setText(String.valueOf(queue.getToken()));
            historyCount_row++;

        }

    }

    @Override
    public int getItemCount() {
        int count = list.size() + historyList.size();
        return count;
    }

    @Override
    public int getItemViewType(int position) {

        int result = TYPE_HISTORY;
        int currentCount = list.size() - 1;
        if (currentCount >= position) {
            result = TYPE_CURRENT;
        }
        return result;
    }

    public class ListQueueVH extends RecyclerView.ViewHolder {
        public TextView txtnumber, txtStoreName, txtStorePhoneNumber, txtToken;

        public ListQueueVH(View itemView) {
            super(itemView);
            txtnumber = (TextView) itemView.findViewById(R.id.txtNumber);
            txtStoreName = (TextView) itemView.findViewById(R.id.txtStoreName);
            txtStorePhoneNumber = (TextView) itemView.findViewById(R.id.txtStorePhoneNo);
            txtToken = (TextView) itemView.findViewById(R.id.txtToken);
        }

    }

    public class ListHistoryQueueVH extends RecyclerView.ViewHolder {
        public TextView txtStoreName, txtStorePhoneNumber, txtToken;

        public ListHistoryQueueVH(View itemView) {
            super(itemView);
            txtStoreName = (TextView) itemView.findViewById(R.id.txtStoreName);
            txtStorePhoneNumber = (TextView) itemView.findViewById(R.id.txtStorePhoneNo);
            txtToken = (TextView) itemView.findViewById(R.id.txtToken);
        }

    }


}
