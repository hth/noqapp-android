package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.model.types.medical.FormVersionEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MerchantProfileModel;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.interfaces.MerchantProfessionalPresenter;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class UserProfileSettingFragment extends Fragment implements MerchantProfessionalPresenter {
    private JsonProfessionalProfilePersonal jsonProfessionalProfilePersonal;
    private ProgressDialog progressDialog;
    private AppCompatRadioButton acrb_simple, acrb_complex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_user_profile_settings, container, false);
        initProgress();
        acrb_simple = view.findViewById(R.id.acrb_simple);
        acrb_complex = view.findViewById(R.id.acrb_complex);

        Button btn_update = view.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfileSettingInfo();
            }
        });
        if (null != jsonProfessionalProfilePersonal)
            updateUI(jsonProfessionalProfilePersonal);

        return view;
    }


    public void updateUI(JsonProfessionalProfilePersonal temp) {
        this.jsonProfessionalProfilePersonal = temp;
        if (jsonProfessionalProfilePersonal.getFormVersion() == FormVersionEnum.MFD1) {
            acrb_complex.setChecked(true);
        } else {
            acrb_simple.setChecked(true);
        }
    }

    public void updateProfileSettingInfo() {
        MerchantProfileModel merchantProfileModel = new MerchantProfileModel();
        merchantProfileModel.setMerchantProfessionalPresenter(this);
        progressDialog.show();
        jsonProfessionalProfilePersonal.setFormVersion(acrb_complex.isChecked() ? FormVersionEnum.MFD1 : FormVersionEnum.MFS1);
        merchantProfileModel.updateProfessionalProfile(UserUtils.getEmail(), UserUtils.getAuth(), jsonProfessionalProfilePersonal);

    }


    @Override
    public void merchantProfessionalResponse(JsonProfessionalProfilePersonal jsonProfessionalProfilePersonal) {
        Toast.makeText(getActivity(), "Professional profile updated", Toast.LENGTH_LONG).show();
        if(null != jsonProfessionalProfilePersonal) {
            Log.v("Json Setting Profile", jsonProfessionalProfilePersonal.toString());
            updateUI(jsonProfessionalProfilePersonal);
            LaunchActivity.getLaunchActivity().setUserProfessionalProfile(jsonProfessionalProfilePersonal);
        }
        dismissProgress();
    }

    @Override
    public void merchantProfessionalError() {
        dismissProgress();
        Toast.makeText(getActivity(), "Setting profile update failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        new ErrorResponseHandler().processError(getActivity(), eej);
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating data...");
    }

    private void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
