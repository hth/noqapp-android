package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.views.activities.PatientProfileActivity;
import com.noqapp.android.merchant.views.activities.PatientProfileHistoryActivity;

import java.util.List;

public class ViewAllPatientInQAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final JsonTopic jsonTopic;
    private List<JsonQueuedPerson> dataSet;

    public ViewAllPatientInQAdapter(List<JsonQueuedPerson> data, Context context, JsonTopic jsonTopic) {
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
        holder.tv_customer_name.setText(TextUtils.isEmpty(jsonQueuedPerson.getCustomerName()) ? context.getString(R.string.unregister_user) : jsonQueuedPerson.getCustomerName());
        holder.card_view.setOnClickListener(v -> {
            Intent intent = new Intent(context, PatientProfileHistoryActivity.class);
            intent.putExtra("qCodeQR", jsonTopic.getCodeQR());
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
        private CardView card_view;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_customer_name = itemView.findViewById(R.id.tv_customer_name);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
