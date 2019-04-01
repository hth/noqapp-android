package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.merchant.presenter.beans.JsonDataVisibility;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;

import android.content.Context;
import android.view.View;

import java.util.List;

public class PeopleInQAdapter extends BasePeopleInQAdapter {

    public PeopleInQAdapter(List<JsonQueuedPerson> data, Context context, PeopleInQAdapterClick peopleInQAdapterClick, String qCodeQR, JsonDataVisibility jsonDataVisibility) {
        super(data, context, peopleInQAdapterClick, qCodeQR, jsonDataVisibility);
    }

    public PeopleInQAdapter(List<JsonQueuedPerson> data, Context context, PeopleInQAdapterClick peopleInQAdapterClick, String qCodeQR, int glowPostion, QueueStatusEnum queueStatusEnum, JsonDataVisibility jsonDataVisibility, String bizCategoryId ) {
        super(data, context, peopleInQAdapterClick, qCodeQR, glowPostion, queueStatusEnum, jsonDataVisibility,bizCategoryId);
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
    void viewOrderClick(Context context, JsonPurchaseOrder jsonPurchaseOrder) {
        // No implementation needed here
    }

    @Override
    public void uploadDocument(Context context, JsonQueuedPerson jsonQueuedPerson) {
        // No implementation needed here
    }

    @Override
    public void onBindViewHolder(MyViewHolder recordHolder, int position) {
        super.onBindViewHolder(recordHolder, position);
        recordHolder.tv_create_case.setVisibility(View.GONE);
        recordHolder.tv_change_name.setVisibility(View.GONE);
        recordHolder.tv_upload_document.setVisibility(View.GONE);
    }
}
