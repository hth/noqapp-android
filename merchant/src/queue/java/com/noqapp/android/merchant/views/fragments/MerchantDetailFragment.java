package com.noqapp.android.merchant.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.DataVisibilityEnum;
import com.noqapp.android.common.model.types.QueueOrderTypeEnum;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.body.merchant.OrderServed;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.HCSMenuActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.OrderDetailActivity;
import com.noqapp.android.merchant.views.activities.ProductListActivity;
import com.noqapp.android.merchant.views.activities.StoreMenuActivity;
import com.noqapp.android.merchant.views.activities.ViewAllPeopleInQOrderActivity;
import com.noqapp.android.merchant.views.adapters.PeopleInQOrderAdapter;
import com.noqapp.android.merchant.views.interfaces.AcquireOrderPresenter;
import com.noqapp.android.merchant.views.interfaces.OrderProcessedPresenter;
import com.noqapp.android.merchant.views.interfaces.PurchaseOrderPresenter;
import com.noqapp.android.merchant.views.model.PurchaseOrderApiCalls;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MerchantDetailFragment extends BaseMerchantDetailFragment implements
        PurchaseOrderPresenter, AcquireOrderPresenter, OrderProcessedPresenter,
        PeopleInQOrderAdapter.PeopleInQOrderAdapterClick, OrderDetailActivity.UpdateWholeList,
        HCSMenuActivity.UpdateWholeList, StoreMenuActivity.UpdateWholeList {

    private PeopleInQOrderAdapter peopleInQOrderAdapter;
    private List<JsonPurchaseOrder> purchaseOrders = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        iv_appointment.setVisibility(View.GONE);
        switch (LaunchActivity.getLaunchActivity().getUserProfile().getBusinessType()) {
            case RS:
            case GS:
            case CF:
                iv_product_list.setVisibility(View.VISIBLE);
                break;
            default:
                iv_product_list.setVisibility(View.GONE);
        }

        iv_product_list.setOnClickListener(v -> {
            if (LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.S_MANAGER) {
                Intent intent = new Intent(getActivity(), ProductListActivity.class);
                intent.putExtra("codeQR", jsonTopic.getCodeQR());
                ((Activity) context).startActivity(intent);
            } else {
                ShowAlertInformation.showThemeDialog(context, "Unauthorized access", "You are not allowed to use this feature");
            }
        });
        return view;
    }

    @Override
    protected void createToken(Context context, String codeQR) {
        if (jsonTopic.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.O) {
            if (jsonTopic.getBusinessType() == BusinessTypeEnum.HS) {
                HCSMenuActivity.updateWholeList = this;
                Intent intent = new Intent(getActivity(), HCSMenuActivity.class);
                intent.putExtra("jsonTopic", jsonTopic);
                ((Activity) context).startActivity(intent);
            } else {
                StoreMenuActivity.updateWholeList = this;
                Intent intent = new Intent(getActivity(), StoreMenuActivity.class);
                intent.putExtra("jsonTopic", jsonTopic);
                ((Activity) context).startActivity(intent);
            }
        } else {
            showCreateTokenDialog(context, codeQR);
        }
    }

    @Override
    protected void showAllPeopleInQHistory() {
        Intent in = new Intent(getActivity(), ViewAllPeopleInQOrderActivity.class);
        in.putExtra("codeQR", jsonTopic.getCodeQR());
        in.putExtra("visibility", DataVisibilityEnum.H == jsonTopic.getJsonDataVisibility().getDataVisibilities().get(LaunchActivity.getLaunchActivity().getUserLevel().name()));
        ((Activity) context).startActivity(in);
    }

    @Override
    public void getAllPeopleInQ(JsonTopic jsonTopic) {
        if (jsonTopic.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.O) {
            PurchaseOrderApiCalls purchaseOrderApiCalls = new PurchaseOrderApiCalls();
            purchaseOrderApiCalls.setPurchaseOrderPresenter(this);
            purchaseOrderApiCalls.showOrders(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonTopic.getCodeQR());
        } else {
            manageQueueApiCalls.setQueuePersonListPresenter(this);
            manageQueueApiCalls.getAllQueuePersonList(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonTopic.getCodeQR());
        }
    }

    @Override
    public void orderAcceptClick(int position) {
        if (tv_counter_name.getText().toString().trim().equals("")) {
            counterNameEmpty();
        } else {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                showProgress();
                OrderServed orderServed = new OrderServed();
                orderServed.setCodeQR(jsonTopic.getCodeQR());
                orderServed.setServedNumber(purchaseOrders.get(position).getToken());
                orderServed.setQueueUserId(purchaseOrders.get(position).getQueueUserId());
                orderServed.setGoTo(tv_counter_name.getText().toString());
                orderServed.setQueueStatus(QueueStatusEnum.N);
                orderServed.setPurchaseOrderState(purchaseOrders.get(position).getPresentOrderState());

                PurchaseOrderApiCalls purchaseOrderApiCalls = new PurchaseOrderApiCalls();
                purchaseOrderApiCalls.setAcquireOrderPresenter(this);
                purchaseOrderApiCalls.acquire(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), orderServed);
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
        }
    }

    @Override
    public void purchaseOrderListResponse(JsonPurchaseOrderList jsonPurchaseOrderList) {
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
            peopleInQOrderAdapter = new PeopleInQOrderAdapter(purchaseOrders, context, jsonTopic.getCodeQR(),
                    this, jsonTopic.getServingNumber(), jsonTopic.getJsonPaymentPermission());
            rv_queue_people.setAdapter(peopleInQOrderAdapter);
            if (jsonTopic.getServingNumber() > 0) {
                rv_queue_people.getLayoutManager().scrollToPosition(jsonTopic.getServingNumber() - 1);
            }
        }
        dismissProgress();
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
    protected void resetList() {
        if (QueueOrderTypeEnum.O == jsonTopic.getBusinessType().getQueueOrderType()) {
            purchaseOrders = new ArrayList<>();
            peopleInQOrderAdapter = new PeopleInQOrderAdapter(
                    purchaseOrders,
                    context,
                    jsonTopic.getCodeQR(),
                    this,
                    jsonTopic.getJsonPaymentPermission());
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
        tvCount = customDialogView.findViewById(R.id.tvcount);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        btn_create_token = customDialogView.findViewById(R.id.btn_create_token);
        btn_create_token.setOnClickListener(v -> {
            if (btn_create_token.getText().equals(mContext.getString(R.string.create_token))) {
                showProgress();
                setDispensePresenter();
                manageQueueApiCalls.dispenseToken(
                        BaseLaunchActivity.getDeviceID(),
                        LaunchActivity.getLaunchActivity().getEmail(),
                        LaunchActivity.getLaunchActivity().getAuth(),
                        codeQR);
                btn_create_token.setClickable(false);
            } else {
                mAlertDialog.dismiss();
            }
        });

        actionbarBack.setOnClickListener(v -> mAlertDialog.dismiss());
        mAlertDialog.show();
    }

    @Override
    public void peopleInQClick(int position) {
        if (jsonTopic.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.O) {

        } else {
            super.peopleInQClick(position);
        }
    }

    @Override
    public void viewOrderClick(Context context, JsonQueuedPerson jsonQueuedPerson, boolean isPaymentNotAllowed) {

    }


    @Override
    public void orderDoneClick(int position) {
        if (tv_counter_name.getText().toString().trim().equals("")) {
            counterNameEmpty();
        } else {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                showProgress();
                setProgressMessage("Completing the order...");
                OrderServed orderServed = new OrderServed();
                orderServed.setCodeQR(jsonTopic.getCodeQR());
                orderServed.setServedNumber(purchaseOrders.get(position).getToken());
                orderServed.setQueueUserId(purchaseOrders.get(position).getQueueUserId());
                orderServed.setGoTo(tv_counter_name.getText().toString());
                orderServed.setQueueStatus(QueueStatusEnum.N);
                orderServed.setPurchaseOrderState(purchaseOrders.get(position).getPresentOrderState());

                PurchaseOrderApiCalls purchaseOrderApiCalls = new PurchaseOrderApiCalls();
                purchaseOrderApiCalls.setOrderProcessedPresenter(this);
                purchaseOrderApiCalls.actionOnOrder(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), orderServed);
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
        }
    }

    @Override
    public void orderCancelClick(int position) {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            showProgress();
            setProgressMessage("Canceling the order...");
            OrderServed orderServed = new OrderServed();
            orderServed.setCodeQR(jsonTopic.getCodeQR());
            orderServed.setServedNumber(purchaseOrders.get(position).getToken());
            orderServed.setQueueUserId(purchaseOrders.get(position).getQueueUserId());
            orderServed.setTransactionId(purchaseOrders.get(position).getTransactionId());
            orderServed.setGoTo(tv_counter_name.getText().toString());
            orderServed.setQueueStatus(QueueStatusEnum.N);
            orderServed.setPurchaseOrderState(purchaseOrders.get(position).getPresentOrderState());
            PurchaseOrderApiCalls purchaseOrderApiCalls = new PurchaseOrderApiCalls();
            purchaseOrderApiCalls.setOrderProcessedPresenter(this);
            purchaseOrderApiCalls.cancel(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), orderServed);
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }

    }

    @Override
    public void viewOrderClick(JsonPurchaseOrder jsonPurchaseOrder, boolean isPaymentNotAllowed) {
        OrderDetailActivity.updateWholeList = this;
        Intent in = new Intent(context, OrderDetailActivity.class);
        in.putExtra("jsonPurchaseOrder", jsonPurchaseOrder);
        in.putExtra(IBConstant.KEY_IS_PAYMENT_NOT_ALLOWED, isPaymentNotAllowed);
        in.putExtra(IBConstant.KEY_IS_PAYMENT_PARTIAL_ALLOWED, jsonTopic.getBusinessType() == BusinessTypeEnum.HS);
        ((Activity) context).startActivity(in);
    }

    @Override
    public void acquireOrderResponse(JsonToken token) {
        //Log.v("Order acquire response",token.toString());
        dismissProgress();
        if (null != token) {
            JsonTopic jt = topicsList.get(currentPosition);
            if (token.getCodeQR().equalsIgnoreCase(jt.getCodeQR())) {
                if (StringUtils.isNotBlank(jt.getCustomerName())) {
                    Log.i(BaseMerchantDetailFragment.class.getSimpleName(), "Show customer name=" + jt.getCustomerName());
                }
                jt.setToken(token.getToken());
                jt.setQueueStatus(token.getQueueStatus());
                jt.setServingNumber(token.getServingNumber());
                jt.setCustomerName(token.getCustomerName());
                topicsList.set(currentPosition, jt);

                //To update merchant list screen
                mAdapterCallback.onMethodCallback(token);
            }
            updateUI(false);
        }
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        new ErrorResponseHandler().processError(getActivity(), eej);
    }

    @Override
    protected void updateUI(boolean isNewCall) {
        if (jsonTopic.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.O) {
            final PurchaseOrderApiCalls purchaseOrderApiCalls = new PurchaseOrderApiCalls();
            purchaseOrderApiCalls.setAcquireOrderPresenter(this);
            final QueueStatusEnum queueStatus = jsonTopic.getQueueStatus();
            queueStatusOuter = queueStatus == QueueStatusEnum.N;
            String cName = mAdapterCallback.getNameList().get(jsonTopic.getCodeQR());
            if (TextUtils.isEmpty(cName)) {
                tv_counter_name.setText("");
            } else {
                tv_counter_name.setText(cName);
            }

            tv_timing.setText("Timing: " + Formatter.convertMilitaryTo12HourFormat(jsonTopic.getHour().getStartHour()) + " - " + Formatter.convertMilitaryTo12HourFormat(jsonTopic.getHour().getEndHour()));
            tv_current_value.setText(String.valueOf(jsonTopic.getServingNumber()));
            /* Add to show only remaining people in queue */
            tv_total_value.setText(String.valueOf(jsonTopic.getToken() - jsonTopic.getServingNumber()));
            tv_title.setText(jsonTopic.getDisplayName());
            //   iv_generate_token.setVisibility(View.GONE);
            iv_view_followup.setVisibility(View.GONE);
            btn_start.setText(context.getString(R.string.start));
            btn_start.setBackgroundResource(R.drawable.start);
            btn_skip.setVisibility(View.GONE);
            tv_skip.setVisibility(View.GONE);

            if (LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.M_ADMIN
                    || LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.S_MANAGER
                    || LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.Q_SUPERVISOR) {
                // TODO(hth) Implement further settings for merchant topic
            }

            switch (queueStatus) {
                case S:
                    tv_start.setText(context.getString(R.string.start));
                    btn_next.setEnabled(false);
                    btn_next.setBackgroundResource(R.drawable.next_inactive);
                    btn_skip.setEnabled(false);
                    btn_skip.setBackgroundResource(R.drawable.skip_inactive);
                    break;
                case R:
                    tv_start.setText(context.getString(R.string.continues));
                    btn_next.setEnabled(false);
                    btn_next.setBackgroundResource(R.drawable.next_inactive);
                    btn_skip.setEnabled(false);
                    btn_skip.setBackgroundResource(R.drawable.skip_inactive);
                    break;
                case N:
                    tv_next.setText(context.getString(R.string.next));
                    btn_next.setEnabled(true);
                    btn_next.setBackgroundResource(R.drawable.next);
                    btn_skip.setEnabled(true);
                    btn_skip.setBackgroundResource(R.drawable.skip);
                    tv_start.setText(context.getString(R.string.pause));
                    btn_start.setBackgroundResource(R.drawable.pause);
                    break;
                case D:
                    tv_start.setText(context.getString(R.string.done));
                    tv_total_value.setText("0");
                    btn_start.setBackgroundResource(R.drawable.stop);
                    btn_next.setEnabled(false);
                    btn_next.setBackgroundResource(R.drawable.next_inactive);
                    btn_skip.setEnabled(false);
                    btn_skip.setBackgroundResource(R.drawable.skip_inactive);
                    break;
                case C:
                    tv_start.setText(context.getString(R.string.closed));
                    btn_start.setEnabled(false);
                    btn_start.setBackgroundResource(R.drawable.stop_inactive);
                    btn_next.setEnabled(false);
                    btn_next.setBackgroundResource(R.drawable.next_inactive);
                    btn_skip.setEnabled(false);
                    btn_skip.setBackgroundResource(R.drawable.skip_inactive);
                    break;
                case P:
                    tv_start.setText(context.getString(R.string.pause));
                    btn_next.setEnabled(false);
                    btn_next.setBackgroundResource(R.drawable.next_inactive);
                    btn_skip.setEnabled(false);
                    btn_skip.setBackgroundResource(R.drawable.skip_inactive);
                    btn_start.setBackgroundResource(R.drawable.pause);
                    break;
                default:
                    Log.e(BaseMerchantDetailFragment.class.getSimpleName(), "Reached un-supported condition");
            }

            btn_next.setOnClickListener(v -> {
                mAdapterCallback.saveCounterNames(jsonTopic.getCodeQR(), tv_counter_name.getText().toString().trim());
                if (tv_counter_name.getText().toString().trim().equals("")) {
                    counterNameEmpty();
                } else {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        showProgress();
                        OrderServed orderServed = new OrderServed();
                        orderServed.setCodeQR(jsonTopic.getCodeQR());
                        orderServed.setServedNumber(jsonTopic.getServingNumber());
                        orderServed.setGoTo(tv_counter_name.getText().toString());
                        orderServed.setQueueStatus(QueueStatusEnum.N);
                        orderServed.setPurchaseOrderState(PurchaseOrderStateEnum.OP);
                        purchaseOrderApiCalls.served(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), orderServed);
                    } else {
                        ShowAlertInformation.showNetworkDialog(context);
                    }
                }
            });
            btn_start.setOnClickListener(v -> {
                mAdapterCallback.saveCounterNames(jsonTopic.getCodeQR(), tv_counter_name.getText().toString().trim());
                if (jsonTopic.getToken() == 0) {
                    new CustomToast().showToast(context, context.getString(R.string.error_empty));
                } else if (jsonTopic.getRemaining() == 0 && jsonTopic.getServingNumber() == 0) {
                    new CustomToast().showToast(context, context.getString(R.string.error_empty_wait));
                } else if (queueStatus == QueueStatusEnum.D) {
                    new CustomToast().showToast(context, context.getString(R.string.error_done_next));
                } else {
                    if (tv_counter_name.getText().toString().trim().equals("")) {
                        counterNameEmpty();
                    } else {
                        if (tv_start.getText().equals(context.getString(R.string.pause))) {
                            ShowCustomDialog showDialog = new ShowCustomDialog(context);
                            showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                                @Override
                                public void btnPositiveClick() {
                                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                                        showProgress();
                                        OrderServed orderServed = new OrderServed();
                                        orderServed.setCodeQR(jsonTopic.getCodeQR());
                                        orderServed.setServedNumber(jsonTopic.getServingNumber());
                                        orderServed.setGoTo(tv_counter_name.getText().toString());
                                        orderServed.setQueueStatus(QueueStatusEnum.N);
                                        orderServed.setPurchaseOrderState(PurchaseOrderStateEnum.OP);
                                        purchaseOrderApiCalls.served(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), orderServed);
                                    } else {
                                        ShowAlertInformation.showNetworkDialog(context);
                                    }
                                }

                                @Override
                                public void btnNegativeClick() {
                                    //Do nothing
                                }
                            });
                            showDialog.displayDialog("Confirm", "Have you completed serving " + String.valueOf(jsonTopic.getServingNumber()));
                        } else {
                            if (LaunchActivity.getLaunchActivity().isOnline()) {
                                showProgress();
                                OrderServed orderServed = new OrderServed();
                                orderServed.setCodeQR(jsonTopic.getCodeQR());
                                orderServed.setServedNumber(jsonTopic.getServingNumber());
                                orderServed.setGoTo(tv_counter_name.getText().toString());
                                orderServed.setQueueStatus(QueueStatusEnum.N);
                                orderServed.setPurchaseOrderState(PurchaseOrderStateEnum.OP);
                                purchaseOrderApiCalls.served(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), orderServed);
                            } else {
                                ShowAlertInformation.showNetworkDialog(context);
                            }
                        }
                    }
                }
            });

            if (LaunchActivity.getLaunchActivity().isOnline()) {
                if (isNewCall) // show progressbar only first time
                    showProgress();
                getAllPeopleInQ(jsonTopic);
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
        } else {
            super.updateUI(isNewCall);
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
        peopleInQOrderAdapter = new PeopleInQOrderAdapter(purchaseOrders, context, jsonTopic.getCodeQR(),
                this, jsonTopic.getServingNumber(), jsonTopic.getJsonPaymentPermission());
        rv_queue_people.setAdapter(peopleInQOrderAdapter);
        if (jsonTopic.getServingNumber() > 0) {
            rv_queue_people.getLayoutManager().scrollToPosition(jsonTopic.getServingNumber() - 1);
        }
        dismissProgress();
    }

    @Override
    public void orderProcessedError() {
        dismissProgress();
    }

    @Override
    public void updateWholeList() {
        getAllPeopleInQ(jsonTopic);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_appointment).setVisible(false);
        menu.findItem(R.id.menu_followup).setVisible(false);
        MenuItem menuItem = menu.findItem(R.id.menu_add);
        if (null != LaunchActivity.getLaunchActivity().getUserProfile() && LaunchActivity.getLaunchActivity().getUserProfile().getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.O) {
            menuItem.setTitle("Create Order");
        } else {
            menuItem.setTitle("Create Token");
        }
    }
}
