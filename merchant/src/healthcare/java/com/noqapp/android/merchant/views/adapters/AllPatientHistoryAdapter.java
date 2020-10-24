package com.noqapp.android.merchant.views.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.DataVisibilityEnum;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.views.activities.AppInitialize;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.PatientProfileHistoryActivity;

import java.util.List;

public class AllPatientHistoryAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final JsonTopic jsonTopic;
    private List<JsonQueuedPerson> dataSet;

    public AllPatientHistoryAdapter(List<JsonQueuedPerson> data, Context context, JsonTopic jsonTopic) {
        this.dataSet = data;
        this.context = context;
        this.jsonTopic = jsonTopic;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_all_patient_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        final JsonQueuedPerson jsonQueuedPerson = dataSet.get(listPosition);
        final String phoneNo = jsonQueuedPerson.getCustomerPhone();
        holder.tv_customer_name.setText(TextUtils.isEmpty(jsonQueuedPerson.getCustomerName()) ? context.getString(R.string.unregister_user) : jsonQueuedPerson.getCustomerName());
        if (null != AppInitialize.getUserProfile()) {
            holder.tv_customer_mobile.setText(TextUtils.isEmpty(phoneNo) ? context.getString(R.string.unregister_user) :
                    PhoneFormatterUtil.formatNumber(AppInitialize.getUserProfile().getCountryShortName(), phoneNo));
        }
        if (DataVisibilityEnum.H == jsonTopic.getJsonDataVisibility().getDataVisibilities().get(AppInitialize.getUserLevel().name())) {
            if (!holder.tv_customer_mobile.getText().equals(context.getString(R.string.unregister_user))) {
                holder.tv_customer_mobile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.copy, 0, 0, 0);
                holder.tv_customer_mobile.setOnTouchListener((v, event) -> {
                    final int DRAWABLE_LEFT = 0;
                    final int DRAWABLE_TOP = 1;
                    final int DRAWABLE_RIGHT = 2;
                    final int DRAWABLE_BOTTOM = 3;

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (holder.tv_customer_mobile.getLeft() - holder.tv_customer_mobile.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                            // your action here
                            copyText(context, PhoneFormatterUtil.phoneStripCountryCode("+" + phoneNo));
                            return true;
                        }
                    }
                    return false;
                });
            }
            holder.tv_customer_mobile.setOnClickListener(v -> {
                if (!holder.tv_customer_mobile.getText().equals(context.getString(R.string.unregister_user))) {
                    AppUtils.makeCall(LaunchActivity.getLaunchActivity(),
                            PhoneFormatterUtil.formatNumber(AppInitialize.getUserProfile().getCountryShortName(), phoneNo));
                }
            });
        } else {
            holder.tv_customer_mobile.setText(AppUtils.hidePhoneNumberWithX(phoneNo));
        }
        holder.tv_customer_name.setOnClickListener(v -> {
            Intent intent = new Intent(context, PatientProfileHistoryActivity.class);
            intent.putExtra(IBConstant.KEY_CODE_QR, jsonTopic.getCodeQR());
            intent.putExtra("data", jsonQueuedPerson);
            intent.putExtra("bizCategoryId", jsonTopic.getBizCategoryId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_customer_name;
        private TextView tv_customer_mobile;
        private CardView card_view;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_customer_name = itemView.findViewById(R.id.tv_customer_name);
            this.tv_customer_mobile = itemView.findViewById(R.id.tv_customer_mobile);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }

    private void copyText(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
        new CustomToast().showToast(context, "Copied Phone Number");
    }
}
