package com.noqapp.android.merchant.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.hbb20.CountryCodePicker;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.model.types.DataVisibilityEnum;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
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
    private String cid = "";
    private CountryCodePicker ccp;
    private long mLastClickTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        iv_view_followup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    Intent in = new Intent(getActivity(), FollowUpListActivity.class);
                    in.putExtra("codeQR", jsonTopic.getCodeQR());
                    in.putExtra("visibility", DataVisibilityEnum.H == jsonTopic.getJsonDataVisibility().getDataVisibilities().get(LaunchActivity.getLaunchActivity().getUserLevel().name()));
                    ((Activity) context).startActivity(in);
                } else {
                    ShowAlertInformation.showNetworkDialog(context);
                }
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
        iv_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AppointmentActivity.class);
                intent.putExtra(IBConstant.KEY_CODE_QR, jsonTopic.getCodeQR());
                intent.putExtra("displayName",jsonTopic.getDisplayName());
                intent.putExtra("bizCategoryId",jsonTopic.getBizCategoryId());
                ((Activity) context).startActivity(intent);
            }
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
        edt_mobile = view.findViewById(R.id.edt_mobile);
        sp_patient_list = view.findViewById(R.id.sp_patient_list);
        tv_select_patient = view.findViewById(R.id.tv_select_patient);


        final EditText edt_id = view.findViewById(R.id.edt_id);
        final RadioGroup rg_user_id = view.findViewById(R.id.rg_user_id);
        final RadioButton rb_mobile = view.findViewById(R.id.rb_mobile);
        builder.setView(view);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        rg_user_id.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_mobile) {
                    ll_mobile.setVisibility(View.VISIBLE);
                    edt_id.setVisibility(View.GONE);
                    edt_id.setText("");
                } else {
                    edt_id.setVisibility(View.VISIBLE);
                    ll_mobile.setVisibility(View.GONE);
                    edt_mobile.setText("");
                }
            }
        });
        cid = "";
        ccp = view.findViewById(R.id.ccp);
        String c_codeValue = LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName();
        int c_code = PhoneFormatterUtil.getCountryCodeFromRegion(c_codeValue.toUpperCase());
        ccp.setDefaultCountryUsingNameCode(String.valueOf(c_code));
        btn_create_order = view.findViewById(R.id.btn_create_order);
        btn_create_token = view.findViewById(R.id.btn_create_token);
        btn_create_token.setText("Search Patient");
        btn_create_token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                boolean isValid = true;
                edt_mobile.setError(null);
                edt_id.setError(null);
                new AppUtils().hideKeyBoard(getActivity());
                // get selected radio button from radioGroup
                int selectedId = rg_user_id.getCheckedRadioButtonId();
                if (selectedId == R.id.rb_mobile) {
                    if (TextUtils.isEmpty(edt_mobile.getText())) {
                        edt_mobile.setError(getString(R.string.error_mobile_blank));
                        isValid = false;
                    }
                } else {
                    if (TextUtils.isEmpty(edt_id.getText())) {
                        edt_id.setError(getString(R.string.error_customer_id));
                        isValid = false;
                    }
                }


                if (isValid) {
                    setProgressMessage("Searching patient...");
                    showProgress();
                    setProgressCancel(false);
                    setDispensePresenter();
                    String phone = "";
                    cid = "";
                    if (rb_mobile.isChecked()) {
                        edt_id.setText("");
                        countryCode = ccp.getSelectedCountryCode();
                        phone = countryCode + edt_mobile.getText().toString();
                        cid = "";
                    } else {
                        cid = edt_id.getText().toString();
                        edt_mobile.setText("");// set blank so that wrong phone no not pass to login screen
                    }
                    businessCustomerApiCalls = new BusinessCustomerApiCalls();
                    businessCustomerApiCalls.setFindCustomerPresenter(MerchantDetailFragment.this);
                    businessCustomerApiCalls.findCustomer(
                            BaseLaunchActivity.getDeviceID(),
                            LaunchActivity.getLaunchActivity().getEmail(),
                            LaunchActivity.getLaunchActivity().getAuth(),
                            new JsonBusinessCustomerLookup().setCodeQR(codeQR).setCustomerPhone(phone).setBusinessCustomerId(cid));
                    btn_create_token.setClickable(false);
                    //  mAlertDialog.dismiss();
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
    public void passPhoneNo(JsonProfile jsonProfile) {
        findCustomerResponse(jsonProfile);
    }

    @Override
    public void findCustomerResponse(JsonProfile jsonProfile) {
        dismissProgress();
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
            btn_create_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                        if (TextUtils.isEmpty(cid)) {
                            phoneNoWithCode = PhoneFormatterUtil.phoneNumberWithCountryCode(jsonProfile.getPhoneRaw(), jsonProfile.getCountryShortName());
                        }

                        JsonBusinessCustomer jsonBusinessCustomer = new JsonBusinessCustomer().
                                setQueueUserId(jsonProfileList.get(sp_patient_list.getSelectedItemPosition()).getQueueUserId());
                        jsonBusinessCustomer
                                .setCodeQR(topicsList.get(currentPosition).getCodeQR())
                                .setCustomerPhone(phoneNoWithCode)
                                .setBusinessCustomerId(cid);
                        manageQueueApiCalls.dispenseTokenWithClientInfo(
                                BaseLaunchActivity.getDeviceID(),
                                LaunchActivity.getLaunchActivity().getEmail(),
                                LaunchActivity.getLaunchActivity().getAuth(),
                                jsonBusinessCustomer);

                    } else {
                        ShowAlertInformation.showNetworkDialog(getActivity());
                    }
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
}
