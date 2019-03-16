package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.activities.DocumentUploadActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.OrderDetailActivity;
import com.noqapp.android.merchant.views.activities.OrderDetailDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PeopleInQOrderAdapter extends RecyclerView.Adapter<PeopleInQOrderAdapter.MyViewHolder> {

    private final Context context;
    private List<JsonPurchaseOrder> dataSet;
    protected String qCodeQR = "";
    private int glowPosition = -1;


    public interface PeopleInQOrderAdapterClick {

        void orderAcceptClick(int position);

        void orderDoneClick(int position);

        void orderCancelClick(int position);

    }

    protected PeopleInQOrderAdapterClick peopleInQOrderAdapterClick;

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
            this.cardview = itemView.findViewById(R.id.cardview);
        }
    }


    public PeopleInQOrderAdapter(List<JsonPurchaseOrder> data, Context context, String qCodeQR, PeopleInQOrderAdapterClick peopleInQOrderAdapterClick) {
        this.dataSet = data;
        this.context = context;
        this.qCodeQR = qCodeQR;
        this.peopleInQOrderAdapterClick = peopleInQOrderAdapterClick;
    }

    public PeopleInQOrderAdapter(List<JsonPurchaseOrder> data, Context context, String qCodeQR, PeopleInQOrderAdapterClick peopleInQOrderAdapterClick, int glowPosition) {
        this.dataSet = data;
        this.context = context;
        this.qCodeQR = qCodeQR;
        this.peopleInQOrderAdapterClick = peopleInQOrderAdapterClick;
        this.glowPosition = glowPosition;
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

        recordHolder.tv_sequence_number.setText(String.valueOf(jsonPurchaseOrder.getToken()));
        recordHolder.tv_customer_name.setText(TextUtils.isEmpty(jsonPurchaseOrder.getCustomerName()) ? context.getString(R.string.unregister_user) : jsonPurchaseOrder.getCustomerName());
        if (null != LaunchActivity.getLaunchActivity()) {
            recordHolder.tv_customer_mobile.setText(TextUtils.isEmpty(phoneNo) ? context.getString(R.string.unregister_user) :
                    PhoneFormatterUtil.formatNumber(LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName(), phoneNo));
        }
        recordHolder.tv_order_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showOrderDetailDialog(context, jsonPurchaseOrder, jsonPurchaseOrder.getPurchaseOrderProducts());
                //Intent in = new Intent(context, OrderDetailActivity.class);
                //context.startActivity(in);
                if (new AppUtils().isTablet(context)) {
                    Intent in = new Intent(context, OrderDetailDialog.class);
                    in.putExtra("jsonPurchaseOrder", jsonPurchaseOrder);
                    //in.putExtra("codeQR", jsonTopic.getCodeQR());
                    ((Activity) context).startActivityForResult(in, Constants.RESULT_SETTING);
                } else {
                    Intent in = new Intent(context, OrderDetailActivity.class);
                    // in.putExtra("codeQR", jsonTopic.getCodeQR());
                    in.putExtra("jsonPurchaseOrder", jsonPurchaseOrder);
                    ((Activity) context).startActivityForResult(in, Constants.RESULT_SETTING);
                    ((Activity) context).overridePendingTransition(R.anim.slide_up, R.anim.stay);

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
            recordHolder.tv_order_cancel.setVisibility(View.GONE);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                builder.setTitle(null);
                View customDialogView = inflater.inflate(R.layout.dialog_logout, null, false);
                builder.setView(customDialogView);
                final AlertDialog mAlertDialog = builder.create();
                mAlertDialog.setCanceledOnTouchOutside(false);
                TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
                TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
                tvtitle.setText("Cancel Order");
                tv_msg.setText("Do you want to cancel the order?");
                Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
                Button btn_no = customDialogView.findViewById(R.id.btn_no);
                btn_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialog.dismiss();
                    }
                });
                btn_yes.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        peopleInQOrderAdapterClick.orderCancelClick(position);
                        mAlertDialog.dismiss();
                    }
                });
                mAlertDialog.show();

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
        recordHolder.tv_order_status.setText("Status: " + jsonPurchaseOrder.getPresentOrderState().getDescription());
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

}
