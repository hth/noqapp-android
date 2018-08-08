package com.noqapp.android.merchant.views.fragments;


import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.order.JsonPurchaseOrder;
import com.noqapp.android.common.beans.order.JsonPurchaseOrderProduct;
import com.noqapp.android.common.model.types.QueueOrderTypeEnum;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.body.order.OrderServed;
import com.noqapp.android.merchant.presenter.beans.order.JsonPurchaseOrderList;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.PeopleInQOrderAdapter;
import com.noqapp.android.merchant.views.interfaces.AcquireOrderPresenter;
import com.noqapp.android.merchant.views.interfaces.OrderProcessedPresenter;
import com.noqapp.android.merchant.views.interfaces.PurchaseOrderPresenter;
import com.noqapp.android.merchant.views.model.PurchaseOrderModel;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MerchantDetailFragment extends BaseMerchantDetailFragment implements PurchaseOrderPresenter, AcquireOrderPresenter, OrderProcessedPresenter, PeopleInQOrderAdapter.PeopleInQOrderAdapterClick {

    private PeopleInQOrderAdapter peopleInQOrderAdapter;
    private List<JsonPurchaseOrder> purchaseOrders = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void createToken(Context context, String codeQR) {
        if (jsonTopic.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.O)
            Toast.makeText(context, "Show the order screen", Toast.LENGTH_LONG).show();
        else
            showCreateTokenDialog(context, codeQR);
    }

    @Override
    public void getAllPeopleInQ(JsonTopic jsonTopic) {
        if (jsonTopic.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.O) {
            PurchaseOrderModel purchaseOrderModel = new PurchaseOrderModel();
            purchaseOrderModel.setPurchaseOrderPresenter(this);
            purchaseOrderModel.fetch(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonTopic.getCodeQR());
        } else {
            manageQueueModel.setQueuePersonListPresenter(this);
            manageQueueModel.getAllQueuePersonList(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonTopic.getCodeQR());
        }
    }

    @Override
    public void purchaseOrderResponse(JsonPurchaseOrderList jsonPurchaseOrderList) {
        if (null != jsonPurchaseOrderList) {
            Log.v("order data:", jsonPurchaseOrderList.toString());
            purchaseOrders = jsonPurchaseOrderList.getPurchaseOrders();
            Collections.sort(
                    purchaseOrders,
                    new Comparator<JsonPurchaseOrder>() {
                        public int compare(JsonPurchaseOrder lhs, JsonPurchaseOrder rhs) {
                            return Integer.compare(lhs.getToken(), rhs.getToken());
                        }
                    }
            );
            peopleInQOrderAdapter = new PeopleInQOrderAdapter(purchaseOrders, context, jsonTopic.getCodeQR(), this);
            rv_queue_people.setAdapter(peopleInQOrderAdapter);
        }
        dismissProgress();
    }

    @Override
    public void purchaseOrderError() {
        dismissProgress();
    }

    @Override
    protected void resetList() {
        if (jsonTopic.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.O) {
            purchaseOrders = new ArrayList<>();
            peopleInQOrderAdapter = new PeopleInQOrderAdapter(purchaseOrders, context, jsonTopic.getCodeQR(), this);
            rv_queue_people.setAdapter(peopleInQOrderAdapter);
        } else {
            super.resetList();
        }
    }

    private void showCreateTokenDialog(final Context mContext, final String codeQR) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_create_token, null, false);
        ImageView actionbarBack = customDialogView.findViewById(R.id.actionbarBack);
        tv_create_token = customDialogView.findViewById(R.id.tvtitle);
        iv_banner = customDialogView.findViewById(R.id.iv_banner);
        tvcount = customDialogView.findViewById(R.id.tvcount);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        btn_create_token = customDialogView.findViewById(R.id.btn_create_token);
        btn_create_token.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (btn_create_token.getText().equals(mContext.getString(R.string.create_token))) {
                    LaunchActivity.getLaunchActivity().progressDialog.show();
                    setDispensePresenter();
                    manageQueueModel.dispenseToken(
                            LaunchActivity.getLaunchActivity().getDeviceID(),
                            LaunchActivity.getLaunchActivity().getEmail(),
                            LaunchActivity.getLaunchActivity().getAuth(),
                            codeQR);
                    btn_create_token.setClickable(false);
                } else {
                    mAlertDialog.dismiss();
                }
            }
        });

        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }

    @Override
    public void PeopleInQClick(int position) {
        if (jsonTopic.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.O) {

        } else {
            super.PeopleInQClick(position);
        }

    }

    @Override
    public void PeopleInQOrderClick(int position) {
        if (tv_counter_name.getText().toString().trim().equals("")) {
            Toast.makeText(context, context.getString(R.string.error_counter_empty), Toast.LENGTH_LONG).show();
        } else {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                //progressDialog.setVisibility(View.VISIBLE);
                // lastSelectedPos = position;
                OrderServed orderServed = new OrderServed();
                orderServed.setCodeQR(jsonTopic.getCodeQR());
                orderServed.setServedNumber(purchaseOrders.get(position).getToken());
                orderServed.setGoTo(tv_counter_name.getText().toString());
                orderServed.setQueueStatus(QueueStatusEnum.N);
                orderServed.setPurchaseOrderState(purchaseOrders.get(position).getPurchaseOrderState());


                PurchaseOrderModel purchaseOrderModel = new PurchaseOrderModel();
                purchaseOrderModel.setAcquireOrderPresenter(this);
                purchaseOrderModel.acquire(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), orderServed);
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
        }
    }

    @Override
    public void orderDoneClick(int position) {
        if (tv_counter_name.getText().toString().trim().equals("")) {
            Toast.makeText(context, context.getString(R.string.error_counter_empty), Toast.LENGTH_LONG).show();
        } else {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                OrderServed orderServed = new OrderServed();
                orderServed.setCodeQR(jsonTopic.getCodeQR());
                orderServed.setServedNumber(purchaseOrders.get(position).getToken());
                orderServed.setGoTo(tv_counter_name.getText().toString());
                orderServed.setQueueStatus(QueueStatusEnum.N);
                orderServed.setPurchaseOrderState(purchaseOrders.get(position).getPurchaseOrderState());


                PurchaseOrderModel purchaseOrderModel = new PurchaseOrderModel();
                purchaseOrderModel.setOrderProcessedPresenter(this);
                purchaseOrderModel.processed(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), orderServed);
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
        }
    }

    @Override
    public void acquireOrderResponse(JsonToken token) {
        //Log.v("Order acquire response",token.toString());
        LaunchActivity.getLaunchActivity().dismissProgress();
        dismissProgress();
        if (null != token) {
            JsonTopic jt = topicsList.get(currrentpos);
            if (token.getCodeQR().equalsIgnoreCase(jt.getCodeQR())) {
                if (StringUtils.isNotBlank(jt.getCustomerName())) {
                    Log.i(BaseMerchantDetailFragment.class.getSimpleName(), "Show customer name=" + jt.getCustomerName());
                }
                jt.setToken(token.getToken());
                jt.setQueueStatus(token.getQueueStatus());
                jt.setServingNumber(token.getServingNumber());
                jt.setCustomerName(token.getCustomerName());
                topicsList.set(currrentpos, jt);

                //To update merchant list screen
                mAdapterCallback.onMethodCallback(token);
            }
            updateUI();
        }
    }

    @Override
    public void acquireOrderError(ErrorEncounteredJson errorEncounteredJson) {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    protected void updateUI() {
        if (jsonTopic.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.O) {
            {
                final PurchaseOrderModel purchaseOrderModel = new PurchaseOrderModel();
                purchaseOrderModel.setAcquireOrderPresenter(this);
                final QueueStatusEnum queueStatus = jsonTopic.getQueueStatus();
                queueStatusOuter = queueStatus == QueueStatusEnum.N;
                String cName = mAdapterCallback.getNameList().get(jsonTopic.getCodeQR());
                if (TextUtils.isEmpty(cName))
                    tv_counter_name.setText("");
                else
                    tv_counter_name.setText(cName);

                tv_timing.setText("Timing: " + Formatter.convertMilitaryTo12HourFormat(jsonTopic.getHour().getStartHour())
                        + " - " + Formatter.convertMilitaryTo12HourFormat(jsonTopic.getHour().getEndHour()));

                tv_current_value.setText(String.valueOf(jsonTopic.getServingNumber()));
                /* Add to show only remaining people in queue */
                tv_total_value.setText(String.valueOf(jsonTopic.getToken() - jsonTopic.getServingNumber()));
                tv_title.setText(jsonTopic.getDisplayName());
                iv_generate_token.setVisibility(View.GONE);
                btn_start.setText(context.getString(R.string.start));
                btn_start.setBackgroundResource(R.mipmap.start);
                btn_skip.setVisibility(View.GONE);

                if (LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.M_ADMIN
                        || LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.S_MANAGER
                        || LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.Q_SUPERVISOR) {
                    // TODO(hth) Implement further settings for merchant topic
                }

                switch (queueStatus) {
                    case S:
                        tv_start.setText(context.getString(R.string.start));
                        btn_next.setEnabled(false);
                        btn_next.setBackgroundResource(R.mipmap.next_inactive);
                        btn_skip.setEnabled(false);
                        btn_skip.setBackgroundResource(R.mipmap.skip_inactive);
                        break;
                    case R:
                        tv_start.setText(context.getString(R.string.continues));
                        btn_next.setEnabled(false);
                        btn_next.setBackgroundResource(R.mipmap.next_inactive);
                        btn_skip.setEnabled(false);
                        btn_skip.setBackgroundResource(R.mipmap.skip_inactive);
                        break;
                    case N:
                        tv_next.setText(context.getString(R.string.next));
                        btn_next.setEnabled(true);
                        btn_next.setBackgroundResource(R.mipmap.next);
                        btn_skip.setEnabled(true);
                        btn_skip.setBackgroundResource(R.mipmap.skip);
                        tv_start.setText(context.getString(R.string.pause));
                        btn_start.setBackgroundResource(R.mipmap.pause);
                        break;
                    case D:
                        tv_start.setText(context.getString(R.string.done));
                        tv_total_value.setText("0");
                        btn_start.setBackgroundResource(R.mipmap.stop);
                        btn_next.setEnabled(false);
                        btn_next.setBackgroundResource(R.mipmap.next_inactive);
                        btn_skip.setEnabled(false);
                        btn_skip.setBackgroundResource(R.mipmap.skip_inactive);
                        break;
                    case C:
                        tv_start.setText(context.getString(R.string.closed));
                        btn_start.setEnabled(false);
                        btn_start.setBackgroundResource(R.mipmap.stop_inactive);
                        btn_next.setEnabled(false);
                        btn_next.setBackgroundResource(R.mipmap.next_inactive);
                        btn_skip.setEnabled(false);
                        btn_skip.setBackgroundResource(R.mipmap.skip_inactive);
                        break;
                    case P:
                        tv_start.setText(context.getString(R.string.pause));
                        btn_next.setEnabled(false);
                        btn_next.setBackgroundResource(R.mipmap.next_inactive);
                        btn_skip.setEnabled(false);
                        btn_skip.setBackgroundResource(R.mipmap.skip_inactive);
                        btn_start.setBackgroundResource(R.mipmap.pause);
                        break;
                    default:
                        Log.e(BaseMerchantDetailFragment.class.getSimpleName(), "Reached un-supported condition");
                }

                btn_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAdapterCallback.saveCounterNames(jsonTopic.getCodeQR(), tv_counter_name.getText().toString().trim());
                        if (tv_counter_name.getText().toString().trim().equals("")) {
                            Toast.makeText(context, context.getString(R.string.error_counter_empty), Toast.LENGTH_LONG).show();
                        } else {
                            if (LaunchActivity.getLaunchActivity().isOnline()) {
                                LaunchActivity.getLaunchActivity().progressDialog.show();
                                OrderServed orderServed = new OrderServed();
                                orderServed.setCodeQR(jsonTopic.getCodeQR());
                                orderServed.setServedNumber(jsonTopic.getServingNumber());
                                orderServed.setGoTo(tv_counter_name.getText().toString());
                                orderServed.setQueueStatus(QueueStatusEnum.N);
                                orderServed.setPurchaseOrderState(PurchaseOrderStateEnum.OP);
                                purchaseOrderModel.served(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), orderServed);
                            } else {
                                ShowAlertInformation.showNetworkDialog(context);
                            }
                        }
                    }
                });
                btn_start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAdapterCallback.saveCounterNames(jsonTopic.getCodeQR(), tv_counter_name.getText().toString().trim());
                        if (jsonTopic.getToken() == 0) {
                            Toast.makeText(context, context.getString(R.string.error_empty), Toast.LENGTH_LONG).show();
                        } else if (jsonTopic.getRemaining() == 0 && jsonTopic.getServingNumber() == 0) {
                            Toast.makeText(context, context.getString(R.string.error_empty_wait), Toast.LENGTH_LONG).show();
                        } else if (queueStatus == QueueStatusEnum.D) {
                            Toast.makeText(context, context.getString(R.string.error_done_next), Toast.LENGTH_LONG).show();
                        } else {
                            if (tv_counter_name.getText().toString().trim().equals("")) {
                                Toast.makeText(context, context.getString(R.string.error_counter_empty), Toast.LENGTH_LONG).show();
                            } else {
                                if (tv_start.getText().equals(context.getString(R.string.pause))) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                    builder.setTitle("Confirm");
                                    builder.setMessage("Have you completed serving " + String.valueOf(jsonTopic.getServingNumber()));
                                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (LaunchActivity.getLaunchActivity().isOnline()) {
                                                LaunchActivity.getLaunchActivity().progressDialog.show();
                                                OrderServed orderServed = new OrderServed();
                                                orderServed.setCodeQR(jsonTopic.getCodeQR());
                                                orderServed.setServedNumber(jsonTopic.getServingNumber());
                                                orderServed.setGoTo(tv_counter_name.getText().toString());
                                                orderServed.setQueueStatus(QueueStatusEnum.N);
                                                orderServed.setPurchaseOrderState(PurchaseOrderStateEnum.OP);
                                                purchaseOrderModel.served(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), orderServed);
                                            } else {
                                                ShowAlertInformation.showNetworkDialog(context);
                                            }
                                            dialog.dismiss();
                                        }
                                    });

                                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                    AlertDialog alert = builder.create();
                                    alert.show();
                                } else {
                                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                                        LaunchActivity.getLaunchActivity().progressDialog.show();
                                        OrderServed orderServed = new OrderServed();
                                        orderServed.setCodeQR(jsonTopic.getCodeQR());
                                        orderServed.setServedNumber(jsonTopic.getServingNumber());
                                        orderServed.setGoTo(tv_counter_name.getText().toString());
                                        orderServed.setQueueStatus(QueueStatusEnum.N);
                                        orderServed.setPurchaseOrderState(PurchaseOrderStateEnum.OP);
                                        purchaseOrderModel.served(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), orderServed);
                                    } else {
                                        ShowAlertInformation.showNetworkDialog(context);
                                    }
                                }
                            }
                        }
                    }
                });

                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    progressDialog.setVisibility(View.VISIBLE);
                    getAllPeopleInQ(jsonTopic);

                } else {
                    ShowAlertInformation.showNetworkDialog(getActivity());
                }
            }
        } else {
            super.updateUI();
        }

    }

    @Override
    public void orderProcessedResponse(JsonPurchaseOrderList jsonPurchaseOrderList) {
        if (jsonPurchaseOrderList.getPurchaseOrders().size() > 0) {
            //Server will return only updated object
            //Hence update only that objects
            JsonPurchaseOrder jsonPurchaseOrder = jsonPurchaseOrderList.getPurchaseOrders().get(0);
            for (int j = 0; j < jsonPurchaseOrderList.getPurchaseOrders().size(); j++) {

                for (int i = 0; i < purchaseOrders.size(); i++) {
                    if (purchaseOrders.get(i).getToken() == jsonPurchaseOrder.getToken()) {
                        purchaseOrders.set(i, jsonPurchaseOrder);
                        break;
                    }
                }
            }
        }
        peopleInQOrderAdapter = new PeopleInQOrderAdapter(purchaseOrders, context, jsonTopic.getCodeQR(), this);
        rv_queue_people.setAdapter(peopleInQOrderAdapter);
    }

    @Override
    public void orderProcessedError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }


}
