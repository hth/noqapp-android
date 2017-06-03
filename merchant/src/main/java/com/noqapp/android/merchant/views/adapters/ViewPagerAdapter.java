package com.noqapp.android.merchant.views.adapters;

/**
 * Created by chandra on 4/16/17.
 */

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueModel;
import com.noqapp.android.merchant.model.types.QueueUserStateEnum;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.body.Served;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.fragments.MerchantViewPagerFragment;
import com.noqapp.android.merchant.views.interfaces.AdapterCallback;
import com.noqapp.android.merchant.views.interfaces.ManageQueuePresenter;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter implements ManageQueuePresenter {
    private static AdapterCallback mAdapterCallback;
    private final String TAG = ViewPagerAdapter.class.getSimpleName();
    private Context context;
    private List<JsonTopic> topics;
    private LayoutInflater inflater;

    public ViewPagerAdapter(Context context, List<JsonTopic> topics) {
        this.context = context;
        this.topics = topics;
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
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container, false);
        ManageQueueModel.manageQueuePresenter = this;
        TextView tv_current_value = (TextView) itemView.findViewById(R.id.tv_current_value);
        TextView tv_total_value = (TextView) itemView.findViewById(R.id.tv_total_value);
        TextView tv_title = (TextView) itemView.findViewById(R.id.tv_title);

        final EditText edt_counter_name = (EditText) itemView.findViewById(R.id.edt_counter_name);
        edt_counter_name.setText(LaunchActivity.getLaunchActivity().getCounterName());
        Button btn_skip = (Button) itemView.findViewById(R.id.btn_skip);
        Button btn_next = (Button) itemView.findViewById(R.id.btn_next);
        Button btn_start = (Button) itemView.findViewById(R.id.btn_start);

        final JsonTopic lq = topics.get(position);
        tv_current_value.setText(String.valueOf(lq.getServingNumber()));
        tv_total_value.setText(String.valueOf(lq.getToken()));
        tv_title.setText(lq.getDisplayName());
        final String status = lq.getQueueStatus().getDescription();
        switch (status) {

            case "Start":
                btn_next.setText(context.getString(R.string.start));
                break;
            case "Re-Start":
                btn_next.setText(context.getString(R.string.continues));
                break;
            case "Next":
                btn_next.setText(context.getString(R.string.next));
                break;
            case "Done":
                btn_next.setText(context.getString(R.string.done));
                break;
            case "Closed":
                btn_next.setText(context.getString(R.string.closed));
                break;
        }
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "you clicked", Toast.LENGTH_LONG).show();
            }
        });
        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchActivity.getLaunchActivity().setCounterName(edt_counter_name.getText().toString().trim());
                if (!status.equals("Start") && !status.equals("Done")) {
                    if(edt_counter_name.getText().toString().trim().equals("")){
                        Toast.makeText(context, context.getString(R.string.error_counter_empty), Toast.LENGTH_LONG).show();
                    }else {
                        if (LaunchActivity.getLaunchActivity().isOnline()) {
                            LaunchActivity.getLaunchActivity().progressDialog.show();
                            Served served = new Served();
                            served.setCodeQR(lq.getCodeQR());
                            served.setQueueStatus(lq.getQueueStatus());
                            served.setQueueUserState(QueueUserStateEnum.N);
                            served.setServedNumber(lq.getServingNumber());
                            served.setGoTo(edt_counter_name.getText().toString());
                            ManageQueueModel.served(
                                    LaunchActivity.getLaunchActivity().getDeviceID(),
                                    LaunchActivity.getLaunchActivity().getEmail(),
                                    LaunchActivity.getLaunchActivity().getAuth(),
                                    served);
                        } else {
                            ShowAlertInformation.showNetworkDialog(context);
                        }
                    }
                } else if (status.equals("Start")) {
                    Toast.makeText(context, context.getString(R.string.error_start), Toast.LENGTH_LONG).show();
                } else if (status.equals("Done")) {
                    Toast.makeText(context, context.getString(R.string.error_done), Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchActivity.getLaunchActivity().setCounterName(edt_counter_name.getText().toString().trim());
                if (lq.getToken() == 0) {
                    Toast.makeText(context, context.getString(R.string.error_empty), Toast.LENGTH_LONG).show();
                } else if (lq.getRemaining() == 0 && lq.getServingNumber() == 0) {
                    Toast.makeText(context, context.getString(R.string.error_empty_wait), Toast.LENGTH_LONG).show();
                } else if (status.equals("Done")) {
                    Toast.makeText(context, context.getString(R.string.error_done_next), Toast.LENGTH_LONG).show();
                } else {
                    if(edt_counter_name.getText().toString().trim().equals("")){
                        Toast.makeText(context, context.getString(R.string.error_counter_empty), Toast.LENGTH_LONG).show();
                    }else {

                        if (LaunchActivity.getLaunchActivity().isOnline()) {
                            LaunchActivity.getLaunchActivity().progressDialog.show();
                            Served served = new Served();
                            served.setCodeQR(lq.getCodeQR());
                            served.setQueueStatus(lq.getQueueStatus());
                            served.setQueueUserState(QueueUserStateEnum.S);
                            served.setServedNumber(lq.getServingNumber());
                            served.setGoTo(edt_counter_name.getText().toString());
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
                topics.set(MerchantViewPagerFragment.pagercurrrentpos, jt);
                notifyDataSetChanged();
                //To update merchant list screen
                mAdapterCallback.onMethodCallback(token);
            }
        }
    }

    @Override
    public void manageQueueError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }
}