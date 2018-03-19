package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.database.utils.NotificationDB;
import com.noqapp.android.merchant.presenter.beans.NotificationBeans;

import java.util.List;

public class NotificationListAdapter extends BaseAdapter {
    private static final String TAG = NotificationListAdapter.class.getSimpleName();
    private Context context;
    private List<NotificationBeans> notificationsList;

    public NotificationListAdapter(Context context, List<NotificationBeans> notificationsList) {
        this.context = context;
        this.notificationsList = notificationsList;
    }

    public int getCount() {
        return this.notificationsList.size();
    }

    public Object getItem(int n) {
        return null;
    }

    public long getItemId(int n) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        RecordHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            recordHolder = new RecordHolder();
            view = layoutInflater.inflate(R.layout.listitem_notification, null);
            recordHolder.tv_msg = (TextView) view.findViewById(R.id.tv_msg);
            recordHolder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            recordHolder.cardview = (CardView) view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        recordHolder.tv_title.setText(notificationsList.get(position).getTitle());
        recordHolder.tv_msg.setText(notificationsList.get(position).getMsg());
        if(notificationsList.get(position).getStatus().equals(NotificationDB.KEY_UNREAD)){
            recordHolder.tv_title.setTypeface(null, Typeface.BOLD);
            recordHolder.cardview.setCardBackgroundColor(Color.WHITE);
        }else{
            recordHolder.tv_title.setTypeface(null, Typeface.NORMAL);
            recordHolder.cardview.setCardBackgroundColor(ContextCompat.getColor(context,R.color.disable_list));
        }
        return view;
    }

    static class RecordHolder {
        TextView tv_title;
        TextView tv_msg;
        CardView cardview;

        RecordHolder() {
        }
    }
}
