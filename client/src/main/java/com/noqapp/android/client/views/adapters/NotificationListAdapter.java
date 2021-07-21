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
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.GetTimeAgoUtils;
import com.noqapp.android.common.pojos.DisplayNotification;
import com.noqapp.android.common.utils.CommonHelper;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

public class NotificationListAdapter extends BaseAdapter {
    private Context context;
    private List<DisplayNotification> notificationsList;
    private DeleteRecord deleteRecord;

    public NotificationListAdapter(
        Context context,
        List<DisplayNotification> notificationsList,
        DeleteRecord deleteRecord
    ) {
        this.context = context;
        this.notificationsList = notificationsList;
        this.deleteRecord = deleteRecord;
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
            view = layoutInflater.inflate(R.layout.list_item_notification, viewGroup, false);
            recordHolder.tv_msg = view.findViewById(R.id.tvMsg);
            recordHolder.tv_title = view.findViewById(R.id.tvTitle);
            recordHolder.tv_create = view.findViewById(R.id.tvCreate);
            recordHolder.iv_business = view.findViewById(R.id.ivBusiness);
            recordHolder.iv_big_image = view.findViewById(R.id.ivBigImage);
            recordHolder.iv_delete = view.findViewById(R.id.ivDelete);
            recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        DisplayNotification displayNotification = notificationsList.get(position);
        recordHolder.tv_title.setText(displayNotification.getTitle());
        recordHolder.tv_msg.setText(displayNotification.getMsg());
        try {
            String dateString = displayNotification.getNotificationCreate();
            long startDate = new Date().getTime() - CommonHelper.stringToDate(dateString).getTime();
            recordHolder.tv_create.setText(GetTimeAgoUtils.getTimeInAgo(startDate));
        } catch (Exception e) {
            e.printStackTrace();
            recordHolder.tv_create.setText("");
        }

        if (displayNotification.getStatus().equals(Constants.KEY_UNREAD)) {
            recordHolder.cardview.setCardBackgroundColor(Color.WHITE);
        } else {
            recordHolder.cardview.setCardBackgroundColor(Color.parseColor("#f6f6f6"));
        }

        if (TextUtils.isEmpty(displayNotification.getImageUrl())) {
            recordHolder.iv_big_image.setVisibility(View.GONE);
        } else {
            recordHolder.iv_big_image.setVisibility(View.VISIBLE);
            Picasso.get().load(displayNotification.getImageUrl()).into(recordHolder.iv_big_image);
        }
        recordHolder.iv_delete.setOnClickListener(v -> {
            if (null != deleteRecord) {
                deleteRecord.deleteNotification(displayNotification);
            }
        });

        return view;
    }

    static class RecordHolder {
        TextView tv_title;
        TextView tv_msg;
        TextView tv_create;
        ImageView iv_business;
        ImageView iv_big_image;
        ImageView iv_delete;
        CardView cardview;

        RecordHolder() {
        }
    }

    public interface DeleteRecord {
        void deleteNotification(DisplayNotification displayNotification);
    }
}
