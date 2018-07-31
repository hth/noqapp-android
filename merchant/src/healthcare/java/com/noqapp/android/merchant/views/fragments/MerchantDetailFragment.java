package com.noqapp.android.merchant.views.fragments;


import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MerchantDetailFragment extends BaseMerchantDetailFragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater,container,savedInstanceState);
    }

    @Override
    protected void createToken(Context context, String codeQR) {
        showCreateTokenDialogWithMobile(context,codeQR);
    }

    @Override
    public void getAllPeopleInQ(JsonTopic jsonTopic) {
        manageQueueModel.setQueuePersonListPresenter(this);
        manageQueueModel.getAllQueuePersonList(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonTopic.getCodeQR());
    }

    private void showCreateTokenDialogWithMobile(final Context mContext, final String codeQR) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_create_token_with_mobile, null, false);
        ImageView actionbarBack = customDialogView.findViewById(R.id.actionbarBack);
        tv_create_token = customDialogView.findViewById(R.id.tvtitle);
        iv_banner = customDialogView.findViewById(R.id.iv_banner);
        tvcount = customDialogView.findViewById(R.id.tvcount);
        edt_mobile = customDialogView.findViewById(R.id.edt_mobile);
        final EditText edt_id = customDialogView.findViewById(R.id.edt_id);
        final RadioGroup rg_user_id = customDialogView.findViewById(R.id.rg_user_id);
        final RadioButton rb_mobile = customDialogView.findViewById(R.id.rb_mobile);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        rg_user_id.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_mobile) {
                    edt_mobile.setVisibility(View.VISIBLE);
                    edt_id.setVisibility(View.GONE);
                    edt_id.setText("");
                } else {
                    edt_id.setVisibility(View.VISIBLE);
                    edt_mobile.setVisibility(View.GONE);
                    edt_mobile.setText("");
                }
            }
        });
        btn_create_token = customDialogView.findViewById(R.id.btn_create_token);
        btn_create_token.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean isValid = true;
                edt_mobile.setError(null);
                edt_id.setError(null);
                new AppUtils().hideKeyBoard(getActivity());
                // get selected radio button from radioGroup
                int selectedId = rg_user_id.getCheckedRadioButtonId();
                if(selectedId == R.id.rb_mobile){
                    if (TextUtils.isEmpty(edt_mobile.getText())) {
                        edt_mobile.setError(getString(R.string.error_mobile_blank));
                        isValid = false;
                    }
                }else{
                    if (TextUtils.isEmpty(edt_id.getText())) {
                        edt_id.setError(getString(R.string.error_customer_id));
                        isValid = false;
                    }
                }


                if(isValid) {
                    if (btn_create_token.getText().equals(mContext.getString(R.string.create_token))) {
                        LaunchActivity.getLaunchActivity().progressDialog.show();
                        setDispensePresenter();
                        String phone = "";
                        String cid = "";
                        if(rb_mobile.isChecked()){
                            edt_id.setText("");
                            phone = "91"+edt_mobile.getText().toString();
                        }else{
                            cid = edt_id.getText().toString();
                            edt_mobile.setText("");// set blank so that wrong phone no not pass to login screen
                        }
                        manageQueueModel.dispenseTokenWithClientInfo(
                                LaunchActivity.getLaunchActivity().getDeviceID(),
                                LaunchActivity.getLaunchActivity().getEmail(),
                                LaunchActivity.getLaunchActivity().getAuth(),
                                new JsonBusinessCustomerLookup().setCodeQR(codeQR).setCustomerPhone(phone).setBusinessCustomerId(cid));
                        btn_create_token.setClickable(false);
                        mAlertDialog.dismiss();
                    } else {
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

}
