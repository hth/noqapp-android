package com.noqapp.android.merchant.views.adapters;

import android.content.Context;

import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.merchant.presenter.beans.JsonDataVisibility;
import com.noqapp.android.merchant.presenter.beans.JsonPaymentPermission;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;

import java.util.List;

public class PeopleInQAdapter extends BasePeopleInQAdapter {

    public PeopleInQAdapter(
            List<JsonQueuedPerson> data,
            Context context,
            PeopleInQAdapterClick peopleInQAdapterClick,
            String codeQR,
            JsonDataVisibility jsonDataVisibility,
            JsonPaymentPermission jsonPaymentPermission
    ) {
        super(data, context, peopleInQAdapterClick, codeQR, jsonDataVisibility, jsonPaymentPermission);
    }

    public PeopleInQAdapter(List<JsonQueuedPerson> data, Context context,
            PeopleInQAdapterClick peopleInQAdapterClick, JsonTopic jsonTopic) {
        super(data, context, peopleInQAdapterClick, jsonTopic);
    }

    @Override
    public void changePatient(final Context context, final JsonQueuedPerson jsonQueuedPerson) {

    }


    @Override
    public void editBusinessCustomerId(final Context mContext, final JsonQueuedPerson jsonQueuedPerson) {

    }

    @Override
    public void uploadDocument(Context context, JsonQueuedPerson jsonQueuedPerson) {

    }

    @Override
    public void createCaseHistory(Context context, JsonQueuedPerson jsonQueuedPerson, String bizCategoryId) {

    }

    @Override
    void approveCustomer(Context context, JsonQueuedPerson jsonQueuedPerson, String bizCategoryId, String action, String codeQR) {
        // Do-nothing: Anjali take a look.
    }
}
