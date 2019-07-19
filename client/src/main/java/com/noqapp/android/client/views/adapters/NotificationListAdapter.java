package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.database.utils.NotificationDB;
import com.noqapp.android.client.utils.GetTimeAgoUtils;
import com.noqapp.android.common.pojos.DisplayNotification;
import com.noqapp.android.common.utils.CommonHelper;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

public class NotificationListAdapter extends BaseAdapter {
    private Context context;
    private List<DisplayNotification> notificationsList;

    public NotificationListAdapter(Context context, List<DisplayNotification> notificationsList) {
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
            recordHolder.iv_big_image = view.findViewById(R.id.iv_big_image);
            recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        recordHolder.tv_title.setText(notificationsList.get(position).getTitle());
        recordHolder.tv_msg.setText(notificationsList.get(position).getMsg());
        try {
            String dateString = notificationsList.get(position).getNotificationCreate();
            long startDate = new Date().getTime() - CommonHelper.stringToDate(dateString).getTime();
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

        if (TextUtils.isEmpty(notificationsList.get(position).getImageUrl())) {
            recordHolder.iv_big_image.setVisibility(View.GONE);
        } else {
            recordHolder.iv_big_image.setVisibility(View.VISIBLE);
            Picasso.get().load(
                    notificationsList.get(position).getImageUrl())
                    .into(recordHolder.iv_big_image);
        }
        return view;
    }

    static class RecordHolder {
        TextView tv_title;
        TextView tv_msg;
        TextView tv_create;
        ImageView iv_business;
        ImageView iv_big_image;
        CardView cardview;

        RecordHolder() {
        }
    }
}
