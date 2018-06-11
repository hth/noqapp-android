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
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueModel;
import com.noqapp.android.merchant.model.types.QueueStatusEnum;
import com.noqapp.android.merchant.model.types.QueueUserStateEnum;
import com.noqapp.android.merchant.model.types.UserLevelEnum;
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
import com.noqapp.android.merchant.views.activities.OutOfSequenceActivity;
import com.noqapp.android.merchant.views.activities.OutOfSequenceDialogActivity;
import com.noqapp.android.merchant.views.activities.SettingActivity;
import com.noqapp.android.merchant.views.activities.SettingDialogActivity;
import com.noqapp.android.merchant.views.adapters.PeopleInQAdapter;
import com.noqapp.android.merchant.views.interfaces.AdapterCallback;
import com.noqapp.android.merchant.views.interfaces.ManageQueuePresenter;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;
import com.noqapp.common.beans.ErrorEncounteredJson;
import com.noqapp.common.utils.Formatter;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MerchantDetailFragment extends Fragment implements ManageQueuePresenter, QueuePersonListPresenter,PeopleInQAdapter.PeopleInQAdapterClick {

    private Context context;
    private TextView tv_create_token;
    private Button btn_create_token;
    private ImageView iv_banner;
    private TextView tvcount;
    private PeopleInQAdapter peopleInQAdapter;
    private List<JsonQueuedPerson> jsonQueuedPersonArrayList;

    private RecyclerView rv_queue_people;
    private ProgressBar progressDialog;
    private View itemView;
    private JsonTopic jsonTopic = null;
    private RelativeLayout rl_left;

    private TextView tv_title, tv_serving_customer, tv_total_value, tv_current_value, tv_counter_name, tv_timing, tv_start, tv_next;
    private Chronometer chronometer;
    private int currrentpos = 0;
    private static AdapterCallback mAdapterCallback;
    private Button btn_skip;
    private Button btn_next;
    private Button btn_start;
    private ImageView iv_edit;
    private ImageView iv_out_of_sequence;
    private boolean queueStatusOuter = false;
    private int lastSelectedPos = -1;

    public static void setAdapterCallBack(AdapterCallback adapterCallback) {
        mAdapterCallback = adapterCallback;
    }

    private ArrayList<JsonTopic> topicsList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (null != bundle) {
            topicsList = (ArrayList<JsonTopic>) bundle.getSerializable("jsonMerchant");
            currrentpos = bundle.getInt("position");
        }

        itemView = inflater.inflate(R.layout.viewpager_item, container, false);
        context = getActivity();
        ManageQueueModel.queuePersonListPresenter = this;
        ManageQueueModel.manageQueuePresenter = this;
        jsonTopic = topicsList.get(currrentpos);


        progressDialog = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        rl_left = (RelativeLayout) itemView.findViewById(R.id.rl_left);
        tv_current_value = (TextView) itemView.findViewById(R.id.tv_current_value);
        tv_total_value = (TextView) itemView.findViewById(R.id.tv_total_value);
        tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        tv_serving_customer = (TextView) itemView.findViewById(R.id.tv_serving_customer);
        tv_timing = (TextView) itemView.findViewById(R.id.tv_timing);
        chronometer = (Chronometer) itemView.findViewById(R.id.chronometer);

        rv_queue_people = (RecyclerView) itemView.findViewById(R.id.rv_queue_people);
        tv_counter_name = (TextView) itemView.findViewById(R.id.tv_counter_name);
        rv_queue_people.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rv_queue_people.setLayoutManager(horizontalLayoutManagaer);
        rv_queue_people.setItemAnimator(new DefaultItemAnimator());
        btn_skip = (Button) itemView.findViewById(R.id.btn_skip);
        btn_next = (Button) itemView.findViewById(R.id.btn_next);
        btn_start = (Button) itemView.findViewById(R.id.btn_start);
        TextView tv_deviceId = (TextView) itemView.findViewById(R.id.tv_deviceId);
        tv_deviceId.setText(UserUtils.getDeviceId());
        tv_deviceId.setVisibility(BuildConfig.BUILD_TYPE.equals("debug") ? View.VISIBLE : View.GONE);

        TextView tv_skip = (TextView) itemView.findViewById(R.id.tv_skip);
        tv_next = (TextView) itemView.findViewById(R.id.tv_next);
        tv_start = (TextView) itemView.findViewById(R.id.tv_start);
        iv_edit = (ImageView) itemView.findViewById(R.id.iv_edit);
        iv_out_of_sequence = (ImageView) itemView.findViewById(R.id.iv_out_of_sequence);
        ImageView iv_settings = (ImageView) itemView.findViewById(R.id.iv_settings);
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
        ImageView iv_generate_token = (ImageView) itemView.findViewById(R.id.iv_generate_token);
        iv_generate_token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    showCreateTokenDialog(context, jsonTopic.getCodeQR());
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
        chronometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
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
        if (new AppUtils().isTablet(getActivity())) {
            LaunchActivity.getLaunchActivity().enableDisableBack(false);
        } else {
            LaunchActivity.getLaunchActivity().enableDisableBack(true);
        }
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
                peopleInQAdapter = new PeopleInQAdapter(jsonQueuedPersonArrayList, context,this);
                rv_queue_people.setAdapter(peopleInQAdapter);
            }
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
        if (null != token && null != tv_create_token) {
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
        final EditText edt_counter = (EditText) customDialogView.findViewById(R.id.edt_counter);
        edt_counter.setText(textView.getText().toString().trim());
        edt_counter.setSelection(textView.getText().toString().length());
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        Button btn_update = (Button) customDialogView.findViewById(R.id.btn_update);
        Button btn_cancel = (Button) customDialogView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                edt_counter.setError(null);
                if (edt_counter.getText().toString().equals("")) {
                    edt_counter.setError(mContext.getString(R.string.empty_counter));
                } else {
                    textView.setText(edt_counter.getText().toString());
                    mAdapterCallback.saveCounterNames(codeQR, edt_counter.getText().toString().trim());
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
        ImageView actionbarBack = (ImageView) customDialogView.findViewById(R.id.actionbarBack);
        tv_create_token = (TextView) customDialogView.findViewById(R.id.tvtitle);
        iv_banner = (ImageView) customDialogView.findViewById(R.id.iv_banner);
        tvcount = (TextView) customDialogView.findViewById(R.id.tvcount);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        btn_create_token = (Button) customDialogView.findViewById(R.id.btn_create_token);
        btn_create_token.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (btn_create_token.getText().equals(mContext.getString(R.string.create_token))) {
                    LaunchActivity.getLaunchActivity().progressDialog.show();
                    setPresenter();
                    ManageQueueModel.dispenseToken(
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


    private void setPresenter() {
        ManageQueueModel.manageQueuePresenter = this;
    }

    @Override
    public void queuePersonListResponse(JsonQueuePersonList jsonQueuePersonList) {
        if (null != jsonQueuePersonList) {
            jsonQueuedPersonArrayList = jsonQueuePersonList.getQueuedPeople();
            // Collections.reverse(jsonQueuedPersonArrayList);
            Collections.sort(
                    jsonQueuedPersonArrayList,
                    new Comparator<JsonQueuedPerson>() {
                        public int compare(JsonQueuedPerson lhs, JsonQueuedPerson rhs) {
                            int returnVal = 0;

                            if (lhs.getToken() < rhs.getToken()) {
                                returnVal = -1;
                            } else if (lhs.getToken() < rhs.getToken()) {
                                returnVal = 1;
                            } else if (lhs.getToken() < rhs.getToken()) {
                                returnVal = 0;
                            }
                            return returnVal;
                        }
                    }
            );

            peopleInQAdapter = new PeopleInQAdapter(jsonQueuedPersonArrayList, context,this);
            rv_queue_people.setAdapter(peopleInQAdapter);

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

        rl_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (queueStatus == QueueStatusEnum.N) {
                mAdapterCallback.saveCounterNames(jsonTopic.getCodeQR(), tv_counter_name.getText().toString().trim());
                if (tv_counter_name.getText().toString().trim().equals("")) {
                    Toast.makeText(context, context.getString(R.string.error_counter_empty), Toast.LENGTH_LONG).show();
                } else {
                    Served served = new Served();
                    served.setCodeQR(jsonTopic.getCodeQR());
                    served.setQueueStatus(jsonTopic.getQueueStatus());
                    // served.setQueueUserState(QueueUserStateEnum.N); don't send for time being
                    //served.setServedNumber(jsonTopic.getServingNumber());
                    served.setGoTo(tv_counter_name.getText().toString());
                    if (new AppUtils().isTablet(context)) {
                        Intent in = new Intent(context, OutOfSequenceDialogActivity.class);
                        in.putExtra("codeQR", jsonTopic.getCodeQR());
                        in.putExtra("data", served);
                        in.putExtra("queueStatus", queueStatus == QueueStatusEnum.N);
                        ((Activity) context).startActivityForResult(in, Constants.RESULT_ACQUIRE);
                    } else {
                        Intent in = new Intent(context, OutOfSequenceActivity.class);
                        in.putExtra("codeQR", jsonTopic.getCodeQR());
                        in.putExtra("data", served);
                        in.putExtra("queueStatus", queueStatus == QueueStatusEnum.N);
                        ((Activity) context).startActivityForResult(in, Constants.RESULT_ACQUIRE);
                        ((Activity) context).overridePendingTransition(R.anim.slide_up, R.anim.stay);

                    }
                }
//                } else {
//                    ShowAlertInformation.showThemeDialog(context, "Error", "Please start the queue to avail this facility");
//                }
            }
        });
        iv_out_of_sequence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (queueStatus == QueueStatusEnum.N) {
                mAdapterCallback.saveCounterNames(jsonTopic.getCodeQR(), tv_counter_name.getText().toString().trim());
                if (tv_counter_name.getText().toString().trim().equals("")) {
                    Toast.makeText(context, context.getString(R.string.error_counter_empty), Toast.LENGTH_LONG).show();
                } else {
                    Served served = new Served();
                    served.setCodeQR(jsonTopic.getCodeQR());
                    served.setQueueStatus(jsonTopic.getQueueStatus());
                    // served.setQueueUserState(QueueUserStateEnum.N); don't send for time being
                    //served.setServedNumber(jsonTopic.getServingNumber());
                    served.setGoTo(tv_counter_name.getText().toString());
                    if (new AppUtils().isTablet(context)) {
                        Intent in = new Intent(context, OutOfSequenceDialogActivity.class);
                        in.putExtra("codeQR", jsonTopic.getCodeQR());
                        in.putExtra("data", served);
                        in.putExtra("queueStatus", queueStatus == QueueStatusEnum.N);
                        ((Activity) context).startActivityForResult(in, Constants.RESULT_ACQUIRE);
                    } else {
                        Intent in = new Intent(context, OutOfSequenceActivity.class);
                        in.putExtra("codeQR", jsonTopic.getCodeQR());
                        in.putExtra("data", served);
                        in.putExtra("queueStatus", queueStatus == QueueStatusEnum.N);
                        ((Activity) context).startActivityForResult(in, Constants.RESULT_ACQUIRE);
                        ((Activity) context).overridePendingTransition(R.anim.slide_up, R.anim.stay);

                    }
                }
//                } else {
//                    ShowAlertInformation.showThemeDialog(context, "Error", "Please start the queue to avail this facility");
//                }
            }
        });


        tv_current_value.setText(String.valueOf(jsonTopic.getServingNumber()));
        /* Add to show only remaining people in queue */
        tv_total_value.setText(String.valueOf(jsonTopic.getToken() - jsonTopic.getServingNumber()));
        tv_title.setText(jsonTopic.getDisplayName());
        tv_serving_customer.setText(Html.fromHtml("Serving: " + (StringUtils.isNotBlank(jsonTopic.getCustomerName()) ? "<b>" + jsonTopic.getCustomerName() + "</b> " : context.getString(R.string.name_unavailable))));

        // check parameter to show client is new or has previously visited
        if (jsonTopic.hasClientVisitedThisStore()) {
            tv_serving_customer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.new_client, 0, 0, 0);
        } else {
            tv_serving_customer.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        btn_start.setText(context.getString(R.string.start));

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
//                btn_next.setVisibility(View.VISIBLE);
//                btn_skip.setVisibility(View.VISIBLE);
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
                // btn_next.setVisibility(View.GONE);
                // btn_skip.setVisibility(View.GONE);
                btn_next.setEnabled(false);
                btn_next.setBackgroundResource(R.mipmap.next_inactive);
                btn_skip.setEnabled(false);
                btn_skip.setBackgroundResource(R.mipmap.skip_inactive);
                break;
            case C:
                tv_start.setText(context.getString(R.string.closed));
//                btn_next.setVisibility(View.GONE);
//                btn_skip.setVisibility(View.GONE);
//                btn_start.setVisibility(View.GONE);
                btn_start.setEnabled(false);
                btn_start.setBackgroundResource(R.mipmap.stop_inactive);
                btn_next.setEnabled(false);
                btn_next.setBackgroundResource(R.mipmap.next_inactive);
                btn_skip.setEnabled(false);
                btn_skip.setBackgroundResource(R.mipmap.skip_inactive);
                break;
            case P:
                tv_start.setText(context.getString(R.string.pause));
                // btn_next.setVisibility(View.GONE);
                // btn_skip.setVisibility(View.GONE);
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
                        ManageQueueModel.served(
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
                            ManageQueueModel.served(
                                    LaunchActivity.getLaunchActivity().getDeviceID(),
                                    LaunchActivity.getLaunchActivity().getEmail(),
                                    LaunchActivity.getLaunchActivity().getAuth(),
                                    served);
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
                                        ManageQueueModel.served(
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
                                ManageQueueModel.served(
                                        LaunchActivity.getLaunchActivity().getDeviceID(),
                                        LaunchActivity.getLaunchActivity().getEmail(),
                                        LaunchActivity.getLaunchActivity().getAuth(),
                                        served);
                                // startChronometer(chronometer);
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
            ManageQueueModel.getAllQueuePersonList(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonTopic.getCodeQR());
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    private void resetList() {
        jsonQueuedPersonArrayList = new ArrayList<>();
        peopleInQAdapter = new PeopleInQAdapter(jsonQueuedPersonArrayList, context,this);
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
                        ManageQueueModel.acquire(
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
}
