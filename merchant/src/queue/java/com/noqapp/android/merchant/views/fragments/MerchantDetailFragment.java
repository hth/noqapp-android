package com.noqapp.android.merchant.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hbb20.CountryCodePicker;
import com.noqapp.android.common.beans.JsonProfile;
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
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.BusinessCustomerApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomer;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.body.merchant.OrderServed;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.AppInitialize;
import com.noqapp.android.merchant.views.activities.AppointmentActivity;
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
        switch (AppInitialize.getUserProfile().getBusinessType()) {
            case RS:
            case GS:
            case CF:
                iv_product_list.setVisibility(View.VISIBLE);
                break;
            default:
                iv_product_list.setVisibility(View.GONE);
        }

        iv_product_list.setOnClickListener(v -> {
            if (AppInitialize.getUserLevel() == UserLevelEnum.S_MANAGER) {
                Intent intent = new Intent(getActivity(), ProductListActivity.class);
                intent.putExtra("codeQR", jsonTopic.getCodeQR());
                ((Activity) context).startActivity(intent);
            } else {
                ShowAlertInformation.showThemeDialog(context, "Unauthorized access", "You are not allowed to use this feature");
            }
        });

        iv_appointment.setOnClickListener(v1 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent intent = new Intent(getActivity(), AppointmentActivity.class);
            intent.putExtra(IBConstant.KEY_CODE_QR, jsonTopic.getCodeQR());
            intent.putExtra("displayName",jsonTopic.getDisplayName());
            intent.putExtra("bizCategoryId",jsonTopic.getBizCategoryId());
            ((Activity) context).startActivity(intent);
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
            showCreateTokenDialogWithMobile(context, codeQR);
        }
    }

    @Override
    protected void showAllPeopleInQHistory() {
        Intent in = new Intent(getActivity(), ViewAllPeopleInQOrderActivity.class);
        in.putExtra("codeQR", jsonTopic.getCodeQR());
        in.putExtra("visibility", DataVisibilityEnum.H == jsonTopic.getJsonDataVisibility().getDataVisibilities().get(AppInitialize.getUserLevel().name()));
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
            if (new NetworkUtil(getActivity()).isOnline()) {
                showProgress();

                JsonPurchaseOrder jsonPurchaseOrder = purchaseOrders.get(position);
                OrderServed orderServed = new OrderServed();
                orderServed.setCodeQR(jsonTopic.getCodeQR());
                orderServed.setServedNumber(jsonPurchaseOrder.getToken());
                orderServed.setQueueUserId(jsonPurchaseOrder.getQueueUserId());
                orderServed.setGoTo(tv_counter_name.getText().toString());
                orderServed.setQueueStatus(QueueStatusEnum.N);
                orderServed.setPurchaseOrderState(jsonPurchaseOrder.getPresentOrderState());
                orderServed.setTransactionId(jsonPurchaseOrder.getTransactionId());

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
            Collections.sort(purchaseOrders, (lhs, rhs) -> Integer.compare(lhs.getToken(), rhs.getToken()));
            peopleInQOrderAdapter = new PeopleInQOrderAdapter(
                purchaseOrders,
                context,
                jsonTopic.getCodeQR(),
                this,
                jsonTopic.getServingNumber(),
                jsonTopic.getJsonPaymentPermission());
            rv_queue_people.setAdapter(peopleInQOrderAdapter);
            if (jsonTopic.getServingNumber() > 0) {
                rv_queue_people.getLayoutManager().scrollToPosition(jsonTopic.getServingNumber() - 1);
            }
            if (null != purchaseOrders) {
                fab_top_bottom.setVisibility(purchaseOrders.size() > MIN_LIST_SIZE ? View.VISIBLE : View.GONE);
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
            if (null != purchaseOrders) {
                fab_top_bottom.setVisibility(purchaseOrders.size() > MIN_LIST_SIZE ? View.VISIBLE : View.GONE);
            }
        } else {
            super.resetList();
        }
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
            if (new NetworkUtil(getActivity()).isOnline()) {
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
        if (new NetworkUtil(getActivity()).isOnline()) {
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

            if (AppInitialize.getUserLevel() == UserLevelEnum.M_ADMIN
                || AppInitialize.getUserLevel() == UserLevelEnum.S_MANAGER
                || AppInitialize.getUserLevel() == UserLevelEnum.Q_SUPERVISOR) {
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
                    if (new NetworkUtil(getActivity()).isOnline()) {
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
                                    if (new NetworkUtil(getActivity()).isOnline()) {
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
                            if (new NetworkUtil(getActivity()).isOnline()) {
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

            if (new NetworkUtil(getActivity()).isOnline()) {
                if (isNewCall) { // show progressbar only first time
                    showProgress();
                }
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

        peopleInQOrderAdapter = new PeopleInQOrderAdapter(
            purchaseOrders,
            context,
            jsonTopic.getCodeQR(),
            this,
            jsonTopic.getServingNumber(),
            jsonTopic.getJsonPaymentPermission());
        rv_queue_people.setAdapter(peopleInQOrderAdapter);
        if (jsonTopic.getServingNumber() > 0) {
            rv_queue_people.getLayoutManager().scrollToPosition(jsonTopic.getServingNumber() - 1);
        }
        if (null != purchaseOrders) {
            fab_top_bottom.setVisibility(purchaseOrders.size() > MIN_LIST_SIZE ? View.VISIBLE : View.GONE);
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
        menu.findItem(R.id.menu_followup).setVisible(false);
        MenuItem menuItem = menu.findItem(R.id.menu_add);
        switch (jsonTopic.getBusinessType().getBusinessSupport()) {
            case OD:
                menuItem.setTitle("Create Order");
                menu.findItem(R.id.menu_appointment).setVisible(false);
                break;
            case OQ:
                menuItem.setTitle("Create Token");
                //Menu shows appointment and product list
                break;
            case QQ:
                menuItem.setTitle("Create Token");
                menu.findItem(R.id.menu_product_list).setVisible(false);
                break;
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
        LinearLayout ll_cust_id = view.findViewById(R.id.ll_cust_id);
        LinearLayout ll_unregistered = view.findViewById(R.id.ll_unregistered);
        edt_mobile = view.findViewById(R.id.edt_mobile);
        sp_patient_list = view.findViewById(R.id.sp_patient_list);
        tv_select_patient = view.findViewById(R.id.tv_select_patient);
        btn_create_token = view.findViewById(R.id.btn_create_token);
        btn_create_another = view.findViewById(R.id.btn_create_another);
        
        final EditText edt_id = view.findViewById(R.id.edt_id);
        final RadioGroup rg_token_type = view.findViewById(R.id.rg_token_type);
        final RadioButton rb_mobile = view.findViewById(R.id.rb_mobile);
        builder.setView(view);
        String c_codeValue = AppInitialize.getUserProfile().getCountryShortName();
        int c_code = PhoneFormatterUtil.getCountryCodeFromRegion(c_codeValue.toUpperCase());
        ccp = view.findViewById(R.id.ccp);
        ccp.setDefaultCountryUsingNameCode(String.valueOf(c_code));
        CountryCodePicker ccp_unregistered = view.findViewById(R.id.ccp_unregistered);
        ccp_unregistered.setDefaultCountryUsingNameCode(String.valueOf(c_code));

        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        rg_token_type.setOnCheckedChangeListener((group, checkedId) -> {
            // View cleanup
            sp_patient_list.setVisibility(View.GONE);
            tv_select_patient.setVisibility(View.GONE);
            edt_mobile.setEnabled(true);
            btn_create_token.setVisibility(View.VISIBLE);
            btn_create_token.setClickable(true);
            if (R.id.rb_mobile == checkedId) {
                ll_mobile.setVisibility(View.VISIBLE);
                ll_cust_id.setVisibility(View.GONE);
                ll_unregistered.setVisibility(View.GONE);
                btn_create_token.setText(getString(R.string.search_registered_customer));
            } else if (R.id.rb_customer_id == checkedId) {
                ll_cust_id.setVisibility(View.VISIBLE);
                ll_mobile.setVisibility(View.GONE);
                ll_unregistered.setVisibility(View.GONE);
                btn_create_token.setText(getString(R.string.search_registered_customer));
            } else {
                ll_unregistered.setVisibility(View.VISIBLE);
                ll_mobile.setVisibility(View.GONE);
                ll_cust_id.setVisibility(View.GONE);
                btn_create_token.setText(getString(R.string.create_token));
            }

            // Bind listeners
            if (rg_token_type.getCheckedRadioButtonId() == R.id.rb_unregistered) {
                EditText edt_mobile_unregistered = view.findViewById(R.id.edt_mobile_unregistered);
                EditText edt_name_unregistered = view.findViewById(R.id.edt_name_unregistered);
                btn_create_token.setOnClickListener(v -> {
                    boolean isValid = true;
                    AppUtils.hideKeyBoard(getActivity());
                    setDispensePresenter();
                    // get selected radio button from radioGroup
                    if (TextUtils.isEmpty(edt_mobile_unregistered.getText())) {
                        edt_mobile_unregistered.setError(getString(R.string.error_mobile_blank));
                        isValid = false;
                    }
                    if (TextUtils.isEmpty(edt_name_unregistered.getText())) {
                        edt_name_unregistered.setError(getString(R.string.error_customer_name));
                        isValid = false;
                    }

                    if (isValid) {
                        btn_create_token.setEnabled(false);
                        JsonBusinessCustomer jsonBusinessCustomer = new JsonBusinessCustomer();
                        jsonBusinessCustomer.setCodeQR(codeQR);
                        jsonBusinessCustomer.setCustomerName(edt_name_unregistered.getText().toString());
                        jsonBusinessCustomer.setCustomerPhone(ccp_unregistered.getDefaultCountryCode() + edt_mobile_unregistered.getText().toString());
                        jsonBusinessCustomer.setRegisteredUser(false);
                        manageQueueApiCalls.dispenseTokenWithClientInfo(
                                AppInitialize.getDeviceID(),
                                AppInitialize.getEmail(),
                                AppInitialize.getAuth(),
                            jsonBusinessCustomer);
                    }
                });
            } else {
                cid = "";
                btn_create_order = view.findViewById(R.id.btn_create_order);
                btn_create_token.setText(getString(R.string.search_registered_customer));
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
                    int selectedId = rg_token_type.getCheckedRadioButtonId();
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

                    if (isValid ) {
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
                                AppInitialize.getDeviceID(),
                                AppInitialize.getEmail(),
                                AppInitialize.getAuth(),
                            new JsonBusinessCustomerLookup().setCodeQR(codeQR).setCustomerPhone(phone).setBusinessCustomerId(cid));
                        btn_create_token.setClickable(false);
                        //  mAlertDialog.dismiss();
                    }
                });
            }
        });

        actionbarBack.setOnClickListener(v -> mAlertDialog.dismiss());
        btn_create_another.setOnClickListener(v -> {
            mAlertDialog.dismiss();
            showCreateTokenDialogWithMobile(context, codeQR);
        });
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
                if (new NetworkUtil(getActivity()).isOnline()) {
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

                    JsonBusinessCustomer jsonBusinessCustomer = new JsonBusinessCustomer()
                        .setQueueUserId(jsonProfile.getQueueUserId());
                    jsonBusinessCustomer
                        .setCodeQR(topicsList.get(currentPosition).getCodeQR())
                        .setCustomerPhone(phoneNoWithCode)
                        .setBusinessCustomerId(cid)
                        .setRegisteredUser(true);
                    manageQueueApiCalls.dispenseTokenWithClientInfo(
                            AppInitialize.getDeviceID(),
                            AppInitialize.getEmail(),
                            AppInitialize.getAuth(),
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
