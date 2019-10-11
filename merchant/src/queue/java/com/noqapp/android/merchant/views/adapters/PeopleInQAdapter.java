package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.merchant.presenter.beans.JsonDataVisibility;
import com.noqapp.android.merchant.presenter.beans.JsonPaymentPermission;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;

import java.util.List;

public class PeopleInQAdapter extends BasePeopleInQAdapter {

    public PeopleInQAdapter(List<JsonQueuedPerson> data, Context context, PeopleInQAdapterClick peopleInQAdapterClick, String qCodeQR, JsonDataVisibility jsonDataVisibility, JsonPaymentPermission jsonPaymentPermission) {
        super(data, context, peopleInQAdapterClick, qCodeQR, jsonDataVisibility, jsonPaymentPermission);
    }

    public PeopleInQAdapter(List<JsonQueuedPerson> data, Context context, PeopleInQAdapterClick peopleInQAdapterClick, JsonTopic jsonTopic) {
        super(data, context, peopleInQAdapterClick, jsonTopic);
    }

    @Override
    public void changePatient(final Context mContext, final JsonQueuedPerson jsonQueuedPerson) {
        // No implementation needed here
    }


    @Override
    public void editBusinessCustomerId(final Context mContext, final JsonQueuedPerson jsonQueuedPerson) {
        // No implementation needed here
    }

    @Override
    public void createCaseHistory(Context context, JsonQueuedPerson jsonQueuedPerson, String bizCategoryId) {
        // No implementation needed here
    }

    @Override
    public void uploadDocument(Context context, JsonQueuedPerson jsonQueuedPerson) {
        // No implementation needed here
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        MyViewHolder recordHolder = (MyViewHolder) viewHolder;
        recordHolder.tv_create_case.setVisibility(View.GONE);
        recordHolder.tv_change_name.setVisibility(View.GONE);
        recordHolder.tv_upload_document.setVisibility(View.GONE);
    }
}
