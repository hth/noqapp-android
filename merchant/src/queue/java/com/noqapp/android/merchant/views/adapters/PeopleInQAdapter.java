package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.merchant.model.types.QueueStatusEnum;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;

import android.content.Context;

import java.util.List;

public class PeopleInQAdapter extends BasePeopleInQAdapter {


    public PeopleInQAdapter(List<JsonQueuedPerson> data, Context context, PeopleInQAdapterClick peopleInQAdapterClick, String qCodeQR) {
        super(data, context, peopleInQAdapterClick, qCodeQR);
    }

    public PeopleInQAdapter(List<JsonQueuedPerson> data, Context context, PeopleInQAdapterClick peopleInQAdapterClick, String qCodeQR, int glowPostion, QueueStatusEnum queueStatusEnum) {
        super(data, context, peopleInQAdapterClick, qCodeQR, glowPostion, queueStatusEnum);
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

}
