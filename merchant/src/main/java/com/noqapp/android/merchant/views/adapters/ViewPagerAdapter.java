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
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.merchant.model.types.QueueUserStateEnum;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.body.Served;
import com.noqapp.android.merchant.views.fragments.MerchantViewPagerFragment;
import com.noqapp.android.merchant.views.interfaces.ManageQueuePresenter;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.model.ManageQueueModel;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.fragments.MerchantListFragment;

import org.apache.commons.lang3.StringUtils;

public class ViewPagerAdapter extends PagerAdapter implements ManageQueuePresenter {
    private static final String TAG = ViewPagerAdapter.class.getSimpleName();

    private Context context;

    private LayoutInflater inflater;


    public ViewPagerAdapter(Context context) {
        this.context = context;


    }

    @Override
    public int getCount() {
        return MerchantListFragment.topics.size();
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



        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,
                false);
        ManageQueueModel.manageQueuePresenter = this;
        TextView tv_current_value = (TextView) itemView.findViewById(R.id.tv_current_value);
        TextView tv_total_value = (TextView) itemView.findViewById(R.id.tv_total_value);
        TextView tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        Button btn_skip = (Button) itemView.findViewById(R.id.btn_skip);
        Button btn_next = (Button) itemView.findViewById(R.id.btn_next);
        final JsonTopic lq = MerchantListFragment.topics.get(position);
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


        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!status.equals("Start") && !status.equals("Done")) {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        LaunchActivity.getLaunchActivity().progressDialog.show();


                        Served served = new Served();
                        served.setCodeQR(lq.getCodeQR());
                        served.setQueueStatus(lq.getQueueStatus());
                        served.setQueueUserState(QueueUserStateEnum.N);
                        served.setServedNumber(lq.getServingNumber());
                        ManageQueueModel.served(LaunchActivity.getLaunchActivity().getDeviceID(), LaunchActivity.getLaunchActivity().getEmail(),
                                LaunchActivity.getLaunchActivity().getAuth(), served);
                    } else {
                        ShowAlertInformation.showNetworkDialog(context);
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
                if (lq.getToken() == 0) {
                    Toast.makeText(context, context.getString(R.string.error_empty), Toast.LENGTH_LONG).show();
                } else if (lq.getRemaining() == 0 && lq.getServingNumber()== 0) {
                    Toast.makeText(context, context.getString(R.string.error_empty_wait), Toast.LENGTH_LONG).show();
                } else if (status.equals("Done")) {
                    Toast.makeText(context, context.getString(R.string.error_done_next), Toast.LENGTH_LONG).show();
                } else {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        LaunchActivity.getLaunchActivity().progressDialog.show();
                        Served served = new Served();
                        served.setCodeQR(lq.getCodeQR());
                        served.setQueueStatus(lq.getQueueStatus());
                        served.setQueueUserState(QueueUserStateEnum.S);
                        served.setServedNumber(lq.getServingNumber());
                        ManageQueueModel.served(LaunchActivity.getLaunchActivity().getDeviceID(), LaunchActivity.getLaunchActivity().getEmail(),
                                LaunchActivity.getLaunchActivity().getAuth(), served);
                    } else {
                        ShowAlertInformation.showNetworkDialog(context);
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
            JsonTopic jt = MerchantListFragment.topics.get(MerchantViewPagerFragment.pagercurrrentpos);
            if (token.getCodeQR().equalsIgnoreCase(jt.getCodeQR())) {
                if (StringUtils.isNotBlank(jt.getCustomerName())) {
                    Log.i(TAG, "Show customer name=" + jt.getCustomerName());
                }
                jt.setToken(token.getToken());
                jt.setQueueStatus(token.getQueueStatus());
                jt.setServingNumber(token.getServingNumber());
                MerchantListFragment.topics.set(MerchantViewPagerFragment.pagercurrrentpos, jt);
                notifyDataSetChanged();
            }

        }
    }

    @Override
    public void manageQueueError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }
}