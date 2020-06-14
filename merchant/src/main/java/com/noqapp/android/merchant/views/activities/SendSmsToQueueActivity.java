package com.noqapp.android.merchant.views.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MessageCustomerApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.body.merchant.MessageCustomer;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.JsonTopicAdapter;
import com.noqapp.android.merchant.views.interfaces.MessageCustomerPresenter;

import java.util.ArrayList;

public class SendSmsToQueueActivity extends BaseActivity implements MessageCustomerPresenter {

    private Spinner sp_queue;
    private ArrayList<JsonTopic> jsonTopics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms_to_q);
        initActionsViews(false);
        tv_toolbar_title.setText("Send SMS to Queues");
        Button btn_submit = findViewById(R.id.btn_submit);
        sp_queue = findViewById(R.id.sp_queue);
        // max character 256 limit
        final EditText edt_body = findViewById(R.id.edt_body);
        final EditText edt_subject = findViewById(R.id.edt_subject);
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            jsonTopics = (ArrayList<JsonTopic>) getIntent().getExtras().getSerializable("jsonTopic");
            JsonTopic jsonTopic = new JsonTopic();
            jsonTopic.setDisplayName("All Queues");
            jsonTopic.setCodeQR("XYZ");
            jsonTopics.add(jsonTopic);
            JsonTopicAdapter adapter = new JsonTopicAdapter(this, jsonTopics);
            sp_queue.setAdapter(adapter);
        }

        btn_submit.setOnClickListener((View v) -> {
            AppUtils.hideKeyBoard(SendSmsToQueueActivity.this);
            edt_body.setError(null);
            if (TextUtils.isEmpty(edt_subject.getText().toString())) {
                edt_subject.setError(getString(R.string.error_title_blank));
            } else if (!TextUtils.isEmpty(edt_subject.getText().toString()) && edt_subject.getText().toString().length() < 4) {
                edt_subject.setError(getString(R.string.error_title_min_length));
            } else if (TextUtils.isEmpty(edt_body.getText().toString())) {
                edt_body.setError(getString(R.string.error_body_blank));
            } else {
                setProgressMessage("Sending sms...");
                showProgress();
                MessageCustomer messageCustomer = new MessageCustomer();
                messageCustomer.setBody(edt_body.getText().toString());
                messageCustomer.setCodeQRs(getSelectedCodeQRList());
                messageCustomer.setTitle(edt_subject.getText().toString());
                MessageCustomerApiCalls messageCustomerApiCalls = new MessageCustomerApiCalls();
                messageCustomerApiCalls.setReceiptInfoPresenter(SendSmsToQueueActivity.this);
                messageCustomerApiCalls.messageCustomer(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(),
                        messageCustomer);
            }

        });


    }

    private ArrayList<String> getSelectedCodeQRList() {
        ArrayList<String> data = new ArrayList<>();
        if (sp_queue.getSelectedItemPosition() == jsonTopics.size() - 1) {
            // Do not send the added JsonTopic for all
            for (int i = 0; i < jsonTopics.size() - 1; i++) {
                data.add(jsonTopics.get(i).getCodeQR());
            }
        } else {
            data.add(jsonTopics.get(sp_queue.getSelectedItemPosition()).getCodeQR());
        }
        return data;
    }

    @Override
    public void messageCustomerResponse(JsonResponse jsonResponse) {
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            new CustomToast().showToast(this, "SMS send successfully");
            finish();
        } else {
            new CustomToast().showToast(this, "Failed to send SMS");
            //Rejected from  server
            ErrorEncounteredJson eej = jsonResponse.getError();
            if (null != eej) {
                new ErrorResponseHandler().processError(this, eej);
            }
        }
        dismissProgress();
    }
}
