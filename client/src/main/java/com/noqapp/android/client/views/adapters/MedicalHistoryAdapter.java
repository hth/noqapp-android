package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonMedicalRecord;

import java.util.List;

public class MedicalHistoryAdapter extends BaseAdapter {
    private static final String TAG = MedicalHistoryAdapter.class.getSimpleName();
    private Context context;
    private List<JsonMedicalRecord> notificationsList;

    public MedicalHistoryAdapter(Context context, List<JsonMedicalRecord> notificationsList) {
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
            view = layoutInflater.inflate(R.layout.listitem_medical_history, null);
            recordHolder.tv_msg = (TextView) view.findViewById(R.id.tv_msg);
            recordHolder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            recordHolder.tv_create = (TextView) view.findViewById(R.id.tv_create);
            recordHolder.cardview = (CardView) view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        recordHolder.tv_title.setText(notificationsList.get(position).getChiefComplain());
        recordHolder.tv_msg.setText(notificationsList.get(position).getClinicalFinding());


//        try {
//            String dateString = notificationsList.get(position).getNotificationCreate();
//            SimpleDateFormat sdf = new SimpleDateFormat(Constants.ISO8601_FMT, Locale.getDefault());
//            Date date = sdf.parse(dateString);
//            long startDate = new Date().getTime() - date.getTime();
//            recordHolder.tv_create.setText(GetTimeAgoUtils.getTimeInAgo(startDate));
//        } catch (Exception e) {
//            e.printStackTrace();
//            recordHolder.tv_create.setText("");
//        }
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
