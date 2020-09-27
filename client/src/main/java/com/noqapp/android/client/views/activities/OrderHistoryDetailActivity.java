package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistorical;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderProductHistorical;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.body.Feedback;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.CommonHelper;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

public class OrderHistoryDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(MyApplication.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_detail);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.screen_order_detail));
        TextView tv_total_order_amt = findViewById(R.id.tv_total_order_amt);
        TextView tv_tax_amt = findViewById(R.id.tv_tax_amt);
        TextView tv_due_amt = findViewById(R.id.tv_due_amt);
        LinearLayout ll_order_details = findViewById(R.id.ll_order_details);

        TextView tv_store_name = findViewById(R.id.tv_store_name);
        TextView tv_address = findViewById(R.id.tv_address);
        TextView tv_payment_mode = findViewById(R.id.tv_payment_mode);
        TextView tv_delivery_address = findViewById(R.id.tv_delivery_address);
        TextView tv_order_date = findViewById(R.id.tv_order_date);
        TextView tv_order_number = findViewById(R.id.tv_order_number);
        TextView tv_order_status = findViewById(R.id.tv_order_status);
        TextView tv_support = findViewById(R.id.tv_support);
        TextView tv_store_rating = findViewById(R.id.tv_store_rating);
        TextView tv_add_review = findViewById(R.id.tv_add_review);
        TextView tv_additional_info = findViewById(R.id.tv_additional_info);
        LinearLayout ll_patient = findViewById(R.id.ll_patient);
        TextView tv_name = findViewById(R.id.tv_name);
        Button btn_reorder = findViewById(R.id.btn_reorder);
        TextView tv_patient_label = findViewById(R.id.tv_patient_label);
        final JsonPurchaseOrderHistorical jsonPurchaseOrder = (JsonPurchaseOrderHistorical) getIntent().getExtras().getSerializable(IBConstant.KEY_DATA);
        if (jsonPurchaseOrder.getBusinessType() == BusinessTypeEnum.PH) {   // to avoid crash it is added for  Pharmacy order place from merchant side directly
            jsonPurchaseOrder.setOrderPrice("0");
        }
        List<JsonProfile> profileList = MyApplication.getAllProfileList();
        tv_name.setText(AppUtils.getNameFromQueueUserID(jsonPurchaseOrder.getQueueUserId(), profileList));
        if (tv_name.getText().toString().equals("")) {
            tv_name.setText("Guest User");
        }
        switch (jsonPurchaseOrder.getBusinessType()) {
            case DO:
            case HS:
            case PH:
                tv_patient_label.setText(getString(R.string.patient_name));
                break;
            default:
                ll_patient.setVisibility(View.GONE);
                tv_patient_label.setText("Customer Name:");
        }
        String currencySymbol = AppUtils.getCurrencySymbol(jsonPurchaseOrder.getCountryShortName());
        tv_support.setOnClickListener((View v) -> {
            Feedback feedback = new Feedback();
            feedback.setMessageOrigin(MessageOriginEnum.O);
            feedback.setCodeQR(jsonPurchaseOrder.getCodeQR());
            feedback.setToken(jsonPurchaseOrder.getTokenNumber());
            Intent in = new Intent(OrderHistoryDetailActivity.this, ContactUsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(IBConstant.KEY_DATA_OBJECT, feedback);
            in.putExtras(bundle);
            startActivity(in);
        });
        btn_reorder.setOnClickListener((View v) -> {
            BizStoreElastic bizStoreElastic = new BizStoreElastic();
            bizStoreElastic.setRating(jsonPurchaseOrder.getRatingCount());
            bizStoreElastic.setDisplayImage("");
            bizStoreElastic.setBusinessName(jsonPurchaseOrder.getDisplayName());
            bizStoreElastic.setCodeQR(jsonPurchaseOrder.getCodeQR());
            bizStoreElastic.setBusinessType(jsonPurchaseOrder.getBusinessType());
            Intent intent = new Intent(OrderHistoryDetailActivity.this, StoreWithMenuActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("BizStoreElastic", bizStoreElastic);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        if (0 == jsonPurchaseOrder.getRatingCount()) {
            tv_add_review.setVisibility(View.VISIBLE);
            if (jsonPurchaseOrder.getPresentOrderState() != PurchaseOrderStateEnum.OD) {
                tv_add_review.setVisibility(View.GONE);
            }
            tv_store_rating.setVisibility(View.GONE);
        } else {
            tv_add_review.setVisibility(View.GONE);
            tv_store_rating.setVisibility(View.VISIBLE);
        }
        tv_add_review.setOnClickListener((View v) -> {
            JsonTokenAndQueue jsonTokenAndQueue = new JsonTokenAndQueue();
            jsonTokenAndQueue.setQueueUserId(jsonPurchaseOrder.getQueueUserId());
            jsonTokenAndQueue.setDisplayName(jsonPurchaseOrder.getDisplayName());
            jsonTokenAndQueue.setStoreAddress(jsonPurchaseOrder.getStoreAddress());
            jsonTokenAndQueue.setBusinessType(jsonPurchaseOrder.getBusinessType());
            jsonTokenAndQueue.setCodeQR(jsonPurchaseOrder.getCodeQR());
            jsonTokenAndQueue.setToken(jsonPurchaseOrder.getTokenNumber());
            jsonTokenAndQueue.setDisplayToken(jsonPurchaseOrder.getDisplayToken());
            Intent in = new Intent(OrderHistoryDetailActivity.this, ReviewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("object", jsonTokenAndQueue);
            in.putExtras(bundle);
            startActivity(in);
        });
        tv_store_rating.setText(String.valueOf(jsonPurchaseOrder.getRatingCount()));
        tv_tax_amt.setText(currencySymbol + "0.00");
        tv_due_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice()));
        tv_total_order_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice()));
        for (int i = 0; i < jsonPurchaseOrder.getJsonPurchaseOrderProductHistoricalList().size(); i++) {
            JsonPurchaseOrderProductHistorical jsonPurchaseOrderProduct = jsonPurchaseOrder.getJsonPurchaseOrderProductHistoricalList().get(i);
            LayoutInflater inflater = LayoutInflater.from(this);
            View inflatedLayout = inflater.inflate(R.layout.order_summary_item, null, false);
            TextView tv_title = inflatedLayout.findViewById(R.id.tv_title);
            TextView tv_total_price = inflatedLayout.findViewById(R.id.tv_total_price);
            //tv_title.setText(jsonPurchaseOrderProduct.getProductName() + " " + AppUtils.getPriceWithUnits(jsonPurchaseOrderProduct.getJsonStoreProduct()) + " " + currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrderProduct.getProductPrice()) + " x " + String.valueOf(jsonPurchaseOrderProduct.getProductQuantity()));
            tv_title.setText(jsonPurchaseOrderProduct.getProductName() + "\n" + currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrderProduct.getProductPrice()) + " x " + jsonPurchaseOrderProduct.getProductQuantity());
            tv_total_price.setText(currencySymbol + CommonHelper.displayPrice(new BigDecimal(jsonPurchaseOrderProduct.getProductPrice()).multiply(new BigDecimal(jsonPurchaseOrderProduct.getProductQuantity())).toString()));
            ll_order_details.addView(inflatedLayout);
        }
        tv_store_name.setText(jsonPurchaseOrder.getDisplayName());
        tv_address.setText(jsonPurchaseOrder.getStoreAddress());
        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
            tv_payment_mode.setText("Paid via: " + jsonPurchaseOrder.getPaymentMode().getDescription());
        } else {
            tv_payment_mode.setText("Payment status: " + jsonPurchaseOrder.getPaymentStatus().getDescription());
        }
        tv_delivery_address.setText(StringUtils.isBlank(jsonPurchaseOrder.getDeliveryAddress()) ? "N/A" : jsonPurchaseOrder.getDeliveryAddress());
        tv_order_status.setText(jsonPurchaseOrder.getPresentOrderState().getFriendlyDescription());
        tv_order_number.setText("ORDER NO.  " + jsonPurchaseOrder.getDisplayToken());
        tv_order_date.setText(CommonHelper.formatStringDate(CommonHelper.SDF_DD_MMM_YY_HH_MM_A, jsonPurchaseOrder.getCreated()));
        tv_additional_info.setText("Additional Note: " + jsonPurchaseOrder.getAdditionalNote());
        tv_additional_info.setVisibility(TextUtils.isEmpty(jsonPurchaseOrder.getAdditionalNote()) ? View.GONE : View.VISIBLE);
    }

}
