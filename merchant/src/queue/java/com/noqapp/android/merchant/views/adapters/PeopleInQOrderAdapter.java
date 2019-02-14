package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.DocumentUploadActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class PeopleInQOrderAdapter extends RecyclerView.Adapter<PeopleInQOrderAdapter.MyViewHolder> {

    private final Context context;
    private List<JsonPurchaseOrder> dataSet;
    protected String qCodeQR = "";
    private int glowPosition = -1;

    public interface PeopleInQOrderAdapterClick {

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
        recordHolder.tv_customer_mobile.setText(TextUtils.isEmpty(phoneNo) ? context.getString(R.string.unregister_user) :
                PhoneFormatterUtil.formatNumber(LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName(), phoneNo));
        recordHolder.tv_order_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderDetailDialog(context, jsonPurchaseOrder, jsonPurchaseOrder.getPurchaseOrderProducts());
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
        } else {
            recordHolder.tv_order_cancel.setVisibility(View.GONE);
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
                intent.putExtra("recordReferenceId", jsonPurchaseOrder.getTransactionId());
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
                break;
            default:
                recordHolder.tv_order_done.setText("Order Done");
                recordHolder.tv_order_cancel.setText("Cancel Order");
                recordHolder.tv_order_prepared.setText("Order prepared");
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


    private void showOrderDetailDialog(final Context mContext, JsonPurchaseOrder jsonPurchaseOrder, List<JsonPurchaseOrderProduct> jsonPurchaseOrderProductList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_order_detail, null, false);
        ImageView actionbarBack = customDialogView.findViewById(R.id.actionbarBack);
        ListView listview = customDialogView.findViewById(R.id.listview);
        TextView tv_item_count = customDialogView.findViewById(R.id.tv_item_count);
        TextView tv_payment_mode = customDialogView.findViewById(R.id.tv_payment_mode);
        TextView tv_address = customDialogView.findViewById(R.id.tv_address);
        TextView tv_cost = customDialogView.findViewById(R.id.tv_cost);
        TextView tv_notes = customDialogView.findViewById(R.id.tv_notes);
        CardView cv_notes = customDialogView.findViewById(R.id.cv_notes);
        tv_notes.setText("Additional Notes: " + jsonPurchaseOrder.getAdditionalNote());
        cv_notes.setVisibility(TextUtils.isEmpty(jsonPurchaseOrder.getAdditionalNote()) ? View.GONE : View.VISIBLE);
        tv_address.setText(Html.fromHtml(jsonPurchaseOrder.getDeliveryAddress()));
        tv_payment_mode.setText(Html.fromHtml(jsonPurchaseOrder.getPaymentType().getDescription()));
        try {
            tv_cost.setText(Html.fromHtml(String.valueOf(Integer.parseInt(jsonPurchaseOrder.getOrderPrice()) / 100)));
        } catch (Exception e) {
            tv_cost.setText(Html.fromHtml(String.valueOf(0 / 100)));
        }
        builder.setView(customDialogView);
        tv_item_count.setText("Total Items: (" + jsonPurchaseOrderProductList.size() + ")");
        OrderItemAdapter adapter = new OrderItemAdapter(mContext, jsonPurchaseOrderProductList);
        listview.setAdapter(adapter);
        final AlertDialog mAlertDialog = builder.create();
        //mAlertDialog.setCanceledOnTouchOutside(false);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }
}
