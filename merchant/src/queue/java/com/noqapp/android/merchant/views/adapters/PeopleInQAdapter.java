package com.noqapp.android.merchant.views.adapters;

import android.content.Context;

import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;

import java.util.List;

public class PeopleInQAdapter extends BasePeopleInQAdapter {


    public PeopleInQAdapter(List<JsonQueuedPerson> data, Context context, PeopleInQAdapterClick peopleInQAdapterClick, String qCodeQR) {
        super(data, context, peopleInQAdapterClick, qCodeQR);
    }

    public PeopleInQAdapter(List<JsonQueuedPerson> data, Context context, PeopleInQAdapterClick peopleInQAdapterClick, String qCodeQR, int glowPostion) {
        super(data, context, peopleInQAdapterClick, qCodeQR, glowPostion);
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
