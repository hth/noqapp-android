package com.noqapp.android.merchant.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.PaymentPermissionEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonPaymentPermission;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.DocumentUploadActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.OrderDetailActFloating;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PeopleInQOrderAdapter extends RecyclerView.Adapter {

    private final Context context;
    private List<JsonPurchaseOrder> dataSet;
    protected String qCodeQR = "";
    private int glowPosition = -1;
    private JsonPaymentPermission jsonPaymentPermission;
    public PeopleInQOrderAdapterClick peopleInQOrderAdapterClick;
    private AlertDialog mAlertDialog;

    private TextView tv_order_data, tv_order_prepared, tv_order_done, tv_order_cancel;
    private TextView tv_order_accept, tv_token, tv_q_name, tv_customer_name, tv_payment_msg;
    private TextView tv_item_count, tv_payment_mode, tv_payment_status, tv_address, tv_cost, tv_multiple_payment;
    private TextView tv_transaction_via, tv_order_state, tv_transaction_id, tv_coupon_discount_amt, tv_grand_total_amt;
    private TextView tv_discount_value, tv_order_status, tv_notes, tv_remaining_amount_value, tv_paid_amount_value;
    private RelativeLayout rl_multiple;
    private ListView listview;
    private CardView cv_notes;

    public interface PeopleInQOrderAdapterClick {
        void orderAcceptClick(int position);

        void orderDoneClick(int position);

        void orderCancelClick(int position);

        void viewOrderClick(JsonPurchaseOrder jsonPurchaseOrder, boolean isPaymentNotAllowed);
    }

    public PeopleInQOrderAdapter(List<JsonPurchaseOrder> data, Context context, String qCodeQR,
                                 PeopleInQOrderAdapterClick peopleInQOrderAdapterClick,
                                 JsonPaymentPermission jsonPaymentPermission) {
        this.dataSet = data;
        this.context = context;
        this.qCodeQR = qCodeQR;
        this.peopleInQOrderAdapterClick = peopleInQOrderAdapterClick;
        this.jsonPaymentPermission = jsonPaymentPermission;
    }

    public PeopleInQOrderAdapter(List<JsonPurchaseOrder> data, Context context, String qCodeQR,
                                 PeopleInQOrderAdapterClick peopleInQOrderAdapterClick,
                                 int glowPosition, JsonPaymentPermission jsonPaymentPermission) {
        this.dataSet = data;
        this.context = context;
        this.qCodeQR = qCodeQR;
        this.peopleInQOrderAdapterClick = peopleInQOrderAdapterClick;
        this.glowPosition = glowPosition;
        this.jsonPaymentPermission = jsonPaymentPermission;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_people_order_queue_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder recordHolder = (MyViewHolder) viewHolder;
        final JsonPurchaseOrder jsonPurchaseOrder = dataSet.get(position);
        final String phoneNo = jsonPurchaseOrder.getCustomerPhone();
        recordHolder.tv_join_timing.setText(Formatter.getTime(jsonPurchaseOrder.getCreated()));
        recordHolder.tv_sequence_number.setText(String.valueOf(jsonPurchaseOrder.getToken()));
        recordHolder.tv_customer_name.setText(TextUtils.isEmpty(jsonPurchaseOrder.getCustomerName()) ? context.getString(R.string.unregister_user) : jsonPurchaseOrder.getCustomerName());
        // recordHolder.iv_new.setVisibility(jsonPurchaseOrder.isClientVisitedThisStore() ? View.INVISIBLE : View.VISIBLE);
        // if (jsonQueuedPerson.isClientVisitedThisBusiness()) {
        recordHolder.rl_sequence_new_time.setBackgroundColor(Color.parseColor("#e07e3d"));
        recordHolder.tv_sequence_number.setTextColor(Color.WHITE);
        recordHolder.tv_join_timing.setTextColor(Color.WHITE);
//        } else {
//            recordHolder.rl_sequence_new_time.setBackgroundColor(Color.parseColor("#aaaaaa"));
//            recordHolder.tv_sequence_number.setTextColor(Color.WHITE);
//            recordHolder.tv_join_timing.setTextColor(Color.WHITE);
//        }


        if (null != LaunchActivity.getLaunchActivity() && null != LaunchActivity.getLaunchActivity().getUserProfile()) {
            recordHolder.tv_customer_mobile.setText(TextUtils.isEmpty(phoneNo) ? context.getString(R.string.unregister_user) :
                    PhoneFormatterUtil.formatNumber(LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName(), phoneNo));
        }

        if (!TextUtils.isEmpty(jsonPurchaseOrder.getTransactionId())) {
            recordHolder.tv_order_data.setVisibility(View.VISIBLE);
            switch (jsonPurchaseOrder.getPaymentStatus()) {
                case PA:
                    recordHolder.tv_order_data.setText("Paid");
                    recordHolder.tv_order_data.setBackgroundResource(R.drawable.bg_nogradient_round);
                    if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.CO) {
                        recordHolder.tv_order_data.setBackgroundResource(R.drawable.grey_background);
                        recordHolder.tv_order_data.setText("Refund Due");
                    }
                    break;
                case PR:
                    recordHolder.tv_order_data.setText("Payment Refunded");
                    recordHolder.tv_order_data.setBackgroundResource(R.drawable.grey_background);
                    break;
                case PC:
                    recordHolder.tv_order_data.setText("Payment Cancelled");
                    recordHolder.tv_order_data.setBackgroundResource(R.drawable.grey_background);
                    break;
                case PP:
                    if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.CO) {
                        recordHolder.tv_order_data.setText("No Payment Due");
                        recordHolder.tv_order_data.setBackgroundResource(R.drawable.grey_background);
                    } else {
                        recordHolder.tv_order_data.setText("Accept Payment");
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
                                recordHolder.tv_order_data.setBackgroundResource(R.drawable.bg_unpaid);
                                break;
                            default:
                                recordHolder.tv_order_data.setBackgroundResource(R.drawable.grey_background);
                        }
                    }
                    break;
                default:
                    recordHolder.tv_order_data.setText("Accept Payment");
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
                            recordHolder.tv_order_data.setBackgroundResource(R.drawable.bg_unpaid);
                            break;
                        default:
                            recordHolder.tv_order_data.setBackgroundResource(R.drawable.grey_background);
                    }
                    break;
            }
        } else {
            recordHolder.tv_order_data.setVisibility(View.GONE);
        }
        recordHolder.tv_order_data.setOnClickListener(v -> {
            if (PaymentPermissionEnum.A == jsonPaymentPermission.getPaymentPermissions().get(LaunchActivity.getLaunchActivity().getUserLevel().name())) {
                peopleInQOrderAdapterClick.viewOrderClick(jsonPurchaseOrder, false);
            } else {
                new CustomToast().showToast(context, context.getString(R.string.payment_not_allowed));
                peopleInQOrderAdapterClick.viewOrderClick(jsonPurchaseOrder, true);
            }
        });

        if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.RP ||
                jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.RD) {
            recordHolder.tv_order_done.setVisibility(View.VISIBLE);
        } else {
            recordHolder.tv_order_done.setVisibility(View.GONE);
        }

        if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.PO) {
            recordHolder.tv_order_cancel.setVisibility(View.VISIBLE);
            recordHolder.tv_order_accept.setVisibility(View.VISIBLE);
        } else {
            if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.VB) {
                recordHolder.tv_order_cancel.setVisibility(View.VISIBLE);
            } else {
                recordHolder.tv_order_cancel.setVisibility(View.GONE);
            }
            recordHolder.tv_order_accept.setVisibility(View.GONE);
        }

        recordHolder.tv_order_done.setOnClickListener(v -> peopleInQOrderAdapterClick.orderDoneClick(position));
        recordHolder.tv_upload_document.setOnClickListener(v -> {
            Intent intent = new Intent(context, DocumentUploadActivity.class);
            intent.putExtra("transactionId", jsonPurchaseOrder.getTransactionId());
            intent.putExtra("qCodeQR", qCodeQR);
            context.startActivity(intent);
        });
        recordHolder.tv_order_cancel.setOnClickListener(v -> {
            ShowCustomDialog showDialog = new ShowCustomDialog(context);
            showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                @Override
                public void btnPositiveClick() {
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
                recordHolder.tv_order_done.setText("Service Completed");
                recordHolder.tv_order_cancel.setText("Cancel Service");
                recordHolder.tv_order_prepared.setText("Start Service");
                recordHolder.tv_upload_document.setVisibility(View.GONE); // Not needed now
                break;
            default:
                recordHolder.tv_order_done.setText("Order Done");
                recordHolder.tv_order_cancel.setText("Cancel Order");
                recordHolder.tv_order_prepared.setText("Order prepared");
                recordHolder.tv_upload_document.setVisibility(View.GONE);
        }
        recordHolder.tv_order_status.setText("Status: " + jsonPurchaseOrder.getPresentOrderState().getFriendlyDescription());
        if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.OP) {
            recordHolder.tv_order_prepared.setVisibility(View.VISIBLE);
        } else {
            recordHolder.tv_order_prepared.setVisibility(View.GONE);
        }
        recordHolder.tv_order_prepared.setOnClickListener(v -> peopleInQOrderAdapterClick.orderDoneClick(position));
        recordHolder.tv_customer_mobile.setOnClickListener(v -> {
            if (!recordHolder.tv_customer_mobile.getText().equals(context.getString(R.string.unregister_user)))
                AppUtils.makeCall(LaunchActivity.getLaunchActivity(), phoneNo);
        });

        recordHolder.tv_order_accept.setOnClickListener(v -> peopleInQOrderAdapterClick.orderAcceptClick(position));

        if (glowPosition > 0 && glowPosition - 1 == position && jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.OP) {
            recordHolder.ll_side.setBackground(ContextCompat.getDrawable(context, R.drawable.cv_border_color));
            OrderDetailActFloating.peopleInQOrderAdapterClick = peopleInQOrderAdapterClick;
            Intent in = new Intent(context, OrderDetailActFloating.class);
            in.putExtra("jsonPurchaseOrder", jsonPurchaseOrder);
            in.putExtra("jsonPaymentPermission", jsonPaymentPermission);
            in.putExtra("position", position);
            in.putExtra(IBConstant.KEY_IS_PAYMENT_PARTIAL_ALLOWED, jsonPurchaseOrder.getBusinessType() == BusinessTypeEnum.HS);
            ((Activity) context).startActivity(in);

//            if (null == mAlertDialog)
//                showLargeDialog(context, jsonPurchaseOrder, position);
//            else if(mAlertDialog.isShowing()){
//                updateLargeDialog(context, jsonPurchaseOrder, position);
//            }
        } else {
            recordHolder.ll_side.setBackground(null);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_customer_name;
        TextView tv_customer_mobile;
        TextView tv_sequence_number;
        TextView tv_order_data;
        TextView tv_order_status;
        TextView tv_order_prepared;
        TextView tv_order_done;
        TextView tv_order_cancel;
        TextView tv_order_accept;
        TextView tv_upload_document;
        TextView tv_join_timing;
        RelativeLayout rl_sequence_new_time;
        LinearLayout ll_side;
        ImageView iv_new;
        CardView cardview;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_customer_name = itemView.findViewById(R.id.tv_customer_name);
            this.tv_customer_mobile = itemView.findViewById(R.id.tv_customer_mobile);
            this.tv_sequence_number = itemView.findViewById(R.id.tv_sequence_number);
            this.tv_order_data = itemView.findViewById(R.id.tv_order_data);
            this.tv_order_status = itemView.findViewById(R.id.tv_order_status);
            this.tv_order_prepared = itemView.findViewById(R.id.tv_order_prepared);
            this.tv_order_done = itemView.findViewById(R.id.tv_order_done);
            this.tv_order_cancel = itemView.findViewById(R.id.tv_order_cancel);
            this.tv_order_accept = itemView.findViewById(R.id.tv_order_accept);
            this.tv_upload_document = itemView.findViewById(R.id.tv_upload_document);
            this.rl_sequence_new_time = itemView.findViewById(R.id.rl_sequence_new_time);
            this.tv_join_timing = itemView.findViewById(R.id.tv_join_timing);
            this.ll_side = itemView.findViewById(R.id.ll_side);
            this.iv_new = itemView.findViewById(R.id.iv_new);
            this.cardview = itemView.findViewById(R.id.cardview);
        }
    }


    private void showLargeDialog(Context mContext, JsonPurchaseOrder jsonPurchaseOrder, int position) {
        //  public static UpdateWholeList updateWholeList;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View dialogView = inflater.inflate(R.layout.dialog_order_float, null, false);
        ImageView actionbarBack = dialogView.findViewById(R.id.actionbarBack);
        tv_order_data = dialogView.findViewById(R.id.tv_order_data);
        tv_order_prepared = dialogView.findViewById(R.id.tv_order_prepared);
        tv_order_done = dialogView.findViewById(R.id.tv_order_done);
        tv_order_cancel = dialogView.findViewById(R.id.tv_order_cancel);
        tv_order_accept = dialogView.findViewById(R.id.tv_order_accept);
        tv_token = dialogView.findViewById(R.id.tv_token);
        tv_q_name = dialogView.findViewById(R.id.tv_q_name);
        tv_customer_name = dialogView.findViewById(R.id.tv_customer_name);
        tv_payment_msg = dialogView.findViewById(R.id.tv_payment_msg);
        listview = dialogView.findViewById(R.id.listview);
        tv_item_count = dialogView.findViewById(R.id.tv_item_count);
        tv_payment_mode = dialogView.findViewById(R.id.tv_payment_mode);
        tv_payment_status = dialogView.findViewById(R.id.tv_payment_status);
        tv_address = dialogView.findViewById(R.id.tv_address);
        tv_cost = dialogView.findViewById(R.id.tv_cost);
        tv_multiple_payment = dialogView.findViewById(R.id.tv_multiple_payment);
        tv_transaction_via = dialogView.findViewById(R.id.tv_transaction_via);
        tv_order_state = dialogView.findViewById(R.id.tv_order_state);
        tv_transaction_id = dialogView.findViewById(R.id.tv_transaction_id);
        rl_multiple = dialogView.findViewById(R.id.rl_multiple);
        tv_coupon_discount_amt = dialogView.findViewById(R.id.tv_coupon_discount_amt);
        tv_grand_total_amt = dialogView.findViewById(R.id.tv_grand_total_amt);
        tv_discount_value = dialogView.findViewById(R.id.tv_discount_value);
        tv_order_status = dialogView.findViewById(R.id.tv_order_status);
        tv_notes = dialogView.findViewById(R.id.tv_notes);
        tv_remaining_amount_value = dialogView.findViewById(R.id.tv_remaining_amount_value);
        tv_paid_amount_value = dialogView.findViewById(R.id.tv_paid_amount_value);
        cv_notes = dialogView.findViewById(R.id.cv_notes);
        String currencySymbol = BaseLaunchActivity.getCurrencySymbol();
        builder.setView(dialogView);
        mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);

        try {
            updateLargeDialog(mContext, jsonPurchaseOrder, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        actionbarBack.setOnClickListener(v -> {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        });
        mAlertDialog.show();
    }


    private void updateLargeDialog(Context mContext, JsonPurchaseOrder jsonPurchaseOrder, int position) {
        String currencySymbol = BaseLaunchActivity.getCurrencySymbol();
        tv_item_count.setText("Total Items: (" + jsonPurchaseOrder.getPurchaseOrderProducts().size() + ")");
        OrderItemAdapter adapter = new OrderItemAdapter(mContext, jsonPurchaseOrder.getPurchaseOrderProducts(), currencySymbol, null);
        listview.setAdapter(adapter);
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
                    peopleInQOrderAdapterClick.viewOrderClick(jsonPurchaseOrder, false);
                } else {
                    new CustomToast().showToast(context, context.getString(R.string.payment_not_allowed));
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
            tv_order_done.setOnClickListener(v -> peopleInQOrderAdapterClick.orderDoneClick(position));
            tv_order_cancel.setOnClickListener(v -> {
                ShowCustomDialog showDialog = new ShowCustomDialog(context);
                showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                    @Override
                    public void btnPositiveClick() {
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
            tv_order_prepared.setOnClickListener(v -> peopleInQOrderAdapterClick.orderDoneClick(position));
            tv_order_accept.setOnClickListener(v -> peopleInQOrderAdapterClick.orderAcceptClick(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
