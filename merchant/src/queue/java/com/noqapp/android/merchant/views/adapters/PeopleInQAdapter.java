package com.noqapp.android.merchant.views.adapters;

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

    public PeopleInQAdapter(List<JsonQueuedPerson> data, Context context, PeopleInQAdapterClick peopleInQAdapterClick, String qCodeQR, int glowPostion, QueueStatusEnum queueStatusEnum, JsonDataVisibility jsonDataVisibility) {
        super(data, context, peopleInQAdapterClick, qCodeQR, glowPostion, queueStatusEnum, jsonDataVisibility);
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
    void createCaseHistory(Context context, JsonQueuedPerson jsonQueuedPerson) {

    }

    @Override
    public void onBindViewHolder(MyViewHolder recordHolder, int position) {
        super.onBindViewHolder(recordHolder, position);
        recordHolder.tv_create_case.setVisibility(View.GONE);
        recordHolder.tv_change_name.setVisibility(View.GONE);
    }
}
