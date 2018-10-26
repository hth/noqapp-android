package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class PeopleInQOrderAdapter extends RecyclerView.Adapter<PeopleInQOrderAdapter.MyViewHolder> {

    private final Context context;
    private List<JsonPurchaseOrder> dataSet;
    protected String qCodeQR = "";

    public interface PeopleInQOrderAdapterClick {

        void PeopleInQOrderClick(int position);

        void orderDoneClick(int position);

        void orderCancelClick(int position);

    }

    private PeopleInQOrderAdapterClick peopleInQOrderAdapterClick;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_customer_name;
        TextView tv_customer_mobile;
        TextView tv_sequence_number;
        TextView tv_status_msg;
        TextView tv_order_data;
        TextView tv_order_status;
        TextView tv_order_prepared;
        TextView tv_order_done;
        TextView tv_order_cancel;
        ImageView iv_info;
        CardView cardview;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_customer_name = itemView.findViewById(R.id.tv_customer_name);
            this.tv_customer_mobile = itemView.findViewById(R.id.tv_customer_mobile);
            this.tv_sequence_number = itemView.findViewById(R.id.tv_sequence_number);
            this.tv_status_msg = itemView.findViewById(R.id.tv_status_msg);
            this.tv_order_data = itemView.findViewById(R.id.tv_order_data);
            this.tv_order_status = itemView.findViewById(R.id.tv_order_status);
            this.tv_order_prepared = itemView.findViewById(R.id.tv_order_prepared);
            this.tv_order_done = itemView.findViewById(R.id.tv_order_done);
            this.tv_order_cancel = itemView.findViewById(R.id.tv_order_cancel);
            this.iv_info = itemView.findViewById(R.id.iv_info);
            this.cardview = itemView.findViewById(R.id.cardview);
        }
    }


    public PeopleInQOrderAdapter(List<JsonPurchaseOrder> data, Context context, String qCodeQR, PeopleInQOrderAdapterClick peopleInQOrderAdapterClick) {
        this.dataSet = data;
        this.context = context;
        this.qCodeQR = qCodeQR;
        this.peopleInQOrderAdapterClick = peopleInQOrderAdapterClick;
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
                //TODO : @ Chandra Please change the country code dynamically, country code you can get it from TOPIC
                PhoneFormatterUtil.formatNumber("IN", phoneNo));
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
        recordHolder.tv_order_status.setText(jsonPurchaseOrder.getPresentOrderState().getDescription());
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
        recordHolder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peopleInQOrderAdapterClick.PeopleInQOrderClick(position);
            }
        });
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
        //listview.setScrollBarFadeDuration(0);
        TextView tv_item_count = customDialogView.findViewById(R.id.tv_item_count);
        TextView tv_payment_mode = customDialogView.findViewById(R.id.tv_payment_mode);
        TextView tv_address = customDialogView.findViewById(R.id.tv_address);
        TextView tv_cost = customDialogView.findViewById(R.id.tv_cost);
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
