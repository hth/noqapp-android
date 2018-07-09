package com.noqapp.android.merchant.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueModel;
import com.noqapp.android.merchant.model.types.QueueStatusEnum;
import com.noqapp.android.merchant.model.types.QueueUserStateEnum;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.body.Served;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.LoginActivity;
import com.noqapp.android.merchant.views.activities.RegistrationActivity;
import com.noqapp.android.merchant.views.activities.SettingActivity;
import com.noqapp.android.merchant.views.activities.SettingDialogActivity;
import com.noqapp.android.merchant.views.adapters.PeopleInQAdapter;
import com.noqapp.android.merchant.views.interfaces.AdapterCallback;
import com.noqapp.android.merchant.views.interfaces.DispenseTokenPresenter;
import com.noqapp.android.merchant.views.interfaces.ManageQueuePresenter;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;
import com.noqapp.common.beans.ErrorEncounteredJson;
import com.noqapp.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.common.model.types.UserLevelEnum;
import com.noqapp.common.utils.Formatter;
import com.noqapp.common.utils.PhoneFormatterUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MerchantDetailFragment extends Fragment implements ManageQueuePresenter,DispenseTokenPresenter, QueuePersonListPresenter, PeopleInQAdapter.PeopleInQAdapterClick,RegistrationActivity.RegisterCallBack,LoginActivity.LoginCallBack {

    private Context context;
    private TextView tv_create_token;
    private Button btn_create_token;
    private ImageView iv_banner;
    private TextView tvcount;
    private PeopleInQAdapter peopleInQAdapter;
    private List<JsonQueuedPerson> jsonQueuedPersonArrayList= new ArrayList<>();
    private EditText edt_mobile;
    private RecyclerView rv_queue_people;
    private ProgressBar progressDialog;
    private View itemView;
    private JsonTopic jsonTopic = null;

    private TextView tv_title, tv_total_value, tv_current_value, tv_counter_name, tv_timing, tv_start, tv_next;
    private Chronometer chronometer;
    private int currrentpos = 0;
    private static AdapterCallback mAdapterCallback;
    private Button btn_skip;
    private Button btn_next;
    private Button btn_start;
    private ImageView iv_edit;
    private boolean queueStatusOuter = false;
    private int lastSelectedPos = -1;
    private LinearLayoutManager horizontalLayoutManagaer;
    private ManageQueueModel manageQueueModel;
    private ArrayList<JsonTopic> topicsList;

    public static void setAdapterCallBack(AdapterCallback adapterCallback) {
        mAdapterCallback = adapterCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (null != bundle) {
            topicsList = (ArrayList<JsonTopic>) bundle.getSerializable("jsonMerchant");
            currrentpos = bundle.getInt("position");
        }

        itemView = inflater.inflate(R.layout.viewpager_item, container, false);
        context = getActivity();
        manageQueueModel = new ManageQueueModel();
        manageQueueModel.setManageQueuePresenter(this);
        jsonTopic = topicsList.get(currrentpos);


        progressDialog = itemView.findViewById(R.id.progress_bar);
        tv_current_value = itemView.findViewById(R.id.tv_current_value);
        tv_total_value = itemView.findViewById(R.id.tv_total_value);
        tv_title = itemView.findViewById(R.id.tv_title);
        tv_timing = itemView.findViewById(R.id.tv_timing);
        chronometer = itemView.findViewById(R.id.chronometer);

        rv_queue_people = itemView.findViewById(R.id.rv_queue_people);
        tv_counter_name = itemView.findViewById(R.id.tv_counter_name);
        rv_queue_people.setHasFixedSize(true);
        horizontalLayoutManagaer
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rv_queue_people.setLayoutManager(horizontalLayoutManagaer);
        rv_queue_people.setItemAnimator(new DefaultItemAnimator());
        btn_skip = itemView.findViewById(R.id.btn_skip);
        btn_next = itemView.findViewById(R.id.btn_next);
        btn_start = itemView.findViewById(R.id.btn_start);
        TextView tv_deviceId =itemView.findViewById(R.id.tv_deviceId);
        tv_deviceId.setText(UserUtils.getDeviceId());
        tv_deviceId.setVisibility(BuildConfig.BUILD_TYPE.equals("debug") ? View.VISIBLE : View.GONE);

        tv_next = itemView.findViewById(R.id.tv_next);
        tv_start = itemView.findViewById(R.id.tv_start);
        iv_edit = itemView.findViewById(R.id.iv_edit);
        ImageView iv_settings = itemView.findViewById(R.id.iv_settings);
        iv_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new AppUtils().isTablet(context)) {
                    Intent in = new Intent(context, SettingDialogActivity.class);
                    in.putExtra("codeQR", jsonTopic.getCodeQR());
                    ((Activity) context).startActivityForResult(in, Constants.RESULT_SETTING);
                } else {
                    Intent in = new Intent(context, SettingActivity.class);
                    in.putExtra("codeQR", jsonTopic.getCodeQR());
                    ((Activity) context).startActivityForResult(in, Constants.RESULT_SETTING);
                    ((Activity) context).overridePendingTransition(R.anim.slide_up, R.anim.stay);

                }
            }
        });
        ImageView iv_generate_token = itemView.findViewById(R.id.iv_generate_token);
        iv_generate_token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    showCreateTokenDialogWithMobile(context, jsonTopic.getCodeQR());
                } else {
                    ShowAlertInformation.showNetworkDialog(context);
                }
            }
        });
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCounterEditDialog(context, tv_counter_name, jsonTopic.getCodeQR());
            }
        });
        tv_counter_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCounterEditDialog(context, tv_counter_name, jsonTopic.getCodeQR());
            }
        });
        updateUI();
        return itemView;
    }


    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.screen_queue_detail));
        LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.VISIBLE);
        LaunchActivity.getLaunchActivity().enableDisableBack(false);

    }

    public void updateListData(final ArrayList<JsonTopic> jsonTopics) {
        try {
            topicsList = jsonTopics;
            resetList();
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPage(int pos) {
        //update UI
        currrentpos = pos;
        jsonTopic = topicsList.get(pos);
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        resetList();
        updateUI();

    }

    @Override
    public void manageQueueResponse(JsonToken token) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        dismissProgress();
        if (null != token) {
            JsonTopic jt = topicsList.get(currrentpos);
            if (token.getCodeQR().equalsIgnoreCase(jt.getCodeQR())) {
                if (StringUtils.isNotBlank(jt.getCustomerName())) {
                    Log.i(MerchantDetailFragment.class.getSimpleName(), "Show customer name=" + jt.getCustomerName());
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
    public void manageQueueError(ErrorEncounteredJson errorEncounteredJson) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        if (null != errorEncounteredJson && errorEncounteredJson.getSystemErrorCode().equals("350")) {
            Toast.makeText(context, getString(R.string.error_client_just_acquired), Toast.LENGTH_LONG).show();
            if (lastSelectedPos >= 0) {
                jsonQueuedPersonArrayList.get(lastSelectedPos).setServerDeviceId("XXX-XXXX-XXXX");
                lastSelectedPos = -1;
                peopleInQAdapter = new PeopleInQAdapter(jsonQueuedPersonArrayList, context, this, jsonTopic.getCodeQR());
                rv_queue_people.setAdapter(peopleInQAdapter);
            }
        }
    }

    @Override
    public void dispenseTokenError(ErrorEncounteredJson errorEncounteredJson) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        dismissProgress();
        if(errorEncounteredJson.getSystemErrorCode().equalsIgnoreCase(MobileSystemErrorCodeEnum.USER_NOT_FOUND.getCode())){
            Toast.makeText(context,errorEncounteredJson.getReason(),Toast.LENGTH_LONG).show();
            Intent in = new Intent(getActivity(), LoginActivity.class);
            in.putExtra("phone_no",edt_mobile.getText().toString());
            context.startActivity(in);
            RegistrationActivity.registerCallBack = this;
            LoginActivity.loginCallBack = this;
        }
    }

    @Override
    public void authenticationFailure(int errorCode) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        if (errorCode == Constants.INVALID_CREDENTIAL) {
            LaunchActivity.getLaunchActivity().clearLoginData(true);
        }
    }

    @Override
    public void dispenseTokenResponse(JsonToken token) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        dismissProgress();
        if (null != token && null != tv_create_token) {
            if(null != edt_mobile)
                edt_mobile.setText("");
            switch (token.getQueueStatus()) {
                case C:
                    tv_create_token.setText("Queue is closed. Cannot generate token.");
                    btn_create_token.setClickable(true);
                    btn_create_token.setText(context.getString(R.string.queue_closed));
                    break;
                case D:
                case N:
                case P:
                case R:
                case S:
                    tv_create_token.setText("The generated token no is ");
                    btn_create_token.setText(context.getString(R.string.done));
                    iv_banner.setBackgroundResource(R.drawable.after_token_generated);
                    tvcount.setText(String.valueOf(token.getToken()));
                    tvcount.setVisibility(View.VISIBLE);
                    btn_create_token.setClickable(true);
                    for (int i = 0; i < jsonQueuedPersonArrayList.size(); i++) {
                        JsonQueuedPerson jt = jsonQueuedPersonArrayList.get(i);
                        if (jt.getToken()==token.getToken()) {
                            Toast.makeText(context,"User already in Queue",Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        progressDialog.setVisibility(View.VISIBLE);
                        manageQueueModel.getAllQueuePersonList(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonTopic.getCodeQR());
                    } else {
                        ShowAlertInformation.showNetworkDialog(getActivity());
                    }
                    break;
                default:
                    Log.e(MerchantDetailFragment.class.getSimpleName(), "Reached un-reachable condition");
                    throw new RuntimeException("Reached unsupported condition");
            }
        }
    }

    private void showCounterEditDialog(final Context mContext, final TextView textView, final String codeQR) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_edit_counter, null, false);
        final AutoCompleteTextView actv_counter =  customDialogView.findViewById(R.id.actv_counter);
        final ArrayList<String> names = LaunchActivity.getLaunchActivity().getCounterNames();
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                (mContext, android.R.layout.simple_list_item_1, names);
        actv_counter.setAdapter(adapter1);
        actv_counter.setThreshold(1);
        AppUtils.setAutoCompleteText(actv_counter,textView.getText().toString().trim());
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Button btn_update = customDialogView.findViewById(R.id.btn_update);
        Button btn_cancel = customDialogView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actv_counter.setError(null);
                if (actv_counter.getText().toString().equals("")) {
                    actv_counter.setError(mContext.getString(R.string.empty_counter));
                } else {
                    new AppUtils().hideKeyBoard(getActivity());
                    textView.setText(actv_counter.getText().toString());
                    mAdapterCallback.saveCounterNames(codeQR, actv_counter.getText().toString().trim());
                        if (!names.contains(actv_counter.getText().toString())) {
                             names.add(actv_counter.getText().toString());
                             LaunchActivity.getLaunchActivity().setCounterNames(names);
                        }
                    mAlertDialog.dismiss();
                }
            }
        });
        mAlertDialog.show();
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


    private void showCreateTokenDialogWithMobile(final Context mContext, final String codeQR) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_create_token_with_mobile, null, false);
        ImageView actionbarBack = customDialogView.findViewById(R.id.actionbarBack);
        tv_create_token = customDialogView.findViewById(R.id.tvtitle);
        iv_banner = customDialogView.findViewById(R.id.iv_banner);
        tvcount = customDialogView.findViewById(R.id.tvcount);
        edt_mobile = customDialogView.findViewById(R.id.edt_mobile);
        final EditText edt_id = customDialogView.findViewById(R.id.edt_id);
        final RadioGroup rg_user_id = customDialogView.findViewById(R.id.rg_user_id);
        final RadioButton rb_mobile = customDialogView.findViewById(R.id.rb_mobile);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        rg_user_id.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_mobile) {
                    edt_mobile.setVisibility(View.VISIBLE);
                    edt_id.setVisibility(View.GONE);
                    edt_id.setText("");
                } else {
                    edt_id.setVisibility(View.VISIBLE);
                    edt_mobile.setVisibility(View.GONE);
                    edt_mobile.setText("");
                }
            }
        });
        btn_create_token = customDialogView.findViewById(R.id.btn_create_token);
        btn_create_token.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean isValid = true;
                edt_mobile.setError(null);
                edt_id.setError(null);
                new AppUtils().hideKeyBoard(getActivity());
                // get selected radio button from radioGroup
                int selectedId = rg_user_id.getCheckedRadioButtonId();
                if(selectedId == R.id.rb_mobile){
                    if (TextUtils.isEmpty(edt_mobile.getText())) {
                        edt_mobile.setError(getString(R.string.error_mobile_blank));
                        isValid = false;
                    }
                }else{
                    if (TextUtils.isEmpty(edt_id.getText())) {
                        edt_id.setError(getString(R.string.error_customer_id));
                        isValid = false;
                    }
                }


                if(isValid) {
                if (btn_create_token.getText().equals(mContext.getString(R.string.create_token))) {
                    LaunchActivity.getLaunchActivity().progressDialog.show();
                    setDispensePresenter();
                    String phone = "";
                    String cid = "";
                    if(rb_mobile.isChecked()){
                        edt_id.setText("");
                        phone = "91"+edt_mobile.getText().toString();
                    }else{
                        cid = edt_id.getText().toString();
                        edt_mobile.setText("");// set blank so that wrong phone no not pass to login screen
                    }
                    manageQueueModel.dispenseTokenWithClientInfo(
                            LaunchActivity.getLaunchActivity().getDeviceID(),
                            LaunchActivity.getLaunchActivity().getEmail(),
                            LaunchActivity.getLaunchActivity().getAuth(),
                            new JsonBusinessCustomerLookup().setCodeQR(codeQR).setCustomerPhone(phone).setBusinessCustomerId(cid));
                    btn_create_token.setClickable(false);
                    mAlertDialog.dismiss();
                } else {
                    mAlertDialog.dismiss();
                }
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


    private void setPresenter() {
       manageQueueModel.setManageQueuePresenter(this);
    }

    private void setDispensePresenter() {
        manageQueueModel.setDispenseTokenPresenter(this);
    }

    @Override
    public void queuePersonListResponse(JsonQueuePersonList jsonQueuePersonList) {
        if (null != jsonQueuePersonList) {
            jsonQueuedPersonArrayList = jsonQueuePersonList.getQueuedPeople();
            Collections.sort(
                    jsonQueuedPersonArrayList,
                    new Comparator<JsonQueuedPerson>() {
                        public int compare(JsonQueuedPerson lhs, JsonQueuedPerson rhs) {
                           return Integer.compare(lhs.getToken(), rhs.getToken());
                        }
                    }
            );
            peopleInQAdapter = new PeopleInQAdapter(jsonQueuedPersonArrayList, context, this, jsonTopic.getCodeQR(),jsonTopic.getServingNumber(),jsonTopic.getQueueStatus());
            rv_queue_people.setAdapter(peopleInQAdapter);
            if(jsonTopic.getServingNumber() > 0)
             rv_queue_people.getLayoutManager().scrollToPosition(jsonTopic.getServingNumber()-1);

        }
        dismissProgress();
    }

    @Override
    public void queuePersonListError() {
        dismissProgress();
    }


    private void dismissProgress() {
        if (null != progressDialog) {
            progressDialog.setVisibility(View.GONE);
        }
    }


    private void updateUI() {

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

        btn_start.setText(context.getString(R.string.start));
        btn_start.setBackgroundResource(R.mipmap.start);

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
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
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
                Log.e(MerchantDetailFragment.class.getSimpleName(), "Reached un-supported condition");
        }

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapterCallback.saveCounterNames(jsonTopic.getCodeQR(), tv_counter_name.getText().toString().trim());
                if (tv_counter_name.getText().toString().trim().equals("")) {
                    Toast.makeText(context, context.getString(R.string.error_counter_empty), Toast.LENGTH_LONG).show();
                } else {
                    chronometer.stop();
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        LaunchActivity.getLaunchActivity().progressDialog.show();
                        Served served = new Served();
                        served.setCodeQR(jsonTopic.getCodeQR());
                        served.setQueueStatus(jsonTopic.getQueueStatus());
                        served.setQueueUserState(QueueUserStateEnum.S);
                        served.setServedNumber(jsonTopic.getServingNumber());
                        served.setGoTo(tv_counter_name.getText().toString());
                        setPresenter();
                        manageQueueModel.served(
                                LaunchActivity.getLaunchActivity().getDeviceID(),
                                LaunchActivity.getLaunchActivity().getEmail(),
                                LaunchActivity.getLaunchActivity().getAuth(),
                                served);
                    } else {
                        ShowAlertInformation.showNetworkDialog(context);
                    }
                }
            }
        });
        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapterCallback.saveCounterNames(jsonTopic.getCodeQR(), tv_counter_name.getText().toString().trim());
                if (queueStatus != QueueStatusEnum.S && queueStatus != QueueStatusEnum.D) {
                    if (tv_counter_name.getText().toString().trim().equals("")) {
                        Toast.makeText(context, context.getString(R.string.error_counter_empty), Toast.LENGTH_LONG).show();
                    } else {
                        if (LaunchActivity.getLaunchActivity().isOnline()) {
                            LaunchActivity.getLaunchActivity().progressDialog.show();
                            Served served = new Served();
                            served.setCodeQR(jsonTopic.getCodeQR());
                            served.setQueueStatus(jsonTopic.getQueueStatus());
                            served.setQueueUserState(QueueUserStateEnum.N);
                            served.setServedNumber(jsonTopic.getServingNumber());
                            served.setGoTo(tv_counter_name.getText().toString());
                            setPresenter();
                            manageQueueModel.served(
                                    LaunchActivity.getLaunchActivity().getDeviceID(),
                                    LaunchActivity.getLaunchActivity().getEmail(),
                                    LaunchActivity.getLaunchActivity().getAuth(),
                                    served);
                            chronometer.stop();
                            chronometer.setBase(SystemClock.elapsedRealtime());
                        } else {
                            ShowAlertInformation.showNetworkDialog(context);
                        }
                    }
                } else if (queueStatus == QueueStatusEnum.S) {
                    Toast.makeText(context, context.getString(R.string.error_start), Toast.LENGTH_LONG).show();
                } else if (queueStatus == QueueStatusEnum.D) {
                    Toast.makeText(context, context.getString(R.string.error_done), Toast.LENGTH_LONG).show();
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
                                        Served served = new Served();
                                        served.setCodeQR(jsonTopic.getCodeQR());
                                        served.setQueueUserState(QueueUserStateEnum.S);
                                        /* send QueueStatusEnum P for pause state */
                                        served.setQueueStatus(QueueStatusEnum.P);
                                        served.setServedNumber(jsonTopic.getServingNumber());
                                        served.setGoTo(tv_counter_name.getText().toString());
                                        setPresenter();
                                        manageQueueModel.served(
                                                LaunchActivity.getLaunchActivity().getDeviceID(),
                                                LaunchActivity.getLaunchActivity().getEmail(),
                                                LaunchActivity.getLaunchActivity().getAuth(),
                                                served);
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
                                Served served = new Served();
                                served.setCodeQR(jsonTopic.getCodeQR());
                                served.setQueueUserState(QueueUserStateEnum.S);
                                /* send QueueStatusEnum as it is for other than pause state */
                                served.setQueueStatus(jsonTopic.getQueueStatus());
                                served.setServedNumber(jsonTopic.getServingNumber());
                                served.setGoTo(tv_counter_name.getText().toString());
                                setPresenter();
                                manageQueueModel.served(
                                        LaunchActivity.getLaunchActivity().getDeviceID(),
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
            }
        });

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.setVisibility(View.VISIBLE);
            manageQueueModel.setQueuePersonListPresenter(this);
            manageQueueModel.getAllQueuePersonList(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonTopic.getCodeQR());
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    private void resetList() {
        jsonQueuedPersonArrayList = new ArrayList<>();
        peopleInQAdapter = new PeopleInQAdapter(jsonQueuedPersonArrayList, context, this, jsonTopic.getCodeQR());
        rv_queue_people.setAdapter(peopleInQAdapter);
    }

    @Override
    public void PeopleInQClick(int position) {
        if (queueStatusOuter) {
            if (jsonQueuedPersonArrayList.get(position).getQueueUserState() == QueueUserStateEnum.A) {
                Toast.makeText(context, getString(R.string.error_client_left_queue), Toast.LENGTH_LONG).show();
            } else {
                if (TextUtils.isEmpty(jsonQueuedPersonArrayList.get(position).getServerDeviceId())) {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        progressDialog.setVisibility(View.VISIBLE);
                        lastSelectedPos = position;
                        Served served = new Served();
                        served.setCodeQR(jsonTopic.getCodeQR());
                        served.setQueueStatus(jsonTopic.getQueueStatus());
                        // served.setQueueUserState(QueueUserStateEnum.N); don't send for time being
                        //served.setServedNumber(jsonTopic.getServingNumber());
                        served.setGoTo(tv_counter_name.getText().toString());
                        served.setServedNumber(jsonQueuedPersonArrayList.get(position).getToken());
                        manageQueueModel.acquire(
                                LaunchActivity.getLaunchActivity().getDeviceID(),
                                LaunchActivity.getLaunchActivity().getEmail(),
                                LaunchActivity.getLaunchActivity().getAuth(),
                                served);
                    } else {
                        ShowAlertInformation.showNetworkDialog(getActivity());
                    }
                } else if (jsonQueuedPersonArrayList.get(position).getServerDeviceId().equals(UserUtils.getDeviceId())) {
                    Toast.makeText(context, getString(R.string.error_client_acquired_by_you), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, getString(R.string.error_client_acquired), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            ShowAlertInformation.showThemeDialog(context, "Error", "Please start the queue to avail this facility");
        }

    }

    @Override
    public void passPhoneNo(String phoneNo, String countryShortName) {
        LaunchActivity.getLaunchActivity().progressDialog.show();
        String phoneNoWithCode = PhoneFormatterUtil.phoneNumberWithCountryCode(phoneNo,  countryShortName);
        setDispensePresenter();
        manageQueueModel.dispenseTokenWithClientInfo(
                LaunchActivity.getLaunchActivity().getDeviceID(),
                LaunchActivity.getLaunchActivity().getEmail(),
                LaunchActivity.getLaunchActivity().getAuth(),
                new JsonBusinessCustomerLookup().setCodeQR(topicsList.get(currrentpos).getCodeQR()).setCustomerPhone(phoneNoWithCode));
    }
}
