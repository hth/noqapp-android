package com.noqapp.android.merchant.views.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.noqapp.android.common.beans.JsonCoupon;
import com.noqapp.android.common.beans.body.CouponOnOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderList;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.DiscountTypeEnum;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.presenter.CouponApplyRemovePresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.CouponApiCalls;
import com.noqapp.android.merchant.model.ReceiptInfoApiCalls;
import com.noqapp.android.merchant.presenter.beans.body.merchant.OrderServed;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.utils.PermissionHelper;
import com.noqapp.android.merchant.utils.ReceiptGeneratorPDF;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.OrderItemAdapter;
import com.noqapp.android.merchant.views.interfaces.ModifyOrderPresenter;
import com.noqapp.android.merchant.views.interfaces.OrderProcessedPresenter;
import com.noqapp.android.merchant.views.interfaces.PaymentProcessPresenter;
import com.noqapp.android.merchant.views.interfaces.PurchaseOrderPresenter;
import com.noqapp.android.merchant.views.interfaces.ReceiptInfoPresenter;
import com.noqapp.android.merchant.views.model.PurchaseOrderApiCalls;
import com.noqapp.android.merchant.views.pojos.Receipt;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class OrderDetailActivity
        extends BaseActivity
        implements PaymentProcessPresenter, PurchaseOrderPresenter, ModifyOrderPresenter,
        OrderProcessedPresenter, ReceiptInfoPresenter, CouponApplyRemovePresenter {
    protected ImageView actionbarBack;
    private JsonPurchaseOrder jsonPurchaseOrder;
    private boolean isProductWithoutPrice = false;
    private TextView tv_cost;
    private String currencySymbol;
    private TextView tv_paid_amount_value, tv_remaining_amount_value, tv_notes;
    private Spinner sp_payment_mode;
    private final String[] payment_modes = {
            "Cash",
            "Cheque",
            "Credit Card",
            "Debit Card",
            "Internet Banking",
            "Paytm"
    };

    private final PaymentModeEnum[] payment_modes_enum = {
            PaymentModeEnum.CA,
            PaymentModeEnum.CQ,
            PaymentModeEnum.CC,
            PaymentModeEnum.DC,
            PaymentModeEnum.NTB,
            PaymentModeEnum.PTM
    };

    private Button btn_update_price;
    private CardView cv_notes;
    private EditText edt_amount;
    private View rl_payment;
    private TextView tv_payment_mode, tv_payment_status, tv_address, tv_multiple_payment, tv_transaction_via, tv_order_state, tv_transaction_id;
    private Button btn_pay_partial, btn_refund;
    public static UpdateWholeList updateWholeList;
    private RelativeLayout rl_multiple;
    private TextView tv_token, tv_q_name, tv_customer_name;
    private OrderItemAdapter adapter;
    private long mLastClickTime = 0;
    private TextView tv_payment_msg;
    private TextView tv_grand_total_amt;
    private TextView tv_coupon_discount_amt;
    private TextView tv_tax;
    private Button btn_discount, btn_remove_discount;
    private TextView tv_discount_value;
    private LinearLayout ll_partial;

    public interface UpdateWholeList {
        void updateWholeList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_order_detail);

        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = findViewById(R.id.actionbarBack);
        jsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getSerializableExtra("jsonPurchaseOrder");
        checkProductWithZeroPrice();
        actionbarBack.setOnClickListener(v -> {
            //onBackPressed();
            // updateWholeList = null;
            finish();
        });

        tv_toolbar_title.setText(getString(R.string.order_details));
        tv_token = findViewById(R.id.tv_token);
        tv_tax = findViewById(R.id.tv_tax);
        tv_q_name = findViewById(R.id.tv_q_name);
        tv_customer_name = findViewById(R.id.tv_customer_name);
        tv_payment_msg = findViewById(R.id.tv_payment_msg);
        ListView listview = findViewById(R.id.listview);
        TextView tv_item_count = findViewById(R.id.tv_item_count);
        tv_payment_mode = findViewById(R.id.tv_payment_mode);
        tv_payment_status = findViewById(R.id.tv_payment_status);
        tv_address = findViewById(R.id.tv_address);
        btn_update_price = findViewById(R.id.btn_update_price);
        tv_cost = findViewById(R.id.tv_cost);
        tv_multiple_payment = findViewById(R.id.tv_multiple_payment);
        tv_transaction_via = findViewById(R.id.tv_transaction_via);
        tv_order_state = findViewById(R.id.tv_order_state);
        tv_transaction_id = findViewById(R.id.tv_transaction_id);
        rl_multiple = findViewById(R.id.rl_multiple);
        sp_payment_mode = findViewById(R.id.sp_payment_mode);
        tv_coupon_discount_amt = findViewById(R.id.tv_coupon_discount_amt);
        tv_grand_total_amt = findViewById(R.id.tv_grand_total_amt);
        btn_discount = findViewById(R.id.btn_discount);
        btn_remove_discount = findViewById(R.id.btn_remove_discount);
        tv_discount_value = findViewById(R.id.tv_discount_value);
        ll_partial = findViewById(R.id.ll_partial);
        btn_discount.setOnClickListener(v -> {
            Intent in = new Intent(OrderDetailActivity.this, CouponActivity.class);
            in.putExtra(IBConstant.KEY_CODE_QR, jsonPurchaseOrder.getCodeQR());
            startActivityForResult(in, Constants.ACTIVITY_RESULT_BACK);
        });
        btn_remove_discount.setOnClickListener(v -> {
            ShowCustomDialog showDialog = new ShowCustomDialog(OrderDetailActivity.this, true);
            showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                @Override
                public void btnPositiveClick() {
                    if (new NetworkUtil(OrderDetailActivity.this).isOnline()) {
                        showProgress();
                        setProgressMessage("Removing discount...");
                        // progressDialog.setCancelable(false);
                        // progressDialog.setCanceledOnTouchOutside(false);
                        CouponApiCalls couponApiCalls = new CouponApiCalls();
                        couponApiCalls.setCouponApplyRemovePresenter(OrderDetailActivity.this);

                        CouponOnOrder couponOnOrder = new CouponOnOrder()
                                .setQueueUserId(jsonPurchaseOrder.getQueueUserId())
                                .setCouponId(jsonPurchaseOrder.getCouponId())
                                .setCodeQR(jsonPurchaseOrder.getCodeQR())
                                .setTransactionId(jsonPurchaseOrder.getTransactionId());

                        couponApiCalls.remove(AppInitialize.getDeviceID(),
                                AppInitialize.getEmail(),
                                AppInitialize.getAuth(),
                                couponOnOrder);
                    } else {
                        ShowAlertInformation.showNetworkDialog(OrderDetailActivity.this);
                    }
                }

                @Override
                public void btnNegativeClick() {
                    //Do nothing
                }
            });
            showDialog.displayDialog("Remove coupon", "Do you want to remove the coupon?");
        });
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, payment_modes);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_payment_mode.setAdapter(aa);
        sp_payment_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    ((TextView) view).setTextColor(Color.BLACK); //Change selected text color
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tv_notes = findViewById(R.id.tv_notes);
        tv_remaining_amount_value = findViewById(R.id.tv_remaining_amount_value);
        tv_paid_amount_value = findViewById(R.id.tv_paid_amount_value);
        cv_notes = findViewById(R.id.cv_notes);
        edt_amount = findViewById(R.id.edt_amount);
        rl_payment = findViewById(R.id.rl_payment);
        btn_refund = findViewById(R.id.btn_refund);
        btn_refund.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            final Dialog dialog = new Dialog(OrderDetailActivity.this, android.R.style.Theme_Dialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_refund);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true);
            ImageView actionbarBack = dialog.findViewById(R.id.actionbarBack);
            final TextView tv_random = dialog.findViewById(R.id.tv_random);
            final EditText edt_random = dialog.findViewById(R.id.edt_random);
            tv_random.setText(AppUtils.randomStringGenerator(3).toUpperCase());
            final Button btn_update = dialog.findViewById(R.id.btn_update);
            btn_update.setOnClickListener(v12 -> {
                edt_random.setError(null);
                AppUtils.hideKeyBoard(OrderDetailActivity.this);
                if (!edt_random.getText().toString().equals(tv_random.getText().toString())) {
                    edt_random.setError(OrderDetailActivity.this.getString(R.string.error_invalid_captcha));
                    new CustomToast().showToast(OrderDetailActivity.this, getString(R.string.error_invalid_captcha));
                } else {
                    // do process
                    if (new NetworkUtil(OrderDetailActivity.this).isOnline()) {
                        showProgress();
                        setProgressMessage("Starting payment refund...");
                        setProgressCancel(false);
                        OrderServed orderServed = new OrderServed();
                        orderServed.setCodeQR(jsonPurchaseOrder.getCodeQR());
                        orderServed.setServedNumber(jsonPurchaseOrder.getToken());
                        orderServed.setQueueUserId(jsonPurchaseOrder.getQueueUserId());
                        orderServed.setTransactionId(jsonPurchaseOrder.getTransactionId());
                        orderServed.setQueueStatus(QueueStatusEnum.N);
                        orderServed.setPurchaseOrderState(jsonPurchaseOrder.getPresentOrderState());
                        PurchaseOrderApiCalls purchaseOrderApiCalls = new PurchaseOrderApiCalls();
                        purchaseOrderApiCalls.setOrderProcessedPresenter(OrderDetailActivity.this);
                        purchaseOrderApiCalls.cancel(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), orderServed);

                    } else {
                        ShowAlertInformation.showNetworkDialog(OrderDetailActivity.this);
                    }
                    btn_update.setClickable(false);
                    dialog.dismiss();
                }
            });

            actionbarBack.setOnClickListener(v1 -> dialog.dismiss());
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();
        });
        Button btn_pay_now = findViewById(R.id.btn_pay_now);
        btn_pay_partial = findViewById(R.id.btn_pay_partial);
        btn_pay_partial.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (isProductWithoutPrice) {
                new CustomToast().showToast(OrderDetailActivity.this, "Some product having 0 price. Please set price to them");
            } else {
                if (TextUtils.isEmpty(edt_amount.getText().toString())) {
                    new CustomToast().showToast(OrderDetailActivity.this, "Please enter amount to pay.");
                } else {
                    if (Double.parseDouble(edt_amount.getText().toString()) * 100 > Double.parseDouble(jsonPurchaseOrder.getOrderPrice())) {
                        new CustomToast().showToast(OrderDetailActivity.this, "Please enter amount less or equal to order amount.");
                    } else {

                        ShowCustomDialog showDialog = new ShowCustomDialog(OrderDetailActivity.this);
                        showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                            @Override
                            public void btnPositiveClick() {
                                if (new NetworkUtil(OrderDetailActivity.this).isOnline()) {
                                    showProgress();
                                    setProgressMessage("Starting payment...");
                                    setProgressCancel(false);
                                    jsonPurchaseOrder.setPaymentMode(payment_modes_enum[sp_payment_mode.getSelectedItemPosition()]);
                                    jsonPurchaseOrder.setPartialPayment(new BigDecimal(edt_amount.getText().toString()).multiply(new BigDecimal("100")).toString());
                                    PurchaseOrderApiCalls purchaseOrderApiCalls = new PurchaseOrderApiCalls();
                                    purchaseOrderApiCalls.setPaymentProcessPresenter(OrderDetailActivity.this);
                                    purchaseOrderApiCalls.partialCounterPayment(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
                                } else {
                                    ShowAlertInformation.showNetworkDialog(OrderDetailActivity.this);
                                }
                            }

                            @Override
                            public void btnNegativeClick() {
                                //Do nothing
                            }
                        });
                        showDialog.displayDialog("Alert", "You are initiating payment process. Please confirm");
                    }
                }
            }
        });
        btn_update_price.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (isProductWithoutPrice) {
                new CustomToast().showToast(OrderDetailActivity.this, "Some product having 0 price. Please set price to them");
            } else {
                showProgress();
                setProgressMessage("Updating price...");
                setProgressCancel(false);
                PurchaseOrderApiCalls purchaseOrderApiCalls = new PurchaseOrderApiCalls();
                purchaseOrderApiCalls.setModifyOrderPresenter(OrderDetailActivity.this);
                purchaseOrderApiCalls.modify(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
            }
        });

        btn_pay_now.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (isProductWithoutPrice) {
                new CustomToast().showToast(OrderDetailActivity.this, "Some product having 0 price. Please set price to them");
            } else {
                ShowCustomDialog showDialog = new ShowCustomDialog(OrderDetailActivity.this);
                showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                    @Override
                    public void btnPositiveClick() {
                        if (new NetworkUtil(OrderDetailActivity.this).isOnline()) {
                            showProgress();
                            setProgressMessage("Starting payment...");
                            setProgressCancel(false);
                            jsonPurchaseOrder.setPaymentMode(payment_modes_enum[sp_payment_mode.getSelectedItemPosition()]);
                            jsonPurchaseOrder.setPartialPayment(jsonPurchaseOrder.getOrderPrice());
                            PurchaseOrderApiCalls purchaseOrderApiCalls = new PurchaseOrderApiCalls();
                            purchaseOrderApiCalls.setPaymentProcessPresenter(OrderDetailActivity.this);
                            purchaseOrderApiCalls.setPurchaseOrderPresenter(OrderDetailActivity.this);
                            purchaseOrderApiCalls.counterPayment(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);

                        } else {
                            ShowAlertInformation.showNetworkDialog(OrderDetailActivity.this);
                        }
                    }

                    @Override
                    public void btnNegativeClick() {
                        //Do nothing
                    }
                });
                showDialog.displayDialog("Alert", "You are initiating payment process. Please confirm");
            }

        });
        currencySymbol = AppInitialize.getCurrencySymbol();
        tv_item_count.setText("Total Items: (" + jsonPurchaseOrder.getPurchaseOrderProducts().size() + ")");
        adapter = new OrderItemAdapter(this, jsonPurchaseOrder.getPurchaseOrderProducts(), currencySymbol, this);
        listview.setAdapter(adapter);
        updateUI();
        PermissionHelper permissionHelper = new PermissionHelper(this);
        ReceiptInfoApiCalls receiptInfoApiCalls = new ReceiptInfoApiCalls();
        receiptInfoApiCalls.setReceiptInfoPresenter(this);
        Button btn_print = findViewById(R.id.btn_print);
        btn_print.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (TextUtils.isEmpty(jsonPurchaseOrder.getTransactionId())) {
                ShowCustomDialog showDialog = new ShowCustomDialog(OrderDetailActivity.this, false);
                showDialog.displayDialog("Alert", "Transaction Id is empty. Receipt can't be generated");
            } else {
                if (permissionHelper.isStoragePermissionAllowed()) {
                    showProgress();
                    setProgressMessage("Fetching receipt info...");
                    setProgressCancel(false);
                    Receipt receipt = new Receipt();
                    receipt.setCodeQR(jsonPurchaseOrder.getCodeQR());
                    receipt.setQueueUserId(jsonPurchaseOrder.getQueueUserId());
                    receipt.setTransactionId(jsonPurchaseOrder.getTransactionId());
                    receiptInfoApiCalls.detail(AppInitialize.getDeviceID(),
                            AppInitialize.getEmail(),
                            AppInitialize.getAuth(), receipt);
                } else {
                    permissionHelper.requestStoragePermission();
                }
            }
        });
    }

    private void updateUI() {
        try {
            btn_refund.setVisibility(View.GONE);
            tv_customer_name.setText(jsonPurchaseOrder.getCustomerName());
            tv_token.setText("Token/Order No. " + jsonPurchaseOrder.getDisplayToken());
            tv_q_name.setText(jsonPurchaseOrder.getDisplayName());
            tv_notes.setText("Additional Notes: " + jsonPurchaseOrder.getAdditionalNote());
            cv_notes.setVisibility(TextUtils.isEmpty(jsonPurchaseOrder.getAdditionalNote()) ? View.GONE : View.VISIBLE);
            tv_address.setText(Html.fromHtml(StringUtils.isBlank(jsonPurchaseOrder.getUserAddressId()) ? "N/A" : jsonPurchaseOrder.getJsonUserAddress().getAddress()));
            tv_order_state.setText(null == jsonPurchaseOrder.getPresentOrderState() ? "N/A" : jsonPurchaseOrder.getPresentOrderState().getFriendlyDescription());
            tv_transaction_id.setText(null == jsonPurchaseOrder.getTransactionId() ? "N/A" : CommonHelper.transactionForDisplayOnly(jsonPurchaseOrder.getTransactionId()));
            tv_paid_amount_value.setText(currencySymbol + jsonPurchaseOrder.computePaidAmount());
            tv_remaining_amount_value.setText(currencySymbol + jsonPurchaseOrder.computeBalanceAmount());
            tv_tax.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getTax()));
            btn_remove_discount.setVisibility(View.GONE);
            btn_discount.setVisibility(View.GONE);
            if (PaymentStatusEnum.PP == jsonPurchaseOrder.getPaymentStatus() ||
                    PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus()) {
                if (isProductWithoutPrice) {
                    rl_payment.setVisibility(View.GONE);
                    btn_update_price.setVisibility(View.VISIBLE);
                } else {
                    rl_payment.setVisibility(View.VISIBLE);
                    btn_update_price.setVisibility(View.GONE);
                    if (PaymentStatusEnum.PP == jsonPurchaseOrder.getPaymentStatus()) {
                        if (TextUtils.isEmpty(jsonPurchaseOrder.getCouponId())) {
                            btn_remove_discount.setVisibility(View.GONE);
                            btn_discount.setVisibility(View.VISIBLE);
                        } else {
                            btn_remove_discount.setVisibility(View.VISIBLE);
                            btn_discount.setVisibility(View.GONE);
                        }
                    }
                }
                if (PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus()) {
                    btn_pay_partial.setVisibility(View.GONE);
                    edt_amount.setVisibility(View.GONE);
                    rl_multiple.setVisibility(View.VISIBLE);
                    tv_multiple_payment.setText(currencySymbol + Double.parseDouble(jsonPurchaseOrder.getPartialPayment()) / 100);
                } else {
                    rl_multiple.setVisibility(View.GONE);
                    tv_multiple_payment.setText("");
                }
            } else {
                rl_payment.setVisibility(View.GONE);
                btn_discount.setVisibility(View.GONE);
                btn_remove_discount.setVisibility(View.GONE);
            }

            if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()
                    || PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus()
                    || PaymentStatusEnum.PR == jsonPurchaseOrder.getPaymentStatus()) {
                if (null != jsonPurchaseOrder.getPaymentMode()) {
                    tv_payment_mode.setText(jsonPurchaseOrder.getPaymentMode().getDescription());
                }
                tv_payment_status.setText(jsonPurchaseOrder.getPaymentStatus().getDescription());
                if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.PO) {
                    btn_refund.setVisibility(View.VISIBLE);
                }
            } else {
                tv_payment_status.setText(jsonPurchaseOrder.getPaymentStatus().getDescription());
            }

            try {
                tv_cost.setText(currencySymbol + jsonPurchaseOrder.computeItemTotal());
                tv_grand_total_amt.setText(currencySymbol + jsonPurchaseOrder.computeBalanceAmount());
            } catch (Exception e) {
                e.printStackTrace();
                tv_cost.setText(currencySymbol + 0 / 100);
                tv_grand_total_amt.setText(currencySymbol + 0 / 100);
            }

            if (jsonPurchaseOrder.getStoreDiscount() > 0) {
                tv_coupon_discount_amt.setText(Constants.MINUS + currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getStoreDiscount()));
            } else {
                tv_coupon_discount_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getStoreDiscount()));
            }

            if (null == jsonPurchaseOrder.getTransactionVia()) {
                tv_transaction_via.setText("N/A");
            } else {
                tv_transaction_via.setText(jsonPurchaseOrder.getTransactionVia().getDescription());
            }

            if (PurchaseOrderStateEnum.CO == jsonPurchaseOrder.getPresentOrderState() && null == jsonPurchaseOrder.getPaymentMode()) {
                rl_payment.setVisibility(View.GONE);
                btn_update_price.setVisibility(View.GONE);
                tv_payment_mode.setText("N/A");
                adapter.setClickEnable(false);
            }
            tv_payment_msg.setVisibility(View.GONE);
            if (getIntent().getBooleanExtra(IBConstant.KEY_IS_PAYMENT_NOT_ALLOWED, false)) {
                rl_payment.setVisibility(View.GONE);
                btn_update_price.setVisibility(View.GONE);
                rl_multiple.setVisibility(View.GONE);
                btn_refund.setVisibility(View.GONE);
                tv_payment_msg.setVisibility(View.VISIBLE);
                if (getIntent().getBooleanExtra(IBConstant.KEY_IS_HISTORY, false)) {
                    tv_payment_msg.setVisibility(View.GONE);
                }
            }

            // Partial Payment Show only for HealthCare Services
            if (getIntent().getBooleanExtra(IBConstant.KEY_IS_PAYMENT_PARTIAL_ALLOWED, false)) {
                ll_partial.setVisibility(View.VISIBLE);
            } else {
                ll_partial.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProductPriceList(JsonPurchaseOrderProduct jpop, int pos) {
        jsonPurchaseOrder.getPurchaseOrderProducts().set(pos, jpop);
        checkProductWithZeroPrice();
        jsonPurchaseOrder.setOrderPrice(String.valueOf(calculateTotalPrice()));
        tv_cost.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice()));
        tv_paid_amount_value.setText(currencySymbol + jsonPurchaseOrder.computePaidAmount());
        tv_remaining_amount_value.setText(currencySymbol + jsonPurchaseOrder.computeBalanceAmount());
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
            for (JsonPurchaseOrderProduct jpop : jsonPurchaseOrder.getPurchaseOrderProducts()) {
                if (jpop.getProductPrice() == 0) {
                    isProductWithoutPrice = true;
                    break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
        if (null != updateWholeList) {
            //updateWholeList = null;
        }
    }

    @Override
    public void paymentProcessResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        dismissProgress();
        if (null != jsonPurchaseOrder) {
            if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus() || PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus()) {
                this.jsonPurchaseOrder = jsonPurchaseOrder;
                updateUI();
                new CustomToast().showToast(OrderDetailActivity.this, "Payment updated successfully");
                if (null != updateWholeList) {
                    updateWholeList.updateWholeList();
                }
            }
        }
    }

    @Override
    public void purchaseOrderListResponse(JsonPurchaseOrderList jsonPurchaseOrderList) {
        dismissProgress();
        if (null != jsonPurchaseOrderList) {
            Log.v("Order data:", jsonPurchaseOrderList.toString());
            finish();
            if (null != updateWholeList) {
                updateWholeList.updateWholeList();
            }
        }
    }

    @Override
    public void purchaseOrderResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        dismissProgress();
        // do nothing
    }

    @Override
    public void purchaseOrderError() {
        dismissProgress();
    }

    @Override
    public void modifyOrderResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        dismissProgress();
        if (null != jsonPurchaseOrder) {
            this.jsonPurchaseOrder = jsonPurchaseOrder;
            updateUI();
            Log.v("Modify order data:", jsonPurchaseOrder.toString());
            if (null != updateWholeList) {
                updateWholeList.updateWholeList();
            }
        }
    }

    @Override
    public void orderProcessedResponse(JsonPurchaseOrderList jsonPurchaseOrderList) {
        dismissProgress();
        if (null != jsonPurchaseOrderList && jsonPurchaseOrderList.getPurchaseOrders().size() > 0) {
            jsonPurchaseOrder = jsonPurchaseOrderList.getPurchaseOrders().get(0);
            if (PaymentStatusEnum.PR == jsonPurchaseOrder.getPaymentStatus()) {
                updateUI();
                new CustomToast().showToast(OrderDetailActivity.this, "Payment refund successfully");
                if (null != updateWholeList) {
                    updateWholeList.updateWholeList();
                }
            }
        }
    }

    @Override
    public void orderProcessedError() {
        dismissProgress();
    }

    @Override
    public void receiptInfoResponse(Receipt receipt) {
        try {
            Log.e("Data", receipt.toString());
            ReceiptGeneratorPDF receiptGeneratorPDF = new ReceiptGeneratorPDF(OrderDetailActivity.this);
            receiptGeneratorPDF.createPdf(receipt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dismissProgress();
    }

    @Override
    public void couponApplyResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        if (null != jsonPurchaseOrder) {
            this.jsonPurchaseOrder = jsonPurchaseOrder;
            updateUI();
            new CustomToast().showToast(OrderDetailActivity.this, "Coupon applied successfully");
            if (null != updateWholeList) {
                updateWholeList.updateWholeList();
            }
            Log.e("Resp: JsonPurchaseOrder", jsonPurchaseOrder.toString());
        } else {
            new CustomToast().showToast(this, "JsonPurchaseOrder is NULL");
        }

        dismissProgress();

    }

    @Override
    public void couponRemoveResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        if (null != jsonPurchaseOrder) {
            this.jsonPurchaseOrder = jsonPurchaseOrder;
            updateUI();
            new CustomToast().showToast(OrderDetailActivity.this, "Coupon removed successfully");
            if (null != updateWholeList) {
                updateWholeList.updateWholeList();
            }
            Log.e("Resp: JsonPurchaseOrder", jsonPurchaseOrder.toString());
        } else {
            new CustomToast().showToast(this, "JsonPurchaseOrder is NULL");
        }

        dismissProgress();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ACTIVITY_RESULT_BACK) {
            if (resultCode == RESULT_OK) {
                JsonCoupon jsonCoupon = (JsonCoupon) data.getSerializableExtra(IBConstant.KEY_OBJECT);
                Log.e("Data received", jsonCoupon.toString());
                if (jsonCoupon.getDiscountType() == DiscountTypeEnum.F) {
                    tv_discount_value.setText(currencySymbol + jsonCoupon.getDiscountAmount());
                } else {
                    tv_discount_value.setText(jsonCoupon.getDiscountAmount() + "% discount");
                }

                if (new NetworkUtil(this).isOnline()) {
                    showProgress();
                    setProgressMessage("Applying discount..");
                    // progressDialog.setCancelable(false);
                    // progressDialog.setCanceledOnTouchOutside(false);
                    CouponApiCalls couponApiCalls = new CouponApiCalls();
                    couponApiCalls.setCouponApplyRemovePresenter(this);

                    CouponOnOrder couponOnOrder = new CouponOnOrder()
                            .setQueueUserId(jsonPurchaseOrder.getQueueUserId())
                            .setCouponId(jsonCoupon.getCouponId())
                            .setCodeQR(jsonPurchaseOrder.getCodeQR())
                            .setTransactionId(jsonPurchaseOrder.getTransactionId());

                    couponApiCalls.apply(AppInitialize.getDeviceID(),
                            AppInitialize.getEmail(),
                            AppInitialize.getAuth(),
                            couponOnOrder);
                } else {
                    ShowAlertInformation.showNetworkDialog(OrderDetailActivity.this);
                }
            }
        }
    }
}
