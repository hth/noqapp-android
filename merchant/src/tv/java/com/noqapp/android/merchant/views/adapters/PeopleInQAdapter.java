package com.noqapp.android.merchant.views.adapters;


import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.merchant.presenter.beans.JsonDataVisibility;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;

import android.content.Context;

import java.util.List;

public class PeopleInQAdapter extends BasePeopleInQAdapter {


    public PeopleInQAdapter(List<JsonQueuedPerson> data, Context context, PeopleInQAdapterClick peopleInQAdapterClick, String qCodeQR, JsonDataVisibility jsonDataVisibility) {
        super(data, context, peopleInQAdapterClick, qCodeQR,jsonDataVisibility);
    }

    public PeopleInQAdapter(List<JsonQueuedPerson> data, Context context, PeopleInQAdapterClick peopleInQAdapterClick, String qCodeQR, int glowPostion, QueueStatusEnum queueStatusEnum, JsonDataVisibility jsonDataVisibility) {
        super(data, context, peopleInQAdapterClick, qCodeQR, glowPostion, queueStatusEnum,jsonDataVisibility);
    }

    @Override
    public void changePatient(final Context context, final JsonQueuedPerson jsonQueuedPerson) {
    }


    @Override
    public void editBusinessCustomerId(final Context mContext, final JsonQueuedPerson jsonQueuedPerson) {
    }

    @Override
    void createCaseHistory(Context context, JsonQueuedPerson jsonQueuedPerson) {
    }


}
