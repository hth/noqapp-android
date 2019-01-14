package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.medical.FormVersionEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.PatientProfilePresenter;
import com.noqapp.android.merchant.model.MedicalHistoryModel;
import com.noqapp.android.merchant.model.PatientProfileModel;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.MedicalRecordPresenter;
import com.noqapp.android.merchant.presenter.beans.body.FindMedicalProfile;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class SimpleFormActivity extends AppCompatActivity implements MedicalRecordPresenter, PatientProfilePresenter {

    private TextView tv_patient_name, tv_address, tv_info;
    private ProgressDialog progressDialog;
    private EditText edt_prescription;
    private String codeQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (new AppUtils().isTablet(getApplicationContext())) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_form);
        final MedicalHistoryModel medicalHistoryModel = new MedicalHistoryModel(this);
        edt_prescription = findViewById(R.id.edt_prescription);
        final JsonQueuedPerson jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        codeQR = getIntent().getStringExtra("qCodeQR");
        tv_patient_name = findViewById(R.id.tv_patient_name);
        tv_address = findViewById(R.id.tv_address);
        tv_info = findViewById(R.id.tv_info);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_prescription));
        initProgress();
        Button btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_prescription.setError(null);
                new AppUtils().hideKeyBoard(SimpleFormActivity.this);
                if (!TextUtils.isEmpty(edt_prescription.getText().toString())) {
                    progressDialog.show();
                    JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
                    jsonMedicalRecord.setRecordReferenceId(jsonQueuedPerson.getRecordReferenceId());
                    jsonMedicalRecord.setFormVersion(FormVersionEnum.MFS1);
                    jsonMedicalRecord.setCodeQR(codeQR);
                    jsonMedicalRecord.setNoteForPatient(edt_prescription.getText().toString());
                    //  if (null != jsonPreferredBusinessList && null != jsonPreferredBusinessList.getPreferredBusinesses() && jsonPreferredBusinessList.getPreferredBusinesses().size() > 0)
                    //      jsonMedicalRecord.setStoreIdPharmacy(jsonPreferredBusinessList.getPreferredBusinesses().get(sp_preferred_list.getSelectedItemPosition()).getBizStoreId());
                    medicalHistoryModel.update(
                            BaseLaunchActivity.getDeviceID(),
                            LaunchActivity.getLaunchActivity().getEmail(),
                            LaunchActivity.getLaunchActivity().getAuth(),
                            jsonMedicalRecord);
                } else {
                    edt_prescription.setError(getString(R.string.error_field_required));
                    Toast.makeText(SimpleFormActivity.this, "Prescription is required", Toast.LENGTH_LONG).show();
                }
            }
        });
        if (TextUtils.isEmpty(tv_patient_name.getText().toString())) {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                progressDialog.show();
                PatientProfileModel profileModel = new PatientProfileModel(this);
                profileModel.fetch(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new FindMedicalProfile().setCodeQR(codeQR).setQueueUserId(jsonQueuedPerson.getQueueUserId()));
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        }

    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("updating data...");
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }


    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
    }

    @Override
    public void medicalRecordResponse(JsonResponse jsonResponse) {
        dismissProgress();
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            Toast.makeText(this, "Medical History updated Successfully", Toast.LENGTH_LONG).show();
            this.finish();
        } else {
            Toast.makeText(this, "Failed to update", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void medicalRecordError() {
        dismissProgress();
        Toast.makeText(this, "Failed to update", Toast.LENGTH_LONG).show();
    }

    @Override
    public void patientProfileResponse(JsonProfile jsonProfile) {
        if (null != jsonProfile) {
            tv_patient_name.setText(jsonProfile.getName() + " (" + new AppUtils().calculateAge(jsonProfile.getBirthday()) + ", " + jsonProfile.getGender().name() + ")");
            tv_address.setText(jsonProfile.getAddress());
            tv_info.setText(Html.fromHtml("<b> Blood Group: </b> B+ ,<b> Weight: </b> 75 Kg"));
        }
        dismissProgress();
    }


    @Override
    public void patientProfileError() {
        dismissProgress();
    }

}
