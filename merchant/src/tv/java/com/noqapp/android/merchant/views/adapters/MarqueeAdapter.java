package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.noqapp.android.merchant.R;

import java.util.List;


public class MarqueeAdapter extends BaseAdapter {
    private Context context;
    private final OnItemClickListener listener;
    private List<String> marqueeList;

    public interface OnItemClickListener {
        void deleteMarquee(String item);

    }

    public MarqueeAdapter(Context context, List<String> marqueeList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.marqueeList = marqueeList;
        this.listener = onItemClickListener;
    }

    public int getCount() {
        return this.marqueeList.size();
    }

    public Object getItem(int n) {
        return null;
    }

    public long getItemId(int n) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        RecordHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            recordHolder = new RecordHolder();
            view = layoutInflater.inflate(R.layout.list_item_marquee, parent, false);
            recordHolder.iv_delete = view.findViewById(R.id.iv_delete);
            recordHolder.tv_title = view.findViewById(R.id.tv_title);
            recordHolder.card_view = view.findViewById(R.id.card_view);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }

        recordHolder.tv_title.setText(marqueeList.get(position));

        recordHolder.iv_delete.setOnClickListener((View v) -> {
           if(null != listener){
               listener.deleteMarquee(marqueeList.get(position));
           }
        });

        return view;
    }

    static class RecordHolder {
        TextView tv_title;
        ImageView iv_delete;
        CardView card_view;

        RecordHolder() {
        }
    }
}
