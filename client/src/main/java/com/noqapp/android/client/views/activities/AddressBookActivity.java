package com.noqapp.android.client.views.activities;


import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ClientProfileApiCall;
import com.noqapp.android.client.presenter.ProfileAddressPresenter;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.AddressListAdapter;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonUserAddress;
import com.noqapp.android.common.beans.JsonUserAddressList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRadioButton;

import java.util.List;


public class AddressBookActivity extends BaseActivity implements ProfileAddressPresenter, AddressListAdapter.RemoveAddress {
    private ProgressDialog progressDialog;
    private RadioGroup rg_address;
    private EditText edt_add_address;
    private Button btn_add_address;
    private ClientProfileApiCall clientProfileApiCall;
    private ListView lv_address;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_addressbook);
        initActionsViews(true);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        lv_address = findViewById(R.id.listview_address);
        initProgress();
        tv_toolbar_title.setText(getString(R.string.screen_addressbook));
        rg_address = findViewById(R.id.rg_address);
        edt_add_address = findViewById(R.id.edt_add_address);
        btn_add_address = findViewById(R.id.btn_add_address);
        btn_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_add_address.setError(null);
                if (TextUtils.isEmpty(edt_add_address.getText().toString())) {
                    edt_add_address.setError(getString(R.string.error_field_required));
                } else {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        progressDialog.show();
                        progressDialog.setMessage("Adding address in progress..");
                        clientProfileApiCall.addProfileAddress(UserUtils.getEmail(), UserUtils.getAuth(), new JsonUserAddress().setAddress(edt_add_address.getText().toString()).setId(""));
                    }
                }
            }
        });
        TextView tv_add_address = findViewById(R.id.tv_add_address);
        tv_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_add_address.setVisibility(View.VISIBLE);
                btn_add_address.setVisibility(View.VISIBLE);
            }
        });
        clientProfileApiCall = new ClientProfileApiCall();
        clientProfileApiCall.setProfileAddressPresenter(this);
        JsonUserAddressList jsonUserAddressList = new JsonUserAddressList();
        jsonUserAddressList.setJsonUserAddresses(LaunchActivity.getUserProfile().getJsonUserAddresses());
        profileAddressResponse(jsonUserAddressList);

    }


    @Override
    public void profileAddressResponse(JsonUserAddressList jsonUserAddressList) {
        edt_add_address.setText("");
        final List<JsonUserAddress> addressList = jsonUserAddressList.getJsonUserAddresses();
        JsonProfile jp = LaunchActivity.getUserProfile();
        jp.setJsonUserAddresses(addressList);
        LaunchActivity.setUserProfile(jp);
        Log.e("address list: ", addressList.toString());
        lv_address.setAdapter(new AddressListAdapter(this, jsonUserAddressList.getJsonUserAddresses(), this));
        lv_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JsonUserAddress jsonUserAddress = addressList.get(position);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("jsonUserAddress", jsonUserAddress);
                setResult(78, resultIntent);
                //setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
        rg_address.removeAllViews();
        for (int i = 0; i < addressList.size(); i++) {

            LayoutInflater inflater = LayoutInflater.from(this);
            View radio_view = inflater.inflate(R.layout.list_item_radio, null, false);
            final AppCompatRadioButton rdbtn = radio_view.findViewById(R.id.acrb);
            rdbtn.setId(i);
            rdbtn.setTag(addressList.get(i).getId());
            rdbtn.setText(addressList.get(i).getAddress());
            rdbtn.setPadding(10, 20, 10, 20);
            rdbtn.setLayoutParams(
                    new RadioGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.FILL_PARENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );
            rdbtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_RIGHT = 2;
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (rdbtn.getRight() - rdbtn.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            // your action here
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddressBookActivity.this);
                            LayoutInflater inflater = LayoutInflater.from(AddressBookActivity.this);
                            builder.setTitle(null);
                            View customDialogView = inflater.inflate(R.layout.dialog_general, null, false);
                            builder.setView(customDialogView);
                            final AlertDialog mAlertDialog = builder.create();
                            mAlertDialog.setCanceledOnTouchOutside(false);
                            TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
                            TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
                            tvtitle.setText("Delete Address");
                            tv_msg.setText("Do you want to delete address from address list?");
                            Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
                            Button btn_no = customDialogView.findViewById(R.id.btn_no);
                            View separator = customDialogView.findViewById(R.id.seperator);
                            btn_no.setVisibility(View.VISIBLE);
                            separator.setVisibility(View.VISIBLE);
                            btn_yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                                        progressDialog.show();
                                        progressDialog.setMessage("Deleting address..");
                                        clientProfileApiCall.deleteProfileAddress(UserUtils.getEmail(), UserUtils.getAuth(), new JsonUserAddress().setAddress(rdbtn.getText().toString()).setId(rdbtn.getTag().toString()));
                                    } else {
                                        ShowAlertInformation.showNetworkDialog(AddressBookActivity.this);
                                    }
                                    mAlertDialog.dismiss();
                                }
                            });
                            btn_no.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    mAlertDialog.dismiss();
                                }
                            });
                            mAlertDialog.show();
                            return true;
                        }
                    }
                    return false;
                }
            });
            rg_address.addView(rdbtn);
        }
        rg_address.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                AppCompatRadioButton radioButtonChosen = group.findViewById(checkedId);
                JsonUserAddress jsonUserAddress = addressList.get(radioButtonChosen.getId());
                Intent resultIntent = new Intent();
                resultIntent.putExtra("jsonUserAddress", jsonUserAddress);
                setResult(78, resultIntent);
                //setResult(Activity.RESULT_OK, resultIntent);
                finish();

            }
        });

        dismissProgress();

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent resultIntent = new Intent();
        // resultIntent.putExtra("jsonUserAddress", "");
        setResult(78, resultIntent);
        finish();
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading data...");
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtilities.authenticationProcessing(this);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej)
            new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void removeAddress(JsonUserAddress jsonUserAddress) {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            progressDialog.setMessage("Deleting address..");
            clientProfileApiCall.deleteProfileAddress(UserUtils.getEmail(), UserUtils.getAuth(),
                    jsonUserAddress);
        } else {
            ShowAlertInformation.showNetworkDialog(AddressBookActivity.this);
        }
    }
}
