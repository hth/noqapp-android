package com.noqapp.android.merchant.views.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.PaymentPermissionEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonPaymentPermission;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.views.adapters.OrderItemAdapter;
import com.noqapp.android.merchant.views.adapters.PeopleInQOrderAdapter;

import org.apache.commons.lang3.StringUtils;

public class OrderDetailActFloating extends BaseActivity {
    protected ImageView actionbarBack;
    private JsonPurchaseOrder jsonPurchaseOrder;
    private boolean isProductWithoutPrice = false;
    private TextView tv_cost;
    private String currencySymbol;
    private TextView tv_paid_amount_value, tv_remaining_amount_value, tv_notes;

    private CardView cv_notes;
    private TextView tv_payment_mode, tv_payment_status, tv_address, tv_multiple_payment, tv_transaction_via, tv_order_state, tv_transaction_id;
    private RelativeLayout rl_multiple;
    private TextView tv_token, tv_q_name, tv_customer_name;
    private OrderItemAdapter adapter;
    private TextView tv_payment_msg;
    private TextView tv_grand_total_amt;
    private TextView tv_coupon_discount_amt;
    private TextView tv_discount_value;
    private CardView cv_footer;
    TextView tv_order_data;
    TextView tv_order_prepared;
    TextView tv_order_done;
    TextView tv_order_cancel;
    TextView tv_order_accept;
    TextView tv_order_status;
    private Context mContext;
    private JsonPaymentPermission jsonPaymentPermission;
    public static PeopleInQOrderAdapter.PeopleInQOrderAdapterClick peopleInQOrderAdapterClick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_order_float);
        this.setFinishOnTouchOutside(false);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = findViewById(R.id.actionbarBack);
        jsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getSerializableExtra("jsonPurchaseOrder");
        jsonPaymentPermission = (JsonPaymentPermission) getIntent().getSerializableExtra("jsonPaymentPermission");
        actionbarBack.setOnClickListener(v -> {
            //onBackPressed();
            // updateWholeList = null;
            finish();
        });
        mContext = this;
        tv_toolbar_title.setText(getString(R.string.order_details));
        tv_order_data = findViewById(R.id.tv_order_data);
        tv_order_prepared = findViewById(R.id.tv_order_prepared);
        tv_order_done = findViewById(R.id.tv_order_done);
        tv_order_cancel = findViewById(R.id.tv_order_cancel);
        tv_order_accept = findViewById(R.id.tv_order_accept);
        tv_order_status = findViewById(R.id.tv_order_status);

        tv_token = findViewById(R.id.tv_token);
        cv_footer = findViewById(R.id.cv_footer);
        tv_q_name = findViewById(R.id.tv_q_name);
        tv_customer_name = findViewById(R.id.tv_customer_name);
        tv_payment_msg = findViewById(R.id.tv_payment_msg);
        ListView listview = findViewById(R.id.listview);
        TextView tv_item_count = findViewById(R.id.tv_item_count);
        tv_payment_mode = findViewById(R.id.tv_payment_mode);
        tv_payment_status = findViewById(R.id.tv_payment_status);
        tv_address = findViewById(R.id.tv_address);
        tv_cost = findViewById(R.id.tv_cost);
        tv_multiple_payment = findViewById(R.id.tv_multiple_payment);
        tv_transaction_via = findViewById(R.id.tv_transaction_via);
        tv_order_state = findViewById(R.id.tv_order_state);
        tv_transaction_id = findViewById(R.id.tv_transaction_id);
        rl_multiple = findViewById(R.id.rl_multiple);
        tv_coupon_discount_amt = findViewById(R.id.tv_coupon_discount_amt);
        tv_grand_total_amt = findViewById(R.id.tv_grand_total_amt);
        tv_discount_value = findViewById(R.id.tv_discount_value);
        cv_footer.setVisibility(View.GONE);


        tv_notes = findViewById(R.id.tv_notes);
        tv_remaining_amount_value = findViewById(R.id.tv_remaining_amount_value);
        tv_paid_amount_value = findViewById(R.id.tv_paid_amount_value);
        cv_notes = findViewById(R.id.cv_notes);


        currencySymbol = BaseLaunchActivity.getCurrencySymbol();
        tv_item_count.setText("Total Items: (" + jsonPurchaseOrder.getPurchaseOrderProducts().size() + ")");
        adapter = new OrderItemAdapter(this, jsonPurchaseOrder.getPurchaseOrderProducts(), currencySymbol, null);
        listview.setAdapter(adapter);
        updateUI(getIntent().getIntExtra("position", -1));


    }

    private void updateUI(int position) {
        try {
            tv_customer_name.setText(jsonPurchaseOrder.getCustomerName());
            tv_token.setText("Token/Order No. " + jsonPurchaseOrder.getToken());
            tv_q_name.setText(jsonPurchaseOrder.getDisplayName());
            tv_notes.setText("Additional Notes: " + jsonPurchaseOrder.getAdditionalNote());
            cv_notes.setVisibility(TextUtils.isEmpty(jsonPurchaseOrder.getAdditionalNote()) ? View.GONE : View.VISIBLE);
            tv_address.setText(Html.fromHtml(StringUtils.isBlank(jsonPurchaseOrder.getDeliveryAddress()) ? "N/A" : jsonPurchaseOrder.getDeliveryAddress()));
            tv_order_state.setText(null == jsonPurchaseOrder.getPresentOrderState() ? "N/A" : jsonPurchaseOrder.getPresentOrderState().getFriendlyDescription());
            tv_transaction_id.setText(null == jsonPurchaseOrder.getTransactionId() ? "N/A" : CommonHelper.transactionForDisplayOnly(jsonPurchaseOrder.getTransactionId()));
            tv_paid_amount_value.setText(currencySymbol + " " + jsonPurchaseOrder.computePaidAmount());
            tv_remaining_amount_value.setText(currencySymbol + " " + jsonPurchaseOrder.computeBalanceAmount());
            if (PaymentStatusEnum.PP == jsonPurchaseOrder.getPaymentStatus() ||
                    PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus()) {
                if (PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus()) {
                    rl_multiple.setVisibility(View.VISIBLE);
                    tv_multiple_payment.setText(currencySymbol + " " + String.valueOf(Double.parseDouble(jsonPurchaseOrder.getPartialPayment()) / 100));
                } else {
                    rl_multiple.setVisibility(View.GONE);
                    tv_multiple_payment.setText("");
                }
            } else {
                // rl_payment.setVisibility(View.GONE);
            }
            if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()
                    || PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus()
                    || PaymentStatusEnum.PR == jsonPurchaseOrder.getPaymentStatus()) {
                if (null != jsonPurchaseOrder.getPaymentMode()) {
                    tv_payment_mode.setText(jsonPurchaseOrder.getPaymentMode().getDescription());
                }
                tv_payment_status.setText(jsonPurchaseOrder.getPaymentStatus().getDescription());
            } else {
                tv_payment_status.setText(jsonPurchaseOrder.getPaymentStatus().getDescription());
            }
            try {
                tv_cost.setText(currencySymbol + " " + jsonPurchaseOrder.computeFinalAmountWithDiscount());
                tv_grand_total_amt.setText(currencySymbol + " " + CommonHelper.displayPrice((jsonPurchaseOrder.getOrderPrice())));
            } catch (Exception e) {
                e.printStackTrace();
                tv_cost.setText(currencySymbol + " " + String.valueOf(0 / 100));
                tv_grand_total_amt.setText(currencySymbol + " " + String.valueOf(0 / 100));
            }
            tv_coupon_discount_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getStoreDiscount()));

            if (null == jsonPurchaseOrder.getTransactionVia()) {
                tv_transaction_via.setText("N/A");
            } else {
                tv_transaction_via.setText(jsonPurchaseOrder.getTransactionVia().getDescription());
            }

            if (PurchaseOrderStateEnum.CO == jsonPurchaseOrder.getPresentOrderState() && null == jsonPurchaseOrder.getPaymentMode()) {
                tv_payment_mode.setText("N/A");
                adapter.setClickEnable(false);
            }
            tv_payment_msg.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(jsonPurchaseOrder.getTransactionId())) {
                tv_order_data.setVisibility(View.VISIBLE);
                switch (jsonPurchaseOrder.getPaymentStatus()) {
                    case PA:
                        tv_order_data.setText("Paid");
                        tv_order_data.setBackgroundResource(R.drawable.bg_nogradient_round);
                        if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.CO) {
                            tv_order_data.setBackgroundResource(R.drawable.grey_background);
                            tv_order_data.setText("Refund Due");
                        }
                        break;
                    case PR:
                        tv_order_data.setText("Payment Refunded");
                        tv_order_data.setBackgroundResource(R.drawable.grey_background);
                        break;
                    case PC:
                        tv_order_data.setText("Payment Cancelled");
                        tv_order_data.setBackgroundResource(R.drawable.grey_background);
                        break;
                    case PP:
                        if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.CO) {
                            tv_order_data.setText("No Payment Due");
                            tv_order_data.setBackgroundResource(R.drawable.grey_background);
                        } else {
                            tv_order_data.setText("Accept Payment");
                            switch (jsonPurchaseOrder.getPresentOrderState()) {
                                case PO:
                                case VB:
                                case NM:
                                case OP:
                                case PR:
                                case RP:
                                case RD:
                                case OW:
                                case LO:
                                case FD:
                                case DA:
                                case OD:
                                    tv_order_data.setBackgroundResource(R.drawable.bg_unpaid);
                                    break;
                                default:
                                    tv_order_data.setBackgroundResource(R.drawable.grey_background);
                            }
                        }
                        break;
                    default:
                        tv_order_data.setText("Accept Payment");
                        switch (jsonPurchaseOrder.getPresentOrderState()) {
                            case PO:
                            case VB:
                            case NM:
                            case OP:
                            case PR:
                            case RP:
                            case RD:
                            case OW:
                            case LO:
                            case FD:
                            case DA:
                            case OD:
                                tv_order_data.setBackgroundResource(R.drawable.bg_unpaid);
                                break;
                            default:
                                tv_order_data.setBackgroundResource(R.drawable.grey_background);
                        }
                        break;
                }
            } else {
                tv_order_data.setVisibility(View.GONE);
            }
            tv_order_data.setOnClickListener(v -> {
                if (PaymentPermissionEnum.A == jsonPaymentPermission.getPaymentPermissions().get(LaunchActivity.getLaunchActivity().getUserLevel().name())) {
                    if (null != peopleInQOrderAdapterClick)
                        peopleInQOrderAdapterClick.viewOrderClick(jsonPurchaseOrder, false);
                } else {
                    new CustomToast().showToast(mContext, mContext.getString(R.string.payment_not_allowed));
                    if (null != peopleInQOrderAdapterClick)
                        peopleInQOrderAdapterClick.viewOrderClick(jsonPurchaseOrder, true);
                }
            });

            if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.RP ||
                    jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.RD) {
                tv_order_done.setVisibility(View.VISIBLE);
            } else {
                tv_order_done.setVisibility(View.GONE);
            }

            if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.PO) {
                tv_order_cancel.setVisibility(View.VISIBLE);
                tv_order_accept.setVisibility(View.VISIBLE);
            } else {
                if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.VB) {
                    tv_order_cancel.setVisibility(View.VISIBLE);
                } else {
                    tv_order_cancel.setVisibility(View.GONE);
                }
                tv_order_accept.setVisibility(View.GONE);
            }
            tv_order_done.setOnClickListener(v -> {
                if (null != peopleInQOrderAdapterClick && position > -1)
                    peopleInQOrderAdapterClick.orderDoneClick(position);
            });
            tv_order_cancel.setOnClickListener(v -> {
                ShowCustomDialog showDialog = new ShowCustomDialog(mContext);
                showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                    @Override
                    public void btnPositiveClick() {
                        if (null != peopleInQOrderAdapterClick && position > -1)
                            peopleInQOrderAdapterClick.orderCancelClick(position);
                    }

                    @Override
                    public void btnNegativeClick() {
                        //Do nothing
                    }
                });
                showDialog.displayDialog("Cancel Order", "Do you want to cancel the order?");
            });
            switch (jsonPurchaseOrder.getBusinessType()) {
                case HS:
                    tv_order_done.setText("Service Completed");
                    tv_order_cancel.setText("Cancel Service");
                    tv_order_prepared.setText("Start Service");
                    break;
                default:
                    tv_order_done.setText("Order Done");
                    tv_order_cancel.setText("Cancel Order");
                    tv_order_prepared.setText("Order prepared");
            }
            tv_order_status.setText("Status: " + jsonPurchaseOrder.getPresentOrderState().getFriendlyDescription());
            if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.OP) {
                tv_order_prepared.setVisibility(View.VISIBLE);
            } else {
                tv_order_prepared.setVisibility(View.GONE);
            }
            tv_order_prepared.setOnClickListener(v -> {
                if (null != peopleInQOrderAdapterClick && position > -1)
                    peopleInQOrderAdapterClick.orderDoneClick(position);
            });
            tv_order_accept.setOnClickListener(v -> {
                if (null != peopleInQOrderAdapterClick && position > -1)
                    peopleInQOrderAdapterClick.orderAcceptClick(position);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
