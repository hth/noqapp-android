package com.noqapp.client.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.noqapp.client.R;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.client.views.activities.LaunchActivity;
import com.noqapp.client.views.fragments.ScanQueueFragment;

import java.util.List;

/**
 * Created by omkar on 4/2/17.
 */

public class ListQueueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TYPE_HISTORY = 1;
    private static final int TYPE_CURRENT = 0;
    private static final int TYPE_HISTORYHeader = 2;
    private static final int TYPE_NOCURRENTQUEUE = 3;
    private static final int TYPE_NOHISTORYQUEUE = 4;
    public int histroyHeaderPosition;
    private List<JsonTokenAndQueue> list;
    private List<JsonTokenAndQueue> historyList;
    private Context context;
    private int historyCount_row = 0;

    public ListQueueAdapter(Context context, List<JsonTokenAndQueue> list, List<JsonTokenAndQueue> historylist) {
        this.context = context;
        this.list = list;
        this.historyList = historylist;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CURRENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_currentqueue, parent, false);
            return new ListQueueVH(view);
        } else if (viewType == TYPE_HISTORYHeader) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_history, parent, false);
            return new HistoryHeaderVH(view);
        } else if (viewType == TYPE_NOCURRENTQUEUE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_nocurrentqueue, parent, false);
            return new NOCurrentQueueVH(view);
        } else if (viewType == TYPE_NOHISTORYQUEUE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_nohistory, parent, false);
            return new NoHistoryQueueVH(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_historyqueueold, parent, false);
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
        } else if (holder instanceof HistoryHeaderVH) {
            HistoryHeaderVH mHistroyHeaderVH = (HistoryHeaderVH) holder;
            mHistroyHeaderVH.tv_historyHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("History Click", "History");
                    histroyHeaderPosition = histroyHeaderPosition + 1;
                    notifyItemRangeRemoved(histroyHeaderPosition, historyList.size());
                }
            });
        } else if (holder instanceof NOCurrentQueueVH) {
            NOCurrentQueueVH noCurrentQueueVH = (NOCurrentQueueVH) holder;
            noCurrentQueueVH.btn_scanQueue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((LaunchActivity) context).replaceFragmentWithoutBackStack(R.id.frame_layout, new ScanQueueFragment());
                }
            });
        } else if (holder instanceof NoHistoryQueueVH) {

        } else {
            ListHistoryQueueVH mholder = (ListHistoryQueueVH) holder;
            JsonTokenAndQueue queue = historyList.get(position - (list.size() + 1));
            mholder.txtStoreName.setText(queue.getBusinessName());
            mholder.txtStorePhoneNumber.setText(queue.getStorePhone());
            mholder.txtToken.setText(String.valueOf(queue.getToken()));

            // historyCount_row++;
            Log.v("Histroy count", "" + historyCount_row + " pos : " + "" + position);
        }

    }

    @Override
    public int getItemCount() {
        int size = list.size();
        int hsize = historyList.size();
        if (hsize == 0) {
            hsize = 1;
        }

        int count = size + hsize + 1;
        return count;
    }

    @Override
    public int getItemViewType(int position) {

        int result = TYPE_HISTORY;
        int size = list.size();
        int hsize = historyList.size();
        int currentCount = 0;
        if (size == 0 && position == 0) {
            result = TYPE_NOCURRENTQUEUE;
            return result;
        } else if (size > 0) {
            currentCount = list.size() - 1;
        }

        int historyHeaderCount = currentCount + 1;
        if (currentCount >= position) {
            result = TYPE_CURRENT;
        } else if (historyHeaderCount == position) {
            result = TYPE_HISTORYHeader;
            histroyHeaderPosition = position;

        } else if (hsize == 0) {
            result = TYPE_NOHISTORYQUEUE;
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

    public class HistoryHeaderVH extends RecyclerView.ViewHolder {
        public Button tv_historyHeader;

        public HistoryHeaderVH(View itemView) {
            super(itemView);
            tv_historyHeader = (Button) itemView.findViewById(R.id.btn_historyheader);
        }
    }

    // When List is empty
    public class NOCurrentQueueVH extends RecyclerView.ViewHolder {
        public Button btn_scanQueue;

        public NOCurrentQueueVH(View itemView) {
            super(itemView);
            btn_scanQueue = (Button) itemView.findViewById(R.id.btnscanQRCode);
        }
    }

    public class NoHistoryQueueVH extends RecyclerView.ViewHolder {

        public NoHistoryQueueVH(View itemView) {
            super(itemView);
        }
    }


}
