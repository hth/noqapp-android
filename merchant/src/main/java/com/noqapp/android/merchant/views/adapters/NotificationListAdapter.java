package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.noqapp.android.common.beans.NotificationBeans;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.database.utils.NotificationDB;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.GetTimeAgoUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
            view = layoutInflater.inflate(R.layout.listitem_notification, viewGroup, false);
            recordHolder.tv_msg = view.findViewById(R.id.tv_msg);
            recordHolder.tv_title = view.findViewById(R.id.tv_title);
            recordHolder.tv_create = view.findViewById(R.id.tv_create);
            recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        recordHolder.tv_title.setText(notificationsList.get(position).getTitle());
        recordHolder.tv_msg.setText(notificationsList.get(position).getMsg());


        try {
            String dateString = notificationsList.get(position).getNotificationCreate();
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.ISO8601_FMT, Locale.getDefault());
            Date date = sdf.parse(dateString);
            long startDate = new Date().getTime() - date.getTime();
            recordHolder.tv_create.setText(GetTimeAgoUtils.getTimeInAgo(startDate));
        } catch (Exception e) {
            e.printStackTrace();
            recordHolder.tv_create.setText("");
        }

        if (notificationsList.get(position).getStatus().equals(NotificationDB.KEY_UNREAD)) {
            recordHolder.cardview.setCardBackgroundColor(Color.WHITE);
        } else {
            recordHolder.cardview.setCardBackgroundColor(Color.parseColor("#f6f6f6"));
        }
        return view;
    }

    static class RecordHolder {
        TextView tv_title;
        TextView tv_msg;
        TextView tv_create;
        CardView cardview;

        RecordHolder() {
        }
    }
}
