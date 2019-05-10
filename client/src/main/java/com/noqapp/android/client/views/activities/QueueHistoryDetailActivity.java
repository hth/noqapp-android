package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonQueueHistorical;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.body.Feedback;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.model.types.QueueUserStateEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.utils.CommonHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class QueueHistoryDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_history_detail);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.screen_queue_detail));
        TextView tv_store_name = findViewById(R.id.tv_store_name);
        TextView tv_address = findViewById(R.id.tv_address);
        TextView tv_order_date = findViewById(R.id.tv_order_date);
        TextView tv_order_number = findViewById(R.id.tv_order_number);
        TextView tv_queue_status = findViewById(R.id.tv_queue_status);
        TextView tv_support = findViewById(R.id.tv_support);
        TextView tv_store_rating = findViewById(R.id.tv_store_rating);
        TextView tv_add_review = findViewById(R.id.tv_add_review);
        TextView tv_name = findViewById(R.id.tv_name);
        TextView tv_patient_label = findViewById(R.id.tv_patient_label);
        Button btn_rejoin = findViewById(R.id.btn_rejoin);
        TextView tv_payment_status = findViewById(R.id.tv_payment_status);
        TextView tv_due_amt = findViewById(R.id.tv_due_amt);
        TextView tv_total_order_amt = findViewById(R.id.tv_total_order_amt);

        final JsonQueueHistorical jsonQueueHistorical = (JsonQueueHistorical) getIntent().getExtras().getSerializable(IBConstant.KEY_DATA);
        tv_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Feedback feedback = new Feedback();
                feedback.setMessageOrigin(MessageOriginEnum.Q);
                feedback.setCodeQR(jsonQueueHistorical.getCodeQR());
                feedback.setToken(jsonQueueHistorical.getTokenNumber());
                Intent in = new Intent(QueueHistoryDetailActivity.this, ContactUsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(IBConstant.KEY_DATA_OBJECT, feedback);
                in.putExtras(bundle);
                startActivity(in);
            }
        });
        btn_rejoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (jsonQueueHistorical.getBusinessType()) {
                    case DO:
                    case BK:
                        // open hospital/Bank profile
                        Intent in = new Intent(QueueHistoryDetailActivity.this, JoinActivity.class);
                        in.putExtra(IBConstant.KEY_CODE_QR, jsonQueueHistorical.getCodeQR());
                        in.putExtra(IBConstant.KEY_FROM_LIST, true);
                        in.putExtra(IBConstant.KEY_IS_CATEGORY, false);
                        startActivity(in);
                        break;
                    default:
                        // open order screen
                        Intent intent = new Intent(QueueHistoryDetailActivity.this, StoreDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("BizStoreElastic", null);
                        intent.putExtras(bundle);
                        startActivity(intent);
                }
            }
        });
        if (0 == jsonQueueHistorical.getRatingCount()) {
            tv_add_review.setVisibility(View.VISIBLE);
            if (jsonQueueHistorical.getQueueUserState() != QueueUserStateEnum.S) {
                tv_add_review.setVisibility(View.GONE);
            }
            tv_store_rating.setVisibility(View.GONE);
        } else {
            tv_add_review.setVisibility(View.GONE);
            tv_store_rating.setVisibility(View.VISIBLE);
        }
        List<JsonProfile> profileList = NoQueueBaseActivity.getAllProfileList();
        tv_name.setText(AppUtilities.getNameFromQueueUserID(jsonQueueHistorical.getQueueUserId(), profileList));
        if (tv_name.getText().toString().equals("")) {
            tv_name.setText("Guest User");
        }
        switch (jsonQueueHistorical.getBusinessType()) {
            case DO:
                tv_patient_label.setText(getString(R.string.patient_name));
                break;
            default:
                tv_patient_label.setText("Customer Name:");
        }
        tv_add_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonTokenAndQueue jsonTokenAndQueue = new JsonTokenAndQueue();
                jsonTokenAndQueue.setQueueUserId(jsonQueueHistorical.getQueueUserId());
                jsonTokenAndQueue.setDisplayName(jsonQueueHistorical.getDisplayName());
                jsonTokenAndQueue.setStoreAddress(jsonQueueHistorical.getStoreAddress());
                jsonTokenAndQueue.setBusinessType(jsonQueueHistorical.getBusinessType());
                jsonTokenAndQueue.setCodeQR(jsonQueueHistorical.getCodeQR());
                jsonTokenAndQueue.setToken(jsonQueueHistorical.getTokenNumber());
                Intent in = new Intent(QueueHistoryDetailActivity.this, ReviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(IBConstant.KEY_DATA_OBJECT, jsonTokenAndQueue);
                in.putExtras(bundle);
                startActivity(in);
            }
        });
        tv_store_rating.setText(String.valueOf(jsonQueueHistorical.getRatingCount()));
        tv_store_name.setText(jsonQueueHistorical.getDisplayName());
        tv_address.setText(jsonQueueHistorical.getStoreAddress());
        tv_queue_status.setText(jsonQueueHistorical.getQueueUserState().getDescription());
        switch (jsonQueueHistorical.getQueueUserState()) {
            case A:
            case N:
                break;
            case Q:
                tv_queue_status.setText(QueueUserStateEnum.Q.getDescription() + " (Expired)");
                break;
            case S:
                break;
            default:
        }
        tv_order_number.setText("TOKEN NO.  " + String.valueOf(jsonQueueHistorical.getTokenNumber()));
        tv_order_date.setText(CommonHelper.formatStringDate(CommonHelper.SDF_DD_MMM_YY_HH_MM_A, jsonQueueHistorical.getCreated()));
        if(null != jsonQueueHistorical.getJsonPurchaseOrder()){
            String currencySymbol = AppUtilities.getCurrencySymbol(jsonQueueHistorical.getCountryShortName());;
            tv_total_order_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonQueueHistorical.getJsonPurchaseOrder().getOrderPrice()));
            if (PaymentStatusEnum.PA == jsonQueueHistorical.getJsonPurchaseOrder().getPaymentStatus()) {
                tv_payment_status.setText("Paid via: " + jsonQueueHistorical.getJsonPurchaseOrder().getPaymentMode().getDescription());
            } else {
                tv_payment_status.setText("Payment status: " + jsonQueueHistorical.getJsonPurchaseOrder().getPaymentStatus().getDescription());
            }
        }
    }

}
