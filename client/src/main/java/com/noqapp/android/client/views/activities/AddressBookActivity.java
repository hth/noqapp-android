package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ClientPreferenceApiCalls;
import com.noqapp.android.client.model.ClientProfileApiCall;
import com.noqapp.android.client.presenter.ClientPreferencePresenter;
import com.noqapp.android.client.presenter.ProfileAddressPresenter;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.AddressListAdapter;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonUserAddress;
import com.noqapp.android.common.beans.JsonUserAddressList;
import com.noqapp.android.common.beans.JsonUserPreference;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;

import java.util.List;

public class AddressBookActivity
        extends BaseActivity
        implements ProfileAddressPresenter, AddressListAdapter.UpdateAddress, ClientPreferencePresenter {
    private EditText edt_add_address;
    private LinearLayout ll_add_address;
    private ClientProfileApiCall clientProfileApiCall;
    private ListView lv_address;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addressbook);
        initActionsViews(true);
        actionbarBack.setOnClickListener((View v) -> onBackPressed());
        lv_address = findViewById(R.id.listview_address);
        tv_toolbar_title.setText(getString(R.string.screen_addressbook));
        edt_add_address = findViewById(R.id.edt_add_address);
        Button btn_add_address = findViewById(R.id.btn_add_address);
        btn_add_address.setOnClickListener((View v) -> {
            edt_add_address.setError(null);
            if (TextUtils.isEmpty(edt_add_address.getText().toString())) {
                edt_add_address.setError(getString(R.string.error_field_required));
            } else {
                AppUtils.hideKeyBoard(AddressBookActivity.this);
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    showProgress();
                    setProgressMessage("Adding address in progress...");
                    clientProfileApiCall.addProfileAddress(
                            UserUtils.getEmail(),
                            UserUtils.getAuth(),
                            new JsonUserAddress().setAddress(edt_add_address.getText().toString()).setId(""));
                }
            }
        });
        ll_add_address = findViewById(R.id.ll_add_address);
        TextView tv_add_address = findViewById(R.id.tv_add_address);
        tv_add_address.setOnClickListener((View v) -> {
            ll_add_address.setVisibility(View.VISIBLE);
        });
        ImageView iv_close = findViewById(R.id.iv_close);
        iv_close.setOnClickListener((View v) -> {
            ll_add_address.setVisibility(View.GONE);
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
        lv_address.setAdapter(
                new AddressListAdapter(
                        this,
                        jsonUserAddressList.getJsonUserAddresses(),
                        this,
                        jp.getJsonUserPreference().getUserAddressId()));

        lv_address.setOnItemClickListener((parent, view, position, id) -> {
            JsonUserAddress jsonUserAddress = addressList.get(position);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("jsonUserAddress", jsonUserAddress);
            setResult(78, resultIntent);
            //setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });

        dismissProgress();
        ll_add_address.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent resultIntent = new Intent();
        // resultIntent.putExtra("jsonUserAddress", "");
        setResult(78, resultIntent);
        finish();
    }

    @Override
    public void removeAddress(JsonUserAddress jsonUserAddress) {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            showProgress();
            setProgressMessage("Deleting address...");
            clientProfileApiCall.deleteProfileAddress(UserUtils.getEmail(), UserUtils.getAuth(), jsonUserAddress);
        } else {
            ShowAlertInformation.showNetworkDialog(AddressBookActivity.this);
        }
    }

    @Override
    public void setPrimaryAddress(JsonUserAddress jsonUserAddress) {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            showProgress();
            setProgressMessage("Updating address...");
            ClientPreferenceApiCalls clientProfileApiCall = new ClientPreferenceApiCalls();
            clientProfileApiCall.setClientPreferencePresenter(this);
            JsonUserPreference jsonUserPreference = LaunchActivity.getUserProfile().getJsonUserPreference();
            if (null != jsonUserAddress) {
                jsonUserPreference.setUserAddressId(jsonUserAddress.getId());
            }
            clientProfileApiCall.order(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonUserPreference);
        } else {
            ShowAlertInformation.showNetworkDialog(AddressBookActivity.this);
        }
    }

    @Override
    public void clientPreferencePresenterResponse(JsonUserPreference jsonUserPreference) {
        if (null != jsonUserPreference) {
            LaunchActivity.getUserProfile().setJsonUserPreference(jsonUserPreference);
            JsonUserAddressList jsonUserAddressList = new JsonUserAddressList();
            jsonUserAddressList.setJsonUserAddresses(LaunchActivity.getUserProfile().getJsonUserAddresses());
            for (int i = 0; i < jsonUserAddressList.getJsonUserAddresses().size(); i++) {
                if (jsonUserAddressList.getJsonUserAddresses().get(i).getId().equals(jsonUserPreference.getUserAddressId())) {
                    jsonUserAddressList.getJsonUserAddresses().get(i).setId(jsonUserPreference.getUserAddressId());
                    return;
                }
            }
            profileAddressResponse(jsonUserAddressList);
        }
    }
}
