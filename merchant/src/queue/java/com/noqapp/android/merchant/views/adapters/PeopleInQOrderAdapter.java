package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.PaymentPermissionEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonPaymentPermission;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.views.activities.DocumentUploadActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class PeopleInQOrderAdapter extends RecyclerView.Adapter<PeopleInQOrderAdapter.MyViewHolder> {

    private final Context context;
    private List<JsonPurchaseOrder> dataSet;
    protected String qCodeQR = "";
    private int glowPosition = -1;
    private JsonPaymentPermission jsonPaymentPermission;
    protected PeopleInQOrderAdapterClick peopleInQOrderAdapterClick;

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
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_people_order_queue_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder recordHolder, final int position) {
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


        if (null != LaunchActivity.getLaunchActivity()) {
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
        recordHolder.tv_order_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PaymentPermissionEnum.A == jsonPaymentPermission.getPaymentPermissions().get(LaunchActivity.getLaunchActivity().getUserLevel().name())) {
                    peopleInQOrderAdapterClick.viewOrderClick(jsonPurchaseOrder,false);
                } else {
                    new CustomToast().showToast(context, context.getString(R.string.payment_not_allowed));
                    peopleInQOrderAdapterClick.viewOrderClick(jsonPurchaseOrder,true);
                }
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

        recordHolder.tv_order_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peopleInQOrderAdapterClick.orderDoneClick(position);
            }
        });
        recordHolder.tv_upload_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DocumentUploadActivity.class);
                intent.putExtra("transactionId", jsonPurchaseOrder.getTransactionId());
                intent.putExtra("qCodeQR", qCodeQR);
                context.startActivity(intent);
            }
        });
        recordHolder.tv_order_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
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
        recordHolder.tv_order_prepared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peopleInQOrderAdapterClick.orderDoneClick(position);
            }
        });
        recordHolder.tv_customer_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recordHolder.tv_customer_mobile.getText().equals(context.getString(R.string.unregister_user)))
                    new AppUtils().makeCall(LaunchActivity.getLaunchActivity(), phoneNo);
            }
        });

        recordHolder.tv_order_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peopleInQOrderAdapterClick.orderAcceptClick(position);
            }
        });

        if (glowPosition > 0 && glowPosition - 1 == position && jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.OP) {
            Animation startAnimation = AnimationUtils.loadAnimation(context, R.anim.show_anim);
            recordHolder.cardview.startAnimation(startAnimation);
            Log.v("Animation true: ", String.valueOf(position));
        } else {
            Animation removeAnimation = AnimationUtils.loadAnimation(context, R.anim.remove_anim);
            recordHolder.cardview.startAnimation(removeAnimation);
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
            this.iv_new = itemView.findViewById(R.id.iv_new);
            this.cardview = itemView.findViewById(R.id.cardview);
        }
    }
}
