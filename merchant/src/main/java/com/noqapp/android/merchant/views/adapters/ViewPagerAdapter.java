package com.noqapp.android.merchant.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueModel;
import com.noqapp.android.merchant.model.types.QueueStatusEnum;
import com.noqapp.android.merchant.model.types.QueueUserStateEnum;
import com.noqapp.android.merchant.model.types.UserLevelEnum;
import com.noqapp.android.merchant.presenter.beans.ErrorEncounteredJson;
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
import com.noqapp.android.merchant.views.fragments.MerchantViewPagerFragment;
import com.noqapp.android.merchant.views.interfaces.AdapterCallback;
import com.noqapp.android.merchant.views.interfaces.ManageQueuePresenter;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;


/**
 * User: chandra
 * Date: 4/16/17 4:02 PM
 */
public class ViewPagerAdapter extends PagerAdapter implements ManageQueuePresenter {
    private static AdapterCallback mAdapterCallback;
    private final String TAG = ViewPagerAdapter.class.getSimpleName();
    private Context context;
    private List<JsonTopic> topics;
    private LayoutInflater inflater;
    private TextView tv_create_token;
    private Button btn_create_token;
    private ImageView iv_banner;
    private TextView tvcount;
    private HashMap<String,String> mHashmap = new HashMap<>();

    public ViewPagerAdapter(Context context, List<JsonTopic> topics) {
        this.context = context;
        this.topics = topics;

        String strOutput = LaunchActivity.getLaunchActivity().getCounterName();
        Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        Gson gson = new Gson();
        if (StringUtils.isBlank(strOutput)) {
            mHashmap.clear();
        } else {
            mHashmap = gson.fromJson(strOutput, type);
        }
        if (mHashmap.size() == 0) {
            for (int i = 0; i < topics.size(); i++) {
                mHashmap.put(topics.get(i).getCodeQR(), "");
            }
        }
    }

    public static void setAdapterCallBack(AdapterCallback adapterCallback) {
        mAdapterCallback = adapterCallback;
    }

    @Override
    public int getCount() {
        return topics.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container, false);
        ManageQueueModel.manageQueuePresenter = this;
        final JsonTopic lq = topics.get(position);
        final QueueStatusEnum queueStatus = lq.getQueueStatus();
        RelativeLayout rl_left = (RelativeLayout) itemView.findViewById(R.id.rl_left);
        TextView tv_current_value = (TextView) itemView.findViewById(R.id.tv_current_value);
        TextView tv_total_value = (TextView) itemView.findViewById(R.id.tv_total_value);
        TextView tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        TextView tv_serving_customer = (TextView) itemView.findViewById(R.id.tv_serving_customer);

        final TextView tv_counter_name = (TextView) itemView.findViewById(R.id.tv_counter_name);

        // update counter name
        String cName = mHashmap.get(lq.getCodeQR());
        if (TextUtils.isEmpty(cName))
            tv_counter_name.setText("");
        else
            tv_counter_name.setText(cName);

        //
        Button btn_skip = (Button) itemView.findViewById(R.id.btn_skip);
        Button btn_next = (Button) itemView.findViewById(R.id.btn_next);
        final Button btn_start = (Button) itemView.findViewById(R.id.btn_start);
        TextView tv_deviceId = (TextView) itemView.findViewById(R.id.tv_deviceId);
        tv_deviceId.setText(UserUtils.getDeviceId());
        tv_deviceId.setVisibility(BuildConfig.BUILD_TYPE.equals("debug") ? View.VISIBLE : View.GONE);
        ImageView iv_settings = (ImageView) itemView.findViewById(R.id.iv_settings);
        iv_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new AppUtils().isTablet(context)) {
                    Intent in = new Intent(context, SettingDialogActivity.class);
                    in.putExtra("codeQR", lq.getCodeQR());
                    ((Activity) context).startActivityForResult(in, Constants.RESULT_SETTING);
                } else {
                    Intent in = new Intent(context, SettingActivity.class);
                    in.putExtra("codeQR", lq.getCodeQR());
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
                    showCreateTokenDialog(context, lq.getCodeQR());
                } else {
                    ShowAlertInformation.showNetworkDialog(context);
                }
            }
        });
        rl_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (queueStatus == QueueStatusEnum.N) {
                    saveCounterNames(lq.getCodeQR(),tv_counter_name.getText().toString().trim());
                    if (tv_counter_name.getText().toString().trim().equals("")) {
                        Toast.makeText(context, context.getString(R.string.error_counter_empty), Toast.LENGTH_LONG).show();
                    } else {
                        Served served = new Served();
                        served.setCodeQR(lq.getCodeQR());
                        served.setQueueStatus(lq.getQueueStatus());
                        // served.setQueueUserState(QueueUserStateEnum.N); don't send for time being
                        //served.setServedNumber(lq.getServingNumber());
                        served.setGoTo(tv_counter_name.getText().toString());
                        if (new AppUtils().isTablet(context)) {
                            Intent in = new Intent(context, OutOfSequenceDialogActivity.class);
                            in.putExtra("codeQR", lq.getCodeQR());
                            in.putExtra("data", served);
                            ((Activity) context).startActivityForResult(in, Constants.RESULT_ACQUIRE);
                        } else {
                            Intent in = new Intent(context, OutOfSequenceActivity.class);
                            in.putExtra("codeQR", lq.getCodeQR());
                            in.putExtra("data", served);
                            ((Activity) context).startActivityForResult(in, Constants.RESULT_ACQUIRE);
                            ((Activity) context).overridePendingTransition(R.anim.slide_up, R.anim.stay);

                        }
                    }
                } else {
                    ShowAlertInformation.showThemeDialog(context, "Error", "Please start the queue to avail this facility");
                }
            }
        });
        ImageView iv_out_of_sequence = (ImageView) itemView.findViewById(R.id.iv_out_of_sequence);
        iv_out_of_sequence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (queueStatus == QueueStatusEnum.N) {
                    saveCounterNames(lq.getCodeQR(),tv_counter_name.getText().toString().trim());
                    if (tv_counter_name.getText().toString().trim().equals("")) {
                        Toast.makeText(context, context.getString(R.string.error_counter_empty), Toast.LENGTH_LONG).show();
                    } else {
                        Served served = new Served();
                        served.setCodeQR(lq.getCodeQR());
                        served.setQueueStatus(lq.getQueueStatus());
                        // served.setQueueUserState(QueueUserStateEnum.N); don't send for time being
                        //served.setServedNumber(lq.getServingNumber());
                        served.setGoTo(tv_counter_name.getText().toString());
                        if (new AppUtils().isTablet(context)) {
                            Intent in = new Intent(context, OutOfSequenceDialogActivity.class);
                            in.putExtra("codeQR", lq.getCodeQR());
                            in.putExtra("data", served);
                            ((Activity) context).startActivityForResult(in, Constants.RESULT_ACQUIRE);
                        } else {
                            Intent in = new Intent(context, OutOfSequenceActivity.class);
                            in.putExtra("codeQR", lq.getCodeQR());
                            in.putExtra("data", served);
                            ((Activity) context).startActivityForResult(in, Constants.RESULT_ACQUIRE);
                            ((Activity) context).overridePendingTransition(R.anim.slide_up, R.anim.stay);

                        }
                    }
                } else {
                    ShowAlertInformation.showThemeDialog(context, "Error", "Please start the queue to avail this facility");
                }
            }
        });


        TextView tv_skip = (TextView) itemView.findViewById(R.id.tv_skip);
        TextView tv_next = (TextView) itemView.findViewById(R.id.tv_next);
        final TextView tv_start = (TextView) itemView.findViewById(R.id.tv_start);
        ImageView iv_edit = (ImageView) itemView.findViewById(R.id.iv_edit);

        tv_current_value.setText(String.valueOf(lq.getServingNumber()));
        /* Add to show only remaining people in queue */
        tv_total_value.setText(String.valueOf(lq.getToken() - lq.getServingNumber()));
        tv_title.setText(lq.getDisplayName());
        tv_serving_customer.setText(Html.fromHtml("Serving: " + (StringUtils.isNotBlank(lq.getCustomerName()) ? "<b>" + lq.getCustomerName() + "</b> " : context.getString(R.string.name_unavailable))));
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
                Log.e(TAG, "Reached un-supported condition");
        }

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCounterNames(lq.getCodeQR(),tv_counter_name.getText().toString().trim());
                if (tv_counter_name.getText().toString().trim().equals("")) {
                    Toast.makeText(context, context.getString(R.string.error_counter_empty), Toast.LENGTH_LONG).show();
                } else {

                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        LaunchActivity.getLaunchActivity().progressDialog.show();
                        Served served = new Served();
                        served.setCodeQR(lq.getCodeQR());
                        served.setQueueStatus(lq.getQueueStatus());
                        served.setQueueUserState(QueueUserStateEnum.S);
                        served.setServedNumber(lq.getServingNumber());
                        served.setGoTo(tv_counter_name.getText().toString());
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
                saveCounterNames(lq.getCodeQR(),tv_counter_name.getText().toString().trim());
                if (queueStatus != QueueStatusEnum.S && queueStatus != QueueStatusEnum.D) {
                    if (tv_counter_name.getText().toString().trim().equals("")) {
                        Toast.makeText(context, context.getString(R.string.error_counter_empty), Toast.LENGTH_LONG).show();
                    } else {
                        if (LaunchActivity.getLaunchActivity().isOnline()) {
                            LaunchActivity.getLaunchActivity().progressDialog.show();
                            Served served = new Served();
                            served.setCodeQR(lq.getCodeQR());
                            served.setQueueStatus(lq.getQueueStatus());
                            served.setQueueUserState(QueueUserStateEnum.N);
                            served.setServedNumber(lq.getServingNumber());
                            served.setGoTo(tv_counter_name.getText().toString());
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
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCounterEditDialog(context, tv_counter_name,lq.getCodeQR());
            }
        });
        tv_counter_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCounterEditDialog(context, tv_counter_name,lq.getCodeQR());
            }
        });
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCounterNames(lq.getCodeQR(),tv_counter_name.getText().toString().trim());
                if (lq.getToken() == 0) {
                    Toast.makeText(context, context.getString(R.string.error_empty), Toast.LENGTH_LONG).show();
                } else if (lq.getRemaining() == 0 && lq.getServingNumber() == 0) {
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
                            builder.setMessage("Have you completed serving " + String.valueOf(lq.getServingNumber()));
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                                        LaunchActivity.getLaunchActivity().progressDialog.show();
                                        Served served = new Served();
                                        served.setCodeQR(lq.getCodeQR());
                                        served.setQueueUserState(QueueUserStateEnum.S);
                                        /*  send   QueueStatusEnum P for pause state */
                                        served.setQueueStatus(QueueStatusEnum.P);
                                        served.setServedNumber(lq.getServingNumber());
                                        served.setGoTo(tv_counter_name.getText().toString());
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
                                served.setCodeQR(lq.getCodeQR());
                                served.setQueueUserState(QueueUserStateEnum.S);
                                  /*  send   QueueStatusEnum as it is for other than pause state */
                                served.setQueueStatus(lq.getQueueStatus());
                                served.setServedNumber(lq.getServingNumber());
                                served.setGoTo(tv_counter_name.getText().toString());
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
                }
            }
        });

        ((ViewPager) container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public void manageQueueResponse(JsonToken token) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        if (null != token) {
            JsonTopic jt = topics.get(MerchantViewPagerFragment.pagercurrrentpos);
            if (token.getCodeQR().equalsIgnoreCase(jt.getCodeQR())) {
                if (StringUtils.isNotBlank(jt.getCustomerName())) {
                    Log.i(TAG, "Show customer name=" + jt.getCustomerName());
                }
                jt.setToken(token.getToken());
                jt.setQueueStatus(token.getQueueStatus());
                jt.setServingNumber(token.getServingNumber());
                jt.setCustomerName(token.getCustomerName());
                topics.set(MerchantViewPagerFragment.pagercurrrentpos, jt);
                notifyDataSetChanged();
                //To update merchant list screen
                mAdapterCallback.onMethodCallback(token);
            }
        }
    }

    @Override
    public void manageQueueError(ErrorEncounteredJson errorEncounteredJson) {
        LaunchActivity.getLaunchActivity().dismissProgress();
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
            switch(token.getQueueStatus()) {
                case C:
                    tv_create_token.setText("Queue is closed. Cannot generate token.");
                    btn_create_token.setClickable(true);
                    btn_create_token.setText(context.getString(R.string.closed_queue));
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
                    Log.e(TAG, "Reached un-reachable condition");
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
                    saveCounterNames(codeQR,edt_counter.getText().toString().trim());
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
        ImageView actionbarBack =  (ImageView) customDialogView.findViewById(R.id.actionbarBack);
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

    private void saveCounterNames(String codeQR, String name){
            mHashmap.put(codeQR, name);
            LaunchActivity.getLaunchActivity().setCounterName(mHashmap);

    }
}