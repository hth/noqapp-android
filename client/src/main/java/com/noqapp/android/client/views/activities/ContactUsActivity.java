package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.FeedbackApiCall;
import com.noqapp.android.client.presenter.beans.body.Feedback;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.FabricEvents;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.presenter.FeedbackPresenter;

public class ContactUsActivity extends BaseActivity implements FeedbackPresenter {
    private Feedback feedback = new Feedback();

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
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            Feedback fb = (Feedback) extras.getSerializable(IBConstant.KEY_DATA_OBJECT);
            if (null != fb) {
                feedback = fb;
            }
        }
        if (UserUtils.isLogin()) {
            btn_submit.setEnabled(true);
            edt_body.setEnabled(true);
            edt_subject.setEnabled(true);
        } else {
            btn_submit.setEnabled(false);
            edt_body.setEnabled(false);
            edt_subject.setEnabled(false);
            new CustomToast().showToast(this, "Please login to contact us");
        }
        btn_submit.setOnClickListener((View v) -> {
            new AppUtilities().hideKeyBoard(ContactUsActivity.this);
            edt_body.setError(null);
            edt_subject.setError(null);
            if (TextUtils.isEmpty(edt_subject.getText().toString())) {
                edt_subject.setError(getString(R.string.error_subject_blank));
            } else if (TextUtils.isEmpty(edt_body.getText().toString())) {
                edt_body.setError(getString(R.string.error_feedback_blank));
            } else {
                setProgressMessage("Sending feedback..");
                showProgress();
                new FeedbackApiCall(ContactUsActivity.this).review(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(),
                        feedback.setBody(edt_body.getText().toString()).setSubject(edt_subject.getText().toString()));
            }

        });

        if (AppUtilities.isRelease()) {
            Answers.getInstance().logCustom(new CustomEvent(FabricEvents.EVENT_CONTACT_US_SCREEN));
        }
    }

    @Override
    public void feedbackResponse(JsonResponse jsonResponse) {
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            new CustomToast().showToast(this, "Feedback submitted successfully");
            finish();
        } else {
            new CustomToast().showToast(this, "Failed to submit feedback");
            //Rejected from  server
            ErrorEncounteredJson eej = jsonResponse.getError();
            if (null != eej) {
                new ErrorResponseHandler().processError(this, eej);
            }
        }
        dismissProgress();
    }
}
