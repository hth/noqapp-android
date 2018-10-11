package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.FeedbackApiModel;
import com.noqapp.android.client.presenter.beans.body.Feedback;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.FeedbackPresenter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactUsActivity extends BaseActivity implements FeedbackPresenter {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.title_activity_contact_us));
        Button btn_submit = findViewById(R.id.btn_submit);
        final EditText edt_subject = findViewById(R.id.edt_subject);
        // max character 500 limit
        final EditText edt_body = findViewById(R.id.edt_body);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppUtilities().hideKeyBoard(ContactUsActivity.this);
                edt_body.setError(null);
                edt_subject.setError(null);
                if (TextUtils.isEmpty(edt_subject.getText().toString())) {
                    edt_subject.setError(getString(R.string.error_subject_blank));
                } else if (TextUtils.isEmpty(edt_body.getText().toString())) {
                    edt_body.setError(getString(R.string.error_feedback_blank));
                } else {
                    progressDialog.setMessage("Sending feedback..");
                    progressDialog.show();
                    new FeedbackApiModel(ContactUsActivity.this).review(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(),
                            new Feedback().setBody(edt_body.getText().toString()).setSubject(edt_subject.getText().toString()));
                }
            }
        });
    }

    @Override
    public void feedbackResponse(JsonResponse jsonResponse) {
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            Toast.makeText(this, "Feedback submitted successfully", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to  submit the feedback", Toast.LENGTH_LONG).show();
            //Rejected from  server
            ErrorEncounteredJson eej = jsonResponse.getError();
            if (null != eej) {
                new ErrorResponseHandler().processError(this, eej);
            }
        }
        dismissProgress();
    }


    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej)
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
        AppUtilities.authenticationProcessing(this);
    }
}
