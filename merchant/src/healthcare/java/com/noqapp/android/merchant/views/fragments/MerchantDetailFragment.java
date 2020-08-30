package com.noqapp.android.merchant.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.hbb20.CountryCodePicker;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.model.types.DataVisibilityEnum;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.BusinessCustomerApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomer;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.AppointmentActivity;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.FollowUpListActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.OrderDetailActivity;
import com.noqapp.android.merchant.views.activities.ViewAllPeopleInQActivity;
import com.noqapp.android.merchant.views.adapters.JsonProfileAdapter;
import com.noqapp.android.merchant.views.interfaces.FindCustomerPresenter;

import java.util.ArrayList;
import java.util.List;

public class MerchantDetailFragment extends BaseMerchantDetailFragment implements
        FindCustomerPresenter, OrderDetailActivity.UpdateWholeList {
    private Spinner sp_patient_list;
    private TextView tv_select_patient;
    private Button btn_create_order;
    private BusinessCustomerApiCalls businessCustomerApiCalls;
    private String countryCode = "";
    private String businessCustomerId = "";
    private CountryCodePicker ccp;
    private long mLastClickTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        iv_view_followup.setOnClickListener(view -> {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                Intent in = new Intent(getActivity(), FollowUpListActivity.class);
                in.putExtra("codeQR", jsonTopic.getCodeQR());
                in.putExtra("visibility", DataVisibilityEnum.H == jsonTopic.getJsonDataVisibility().getDataVisibilities().get(LaunchActivity.getLaunchActivity().getUserLevel().name()));
                ((Activity) context).startActivity(in);
            } else {
                ShowAlertInformation.showNetworkDialog(context);
            }
        });
        iv_product_list.setVisibility(View.GONE);
        switch (LaunchActivity.getLaunchActivity().getUserProfile().getBusinessType()) {
            case DO:
            case HS:
                iv_appointment.setVisibility(View.VISIBLE);
                fl_appointment.setVisibility(View.VISIBLE);
                break;
            default:
                fl_appointment.setVisibility(View.GONE);
                iv_appointment.setVisibility(View.GONE);
        }
        iv_appointment.setOnClickListener(v1 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent intent = new Intent(getActivity(), AppointmentActivity.class);
            intent.putExtra(IBConstant.KEY_CODE_QR, jsonTopic.getCodeQR());
            intent.putExtra("displayName",jsonTopic.getDisplayName());
            intent.putExtra("bizCategoryId",jsonTopic.getBizCategoryId());
            ((Activity) context).startActivity(intent);
        });
        return v;
    }

    @Override
    protected void createToken(Context context, String codeQR) {
        showCreateTokenDialogWithMobile(context, codeQR);
    }

    @Override
    public void getAllPeopleInQ(JsonTopic jsonTopic) {
        manageQueueApiCalls.setQueuePersonListPresenter(this);
        manageQueueApiCalls.getAllQueuePersonList(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonTopic.getCodeQR());
    }

    private void showCreateTokenDialogWithMobile(final Context mContext, final String codeQR) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View view = inflater.inflate(R.layout.dialog_create_token_with_mobile, null, false);
        ImageView actionbarBack = view.findViewById(R.id.actionbarBack);
        tv_create_token = view.findViewById(R.id.tvtitle);
        tvCount = view.findViewById(R.id.tvcount);
        ll_main_section = view.findViewById(R.id.ll_main_section);
        ll_mobile = view.findViewById(R.id.ll_mobile);
        LinearLayout ll_cust_id = view.findViewById(R.id.ll_cust_id);
        LinearLayout ll_unregistered = view.findViewById(R.id.ll_unregistered);
        edt_mobile = view.findViewById(R.id.edt_mobile);
        sp_patient_list = view.findViewById(R.id.sp_patient_list);
        tv_select_patient = view.findViewById(R.id.tv_select_patient);
        btn_create_token = view.findViewById(R.id.btn_create_token);


        final EditText edt_id = view.findViewById(R.id.edt_id);
        final RadioGroup rg_token_type = view.findViewById(R.id.rg_token_type);
        final RadioButton rb_mobile = view.findViewById(R.id.rb_mobile);
        builder.setView(view);
        String c_codeValue = LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName();
        int c_code = PhoneFormatterUtil.getCountryCodeFromRegion(c_codeValue.toUpperCase());
        ccp = view.findViewById(R.id.ccp);
        ccp.setDefaultCountryUsingNameCode(String.valueOf(c_code));
        CountryCodePicker ccp_unregistered = view.findViewById(R.id.ccp_unregistered);
        ccp_unregistered.setDefaultCountryUsingNameCode(String.valueOf(c_code));

        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        rg_token_type.setOnCheckedChangeListener((group, checkedId) -> {
            // View cleanup
            sp_patient_list.setVisibility(View.GONE);
            tv_select_patient.setVisibility(View.GONE);
            edt_mobile.setEnabled(true);
            btn_create_token.setVisibility(View.VISIBLE);
            btn_create_token.setClickable(true);
            if (R.id.rb_mobile == checkedId) {
                ll_mobile.setVisibility(View.VISIBLE);
                ll_cust_id.setVisibility(View.GONE);
                ll_unregistered.setVisibility(View.GONE);
                btn_create_token.setText(getString(R.string.search_registered_patient));
            } else if (R.id.rb_customer_id == checkedId) {
                ll_cust_id.setVisibility(View.VISIBLE);
                ll_mobile.setVisibility(View.GONE);
                ll_unregistered.setVisibility(View.GONE);
                btn_create_token.setText(getString(R.string.search_registered_patient));
            } else {
                ll_unregistered.setVisibility(View.VISIBLE);
                ll_mobile.setVisibility(View.GONE);
                ll_cust_id.setVisibility(View.GONE);
                btn_create_token.setText(getString(R.string.create_token));
            }

            // Bind listeners
            if (rg_token_type.getCheckedRadioButtonId() == R.id.rb_unregistered) {
                EditText edt_mobile_unregistered = view.findViewById(R.id.edt_mobile_unregistered);
                EditText edt_name_unregistered = view.findViewById(R.id.edt_name_unregistered);
                btn_create_token.setOnClickListener(v -> {
                    boolean isValid = true;
                    AppUtils.hideKeyBoard(getActivity());
                    setDispensePresenter();
                    // get selected radio button from radioGroup
                    if (TextUtils.isEmpty(edt_mobile_unregistered.getText())) {
                        edt_mobile_unregistered.setError(getString(R.string.error_mobile_blank));
                        isValid = false;
                    }
                    if (TextUtils.isEmpty(edt_name_unregistered.getText())) {
                        edt_name_unregistered.setError(getString(R.string.error_patient_name));
                        isValid = false;
                    }

                    if (isValid) {
                        JsonBusinessCustomer jsonBusinessCustomer = new JsonBusinessCustomer();
                        jsonBusinessCustomer.setCodeQR(codeQR);
                        jsonBusinessCustomer.setCustomerName(edt_name_unregistered.getText().toString());
                        jsonBusinessCustomer.setCustomerPhone(ccp_unregistered.getDefaultCountryCode() + edt_mobile_unregistered.getText().toString());
                        jsonBusinessCustomer.setRegisteredUser(false);
                        manageQueueApiCalls.dispenseTokenWithClientInfo(
                                BaseLaunchActivity.getDeviceID(),
                                LaunchActivity.getLaunchActivity().getEmail(),
                                LaunchActivity.getLaunchActivity().getAuth(),
                                jsonBusinessCustomer);
                    }
                });
            } else {
                businessCustomerId = "";
                btn_create_order = view.findViewById(R.id.btn_create_order);
                btn_create_token.setText(getString(R.string.search_registered_patient));
                btn_create_token.setOnClickListener(v -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    boolean isValid = true;
                    edt_mobile.setError(null);
                    edt_id.setError(null);
                    AppUtils.hideKeyBoard(getActivity());
                    // get selected radio button from radioGroup
                    int selectedId = rg_token_type.getCheckedRadioButtonId();
                    if (selectedId == R.id.rb_mobile) {
                        if (TextUtils.isEmpty(edt_mobile.getText())) {
                            edt_mobile.setError(getString(R.string.error_mobile_blank));
                            isValid = false;
                        }
                    } else {
                        if (TextUtils.isEmpty(edt_id.getText())) {
                            edt_id.setError(getString(R.string.error_patient_id));
                            isValid = false;
                        }
                    }

                    if (isValid ) {
                        setProgressMessage("Searching Patient...");
                        showProgress();
                        setProgressCancel(false);
                        setDispensePresenter();
                        String phone = "";
                        businessCustomerId = "";
                        if (rb_mobile.isChecked()) {
                            edt_id.setText("");
                            countryCode = ccp.getSelectedCountryCode();
                            phone = countryCode + edt_mobile.getText().toString();
                            businessCustomerId = "";
                        } else {
                            businessCustomerId = edt_id.getText().toString();
                            edt_mobile.setText("");// set blank so that wrong phone no not pass to login screen
                        }
                        businessCustomerApiCalls = new BusinessCustomerApiCalls();
                        businessCustomerApiCalls.setFindCustomerPresenter(MerchantDetailFragment.this);
                        businessCustomerApiCalls.findCustomer(
                                BaseLaunchActivity.getDeviceID(),
                                LaunchActivity.getLaunchActivity().getEmail(),
                                LaunchActivity.getLaunchActivity().getAuth(),
                                new JsonBusinessCustomerLookup().setCodeQR(codeQR).setCustomerPhone(phone).setBusinessCustomerId(businessCustomerId));
                        btn_create_token.setClickable(false);
                        //  mAlertDialog.dismiss();
                    }
                });
            }
        });

        actionbarBack.setOnClickListener(v -> mAlertDialog.dismiss());
        mAlertDialog.show();
    }

    @Override
    public void userFound(JsonProfile jsonProfile) {
        findCustomerResponse(jsonProfile);
    }

    @Override
    public void userRegistered(JsonProfile jsonProfile) {
        findCustomerResponse(jsonProfile);
    }

    @Override
    public void findCustomerResponse(JsonProfile jsonProfile) {
        dismissProgress();
        mLastClickTime = 0;
        if (null != jsonProfile) {
            List<JsonProfile> jsonProfileList = new ArrayList<>();
            jsonProfileList.add(jsonProfile);
            if (jsonProfile.getDependents().size() > 0) {
                jsonProfileList.addAll(jsonProfile.getDependents());
            }
            JsonProfileAdapter adapter = new JsonProfileAdapter(getActivity(), jsonProfileList);
            sp_patient_list.setAdapter(adapter);
            sp_patient_list.setVisibility(View.VISIBLE);
            edt_mobile.setEnabled(false);
            tv_select_patient.setVisibility(View.VISIBLE);
            btn_create_order.setVisibility(View.VISIBLE);
            btn_create_token.setVisibility(View.GONE);
            btn_create_order.setOnClickListener(v -> {
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    btn_create_order.setEnabled(false);
                    setProgressMessage("Creating token...");
                    showProgress();
                    setProgressCancel(false);
                    String phoneNoWithCode = "";
                    setDispensePresenter();
                    if (TextUtils.isEmpty(businessCustomerId)) {
                        phoneNoWithCode = PhoneFormatterUtil.phoneNumberWithCountryCode(jsonProfile.getPhoneRaw(), jsonProfile.getCountryShortName());
                    }

                    JsonBusinessCustomer jsonBusinessCustomer = new JsonBusinessCustomer()
                            .setQueueUserId(jsonProfileList.get(sp_patient_list.getSelectedItemPosition()).getQueueUserId());
                    jsonBusinessCustomer
                            .setCodeQR(topicsList.get(currentPosition).getCodeQR())
                            .setCustomerPhone(phoneNoWithCode)
                            .setBusinessCustomerId(businessCustomerId)
                            .setRegisteredUser(true);

                    manageQueueApiCalls.dispenseTokenWithClientInfo(
                            BaseLaunchActivity.getDeviceID(),
                            LaunchActivity.getLaunchActivity().getEmail(),
                            LaunchActivity.getLaunchActivity().getAuth(),
                            jsonBusinessCustomer);

                } else {
                    ShowAlertInformation.showNetworkDialog(getActivity());
                }
            });
        }
    }

    @Override
    public void viewOrderClick(Context context, JsonQueuedPerson jsonQueuedPerson, boolean isPaymentNotAllowed) {
        OrderDetailActivity.updateWholeList = this;
        Intent in = new Intent(context, OrderDetailActivity.class);
        in.putExtra("jsonQueuedPerson", jsonQueuedPerson);
        in.putExtra(IBConstant.KEY_IS_PAYMENT_NOT_ALLOWED, isPaymentNotAllowed);
        ((Activity) context).startActivity(in);
    }

    @Override
    protected void showAllPeopleInQHistory() {
        Intent in = new Intent(getActivity(), ViewAllPeopleInQActivity.class);
        in.putExtra("codeQR", jsonTopic.getCodeQR());
        in.putExtra("visibility", DataVisibilityEnum.H == jsonTopic.getJsonDataVisibility().getDataVisibilities().get(LaunchActivity.getLaunchActivity().getUserLevel().name()));
        ((Activity) context).startActivity(in);
    }


    @Override
    public void updateWholeList() {
        getAllPeopleInQ(jsonTopic);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_product_list).setVisible(false);
    }
}
