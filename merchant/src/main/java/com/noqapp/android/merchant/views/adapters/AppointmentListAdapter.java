package com.noqapp.android.merchant.views.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.EventDay;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.AppInitialize;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.pojos.DataObj;
import com.noqapp.android.merchant.views.utils.MedicalDataStatic;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AppointmentListAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final OnItemClickListener listener;
    private List<EventDay> dataSet;
    private String bizCategoryId;


    public AppointmentListAdapter(List<EventDay> data, Context context, OnItemClickListener listener, String bizCategoryId) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
        this.bizCategoryId = bizCategoryId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_appointment_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        JsonSchedule jsonSchedule = (JsonSchedule) dataSet.get(position).getEventObject();
        holder.tv_title.setText(jsonSchedule.getJsonProfile().getName());
        holder.tv_gender_age.setText(AppUtils.calculateAge(jsonSchedule.getJsonProfile().
            getBirthday()) + ", " + jsonSchedule.getJsonProfile().getGender().name());
        holder.tv_customer_mobile.setText(
            TextUtils.isEmpty(jsonSchedule.getJsonProfile().getPhoneRaw())
                ? context.getString(R.string.unregister_user)
                : PhoneFormatterUtil.formatNumber(AppInitialize.getUserProfile().getCountryShortName(), jsonSchedule.getJsonProfile().getPhoneRaw()));
        holder.tv_customer_mobile.setOnClickListener(v -> {
            if (!holder.tv_customer_mobile.getText().equals(context.getString(R.string.unregister_user))) {
                AppUtils.makeCall(
                    LaunchActivity.getLaunchActivity(),
                    PhoneFormatterUtil.formatNumber(
                        AppInitialize.getUserProfile().getCountryShortName(),
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
        holder.tv_chief_complaints.setText(
            TextUtils.isEmpty(jsonSchedule.getChiefComplain())
                ? context.getString(R.string.unregister_user)
                : jsonSchedule.getChiefComplain());
        holder.rl_edit_complaints.setOnClickListener(v -> showAddComplaintsDialog(context, holder.tv_chief_complaints, jsonSchedule, position));
        holder.card_view.setCardBackgroundColor(Color.WHITE);
        holder.rl_accept.setBackgroundColor(Color.WHITE);
        holder.rl_reject.setBackgroundColor(Color.WHITE);
        switch (jsonSchedule.getAppointmentStatus()) {
            case U:
                holder.iv_accept.setBackground(ContextCompat.getDrawable(context, R.drawable.accept_allowed));
                holder.iv_reject.setBackground(ContextCompat.getDrawable(context, R.drawable.reject_allowed));
                break;
            case A:
                holder.iv_accept.setBackground(ContextCompat.getDrawable(context, R.drawable.accepted));
                holder.iv_reject.setBackground(ContextCompat.getDrawable(context, R.drawable.reject_allowed));
                break;
            case R:
                holder.iv_accept.setBackground(ContextCompat.getDrawable(context, R.drawable.accept_not_allowed));
                holder.iv_reject.setBackground(ContextCompat.getDrawable(context, R.drawable.rejected));
                holder.card_view.setCardBackgroundColor(Color.parseColor("#cccccc"));
                holder.rl_accept.setBackgroundColor(Color.parseColor("#cccccc"));
                holder.rl_reject.setBackgroundColor(Color.parseColor("#cccccc"));
                break;
            case S:
                holder.iv_accept.setBackground(ContextCompat.getDrawable(context, R.drawable.accept_not_allowed));
                holder.iv_reject.setBackground(ContextCompat.getDrawable(context, R.drawable.reject_not_allowed));
                break;
        }
        holder.rl_reject.setOnClickListener(v -> {
            if (null != listener) {
                switch (jsonSchedule.getAppointmentStatus()) {
                    case U:
                        listener.appointmentReject(jsonSchedule, position);
                        break;
                    case A:
                        listener.appointmentReject(jsonSchedule, position);
                        break;
                    case R:
                        new CustomToast().showToast(context, "Appointment already rejected. It cannot be modified");
                        break;
                    case S:
                        new CustomToast().showToast(context, "Appointment already serviced. It cannot be modified");
                        break;
                }
            }
        });
        holder.rl_accept.setOnClickListener(v -> {
            if (null != listener) {
                switch (jsonSchedule.getAppointmentStatus()) {
                    case U:
                        listener.appointmentAccept(jsonSchedule, position);
                        break;
                    case A:
                        new CustomToast().showToast(context, "Appointment already accepted.");
                        break;
                    case R:
                        new CustomToast().showToast(context, "Appointment already rejected. It cannot be modified");
                        break;
                    case S:
                        new CustomToast().showToast(context, "Appointment already serviced. It cannot be modified");
                        break;
                }
            }
        });

        holder.tv_appointment_time.setOnClickListener(v -> listener.appointmentReschedule(jsonSchedule, position));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void appointmentAccept(JsonSchedule jsonSchedule, int pos);

        void appointmentReject(JsonSchedule jsonSchedule, int pos);

        void appointmentEdit(JsonSchedule jsonSchedule, int pos);

        void appointmentReschedule(JsonSchedule jsonSchedule, int pos);
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
        private RelativeLayout rl_edit_complaints;
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
            this.rl_edit_complaints = itemView.findViewById(R.id.rl_edit_complaints);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }

    private void showAddComplaintsDialog(final Context mContext, TextView textView, JsonSchedule jsonSchedule, int position) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_complaint);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        final AutoCompleteTextView actv_chief_complaints = dialog.findViewById(R.id.actv_chief_complaints);
        final ArrayList<String> data = new ArrayList<>();
        ArrayList<DataObj> temp = MedicalDataStatic.getSymptomsOnCategoryType(bizCategoryId);
        if (temp.size() > 0) {
            for (int i = 0; i < temp.size(); i++) {
                data.add(temp.get(i).getShortName());
            }
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, data);
        actv_chief_complaints.setAdapter(adapter1);
        actv_chief_complaints.setThreshold(1);
        actv_chief_complaints.setDropDownBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.white)));
        Button btnPositive = dialog.findViewById(R.id.btnPositive);
        Button btnNegative = dialog.findViewById(R.id.btnNegative);
        btnPositive.setOnClickListener(v -> {
            actv_chief_complaints.setError(null);
            if (actv_chief_complaints.getText().toString().equals("")) {
                actv_chief_complaints.setError("Chief complaints cannot be empty");
            } else {
                AppUtils.hideKeyBoard((Activity) mContext);
                jsonSchedule.setChiefComplain(actv_chief_complaints.getText().toString());
                listener.appointmentEdit(jsonSchedule, position);
                dialog.dismiss();
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
}
