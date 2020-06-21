package com.noqapp.android.merchant.views.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.pojos.DisplayNotification;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.database.utils.NotificationDB;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.GetTimeAgoUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationListAdapter extends BaseAdapter {
    private Context context;
    private List<DisplayNotification> notificationsList;
    private DeleteRecord deleteRecord;

    public NotificationListAdapter(
            Context context,
            List<DisplayNotification> notificationsList,
            DeleteRecord deleteRecord) {
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
            view = layoutInflater.inflate(R.layout.listitem_notification, viewGroup, false);
            recordHolder.tv_msg = view.findViewById(R.id.tv_msg);
            recordHolder.tv_title = view.findViewById(R.id.tv_title);
            recordHolder.tv_create = view.findViewById(R.id.tv_create);
            recordHolder.iv_delete = view.findViewById(R.id.iv_delete);
            recordHolder.iv_copy = view.findViewById(R.id.iv_copy);
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
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.ISO8601_FMT, Locale.getDefault());
            Date date = sdf.parse(dateString);
            long startDate = new Date().getTime() - date.getTime();
            recordHolder.tv_create.setText(GetTimeAgoUtils.getTimeInAgo(startDate));
        } catch (Exception e) {
            e.printStackTrace();
            recordHolder.tv_create.setText("");
        }

        if (displayNotification.getStatus().equals(NotificationDB.KEY_UNREAD)) {
            recordHolder.cardview.setCardBackgroundColor(Color.WHITE);
        } else {
            recordHolder.cardview.setCardBackgroundColor(Color.parseColor("#f6f6f6"));
        }
        recordHolder.iv_delete.setOnClickListener(v -> {
            if (null != deleteRecord) {
                deleteRecord.deleteNotification(displayNotification);
            }
        });

        recordHolder.iv_copy.setOnClickListener(v -> {
            copyText(displayNotification.getMsg());
        });
        return view;
    }

    static class RecordHolder {
        TextView tv_title;
        TextView tv_msg;
        TextView tv_create;
        ImageView iv_delete;
        ImageView iv_copy;
        CardView cardview;

        RecordHolder() {
        }
    }

    public interface DeleteRecord {
        void deleteNotification(DisplayNotification displayNotification);
    }


    private void copyText(String selectedText) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", selectedText);
        clipboard.setPrimaryClip(clip);
        if (selectedText.equals("")) {
            new CustomToast().showToast(context, "Nothing to copy");
        }else {
            new CustomToast().showToast(context, "Copied");
            Log.d("Notification text copy",selectedText);
        }
    }
}
