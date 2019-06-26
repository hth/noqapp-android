package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.model.types.QueueUserStateEnum;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.model.types.medical.FormVersionEnum;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomer;
import com.noqapp.android.merchant.presenter.beans.JsonDataVisibility;
import com.noqapp.android.merchant.presenter.beans.JsonPaymentPermission;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.body.merchant.ChangeUserInQueue;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.DocumentUploadActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.PatientProfileActivity;
import com.noqapp.android.merchant.views.activities.PhysicalActivity;
import com.noqapp.android.merchant.views.activities.PhysicalDialogActivity;
import com.noqapp.android.merchant.views.activities.PreferenceActivity;
import com.noqapp.android.merchant.views.pojos.PreferenceObjects;

import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

import java.util.List;
import java.util.Random;

public class PeopleInQAdapter extends BasePeopleInQAdapter {

    public PeopleInQAdapter(
            List<JsonQueuedPerson> data,
            Context context,
            PeopleInQAdapterClick peopleInQAdapterClick,
            String qCodeQR,
            JsonDataVisibility jsonDataVisibility,
            JsonPaymentPermission jsonPaymentPermission
    ) {
        super(data, context, peopleInQAdapterClick, qCodeQR, jsonDataVisibility, jsonPaymentPermission);
    }

    public PeopleInQAdapter(
            List<JsonQueuedPerson> data,
            Context context,
            PeopleInQAdapterClick peopleInQAdapterClick,
            String qCodeQR,
            int glowPosition,
            QueueStatusEnum queueStatusEnum,
            JsonDataVisibility jsonDataVisibility,
            JsonPaymentPermission jsonPaymentPermission,
            String bizCategoryId
    ) {
        super(data, context, peopleInQAdapterClick, qCodeQR, glowPosition, queueStatusEnum, jsonDataVisibility, jsonPaymentPermission, bizCategoryId);
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
                               // LaunchActivity.getLaunchActivity().progressDialog.show();
                                ChangeUserInQueue changeUserInQueue = new ChangeUserInQueue();
                                changeUserInQueue.setCodeQR(qCodeQR);
                                changeUserInQueue.setTokenNumber(jsonQueuedPerson.getToken());
                                changeUserInQueue.setChangeToQueueUserId(jsonQueuedPerson.getDependents().get(sp_patient_list.getSelectedItemPosition()).getQueueUserId());
                                changeUserInQueue.setExistingQueueUserId(jsonQueuedPerson.getQueueUserId());

                                manageQueueApiCalls.changeUserInQueue(
                                        BaseLaunchActivity.getDeviceID(),
                                        LaunchActivity.getLaunchActivity().getEmail(),
                                        LaunchActivity.getLaunchActivity().getAuth(), changeUserInQueue);
                                mAlertDialog.dismiss();
                            } else {
                                mAlertDialog.dismiss();
                                ShowAlertInformation.showNetworkDialog(context);
                            }
                        } else {
                            new CustomToast().showToast(context, "Please select a patient name other than the current name");
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
                new CustomToast().showToast(context, context.getString(R.string.msg_client_already_acquired));
            }
        } else {
            new CustomToast().showToast(context, "This person is no longer in queue");
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
        ((TextView) customDialogView.findViewById(R.id.tv_patient_name)).setText(jsonQueuedPerson.getCustomerName());
        final TextView tv_random = customDialogView.findViewById(R.id.tv_random);
        final EditText edt_random = customDialogView.findViewById(R.id.edt_random);
        TextView tv_reach_limit = customDialogView.findViewById(R.id.tv_reach_limit);
        tv_random.setText(String.format("%04d", new Random().nextInt(10000)));
        if (jsonQueuedPerson.getBusinessCustomerIdChangeCount() > 1) {
            tv_random.setVisibility(View.VISIBLE);
            edt_random.setVisibility(View.VISIBLE);
            tv_reach_limit.setVisibility(View.VISIBLE);
        }
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        final Button btn_update = customDialogView.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_id.setError(null);
                edt_random.setError(null);
                new AppUtils().hideKeyBoard((Activity) mContext);

                if (TextUtils.isEmpty(edt_id.getText().toString())) {
                    edt_id.setError(mContext.getString(R.string.error_customer_id));
                } else {
                    if (!edt_id.getText().toString().matches("^[a-zA-Z0-9]+$")) {
                        edt_id.setError(mContext.getString(R.string.error_customer_id_input));
                    } else if (!TextUtils.isEmpty(jsonQueuedPerson.getBusinessCustomerId()) && jsonQueuedPerson.getBusinessCustomerId().equalsIgnoreCase(edt_id.getText().toString())) {
                        edt_id.setError(mContext.getString(R.string.error_customer_id_exist));
                    } else {
                        if (jsonQueuedPerson.getBusinessCustomerIdChangeCount() > 1 && !edt_random.getText().toString().equalsIgnoreCase(tv_random.getText().toString())) {
                            edt_random.setError(mContext.getString(R.string.error_invalid_captcha));
                        } else {
                          //  LaunchActivity.getLaunchActivity().progressDialog.show();
                            String phoneNoWithCode = PhoneFormatterUtil.phoneNumberWithCountryCode(jsonQueuedPerson.getCustomerPhone(), LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName());
                            JsonBusinessCustomer jsonBusinessCustomer = new JsonBusinessCustomer().setQueueUserId(jsonQueuedPerson.getQueueUserId());
                            jsonBusinessCustomer.setCodeQR(qCodeQR);
                            jsonBusinessCustomer.setCustomerPhone(phoneNoWithCode);
                            jsonBusinessCustomer.setBusinessCustomerId(edt_id.getText().toString());
                            if (TextUtils.isEmpty(jsonQueuedPerson.getBusinessCustomerId())) {
                                businessCustomerApiCalls.addId(
                                        BaseLaunchActivity.getDeviceID(),
                                        LaunchActivity.getLaunchActivity().getEmail(),
                                        LaunchActivity.getLaunchActivity().getAuth(), jsonBusinessCustomer);
                            } else {
                                businessCustomerApiCalls.editId(
                                        BaseLaunchActivity.getDeviceID(),
                                        LaunchActivity.getLaunchActivity().getEmail(),
                                        LaunchActivity.getLaunchActivity().getAuth(), jsonBusinessCustomer);
                            }
                            btn_update.setClickable(false);
                            mAlertDialog.dismiss();
                        }
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
    public void uploadDocument(Context context, JsonQueuedPerson jsonQueuedPerson) {
        Intent intent = new Intent(context, DocumentUploadActivity.class);
        intent.putExtra("recordReferenceId", jsonQueuedPerson.getRecordReferenceId());
        intent.putExtra("qCodeQR", qCodeQR);
        context.startActivity(intent);
    }

    @Override
    public void createCaseHistory(Context context, JsonQueuedPerson jsonQueuedPerson, String bizCategoryId) {
        if (LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.Q_SUPERVISOR) {

            if (new AppUtils().isTablet(context)) {
                Intent intent = new Intent(context, PhysicalDialogActivity.class);
                intent.putExtra("qCodeQR", qCodeQR);
                intent.putExtra("data", jsonQueuedPerson);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, PhysicalActivity.class);
                intent.putExtra("qCodeQR", qCodeQR);
                intent.putExtra("data", jsonQueuedPerson);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_up, R.anim.stay);

            }

        } else {
            if (jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.Q || jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.S) {
                if (TextUtils.isEmpty(jsonQueuedPerson.getServerDeviceId()) || jsonQueuedPerson.getServerDeviceId().equals(UserUtils.getDeviceId())) {
                    if (null == LaunchActivity.getLaunchActivity().getUserProfessionalProfile()) {
                        // temporary crash fix
                        LaunchActivity.getLaunchActivity().setUserProfessionalProfile(new JsonProfessionalProfilePersonal().setFormVersion(FormVersionEnum.MFD1));
                    }

                    PreferenceObjects preferenceObjects = null;
                    try {
                        preferenceObjects = new Gson().fromJson(LaunchActivity.getLaunchActivity().getSuggestionsPrefs(), PreferenceObjects.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (null == preferenceObjects || preferenceObjects.isEmpty()) {
                        ShowCustomDialog showDialog = new ShowCustomDialog(context);
                        showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                            @Override
                            public void btnPositiveClick() {
                                Intent in = new Intent(context, PreferenceActivity.class);
                                context.startActivity(in);
                            }

                            @Override
                            public void btnNegativeClick() {
                                Intent intent = new Intent(context, PatientProfileActivity.class);
                                intent.putExtra("qCodeQR", qCodeQR);
                                intent.putExtra("data", jsonQueuedPerson);
                                intent.putExtra("bizCategoryId", bizCategoryId);
                                context.startActivity(intent);
                            }
                        });
                        showDialog.displayDialog("Alert", "You have not set your setting preferences. Do you want to set it now?");
                    } else {
                        Intent intent = new Intent(context, PatientProfileActivity.class);
                        intent.putExtra("qCodeQR", qCodeQR);
                        intent.putExtra("data", jsonQueuedPerson);
                        intent.putExtra("bizCategoryId", bizCategoryId);
                        context.startActivity(intent);
                    }
                } else {
                    new CustomToast().showToast(context, context.getString(R.string.msg_client_already_acquired));
                }

            } else {
                new CustomToast().showToast(context, "Currently you are not serving this person");
            }
        }
    }
}
