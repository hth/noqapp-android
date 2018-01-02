package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.Formatter;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.fragments.JoinFragment;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;

import java.util.List;


public class CategoryListAdapter extends BaseAdapter {
    private Context context;
    private List<JsonQueue> jsonQueues;

    public CategoryListAdapter(Context context, List<JsonQueue> jsonQueues) {
        this.context = context;
        this.jsonQueues = jsonQueues;
    }

    public int getCount() {
        return this.jsonQueues.size();
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
            view = layoutInflater.inflate(R.layout.listitem_category, null);
            // recordHolder.tv_number = (TextView) view.findViewById(R.id.tv_number);
            recordHolder.tv_queue_name = (TextView) view.findViewById(R.id.tv_queue_name);
            recordHolder.tv_store_timing = (TextView) view.findViewById(R.id.tv_store_timing);
            recordHolder.tv_store_status = (TextView) view.findViewById(R.id.tv_store_status);
            // recordHolder.tv_inqueue = (TextView) view.findViewById(R.id.tv_inqueue);
            // recordHolder.tv_label = (TextView) view.findViewById(R.id.tv_label);
            recordHolder.cardview = (CardView) view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        final JsonQueue jsonQueue = jsonQueues.get(position);

        recordHolder.tv_queue_name.setText(jsonQueue.getDisplayName());
        if (jsonQueue.isDayClosed()) {
            recordHolder.tv_store_timing.setText(context.getString(R.string.store_closed));
        } else {
            recordHolder.tv_store_timing.setText(
                    context.getString(R.string.store_hour)
                            + " "
                            + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getStartHour())
                            + " - "
                            + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getEndHour()));
        }

        int systemHourMinutes = AppUtilities.getSystemHourMinutes();
        if (!jsonQueue.isDayClosed()) {
            // Before Token Available Time
            if (systemHourMinutes < jsonQueue.getTokenAvailableFrom()) {
                if (jsonQueue.getBusinessType() != null) {
                    switch (jsonQueue.getBusinessType()) {
                        case DO:
                            recordHolder.tv_store_status.setText("Closed Now. Appointment booking starts at " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getTokenAvailableFrom()));
                            break;
                        default:
                            recordHolder.tv_store_status.setText("Closed Now. You can join queue at " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getTokenAvailableFrom()));
                            break;
                    }
                } else {
                    recordHolder.tv_store_status.setText("Closed Now. You can join queue at " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getTokenAvailableFrom()));
                }
                recordHolder.tv_store_status.setTextColor(Color.parseColor("#095053"));
            }

            // Between Token Available and Start Hour
            if (systemHourMinutes >= jsonQueue.getTokenAvailableFrom() && systemHourMinutes < jsonQueue.getStartHour()) {
                if (jsonQueue.getBusinessType() != null) {
                    switch (jsonQueue.getBusinessType()) {
                        case DO:
                            recordHolder.tv_store_status.setText("Appointment booking for today started");
                            break;
                        default:
                            recordHolder.tv_store_status.setText("Now you can join queue. Queue service will begin at " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getStartHour()));
                            break;
                    }
                } else {
                    recordHolder.tv_store_status.setText("Now you can join queue. Queue service will begin at " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getStartHour()));
                }
                recordHolder.tv_store_status.setTextColor(Color.parseColor("#a86041"));
            }

            // After Start Hour and Before Token Not Available From
            if (systemHourMinutes >= jsonQueue.getStartHour() && systemHourMinutes < jsonQueue.getTokenNotAvailableFrom()) {
                if (jsonQueue.getBusinessType() != null) {
                    switch (jsonQueue.getBusinessType()) {
                        case DO:
                            recordHolder.tv_store_status.setText("Open Now. Book Appointments");
                            break;
                        default:
                            recordHolder.tv_store_status.setText("Open Now. Join Queue.");
                            break;
                    }
                } else {
                    recordHolder.tv_store_status.setText("Open Now. Join Queue.");
                }
                recordHolder.tv_store_status.setTextColor(Color.parseColor("#095053"));
            }

            // When between Token Not Available From and End Hour
            if (systemHourMinutes >= jsonQueue.getTokenNotAvailableFrom() && systemHourMinutes < jsonQueue.getEndHour()) {
                recordHolder.tv_store_status.setText("Closing Soon");
                recordHolder.tv_store_status.setTextColor(Color.parseColor("#a86041"));
            }

            // When after End Hour
            if (systemHourMinutes >= jsonQueue.getEndHour()) {
                recordHolder.tv_store_status.setText("Closed Now");
            }

            recordHolder.tv_store_status.setTypeface(null, Typeface.BOLD);
        } else {
            // Show when will this be open next.
            recordHolder.tv_store_status.setText("Show some smart message");
            recordHolder.tv_store_status.setTextColor(Color.DKGRAY);
            recordHolder.tv_store_status.setTypeface(null, Typeface.BOLD);
        }

        recordHolder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putString(NoQueueBaseFragment.KEY_CODE_QR, "");
                b.putBoolean(NoQueueBaseFragment.KEY_FROM_LIST, false);
                b.putBoolean(NoQueueBaseFragment.KEY_IS_HISTORY, false);
                b.putSerializable("object", jsonQueue);
                JoinFragment jf = new JoinFragment();
                jf.setArguments(b);
                NoQueueBaseFragment.replaceFragmentWithBackStack(LaunchActivity.getLaunchActivity(), R.id.frame_layout, jf, JoinFragment.class.getName(), LaunchActivity.tabHome);
            }
        });
        return view;
    }

    static class RecordHolder {
        // TextView tv_number;
        TextView tv_queue_name;
        TextView tv_store_timing;
        TextView tv_store_status;
        // TextView tv_inqueue;
        // TextView tv_label;
        CardView cardview;

        RecordHolder() {
        }
    }
}
