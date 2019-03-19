package com.noqapp.android.merchant.views.activities;


import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderList;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.OrderItemAdapter;
import com.noqapp.android.merchant.views.interfaces.PaymentProcessPresenter;
import com.noqapp.android.merchant.views.interfaces.PurchaseOrderPresenter;
import com.noqapp.android.merchant.views.model.PurchaseOrderApiCalls;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class OrderDetailActivity extends AppCompatActivity implements PaymentProcessPresenter, PurchaseOrderPresenter {
    private ProgressDialog progressDialog;
    protected ImageView actionbarBack;
    protected boolean isDialog = false;
    private JsonPurchaseOrder jsonPurchaseOrder;
    private boolean isProductWithoutPrice = false;
    private TextView tv_cost;
    private String currencySymbol;
    private TextView tv_paid_amount_value, tv_remaining_amount_value;
    private Spinner sp_payment_mode;
    private String[] payment_modes = {"Cash", "Debit Card", "Credit Card", "Internet Banking", "Paytm"};
    private Button btn_update_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!isDialog) {
            if (new AppUtils().isTablet(getApplicationContext())) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_order_detail);
        if (isDialog) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenWidth = (int) (metrics.widthPixels * 0.70);
            int height = (int) (metrics.heightPixels * 0.80);
            getWindow().setLayout(screenWidth, height);
        }
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = findViewById(R.id.actionbarBack);
        initProgress();
        jsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getSerializableExtra("jsonPurchaseOrder");
        checkProductWithZeroPrice();
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
                finish();
            }
        });

        tv_toolbar_title.setText(getString(R.string.order_details));

        ListView listview = findViewById(R.id.listview);
        TextView tv_item_count = findViewById(R.id.tv_item_count);
        TextView tv_payment_mode = findViewById(R.id.tv_payment_mode);
        TextView tv_payment_status = findViewById(R.id.tv_payment_status);
        TextView tv_address = findViewById(R.id.tv_address);
        tv_cost = findViewById(R.id.tv_cost);
        sp_payment_mode = findViewById(R.id.sp_payment_mode);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, payment_modes);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_payment_mode.setAdapter(aa);
//        sp_payment_mode.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                ((TextView) sp_payment_mode.getSelectedView()).setTextColor(Color.BLACK);
//            }
//        });
        TextView tv_notes = findViewById(R.id.tv_notes);
        tv_remaining_amount_value = findViewById(R.id.tv_remaining_amount_value);
        tv_paid_amount_value = findViewById(R.id.tv_paid_amount_value);
        CardView cv_notes = findViewById(R.id.cv_notes);
        EditText edt_amount = findViewById(R.id.edt_amount);
        RelativeLayout rl_payment = findViewById(R.id.rl_payment);
        tv_notes.setText("Additional Notes: " + jsonPurchaseOrder.getAdditionalNote());
        cv_notes.setVisibility(TextUtils.isEmpty(jsonPurchaseOrder.getAdditionalNote()) ? View.GONE : View.VISIBLE);
        tv_address.setText(Html.fromHtml(jsonPurchaseOrder.getDeliveryAddress()));
        currencySymbol = BaseLaunchActivity.getCurrencySymbol();
        try {
            if (TextUtils.isEmpty(jsonPurchaseOrder.getPartialPayment())) {
                tv_paid_amount_value.setText(currencySymbol + " " + String.valueOf(0));
                tv_remaining_amount_value.setText(currencySymbol + " " + String.valueOf(Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100));
            } else {
                tv_paid_amount_value.setText(currencySymbol + " " + String.valueOf(Double.parseDouble(jsonPurchaseOrder.getPartialPayment()) / 100));
                tv_remaining_amount_value.setText(currencySymbol + " " + String.valueOf((Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) - Double.parseDouble(jsonPurchaseOrder.getPartialPayment())) / 100));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button btn_pay_now = findViewById(R.id.btn_pay_now);
        Button btn_pay_partial = findViewById(R.id.btn_pay_partial);
        if (PaymentStatusEnum.PP == jsonPurchaseOrder.getPaymentStatus() ||
                PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus()) {
            if(isProductWithoutPrice){
                rl_payment.setVisibility(View.GONE);
            }else {
                rl_payment.setVisibility(View.VISIBLE);
            }
            if (PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus()) {
                btn_pay_partial.setVisibility(View.INVISIBLE);
                edt_amount.setVisibility(View.INVISIBLE);
            }
        } else {
            rl_payment.setVisibility(View.GONE);
        }
        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus() ||
                PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus()) {
            tv_payment_mode.setText(jsonPurchaseOrder.getPaymentMode().getDescription());
            tv_payment_status.setText(jsonPurchaseOrder.getPaymentStatus().getDescription());
            if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
                tv_paid_amount_value.setText(currencySymbol + " " + String.valueOf(Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100));
                tv_remaining_amount_value.setText(currencySymbol + " 0");
            }
        } else {
            tv_payment_status.setText(jsonPurchaseOrder.getPaymentStatus().getDescription());
        }

        try {
            tv_cost.setText(currencySymbol + " " + String.valueOf(Integer.parseInt(jsonPurchaseOrder.getOrderPrice()) / 100));
        } catch (Exception e) {
            tv_cost.setText(currencySymbol + " " + String.valueOf(0 / 100));
        }
        initProgress();

        btn_pay_partial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isProductWithoutPrice) {
                    Toast.makeText(OrderDetailActivity.this, "Some product having 0 price. Please set price to them", Toast.LENGTH_LONG).show();
                } else {
                    if (TextUtils.isEmpty(edt_amount.getText().toString())) {
                        Toast.makeText(OrderDetailActivity.this, "Please enter amount to pay.", Toast.LENGTH_LONG).show();
                    } else {
                        if (Double.parseDouble(edt_amount.getText().toString()) * 100 > Double.parseDouble(jsonPurchaseOrder.getOrderPrice())) {
                            Toast.makeText(OrderDetailActivity.this, "Please enter amount less or equal to order amount.", Toast.LENGTH_LONG).show();
                        } else {
                            progressDialog.show();
                            progressDialog.setMessage("Starting payment..");
                            //jsonPurchaseOrder.setPaymentMode(PaymentModeEnum.CA); //not required here
                            jsonPurchaseOrder.setPartialPayment(String.valueOf(Double.parseDouble(edt_amount.getText().toString()) * 100));
                            PurchaseOrderApiCalls purchaseOrderApiCalls = new PurchaseOrderApiCalls();
                            purchaseOrderApiCalls.setPaymentProcessPresenter(OrderDetailActivity.this);
                            purchaseOrderApiCalls.setPurchaseOrderPresenter(OrderDetailActivity.this);
                            purchaseOrderApiCalls.modify(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
                        }
                    }
                }
            }
        });

        btn_pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isProductWithoutPrice) {
                    Toast.makeText(OrderDetailActivity.this, "Some product having 0 price. Please set price to them", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.show();
                    progressDialog.setMessage("Starting payment..");
                    //jsonPurchaseOrder.setPaymentMode(PaymentModeEnum.CA); //not required here
                    jsonPurchaseOrder.setPartialPayment(jsonPurchaseOrder.getOrderPrice());
                    PurchaseOrderApiCalls purchaseOrderApiCalls = new PurchaseOrderApiCalls();
                    purchaseOrderApiCalls.setPaymentProcessPresenter(OrderDetailActivity.this);
                    purchaseOrderApiCalls.setPurchaseOrderPresenter(OrderDetailActivity.this);
                    purchaseOrderApiCalls.modify(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
                }

            }
        });
        tv_item_count.setText("Total Items: (" + jsonPurchaseOrder.getPurchaseOrderProducts().size() + ")");
        OrderItemAdapter adapter = new OrderItemAdapter(this, jsonPurchaseOrder.getPurchaseOrderProducts(), currencySymbol, this);
        listview.setAdapter(adapter);
    }

    public void updateProductPriceList(JsonPurchaseOrderProduct jpop, int pos) {
        jsonPurchaseOrder.getPurchaseOrderProducts().set(pos, jpop);
        checkProductWithZeroPrice();
        jsonPurchaseOrder.setOrderPrice(String.valueOf(calculateTotalPrice()));
        tv_cost.setText(currencySymbol + " " + String.valueOf(Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100));
        try {
            if (TextUtils.isEmpty(jsonPurchaseOrder.getPartialPayment())) {
                tv_paid_amount_value.setText(currencySymbol + " " + String.valueOf(0));
                tv_remaining_amount_value.setText(currencySymbol + " " + String.valueOf(Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100));
            } else {
                tv_paid_amount_value.setText(currencySymbol + " " + String.valueOf(Double.parseDouble(jsonPurchaseOrder.getPartialPayment()) / 100));
                tv_remaining_amount_value.setText(currencySymbol + " " + String.valueOf((Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) - Double.parseDouble(jsonPurchaseOrder.getPartialPayment())) / 100));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double calculateTotalPrice() {
        int amount = 0;
        for (int i = 0; i < jsonPurchaseOrder.getPurchaseOrderProducts().size(); i++) {
            amount += 1 * jsonPurchaseOrder.getPurchaseOrderProducts().get(i).getProductPrice();
        }
        return amount;
    }

    public void checkProductWithZeroPrice() {
        isProductWithoutPrice = false;
        if (null != jsonPurchaseOrder && null != jsonPurchaseOrder.getPurchaseOrderProducts() && jsonPurchaseOrder.getPurchaseOrderProducts().size() > 0) {
            for (JsonPurchaseOrderProduct jpop :
                    jsonPurchaseOrder.getPurchaseOrderProducts()) {
                if (jpop.getProductPrice() == 0) {
                    isProductWithoutPrice = true;
                    break;
                }
            }
        }
    }


    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Queue Settings...");
    }

    private void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
        if (null != BaseLaunchActivity.merchantListFragment) {
            BaseLaunchActivity.merchantListFragment.onRefresh();
        }
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        new ErrorResponseHandler().processError(OrderDetailActivity.this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
    }

    @Override
    public void paymentProcessResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        dismissProgress();
        if (null != jsonPurchaseOrder) {
            if (jsonPurchaseOrder.getPaymentStatus() == PaymentStatusEnum.PA ||
                    jsonPurchaseOrder.getPaymentStatus() == PaymentStatusEnum.MP) {
                Toast.makeText(OrderDetailActivity.this, "Payment updated successfully", Toast.LENGTH_LONG).show();
                if (null != BaseLaunchActivity.merchantListFragment) {
                    BaseLaunchActivity.merchantListFragment.onRefresh();
                }
            }
        }
    }

    @Override
    public void purchaseOrderResponse(JsonPurchaseOrderList jsonPurchaseOrderList) {
        dismissProgress();
        if (null != jsonPurchaseOrderList) {
            Log.v("order data:", jsonPurchaseOrderList.toString());
            finish();
            if (null != BaseLaunchActivity.merchantListFragment) {
                BaseLaunchActivity.merchantListFragment.onRefresh();
            }
        }
    }

    @Override
    public void purchaseOrderError() {
        dismissProgress();
    }
}
