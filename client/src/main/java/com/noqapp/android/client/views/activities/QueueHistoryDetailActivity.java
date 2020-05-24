package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonQueueHistorical;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.body.Feedback;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.model.types.QueueUserStateEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.utils.CommonHelper;

import java.util.List;

public class QueueHistoryDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
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
        TextView tv_discount_amount = findViewById(R.id.tv_discount_amount);
        TextView tv_grand_total = findViewById(R.id.tv_grand_total);
        LinearLayout ll_order_details = findViewById(R.id.ll_order_details);

        final JsonQueueHistorical jsonQueueHistorical = (JsonQueueHistorical) getIntent().getExtras().getSerializable(IBConstant.KEY_DATA);
        tv_support.setOnClickListener((View v) -> {
            Feedback feedback = new Feedback();
            feedback.setMessageOrigin(MessageOriginEnum.Q);
            feedback.setCodeQR(jsonQueueHistorical.getCodeQR());
            feedback.setToken(jsonQueueHistorical.getTokenNumber());
            Intent in = new Intent(QueueHistoryDetailActivity.this, ContactUsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(IBConstant.KEY_DATA_OBJECT, feedback);
            in.putExtras(bundle);
            startActivity(in);
        });
        btn_rejoin.setOnClickListener((View v) -> {
            Intent in;
            Bundle b = new Bundle();
            switch (jsonQueueHistorical.getBusinessType()) {
                case DO:
                case BK:
                    // open hospital/Bank profile
                    in = new Intent(QueueHistoryDetailActivity.this, BeforeJoinActivity.class);
                    in.putExtra(IBConstant.KEY_IS_DO,jsonQueueHistorical.getBusinessType()== BusinessTypeEnum.DO);
                    in.putExtra(IBConstant.KEY_CODE_QR, jsonQueueHistorical.getCodeQR());
                    in.putExtra(IBConstant.KEY_FROM_LIST, true);
                    in.putExtra(IBConstant.KEY_IS_CATEGORY, false);
                    startActivity(in);
                    break;
                case HS:
                case PH: {
                    // open order screen
                    in = new Intent(this, StoreDetailActivity.class);
                    b.putSerializable("BizStoreElastic",AppUtils.getStoreElastic(jsonQueueHistorical));
                    in.putExtras(b);
                    startActivity(in);
                }
                break;
                default: {
                    // open order screen
                    in = new Intent(this, StoreWithMenuActivity.class);
                    b.putSerializable("BizStoreElastic", AppUtils.getStoreElastic(jsonQueueHistorical));
                    in.putExtras(b);
                    startActivity(in);
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
        tv_name.setText(AppUtils.getNameFromQueueUserID(jsonQueueHistorical.getQueueUserId(), profileList));
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
        tv_add_review.setOnClickListener((View v) -> {
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
        if (null != jsonQueueHistorical.getJsonPurchaseOrder()) {
            String currencySymbol = AppUtils.getCurrencySymbol(jsonQueueHistorical.getCountryShortName());
            ;
            tv_total_order_amt.setText(currencySymbol + jsonQueueHistorical.getJsonPurchaseOrder().computeFinalAmountWithDiscount());
            tv_grand_total.setText(currencySymbol + CommonHelper.displayPrice(jsonQueueHistorical.getJsonPurchaseOrder().getOrderPrice()));
            tv_discount_amount.setText(currencySymbol + CommonHelper.displayPrice(jsonQueueHistorical.getJsonPurchaseOrder().getStoreDiscount()));
            if (PaymentStatusEnum.PA == jsonQueueHistorical.getJsonPurchaseOrder().getPaymentStatus()) {
                tv_payment_status.setText("Paid via: " + jsonQueueHistorical.getJsonPurchaseOrder().getPaymentMode().getDescription());
            } else {
                tv_payment_status.setText("Payment status: " + jsonQueueHistorical.getJsonPurchaseOrder().getPaymentStatus().getDescription());
            }
        }else{
            ll_order_details.setVisibility(View.GONE);
        }
    }

}
