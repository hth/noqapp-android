package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.database.utils.NotificationDB;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.GetTimeAgoUtils;
import com.noqapp.android.common.beans.NotificationBeans;
import com.noqapp.android.common.model.types.BusinessTypeEnum;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationListAdapter extends BaseAdapter {
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
            recordHolder.iv_business = view.findViewById(R.id.iv_business);
            recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        recordHolder.tv_title.setText(notificationsList.get(position).getTitle());
        recordHolder.tv_msg.setText(notificationsList.get(position).getMsg());
        BusinessTypeEnum businessType = notificationsList.get(position).getBusinessType();
        switch (businessType) {
            case DO:
                recordHolder.iv_business.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hospital));
                recordHolder.iv_business.setColorFilter(context.getResources().getColor(R.color.bussiness_hospital));
                break;
            case BK:
                recordHolder.iv_business.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bank));
                recordHolder.iv_business.setColorFilter(context.getResources().getColor(R.color.bussiness_bank));
                break;
            default:
                recordHolder.iv_business.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.store));
                recordHolder.iv_business.setColorFilter(context.getResources().getColor(R.color.bussiness_store));
        }
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
            recordHolder.tv_title.setTypeface(null, Typeface.BOLD);
            recordHolder.cardview.setCardBackgroundColor(Color.WHITE);
        } else {
            recordHolder.tv_title.setTypeface(null, Typeface.NORMAL);
            recordHolder.cardview.setCardBackgroundColor(ContextCompat.getColor(context, R.color.color_me_bg));
        }
        return view;
    }

    static class RecordHolder {
        TextView tv_title;
        TextView tv_msg;
        TextView tv_create;
        ImageView iv_business;
        CardView cardview;

        RecordHolder() {
        }
    }
}
