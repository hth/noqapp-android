package com.noqapp.android.merchant.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hbb20.CountryCodePicker;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.BusinessSupportEnum;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.DataVisibilityEnum;
import com.noqapp.android.common.model.types.QueueOrderTypeEnum;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.BusinessCustomerApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomer;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.body.merchant.OrderServed;
import com.noqapp.android.merchant.utils.AppUtils;
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
import com.noqapp.android.merchant.views.adapters.JsonProfileAdapter;
import com.noqapp.android.merchant.views.adapters.PeopleInQOrderAdapter;
import com.noqapp.android.merchant.views.interfaces.AcquireOrderPresenter;
import com.noqapp.android.merchant.views.interfaces.FindCustomerPresenter;
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
        HCSMenuActivity.UpdateWholeList, StoreMenuActivity.UpdateWholeList, FindCustomerPresenter {

    private PeopleInQOrderAdapter peopleInQOrderAdapter;
    private List<JsonPurchaseOrder> purchaseOrders = new ArrayList<>();
    private Spinner sp_patient_list;
    private TextView tv_select_patient;
    private Button btn_create_order;
    private BusinessCustomerApiCalls businessCustomerApiCalls;
    private String countryCode = "";
    private String cid = "";
    private CountryCodePicker ccp;
    private long mLastClickTime = 0;
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
        if (!LaunchActivity.isTablet)
            rv_queue_people.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
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
            //showCreateTokenDialog(context, codeQR);
            showCreateTokenDialogWithMobile(context, codeQR);
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
        CountryCodePicker countryCode = customDialogView.findViewById(R.id.ccp);
        edt_mobile = customDialogView.findViewById(R.id.edt_mobile);
        EditText edtName = customDialogView.findViewById(R.id.edt_name);
        RelativeLayout userInfo = customDialogView.findViewById(R.id.ll_center);
        LinearLayout tokenInfo = customDialogView.findViewById(R.id.ll_bottom);
        tv_create_token = customDialogView.findViewById(R.id.tvtitle);
        tvCount = customDialogView.findViewById(R.id.tvcount);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        edtName.addTextChangedListener(new TextWatcher()  {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s)  {
                if (edtName.getText().toString().length() <= 0) {
                    edtName.setError("Enter Name");
                } else {
                    edtName.setError(null);
                }
            }
        });

        edt_mobile.addTextChangedListener(new TextWatcher()  {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s)  {
                if (edt_mobile.getText().toString().length() <= 0) {
                    edt_mobile.setError("Enter Phone No");
                } else {
                    edt_mobile.setError(null);
                }
            }
        });
        btn_create_token = customDialogView.findViewById(R.id.btn_create_token);
        btn_create_token.setOnClickListener(v -> {
            if (btn_create_token.getText().equals(mContext.getString(R.string.create_token))) {
                if (edt_mobile.getText().toString().length() <= 0) {
                    edt_mobile.setError("Enter Phone No");
                    return;
                }
                if (edtName.getText().toString().length() <= 0 ) {
                    edtName.setError("Enter Name");
                    return;
                }
                showProgress();
                setDispensePresenter();
                JsonBusinessCustomer jsonBusinessCustomer = new JsonBusinessCustomer();
                jsonBusinessCustomer.setCodeQR(codeQR);
                jsonBusinessCustomer.setCustomerName(edtName.getText().toString());

                jsonBusinessCustomer.setCustomerPhone(countryCode.getDefaultCountryCode() + edt_mobile.getText().toString());
                jsonBusinessCustomer.setRegisteredUser(false);
                manageQueueApiCalls.dispenseTokenWithClientInfo(
                        BaseLaunchActivity.getDeviceID(),
                        LaunchActivity.getLaunchActivity().getEmail(),
                        LaunchActivity.getLaunchActivity().getAuth(),
                        jsonBusinessCustomer);
                btn_create_token.setClickable(false);
                userInfo.setVisibility(View.GONE);
                tokenInfo.setVisibility(View.VISIBLE);
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
        if (jsonTopic.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.O) {
            menuItem.setTitle("Create Order");
        } else {
            menuItem.setTitle("Create Token");
        }
    }


    private void showCreateTokenDialogWithMobile(final Context mContext, final String codeQR) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View view = inflater.inflate(R.layout.dialog_create_token_with_mobile, null, false);
        ImageView actionbarBack = view.findViewById(R.id.actionbarBack);
        tv_create_token = view.findViewById(R.id.tvtitle);
        tvCount = view.findViewById(R.id.tvcount);
        ll_main_section = view.findViewById(R.id.ll_main_section);
        ll_mobile = view.findViewById(R.id.ll_mobile);
        edt_mobile = view.findViewById(R.id.edt_mobile);
        sp_patient_list = view.findViewById(R.id.sp_patient_list);
        tv_select_patient = view.findViewById(R.id.tv_select_patient);


        final EditText edt_id = view.findViewById(R.id.edt_id);
        final RadioGroup rg_user_id = view.findViewById(R.id.rg_user_id);
        final RadioButton rb_mobile = view.findViewById(R.id.rb_mobile);
        builder.setView(view);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        rg_user_id.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_mobile) {
                    ll_mobile.setVisibility(View.VISIBLE);
                    edt_id.setVisibility(View.GONE);
                    edt_id.setText("");
                } else {
                    edt_id.setVisibility(View.VISIBLE);
                    ll_mobile.setVisibility(View.GONE);
                    edt_mobile.setText("");
                }
            }
        });
        cid = "";
        ccp = view.findViewById(R.id.ccp);
        String c_codeValue = LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName();
        int c_code = PhoneFormatterUtil.getCountryCodeFromRegion(c_codeValue.toUpperCase());
        ccp.setDefaultCountryUsingNameCode(String.valueOf(c_code));
        btn_create_order = view.findViewById(R.id.btn_create_order);
        btn_create_token = view.findViewById(R.id.btn_create_token);
        btn_create_token.setText("Search Customer");
        btn_create_token.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            boolean isValid = true;
            edt_mobile.setError(null);
            edt_id.setError(null);
            AppUtils.hideKeyBoard(getActivity());
            // get selected radio button from radioGroup
            int selectedId = rg_user_id.getCheckedRadioButtonId();
            if (selectedId == R.id.rb_mobile) {
                if (TextUtils.isEmpty(edt_mobile.getText())) {
                    edt_mobile.setError(getString(R.string.error_mobile_blank));
                    isValid = false;
                }
            } else {
                if (TextUtils.isEmpty(edt_id.getText())) {
                    edt_id.setError(getString(R.string.error_customer_id));
                    isValid = false;
                }
            }


            if (isValid) {
                setProgressMessage("Searching customer...");
                showProgress();
                setProgressCancel(false);
                setDispensePresenter();
                String phone = "";
                cid = "";
                if (rb_mobile.isChecked()) {
                    edt_id.setText("");
                    countryCode = ccp.getSelectedCountryCode();
                    phone = countryCode + edt_mobile.getText().toString();
                    cid = "";
                } else {
                    cid = edt_id.getText().toString();
                    edt_mobile.setText("");// set blank so that wrong phone no not pass to login screen
                }
                businessCustomerApiCalls = new BusinessCustomerApiCalls();
                businessCustomerApiCalls.setFindCustomerPresenter(MerchantDetailFragment.this);
                businessCustomerApiCalls.findCustomer(
                        BaseLaunchActivity.getDeviceID(),
                        LaunchActivity.getLaunchActivity().getEmail(),
                        LaunchActivity.getLaunchActivity().getAuth(),
                        new JsonBusinessCustomerLookup().setCodeQR(codeQR).setCustomerPhone(phone).setBusinessCustomerId(cid));
                btn_create_token.setClickable(false);
                //  mAlertDialog.dismiss();
            }
        });

        actionbarBack.setOnClickListener(v -> mAlertDialog.dismiss());
        mAlertDialog.show();
    }


    @Override
    public void findCustomerResponse(JsonProfile jsonProfile) {
        dismissProgress();
        mLastClickTime = 0;
        if (null != jsonProfile) {
            List<JsonProfile> jsonProfileList = new ArrayList<>();
            jsonProfileList.add(jsonProfile);
            if (jsonProfile.getDependents().size() > 0) {
                jsonProfileList.addAll(jsonProfile.getDependents());
            }
            JsonProfileAdapter adapter = new JsonProfileAdapter(getActivity(), jsonProfileList);
            sp_patient_list.setAdapter(adapter);
            sp_patient_list.setVisibility(View.VISIBLE);
            sp_patient_list.setEnabled(false);
            edt_mobile.setEnabled(false);
            tv_select_patient.setVisibility(View.VISIBLE);
            tv_select_patient.setText("Customer Name");
            btn_create_order.setVisibility(View.VISIBLE);
            btn_create_token.setVisibility(View.GONE);
            btn_create_order.setOnClickListener(v -> {
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    btn_create_order.setEnabled(false);
                    setProgressMessage("Creating token...");
                    showProgress();
                    setProgressCancel(false);
                    String phoneNoWithCode = "";
                    setDispensePresenter();
                    if (TextUtils.isEmpty(cid)) {
                        phoneNoWithCode = PhoneFormatterUtil.phoneNumberWithCountryCode(jsonProfile.getPhoneRaw(), jsonProfile.getCountryShortName());
                    }

                    JsonBusinessCustomer jsonBusinessCustomer = new JsonBusinessCustomer().
                            setQueueUserId(jsonProfile.getQueueUserId());
                    jsonBusinessCustomer
                            .setCodeQR(topicsList.get(currentPosition).getCodeQR())
                            .setCustomerPhone(phoneNoWithCode)
                            .setBusinessCustomerId(cid);
                    manageQueueApiCalls.dispenseTokenWithClientInfo(
                            BaseLaunchActivity.getDeviceID(),
                            LaunchActivity.getLaunchActivity().getEmail(),
                            LaunchActivity.getLaunchActivity().getAuth(),
                            jsonBusinessCustomer);

                } else {
                    ShowAlertInformation.showNetworkDialog(getActivity());
                }
            });
        }

    }

    @Override
    public void userFound(JsonProfile jsonProfile) {
        findCustomerResponse(jsonProfile);
    }

    @Override
    public void userRegistered(JsonProfile jsonProfile) {
        findCustomerResponse(jsonProfile);
    }
}
