package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.EventDay;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.MyViewHolder> {
    private final Context context;
    private final OnItemClickListener listener;
    private List<EventDay> dataSet;


    public AppointmentListAdapter(List<EventDay> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_appointment_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        JsonSchedule jsonSchedule = (JsonSchedule) dataSet.get(position).getEventObject();
        holder.tv_title.setText(jsonSchedule.getJsonProfile().getName());
        holder.tv_gender_age.setText(new AppUtils().calculateAge(jsonSchedule.getJsonProfile().
                getBirthday()) + ", " + jsonSchedule.getJsonProfile().getGender().name());
        holder.tv_customer_mobile.setText(
                TextUtils.isEmpty(jsonSchedule.getJsonProfile().getPhoneRaw())
                        ? context.getString(R.string.unregister_user)
                        : PhoneFormatterUtil.formatNumber(LaunchActivity.getLaunchActivity().
                                getUserProfile().getCountryShortName(),
                        jsonSchedule.getJsonProfile().getPhoneRaw()));
        holder.tv_customer_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.tv_customer_mobile.getText().equals(context.getString(R.string.unregister_user)))
                    new AppUtils().makeCall(LaunchActivity.getLaunchActivity(), PhoneFormatterUtil.
                            formatNumber(LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName(),
                                    jsonSchedule.getJsonProfile().getPhoneRaw()));
            }
        });

        try {
            if (!TextUtils.isEmpty(jsonSchedule.getJsonProfile().getProfileImage())) {
                Picasso.get()
                        .load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + jsonSchedule.getJsonProfile().getProfileImage())
                        .into(holder.iv_profile);

            } else {
                Picasso.get().load(R.drawable.profile_blue).into(holder.iv_profile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Picasso.get().load(R.drawable.profile_blue).into(holder.iv_profile);
        }

        holder.tv_appointment_time.setText(Formatter.convertMilitaryTo24HourFormat(jsonSchedule.getStartTime())
                + " - " + Formatter.convertMilitaryTo24HourFormat(jsonSchedule.getEndTime()));
        holder.tv_appointment_status.setText(jsonSchedule.getAppointmentStatus().getDescription());
//        switch (jsonSchedule.getAppointmentStatus()) {
//            case U:
//                holder.iv_accept.setBackground(ContextCompat.getDrawable(context, R.drawable.accept_empty));
//                holder.iv_reject.setBackground(ContextCompat.getDrawable(context, R.drawable.reject_empty));
//                break;
//            case A:
//                holder.iv_accept.setBackground(ContextCompat.getDrawable(context, R.drawable.accept));
//                holder.iv_reject.setBackground(ContextCompat.getDrawable(context, R.drawable.reject_empty));
//                break;
//            case R:
//                holder.iv_accept.setBackground(ContextCompat.getDrawable(context, R.drawable.accept_empty));
//                holder.iv_reject.setBackground(ContextCompat.getDrawable(context, R.drawable.reject));
//                break;
//            case S:
//                // Define what to do
//                break;
//        }
        holder.rl_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    switch (jsonSchedule.getAppointmentStatus()) {
                        case U:
                            listener.appointmentReject(jsonSchedule, position);
                            break;
                        case A:
                            listener.appointmentReject(jsonSchedule, position);
                            break;
                        case R:
                            Toast.makeText(context, "Appointment already rejected. It cannot be reverse", Toast.LENGTH_SHORT).show();
                            break;
                        case S:
                            Toast.makeText(context, "Appointment already serviced. It cannot be reverse", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
        holder.rl_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    switch (jsonSchedule.getAppointmentStatus()) {
                        case U:
                            listener.appointmentAccept(jsonSchedule, position);
                            break;
                        case A:
                            Toast.makeText(context, "Appointment already accepted.", Toast.LENGTH_SHORT).show();
                            break;
                        case R:
                            Toast.makeText(context, "Appointment already rejected. It cannot be reverse", Toast.LENGTH_SHORT).show();
                            break;
                        case S:
                            Toast.makeText(context, "Appointment already serviced. It cannot be reverse", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void appointmentAccept(JsonSchedule jsonSchedule, int pos);

        void appointmentReject(JsonSchedule jsonSchedule, int pos);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private TextView tv_gender_age;
        private TextView tv_customer_mobile;
        private TextView tv_appointment_time;
        private TextView tv_appointment_status;
        private TextView tv_chief_complaints;
        private CardView card_view;
        private ImageView iv_accept;
        private ImageView iv_reject;
        private ImageView iv_profile;
        private RelativeLayout rl_accept;
        private RelativeLayout rl_reject;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_gender_age = itemView.findViewById(R.id.tv_gender_age);
            this.tv_appointment_time = itemView.findViewById(R.id.tv_appointment_time);
            this.tv_appointment_status = itemView.findViewById(R.id.tv_appointment_status);
            this.tv_customer_mobile = itemView.findViewById(R.id.tv_customer_mobile);
            this.tv_chief_complaints = itemView.findViewById(R.id.tv_chief_complaints);
            this.iv_accept = itemView.findViewById(R.id.iv_accept);
            this.iv_reject = itemView.findViewById(R.id.iv_reject);
            this.iv_profile = itemView.findViewById(R.id.iv_profile);
            this.rl_accept = itemView.findViewById(R.id.rl_accept);
            this.rl_reject = itemView.findViewById(R.id.rl_reject);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
