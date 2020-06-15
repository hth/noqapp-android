package com.noqapp.android.merchant.views.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.ActionTypeEnum;
import com.noqapp.android.common.model.types.CustomerPriorityLevelEnum;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.model.types.QueueUserStateEnum;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.BusinessCustomerApiCalls;
import com.noqapp.android.merchant.model.ManageQueueApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomer;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CustomerPriority;
import com.noqapp.android.merchant.presenter.beans.body.merchant.Served;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.LoginActivity;
import com.noqapp.android.merchant.views.activities.RegistrationActivity;
import com.noqapp.android.merchant.views.activities.SettingActivity;
import com.noqapp.android.merchant.views.adapters.BasePeopleInQAdapter;
import com.noqapp.android.merchant.views.adapters.PeopleInQAdapter;
import com.noqapp.android.merchant.views.interfaces.AdapterCallback;
import com.noqapp.android.merchant.views.interfaces.ApproveCustomerPresenter;
import com.noqapp.android.merchant.views.interfaces.DispenseTokenPresenter;
import com.noqapp.android.merchant.views.interfaces.ManageQueuePresenter;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseMerchantDetailFragment extends BaseFragment implements ManageQueuePresenter,
        DispenseTokenPresenter, QueuePersonListPresenter, PeopleInQAdapter.PeopleInQAdapterClick,
        RegistrationActivity.RegisterCallBack, LoginActivity.LoginCallBack, ApproveCustomerPresenter {

    protected Context context;
    protected TextView tv_create_token;
    protected Button btn_create_token;
    protected TextView tvCount;
    private PeopleInQAdapter peopleInQAdapter;
    private List<JsonQueuedPerson> jsonQueuedPersonArrayList = new ArrayList<>();
    protected EditText edt_mobile;
    protected RecyclerView rv_queue_people;
    protected JsonTopic jsonTopic = null;
    protected TextView tv_counter_name;
    protected TextView tv_title, tv_total_value, tv_current_value, tv_timing, tv_start, tv_next, tv_skip;
    private Chronometer chronometer;
    protected int currentPosition = 0;
    protected static AdapterCallback mAdapterCallback;
    protected Button btn_skip;
    protected Button btn_next;
    protected Button btn_start;
    protected ImageView iv_product_list, iv_appointment;
    protected boolean queueStatusOuter = false;
    private int lastSelectedPos = -1;
    protected ManageQueueApiCalls manageQueueApiCalls;
    protected ArrayList<JsonTopic> topicsList;
    protected ImageView iv_generate_token, iv_queue_history, iv_view_followup;
    // variable to track event time
    private long mLastClickTime = 0;
    protected LinearLayout ll_mobile;
    protected LinearLayout ll_main_section;
    protected TextView tv_appointment_count;
    protected FrameLayout fl_appointment;
    private ImageView iv_settings;
    private BusinessCustomerApiCalls businessCustomerApiCalls;

    public static void setAdapterCallBack(AdapterCallback adapterCallback) {
        mAdapterCallback = adapterCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        if (null != bundle) {
            topicsList = (ArrayList<JsonTopic>) bundle.getSerializable("jsonMerchant");
            currentPosition = bundle.getInt("position");
        }

        View itemView = inflater.inflate(R.layout.viewpager_item, container, false);
        context = getActivity();
        manageQueueApiCalls = new ManageQueueApiCalls();
        manageQueueApiCalls.setManageQueuePresenter(this);
        businessCustomerApiCalls = new BusinessCustomerApiCalls();
        businessCustomerApiCalls.setApproveCustomerPresenter(this);
        if (null != topicsList && topicsList.size() > 0) {
            jsonTopic = topicsList.get(currentPosition);
        }

        //progressDialog = itemView.findViewById(R.id.progress_bar);
        tv_current_value = itemView.findViewById(R.id.tv_current_value);
        tv_total_value = itemView.findViewById(R.id.tv_total_value);
        tv_title = itemView.findViewById(R.id.tv_title);
        tv_timing = itemView.findViewById(R.id.tv_timing);
        tv_appointment_count = itemView.findViewById(R.id.tv_appointment_count);
        fl_appointment = itemView.findViewById(R.id.fl_appointment);
        chronometer = itemView.findViewById(R.id.chronometer);

        rv_queue_people = itemView.findViewById(R.id.rv_queue_people);
        tv_counter_name = itemView.findViewById(R.id.tv_counter_name);
        rv_queue_people.setHasFixedSize(true);
        rv_queue_people.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rv_queue_people.setItemAnimator(new DefaultItemAnimator());
        btn_skip = itemView.findViewById(R.id.btn_skip);
        btn_next = itemView.findViewById(R.id.btn_next);
        btn_start = itemView.findViewById(R.id.btn_start);

        tv_next = itemView.findViewById(R.id.tv_next);
        tv_start = itemView.findViewById(R.id.tv_start);
        tv_skip = itemView.findViewById(R.id.tv_skip);
        iv_product_list = itemView.findViewById(R.id.iv_product_list);
        iv_appointment = itemView.findViewById(R.id.iv_appointment);
        iv_settings = itemView.findViewById(R.id.iv_settings);
        iv_settings.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent in = new Intent(context, SettingActivity.class);
            in.putExtra("codeQR", jsonTopic.getCodeQR());
            ((Activity) context).startActivityForResult(in, Constants.RESULT_SETTING);
            ((Activity) context).overridePendingTransition(R.anim.slide_up, R.anim.stay);
        });
        iv_generate_token = itemView.findViewById(R.id.iv_generate_token);
        iv_generate_token.setOnClickListener(view -> {
            // Preventing multiple clicks, using threshold of 3 second
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                createToken(context, jsonTopic.getCodeQR());
            } else {
                ShowAlertInformation.showNetworkDialog(context);
            }
        });
        iv_queue_history = itemView.findViewById(R.id.iv_queue_history);
        iv_queue_history.setOnClickListener(view -> {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                showAllPeopleInQHistory();
            } else {
                ShowAlertInformation.showNetworkDialog(context);
            }
        });
        iv_view_followup = itemView.findViewById(R.id.iv_view_followup);
        tv_counter_name.setOnClickListener(v -> showCounterEditDialog(context, tv_counter_name, jsonTopic.getCodeQR()));
        updateUI(true);
        return itemView;
    }

    protected abstract void createToken(Context context, String codeQR);

    protected abstract void showAllPeopleInQHistory();

    @Override
    public void onResume() {
        super.onResume();
        if (UserUtils.isLogin()) {
            LaunchActivity.getLaunchActivity().setActionBarTitle(jsonTopic.getDisplayName());
            LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.VISIBLE);
            LaunchActivity.getLaunchActivity().enableDisableBack(false);
        } else {
            LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.GONE);
        }
    }

    public void updateListData(final ArrayList<JsonTopic> jsonTopics) {
        try {
            topicsList = jsonTopics;
            // resetList();
            updateUI(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPage(int pos) {
        //update UI
        currentPosition = pos;
        jsonTopic = topicsList.get(pos);
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        resetList();
        updateUI(true);
    }

    @Override
    public void manageQueueError() {
        dismissProgress();
    }

    @Override
    public void manageQueueResponse(JsonToken token) {
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
        if (null != eej && eej.getSystemErrorCode().equalsIgnoreCase(MobileSystemErrorCodeEnum.USER_NOT_FOUND.getCode())) {
            new CustomToast().showToast(context, eej.getReason());
            Intent in = new Intent(getActivity(), LoginActivity.class);
            in.putExtra("phone_no", edt_mobile.getText().toString());
            context.startActivity(in);
            RegistrationActivity.registerCallBack = this;
            LoginActivity.loginCallBack = this;
        } else if (null != eej && eej.getSystemErrorCode().equals(MobileSystemErrorCodeEnum.MERCHANT_COULD_NOT_ACQUIRE.getCode())) {
            new CustomToast().showToast(context, getString(R.string.error_client_just_acquired));
            if (lastSelectedPos >= 0) {
                jsonQueuedPersonArrayList.get(lastSelectedPos).setServerDeviceId("XXX-XXXX-XXXX");
                lastSelectedPos = -1;
                peopleInQAdapter = new PeopleInQAdapter(
                        jsonQueuedPersonArrayList,
                        context,
                        this,
                        jsonTopic.getCodeQR(),
                        jsonTopic.getJsonDataVisibility(),
                        jsonTopic.getJsonPaymentPermission());
                rv_queue_people.setAdapter(peopleInQAdapter);
            }
        } else {
            new ErrorResponseHandler().processError(getActivity(), eej);
        }
    }

    @Override
    public void dispenseTokenResponse(JsonToken token) {
        dismissProgress();
        if (null != token && null != tv_create_token) {
            if (null != edt_mobile) {
                edt_mobile.setText("");
            }
            if (null != ll_main_section) {
                ll_main_section.setVisibility(View.GONE);
            }

            switch (token.getQueueStatus()) {
                case C:
                    tv_create_token.setText("Queue is closed. Cannot generate token.");
                    btn_create_token.setClickable(true);
                    btn_create_token.setText(context.getString(R.string.queue_closed));
                    if (null != getActivity()) {
                        ShowAlertInformation.showThemeDialog(getActivity(), "Queue is closed", "Cannot generate token.");
                    }
                    break;
                case D:
                case N:
                case P:
                case R:
                case S:
                    tv_create_token.setText("Token Number");
                    btn_create_token.setText(context.getString(R.string.done));
                    tvCount.setText(String.valueOf(token.getToken()));
                    tvCount.setVisibility(View.VISIBLE);
                    btn_create_token.setClickable(true);
                    for (int i = 0; i < jsonQueuedPersonArrayList.size(); i++) {
                        JsonQueuedPerson jt = jsonQueuedPersonArrayList.get(i);
                        if (jt.getToken() == token.getToken()) {
                            new CustomToast().showToast(context, "User already in Queue");
                            break;
                        }
                    }
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        // setProgressMessage("Fetching list...");
                        //showProgress();
                        manageQueueApiCalls.getAllQueuePersonList(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonTopic.getCodeQR());
                    } else {
                        ShowAlertInformation.showNetworkDialog(getActivity());
                    }
                    break;
                default:
                    Log.e(BaseMerchantDetailFragment.class.getSimpleName(), "Reached un-reachable condition");
                    throw new RuntimeException("Reached unsupported condition");
            }
        }
    }

    private void showCounterEditDialog(final Context mContext, final TextView textView, final String codeQR) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_counter);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        final AutoCompleteTextView actv_counter = dialog.findViewById(R.id.actv_counter);
        final ArrayList<String> names = LaunchActivity.getLaunchActivity().getCounterNames();
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, names);
        actv_counter.setAdapter(adapter1);
        actv_counter.setThreshold(1);
        actv_counter.setDropDownBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.white)));
        AppUtils.setAutoCompleteText(actv_counter, textView.getText().toString().trim());
        Button btnPositive = dialog.findViewById(R.id.btnPositive);
        Button btnNegative = dialog.findViewById(R.id.btnNegative);
        btnPositive.setOnClickListener(v -> {
            actv_counter.setError(null);
            if (actv_counter.getText().toString().equals("")) {
                actv_counter.setError(mContext.getString(R.string.empty_counter));
            } else {
                AppUtils.hideKeyBoard(getActivity());
                textView.setText(actv_counter.getText().toString());
                mAdapterCallback.saveCounterNames(codeQR, actv_counter.getText().toString().trim());
                if (!names.contains(actv_counter.getText().toString())) {
                    names.add(actv_counter.getText().toString());
                    LaunchActivity.getLaunchActivity().setCounterNames(names);
                }
                dialog.dismiss();
            }
        });
        btnNegative.setOnClickListener(v -> dialog.dismiss());
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }


    private void setPresenter() {
        manageQueueApiCalls.setManageQueuePresenter(this);
        businessCustomerApiCalls.setApproveCustomerPresenter(this);
    }

    protected void setDispensePresenter() {
        manageQueueApiCalls.setDispenseTokenPresenter(this);
    }

    @Override
    public void queuePersonListResponse(JsonQueuePersonList jsonQueuePersonList) {
        if (null != jsonQueuePersonList) {
            jsonQueuedPersonArrayList = jsonQueuePersonList.getQueuedPeople();
            tv_appointment_count.setText(String.valueOf(jsonQueuePersonList.getAppointmentCountForToday()));
            Collections.sort(jsonQueuedPersonArrayList, (lhs, rhs) -> Integer.compare(lhs.getToken(), rhs.getToken()));
            // if (null == peopleInQAdapter) {
                 peopleInQAdapter = new PeopleInQAdapter(jsonQueuedPersonArrayList, context, this, jsonTopic);
                 rv_queue_people.setAdapter(peopleInQAdapter);
//            } else {
//                peopleInQAdapter.updateDataSet(jsonQueuedPersonArrayList,jsonTopic);
//            }
            if (jsonTopic.getServingNumber() > 0) {
                rv_queue_people.getLayoutManager().scrollToPosition(jsonTopic.getServingNumber() - 1);
            }
        }
        dismissProgress();
    }

    @Override
    public void queuePersonListError() {
        dismissProgress();
    }

    protected void updateUI(boolean isNewCall) {
        final QueueStatusEnum queueStatus = jsonTopic.getQueueStatus();
        queueStatusOuter = queueStatus == QueueStatusEnum.N;
        String cName = mAdapterCallback.getNameList().get(jsonTopic.getCodeQR());
        if (TextUtils.isEmpty(cName)) {
            tv_counter_name.setText("");
        } else {
            tv_counter_name.setText(cName);
        }

        tv_timing.setText("Timing: " + Formatter.convertMilitaryTo12HourFormat(jsonTopic.getHour().getStartHour())
                + " - " + Formatter.convertMilitaryTo12HourFormat(jsonTopic.getHour().getEndHour()));

        tv_current_value.setText(String.valueOf(jsonTopic.getServingNumber()));
        /* Add to show only remaining people in queue */
        tv_total_value.setText(String.valueOf(jsonTopic.getToken() - jsonTopic.getServingNumber()));
        tv_title.setText(jsonTopic.getDisplayName());

        btn_start.setText(context.getString(R.string.start));
        btn_start.setBackgroundResource(R.drawable.start);

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
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
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
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    showProgress();
                    setProgressMessage("Calling next person in Q...");
                    Served served = new Served();
                    served.setCodeQR(jsonTopic.getCodeQR());
                    served.setQueueStatus(jsonTopic.getQueueStatus());
                    served.setQueueUserState(QueueUserStateEnum.S);
                    served.setServedNumber(jsonTopic.getServingNumber());
                    served.setGoTo(tv_counter_name.getText().toString());
                    setPresenter();
                    manageQueueApiCalls.served(
                            BaseLaunchActivity.getDeviceID(),
                            LaunchActivity.getLaunchActivity().getEmail(),
                            LaunchActivity.getLaunchActivity().getAuth(),
                            served);
                } else {
                    ShowAlertInformation.showNetworkDialog(context);
                }
            }
        });
        btn_skip.setOnClickListener(v -> {
            mAdapterCallback.saveCounterNames(jsonTopic.getCodeQR(), tv_counter_name.getText().toString().trim());
            if (queueStatus != QueueStatusEnum.S && queueStatus != QueueStatusEnum.D) {
                if (tv_counter_name.getText().toString().trim().equals("")) {
                    counterNameEmpty();
                } else {
                    ShowCustomDialog showDialog = new ShowCustomDialog(context);
                    showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                        @Override
                        public void btnPositiveClick() {
                            if (LaunchActivity.getLaunchActivity().isOnline()) {
                                setProgressMessage("Skip current person in Q...");
                                showProgress();
                                Served served = new Served();
                                served.setCodeQR(jsonTopic.getCodeQR());
                                served.setQueueStatus(jsonTopic.getQueueStatus());
                                served.setQueueUserState(QueueUserStateEnum.N);
                                served.setServedNumber(jsonTopic.getServingNumber());
                                served.setGoTo(tv_counter_name.getText().toString());
                                setPresenter();
                                manageQueueApiCalls.served(
                                        BaseLaunchActivity.getDeviceID(),
                                        LaunchActivity.getLaunchActivity().getEmail(),
                                        LaunchActivity.getLaunchActivity().getAuth(),
                                        served);
                                chronometer.stop();
                                chronometer.setBase(SystemClock.elapsedRealtime());
                            } else {
                                ShowAlertInformation.showNetworkDialog(context);
                            }
                        }

                        @Override
                        public void btnNegativeClick() {
                            //Do nothing
                        }
                    });
                    showDialog.displayDialog("Alert", "Do you really want to skip the user. Please confirm");
                }
            } else if (queueStatus == QueueStatusEnum.S) {
                new CustomToast().showToast(context, context.getString(R.string.error_start));
            } else if (queueStatus == QueueStatusEnum.D) {
                new CustomToast().showToast(context, context.getString(R.string.error_done));
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
                                    setProgressMessage("Pause the Queue...");
                                    showProgress();
                                    Served served = new Served();
                                    served.setCodeQR(jsonTopic.getCodeQR());
                                    served.setQueueUserState(QueueUserStateEnum.S);
                                    /* send QueueStatusEnum P for pause state */
                                    served.setQueueStatus(QueueStatusEnum.P);
                                    served.setServedNumber(jsonTopic.getServingNumber());
                                    served.setGoTo(tv_counter_name.getText().toString());
                                    setPresenter();
                                    manageQueueApiCalls.served(
                                            BaseLaunchActivity.getDeviceID(),
                                            LaunchActivity.getLaunchActivity().getEmail(),
                                            LaunchActivity.getLaunchActivity().getAuth(),
                                            served);
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
                            setProgressMessage("Starting Queue...");
                            showProgress();
                            Served served = new Served();
                            served.setCodeQR(jsonTopic.getCodeQR());
                            served.setQueueUserState(QueueUserStateEnum.S);
                            /* send QueueStatusEnum as it is for other than pause state */
                            served.setQueueStatus(jsonTopic.getQueueStatus());
                            served.setServedNumber(jsonTopic.getServingNumber());
                            served.setGoTo(tv_counter_name.getText().toString());
                            setPresenter();
                            manageQueueApiCalls.served(
                                    BaseLaunchActivity.getDeviceID(),
                                    LaunchActivity.getLaunchActivity().getEmail(),
                                    LaunchActivity.getLaunchActivity().getAuth(),
                                    served);
                            chronometer.stop();
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            chronometer.start();
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
    }

    public abstract void getAllPeopleInQ(JsonTopic jsonTopic);

    protected void resetList() {
        jsonQueuedPersonArrayList = new ArrayList<>();
        peopleInQAdapter = new PeopleInQAdapter(
                jsonQueuedPersonArrayList,
                context,
                this,
                jsonTopic.getCodeQR(),
                jsonTopic.getJsonDataVisibility(),
                jsonTopic.getJsonPaymentPermission());
        rv_queue_people.setAdapter(peopleInQAdapter);
    }

    @Override
    public void peopleInQClick(int position) {
        if (jsonQueuedPersonArrayList.get(position).getQueueUserState() == QueueUserStateEnum.A) {
            new CustomToast().showToast(context, getString(R.string.error_client_left_queue));
        } else {
            if (TextUtils.isEmpty(jsonQueuedPersonArrayList.get(position).getServerDeviceId())) {
                ShowCustomDialog showDialog = new ShowCustomDialog(context);
                showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                    @Override
                    public void btnPositiveClick() {
                        if (LaunchActivity.getLaunchActivity().isOnline()) {
                            showProgress();
                            lastSelectedPos = position;
                            Served served = new Served();
                            served.setCodeQR(jsonTopic.getCodeQR());
                            served.setQueueStatus(jsonTopic.getQueueStatus());
                            // served.setQueueUserState(QueueUserStateEnum.N); don't send for time being
                            //served.setServedNumber(jsonTopic.getServingNumber());
                            served.setGoTo(tv_counter_name.getText().toString());
                            served.setServedNumber(jsonQueuedPersonArrayList.get(position).getToken());
                            manageQueueApiCalls.acquire(
                                    BaseLaunchActivity.getDeviceID(),
                                    LaunchActivity.getLaunchActivity().getEmail(),
                                    LaunchActivity.getLaunchActivity().getAuth(),
                                    served);
                        } else {
                            ShowAlertInformation.showNetworkDialog(getActivity());
                        }
                    }

                    @Override
                    public void btnNegativeClick() {
                        //Do nothing
                    }
                });
                showDialog.displayDialog("Alert", "Do you want to acquire it?");
            } else if (jsonQueuedPersonArrayList.get(position).getServerDeviceId().equals(UserUtils.getDeviceId())) {
                new CustomToast().showToast(context, getString(R.string.error_client_acquired_by_you));
            } else {
                new CustomToast().showToast(context, getString(R.string.error_client_acquired));
            }
        }
    }

    // Handler for Approve/Reject/Reset button
    // Calls businessCustomerApiCalls to update priority and customer attribute
    @Override
    public void actionOnBusinessCustomer(
            Context context,
            JsonQueuedPerson jsonQueuedPerson,
            CustomerPriorityLevelEnum customerPriorityLevel,
            ActionTypeEnum action,
            String codeQR
    ) {
        CustomerPriority customerPriority = new CustomerPriority();
        customerPriority.setActionType(action);
        customerPriority.setCustomerPriorityLevel(customerPriorityLevel);
        customerPriority.setQueueUserId(jsonQueuedPerson.getQueueUserId());
        customerPriority.setCodeQR(codeQR);

        Log.v("ApproveCustomer", "Approve");
        businessCustomerApiCalls = new BusinessCustomerApiCalls();
        businessCustomerApiCalls.setApproveCustomerPresenter(this);
        businessCustomerApiCalls.accessCustomer(
                BaseLaunchActivity.getDeviceID(),
                LaunchActivity.getLaunchActivity().getEmail(),
                LaunchActivity.getLaunchActivity().getAuth(),
                customerPriority
        );
    }

    // Once the above API sends the updated JsonQueuedPerson response
    // we update the dataset list object with updated information
    // and notifiy the adapter
    @Override
    public void approveCustomerResponse(JsonQueuedPerson jsonQueuedPerson) {
        int index = -1;

        for (int i = 0; i < jsonQueuedPersonArrayList.size(); i++) {
            JsonQueuedPerson queuedPerson = jsonQueuedPersonArrayList.get(i);
            if (queuedPerson.getToken() == jsonQueuedPerson.getToken()) {
                queuedPerson.setCustomerPriorityLevel(jsonQueuedPerson.getCustomerPriorityLevel());
                queuedPerson.setBusinessCustomerAttributes(jsonQueuedPerson.getBusinessCustomerAttributes());
                index = i;
                break;
            }
        }

        if (index != -1) {
            new CustomToast().showToast(this.context, "Action processed successfully!");
            peopleInQAdapter.notifyItemChanged(index);
        }
    }

    @Override
    public void userRegistered(JsonProfile jsonProfile) {
        userFound(jsonProfile);
    }

    @Override
    public void userFound(JsonProfile jsonProfile) {
        showProgress();
        String phoneNoWithCode = PhoneFormatterUtil.phoneNumberWithCountryCode(jsonProfile.getPhoneRaw(), jsonProfile.getCountryShortName());
        setDispensePresenter();

        JsonBusinessCustomer jsonBusinessCustomer = new JsonBusinessCustomer().setQueueUserId(jsonProfile.getQueueUserId());
        jsonBusinessCustomer
                .setCodeQR(topicsList.get(currentPosition).getCodeQR())
                .setCustomerPhone(phoneNoWithCode);

        manageQueueApiCalls.dispenseTokenWithClientInfo(
                BaseLaunchActivity.getDeviceID(),
                LaunchActivity.getLaunchActivity().getEmail(),
                LaunchActivity.getLaunchActivity().getAuth(),
                jsonBusinessCustomer);
    }

    protected void counterNameEmpty() {
        new CustomToast().showToast(context, context.getString(R.string.error_counter_empty));
        tv_counter_name.performClick();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        if(menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            //noinspection RestrictedApi
            m.setOptionalIconsVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                iv_generate_token.performClick();
                break;
            case R.id.menu_edit:
                tv_counter_name.performClick();
                break;
            case R.id.menu_appointment:
                iv_appointment.performClick();
                break;
            case R.id.menu_q_history:
                iv_queue_history.performClick();
                break;
            case R.id.menu_followup:
                iv_view_followup.performClick();
                break;
            case R.id.menu_product_list:
                iv_product_list.performClick();
                break;
            case R.id.menu_settings:
                iv_settings.performClick();
                break;
            default:
                break;
        }
        return true;

    }
}
