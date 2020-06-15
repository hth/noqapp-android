package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.TokenStatusUtils;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.model.types.QueueOrderTypeEnum;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.Formatter;

import java.util.List;

public class CurrentActivityAdapter extends RecyclerView.Adapter {
    private static final String TAG = CurrentActivityAdapter.class.getSimpleName();
    private final Context context;
    private final OnItemClickListener listener;
    private List<Object> dataSet;

    public CurrentActivityAdapter(List<Object> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item_current_activity, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder Vholder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) Vholder;
        Object object = dataSet.get(listPosition);
        if (object instanceof JsonTokenAndQueue) {
            holder.ll_queue.setVisibility(View.VISIBLE);
            holder.ll_appointment.setVisibility(View.GONE);
            final JsonTokenAndQueue jsonTokenAndQueue = (JsonTokenAndQueue) object;
            holder.tv_name.setText(jsonTokenAndQueue.getDisplayName());
            holder.card_view.setOnClickListener((View v) -> {
                listener.currentQorOrderItemClick(jsonTokenAndQueue);
            });
            holder.tv_position_in_queue_value.setText(String.valueOf(jsonTokenAndQueue.afterHowLong()));
            if (jsonTokenAndQueue.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.Q) {
                holder.tv_token_label.setText(context.getString(R.string.token));
                if (jsonTokenAndQueue.getToken() <= jsonTokenAndQueue.getServingNumber()) {
                    holder.tv_position_in_queue_label.setText("It's your turn!!!");
                    holder.tv_position_in_queue_value.setVisibility(View.GONE);
                } else if (jsonTokenAndQueue.getServingNumber() == 0) {
                    holder.tv_position_in_queue_label.setText("Queue not yet started");
                    holder.tv_position_in_queue_value.setVisibility(View.GONE);
                    switch (jsonTokenAndQueue.getBusinessType()) {
                        case CD:
                        case CDQ:
                            String slot = TokenStatusUtils.timeSlot(jsonTokenAndQueue.getServiceEndTime());
                            if (!TextUtils.isEmpty(slot)) {
                                holder.tv_wait_time.setText(String.format(this.context.getString(R.string.time_slot), slot));
                            }
                            break;
                        default:
                            // Display wait time
                            String waitTime = displayWaitTimes(jsonTokenAndQueue);
                            if (!TextUtils.isEmpty(waitTime)) {
                                holder.tv_wait_time.setText(String.format(this.context.getString(R.string.estimated_time), waitTime));
                            }
                    }
                } else {
                    holder.tv_position_in_queue_label.setText(context.getString(R.string.position_in_queue_label));
                    holder.tv_position_in_queue_value.setVisibility(View.VISIBLE);
                    switch (jsonTokenAndQueue.getBusinessType()) {
                        case CD:
                        case CDQ:
                            String slot = TokenStatusUtils.timeSlot(jsonTokenAndQueue.getServiceEndTime());
                            if (!TextUtils.isEmpty(slot)) {
                                holder.tv_wait_time.setText(String.format(this.context.getString(R.string.time_slot), slot));
                            }
                            break;
                        default:
                            // Display wait time
                            String waitTime = displayWaitTimes(jsonTokenAndQueue);
                            if (!TextUtils.isEmpty(waitTime)) {
                                holder.tv_wait_time.setText(String.format(this.context.getString(R.string.estimated_time), waitTime));
                            }
                    }
                }
            } else if (jsonTokenAndQueue.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.O) {
                holder.tv_token_label.setText(context.getString(R.string.order));
                if (jsonTokenAndQueue.getToken() - jsonTokenAndQueue.getServingNumber() <= 0) {
                    switch (jsonTokenAndQueue.getPurchaseOrderState()) {
                        case OP:
                            holder.tv_position_in_queue_label.setText("Order being prepared");
                            break;
                        default:
                            holder.tv_position_in_queue_label.setText(jsonTokenAndQueue.getPurchaseOrderState().getFriendlyDescription());
                            break;
                    }
                    holder.tv_position_in_queue_value.setVisibility(View.GONE);
                } else if (jsonTokenAndQueue.getServingNumber() == 0) {
                    holder.tv_position_in_queue_label.setText("Queue not yet started");
                    holder.tv_position_in_queue_value.setVisibility(View.GONE);
                } else {
                    holder.tv_position_in_queue_label.setText(context.getString(R.string.serving_now));
                    holder.tv_position_in_queue_value.setVisibility(View.VISIBLE);
                }
            }

            holder.tv_token_value.setText(String.valueOf(jsonTokenAndQueue.getToken()));
        } else if (object instanceof JsonSchedule) {
            JsonSchedule jsonSchedule = (JsonSchedule) object;
            holder.ll_queue.setVisibility(View.GONE);
            holder.ll_appointment.setVisibility(View.VISIBLE);
            holder.tv_title.setText(jsonSchedule.getJsonQueueDisplay().getDisplayName());
            holder.tv_degree.setText(MedicalDepartmentEnum.valueOf(jsonSchedule.getJsonQueueDisplay().getBizCategoryId()).getDescription());
            holder.tv_store_address.setText(AppUtils.getStoreAddress(jsonSchedule.getJsonQueueDisplay().getTown(), jsonSchedule.getJsonQueueDisplay().getArea()));
            holder.tv_schedule_time.setText(Formatter.convertMilitaryTo24HourFormat(jsonSchedule.getStartTime()));
            try {
                String date = CommonHelper.SDF_DOB_FROM_UI.format(CommonHelper.SDF_YYYY_MM_DD.parse(jsonSchedule.getScheduleDate()));
                holder.tv_schedule_date.setText(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.card_view.setOnClickListener((View v) -> {
                listener.currentAppointmentClick(jsonSchedule);
            });
        }
    }

    // Display wait time
    private String  displayWaitTimes(final JsonTokenAndQueue jsonTokenAndQueue){
        try {
            long avgServiceTime = jsonTokenAndQueue.getAverageServiceTime();
            if (avgServiceTime == 0) {
                SharedPreferences prefs = this.context.getSharedPreferences(Constants.APP_PACKAGE, Context.MODE_PRIVATE);
                avgServiceTime = prefs.getLong(String.format(Constants.ESTIMATED_WAIT_TIME_PREF_KEY, jsonTokenAndQueue.getCodeQR()), 0);
            }
            return TokenStatusUtils.calculateEstimatedWaitTime(
                    avgServiceTime,
                    jsonTokenAndQueue.afterHowLong(),
                    jsonTokenAndQueue.getQueueStatus(),
                    jsonTokenAndQueue.getStartHour());
        } catch (Exception e) {
            Log.e(TAG, "Error setting wait time reason: " + e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void currentQorOrderItemClick(JsonTokenAndQueue item);

        void currentAppointmentClick(JsonSchedule jsonSchedule);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private CardView card_view;
        private TextView tv_token_value;
        private TextView tv_token_label;
        private TextView tv_wait_time;
        private TextView tv_position_in_queue_value;
        private TextView tv_position_in_queue_label;
        private LinearLayout ll_appointment;
        private LinearLayout ll_queue;

        private TextView tv_title;
        private TextView tv_degree;
        private TextView tv_schedule_time;
        private TextView tv_schedule_date;
        private TextView tv_store_address;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_token_value = itemView.findViewById(R.id.tv_token_value);
            this.tv_token_label = itemView.findViewById(R.id.tv_token_label);
            this.tv_wait_time = itemView.findViewById(R.id.tv_wait_time);
            this.tv_position_in_queue_label = itemView.findViewById(R.id.tv_position_in_queue_label);
            this.tv_position_in_queue_value = itemView.findViewById(R.id.tv_position_in_queue_value);
            this.ll_appointment = itemView.findViewById(R.id.ll_appointment);
            this.ll_queue = itemView.findViewById(R.id.ll_queue);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_degree = itemView.findViewById(R.id.tv_degree);
            tv_schedule_time = itemView.findViewById(R.id.tv_schedule_time);
            tv_schedule_date = itemView.findViewById(R.id.tv_schedule_date);
            tv_store_address = itemView.findViewById(R.id.tv_store_address);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }

}
