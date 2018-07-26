package com.noqapp.android.merchant.views.adapters;


import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.types.QueueStatusEnum;
import com.noqapp.android.merchant.model.types.QueueUserStateEnum;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomer;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.body.ChangeUserInQueue;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.MedicalCaseHistoryTabbed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class PeopleInQAdapter extends BasePeopleInQAdapter {


    public PeopleInQAdapter(List<JsonQueuedPerson> data, Context context, PeopleInQAdapterClick peopleInQAdapterClick, String qCodeQR) {
        super(data, context, peopleInQAdapterClick, qCodeQR);
    }

    public PeopleInQAdapter(List<JsonQueuedPerson> data, Context context, PeopleInQAdapterClick peopleInQAdapterClick, String qCodeQR, int glowPostion, QueueStatusEnum queueStatusEnum) {
        super(data, context, peopleInQAdapterClick, qCodeQR, glowPostion, queueStatusEnum);
    }

    @Override
    public void changePatient(final Context context, final JsonQueuedPerson jsonQueuedPerson) {
        if (jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.Q) {
            if (TextUtils.isEmpty(jsonQueuedPerson.getServerDeviceId()) || jsonQueuedPerson.getServerDeviceId().equals(UserUtils.getDeviceId())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                builder.setTitle(null);
                View customDialogView = inflater.inflate(R.layout.dialog_change_patient, null, false);
                ImageView actionbarBack = customDialogView.findViewById(R.id.actionbarBack);
                final Spinner sp_patient_list = customDialogView.findViewById(R.id.sp_patient_list);
                builder.setView(customDialogView);
                final AlertDialog mAlertDialog = builder.create();
                mAlertDialog.setCanceledOnTouchOutside(false);
                DependentAdapter adapter = new DependentAdapter(context, jsonQueuedPerson.getDependents());
                sp_patient_list.setAdapter(adapter);
                Button btn_update = customDialogView.findViewById(R.id.btn_update);
                btn_update.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (!jsonQueuedPerson.getDependents().get(sp_patient_list.getSelectedItemPosition()).getQueueUserId().equalsIgnoreCase(jsonQueuedPerson.getQueueUserId())) {
                            if (LaunchActivity.getLaunchActivity().isOnline()) {
                                LaunchActivity.getLaunchActivity().progressDialog.show();
                                ChangeUserInQueue changeUserInQueue = new ChangeUserInQueue();
                                changeUserInQueue.setCodeQR(qCodeQR);
                                changeUserInQueue.setTokenNumber(jsonQueuedPerson.getToken());
                                changeUserInQueue.setChangeToQueueUserId(jsonQueuedPerson.getDependents().get(sp_patient_list.getSelectedItemPosition()).getQueueUserId());
                                changeUserInQueue.setExistingQueueUserId(jsonQueuedPerson.getQueueUserId());

                                manageQueueModel.changeUserInQueue(
                                        LaunchActivity.getLaunchActivity().getDeviceID(),
                                        LaunchActivity.getLaunchActivity().getEmail(),
                                        LaunchActivity.getLaunchActivity().getAuth(), changeUserInQueue);
                                mAlertDialog.dismiss();
                            } else {
                                mAlertDialog.dismiss();
                                ShowAlertInformation.showNetworkDialog(context);
                            }
                        } else {
                            Toast.makeText(context, "please select the patient name other than the current name", Toast.LENGTH_LONG).show();
                        }
                    }

                });

                actionbarBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialog.dismiss();
                    }
                });
                mAlertDialog.show();
            } else {
                Toast.makeText(context, context.getString(R.string.msg_client_already_acquired), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "This person is no longer in queue", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void editBusinessCustomerId(final Context mContext, final JsonQueuedPerson jsonQueuedPerson) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_add_patient_id, null, false);
        ImageView actionbarBack = customDialogView.findViewById(R.id.actionbarBack);
        final EditText edt_id = customDialogView.findViewById(R.id.edt_id);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        final Button btn_update = customDialogView.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_id.setError(null);
                new AppUtils().hideKeyBoard((Activity) mContext);

                if (TextUtils.isEmpty(edt_id.getText().toString())) {
                    edt_id.setError(mContext.getString(R.string.error_customer_id));
                } else {
                    if (!TextUtils.isEmpty(jsonQueuedPerson.getBusinessCustomerId()) && jsonQueuedPerson.getBusinessCustomerId().equalsIgnoreCase(edt_id.getText().toString())) {
                        edt_id.setError(mContext.getString(R.string.error_customer_id_exist));
                    } else {
                        LaunchActivity.getLaunchActivity().progressDialog.show();
                        String phoneNoWithCode = PhoneFormatterUtil.phoneNumberWithCountryCode(jsonQueuedPerson.getCustomerPhone(), LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName());
                        JsonBusinessCustomer jsonBusinessCustomer = new JsonBusinessCustomer().setQueueUserId(jsonQueuedPerson.getQueueUserId());
                        jsonBusinessCustomer.setCodeQR(qCodeQR);
                        jsonBusinessCustomer.setCustomerPhone(phoneNoWithCode);
                        jsonBusinessCustomer.setBusinessCustomerId(edt_id.getText().toString());
                        if (TextUtils.isEmpty(jsonQueuedPerson.getBusinessCustomerId())) {
                            businessCustomerModel.addId(
                                    LaunchActivity.getLaunchActivity().getDeviceID(),
                                    LaunchActivity.getLaunchActivity().getEmail(),
                                    LaunchActivity.getLaunchActivity().getAuth(), jsonBusinessCustomer);
                        } else {
                            businessCustomerModel.editId(
                                    LaunchActivity.getLaunchActivity().getDeviceID(),
                                    LaunchActivity.getLaunchActivity().getEmail(),
                                    LaunchActivity.getLaunchActivity().getAuth(), jsonBusinessCustomer);
                        }
                        btn_update.setClickable(false);
                        mAlertDialog.dismiss();
                    }

                }
            }
        });

        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }

    @Override
    void createCaseHistory(Context context, JsonQueuedPerson jsonQueuedPerson) {
        if (jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.Q) {
            if (!TextUtils.isEmpty(jsonQueuedPerson.getQueueUserId())) {
              if (TextUtils.isEmpty(jsonQueuedPerson.getServerDeviceId())||jsonQueuedPerson.getServerDeviceId().equals(UserUtils.getDeviceId())) {
                  Intent intent = new Intent(context, MedicalCaseHistoryTabbed.class);
                  intent.putExtra("qCodeQR", qCodeQR);
                  intent.putExtra("data", jsonQueuedPerson);
                  context.startActivity(intent);
                }else{
                  Toast.makeText(context, context.getString(R.string.msg_client_already_acquired), Toast.LENGTH_LONG).show();
              }

            } else {
                Toast.makeText(context, "This person in not the register user. You cannot create the case history", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "Currently you are not serving this person", Toast.LENGTH_LONG).show();
        }
    }


}
