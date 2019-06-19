package com.noqapp.android.client.views.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.PurchaseOrderApiCall;
import com.noqapp.android.client.presenter.PurchaseOrderPresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistorical;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderProductHistorical;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.StoreDetailActivity;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.CommonHelper;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter implements PurchaseOrderPresenter {
    private final Context context;
    private final OnItemClickListener listener;
    private ArrayList<JsonPurchaseOrderHistorical> dataSet;
    private ProgressDialog progressDialog;

    public OrderHistoryAdapter(ArrayList<JsonPurchaseOrderHistorical> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            ViewGroup parent,
            int viewType
    ) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_order_history, parent, false);
        RecyclerView.ViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        final JsonPurchaseOrderHistorical jsonPurchaseOrderHistorical = dataSet.get(listPosition);
        holder.tv_name.setText(jsonPurchaseOrderHistorical.getDisplayName());
        holder.tv_address.setText(AppUtilities.getStoreAddress(jsonPurchaseOrderHistorical.getTown(), jsonPurchaseOrderHistorical.getArea()));
        holder.tv_order_date.setText(CommonHelper.formatStringDate(CommonHelper.SDF_DD_MMM_YY_HH_MM_A, jsonPurchaseOrderHistorical.getCreated()));
        holder.tv_order_item.setText(getOrderItems(jsonPurchaseOrderHistorical.getJsonPurchaseOrderProductHistoricalList()));
        try {
            holder.tv_order_amount.setText(AppUtilities.getCurrencySymbol(jsonPurchaseOrderHistorical.getCountryShortName()) + " " + String.valueOf(Integer.parseInt(jsonPurchaseOrderHistorical.getOrderPrice()) / 100));
        } catch (Exception e) {
            holder.tv_order_amount.setText("0");
            e.printStackTrace();
        }
        holder.tv_queue_status.setText(jsonPurchaseOrderHistorical.getPresentOrderState().getFriendlyDescription());
        switch (jsonPurchaseOrderHistorical.getPresentOrderState()) {
            case CO:
                holder.tv_queue_status.setTextColor(ContextCompat.getColor(context, R.color.colorMobile));
                break;
            case OD:
                holder.tv_queue_status.setTextColor(ContextCompat.getColor(context, R.color.green));
                break;
            default:
                holder.tv_queue_status.setTextColor(ContextCompat.getColor(context, R.color.text_header_color));
        }
        holder.iv_details.setOnClickListener((View v) -> {
            listener.onStoreItemClick(jsonPurchaseOrderHistorical, v, listPosition);

        });
        if (jsonPurchaseOrderHistorical.getBusinessType() == BusinessTypeEnum.PH && (jsonPurchaseOrderHistorical.getPresentOrderState() == PurchaseOrderStateEnum.PO ||
                jsonPurchaseOrderHistorical.getPresentOrderState() == PurchaseOrderStateEnum.OP)) {
            holder.btn_activate.setVisibility(View.VISIBLE);
            holder.btn_reorder.setVisibility(View.GONE);
        } else {
            holder.btn_activate.setVisibility(View.GONE);
            holder.btn_reorder.setVisibility(View.VISIBLE);
        }
        holder.btn_reorder.setOnClickListener((View v) -> {
            BizStoreElastic bizStoreElastic = new BizStoreElastic();
            bizStoreElastic.setRating(jsonPurchaseOrderHistorical.getRatingCount());
            bizStoreElastic.setDisplayImage("");
            bizStoreElastic.setBusinessName(jsonPurchaseOrderHistorical.getDisplayName());
            bizStoreElastic.setCodeQR(jsonPurchaseOrderHistorical.getCodeQR());
            bizStoreElastic.setBusinessType(jsonPurchaseOrderHistorical.getBusinessType());
            Intent intent = new Intent(context, StoreDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("BizStoreElastic", bizStoreElastic);
            intent.putExtras(bundle);
            context.startActivity(intent);

        });
        holder.btn_activate.setOnClickListener((View v) -> {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Activating order in progress...");
                progressDialog.show();
                new PurchaseOrderApiCall(OrderHistoryAdapter.this).
                        activateOrder(UserUtils.getDeviceId(), UserUtils.getEmail(),
                                UserUtils.getAuth(), jsonPurchaseOrderHistorical);
            } else {
                ShowAlertInformation.showNetworkDialog(context);
            }

        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public void purchaseOrderResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        // implementation not required here
        progressDialog.dismiss();
    }

    @Override
    public void purchaseOrderCancelResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        // implementation not required here
        progressDialog.dismiss();
    }

    @Override
    public void payCashResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        // implementation not required here
        progressDialog.dismiss();
    }

    @Override
    public void purchaseOrderActivateResponse(JsonPurchaseOrderHistorical jsonPurchaseOrderHistorical) {
        if (null != jsonPurchaseOrderHistorical) {
            new CustomToast().showToast(context, "Order activated successfully.");
            progressDialog.dismiss();
        }
    }


    @Override
    public void authenticationFailure() {
        progressDialog.dismiss();
        AppUtilities.authenticationProcessing(context);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        progressDialog.dismiss();
        new ErrorResponseHandler().processFailureResponseCode(context, errorCode);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        progressDialog.dismiss();
        if (null != eej)
            new ErrorResponseHandler().processError(context, eej);
    }

    public interface OnItemClickListener {
        void onStoreItemClick(JsonPurchaseOrderHistorical item, View view, int pos);
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_address;
        private TextView tv_order_date;
        private TextView tv_order_amount;
        private TextView tv_order_item;
        private TextView tv_queue_status;
        private ImageView iv_details;
        private Button btn_reorder;
        private Button btn_activate;
        private CardView card_view;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_address = itemView.findViewById(R.id.tv_address);
            this.tv_order_date = itemView.findViewById(R.id.tv_order_date);
            this.tv_order_amount = itemView.findViewById(R.id.tv_order_amount);
            this.tv_order_item = itemView.findViewById(R.id.tv_order_item);
            this.tv_queue_status = itemView.findViewById(R.id.tv_queue_status);
            this.btn_reorder = itemView.findViewById(R.id.btn_reorder);
            this.btn_activate = itemView.findViewById(R.id.btn_activate);
            this.iv_details = itemView.findViewById(R.id.iv_details);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }

    private String getOrderItems(List<JsonPurchaseOrderProductHistorical> data) {
        String result = "";
        for (int i = 0; i < data.size(); i++) {
            result += data.get(i).getProductName() + " x " + String.valueOf(data.get(i).getProductQuantity()) + ", ";
        }
        return result.endsWith(", ") ? result.substring(0, result.length() - 2) : result;
    }
}
