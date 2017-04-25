package com.noqapp.merchant.views.adapters;

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

import com.noqapp.merchant.R;
import com.noqapp.merchant.model.ManageQueueModel;
import com.noqapp.merchant.model.types.QueueUserStateEnum;
import com.noqapp.merchant.presenter.beans.JsonToken;
import com.noqapp.merchant.presenter.beans.JsonTopic;
import com.noqapp.merchant.presenter.beans.body.Served;
import com.noqapp.merchant.views.activities.LaunchActivity;
import com.noqapp.merchant.views.fragments.MerchantListFragment;
import com.noqapp.merchant.views.interfaces.ManageQueuePresenter;

import org.apache.commons.lang3.StringUtils;

public class ViewPagerAdapter  extends PagerAdapter implements ManageQueuePresenter{
    private static final String TAG = ViewPagerAdapter.class.getSimpleName();

    private Context context;

    private LayoutInflater inflater;
    private int selectedpos=0;

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

        selectedpos=position;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,
                false);
        ManageQueueModel.manageQueuePresenter=this;
        TextView tv_current_value = (TextView) itemView.findViewById(R.id.tv_current_value);
        TextView tv_total_value = (TextView) itemView.findViewById(R.id.tv_total_value);
        TextView tv_title= (TextView) itemView.findViewById(R.id.tv_title);
        Button btn_skip = (Button) itemView.findViewById(R.id.btn_skip);
        Button btn_next = (Button) itemView.findViewById(R.id.btn_next);
        final JsonTopic lq = MerchantListFragment.topics.get(position);
        tv_current_value.setText(String.valueOf(lq.getServingNumber()));
        tv_total_value.setText(String.valueOf(lq.getToken()));
        tv_title.setText(lq.getDisplayName());
        final String status = lq.getQueueStatus().getDescription();
        switch (status){

            case  "Start":
                btn_next.setText("Start");
                break;
            case  "Re-Start":
                btn_next.setText("Continue");
                break;
            case  "Next":
                btn_next.setText("Next");
                break;
            case  "Done":
                btn_next.setText("Done");
                break;
            case  "Closed":
                btn_next.setText("Closed");
                break;
        }



        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!status.equals("Start") && !status.equals("Done")) {
                    LaunchActivity.getLaunchActivity().progressDialog.show();
                    Served served = new Served();
                    served.setCodeQR(lq.getCodeQR());
                    served.setQueueStatus(lq.getQueueStatus());
                    served.setQueueUserState(QueueUserStateEnum.N);
                    served.setServedNumber(lq.getServingNumber());
                    ManageQueueModel.served(LaunchActivity.DID,LaunchActivity.getLaunchActivity().getEmail(),
                            LaunchActivity.getLaunchActivity().getAuth(), served);
                }else if (status.equals("Start")){
                    Toast.makeText(context,"Queue hasn't stated, you can't skip.",Toast.LENGTH_LONG).show();
                }else if (status.equals("Done")){
                    Toast.makeText(context,"No one in the queue. you can't skip",Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lq.getToken()==0) {
                    Toast.makeText(context,"No one in the queue",Toast.LENGTH_LONG).show();
                }else if(lq.getRemaining()==0) {
                    Toast.makeText(context,"No one in the queue. Please wait",Toast.LENGTH_LONG).show();
                }else {
                    LaunchActivity.getLaunchActivity().progressDialog.show();
                    Served served = new Served();
                    served.setCodeQR(lq.getCodeQR());
                    served.setQueueStatus(lq.getQueueStatus());
                    served.setQueueUserState(QueueUserStateEnum.S);
                    served.setServedNumber(lq.getServingNumber());
                    ManageQueueModel.served(LaunchActivity.DID,LaunchActivity.getLaunchActivity().getEmail(),
                            LaunchActivity.getLaunchActivity().getAuth(), served);
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
        if(null!=token){
            JsonTopic jt = MerchantListFragment.topics.get(selectedpos);
            if(token.getCodeQR().equalsIgnoreCase(jt.getCodeQR())) {
                if (StringUtils.isNotBlank(jt.getCustomerName())) {
                    Log.i(TAG, "Show customer name=" + jt.getCustomerName());
                }
                jt.setToken(token.getToken());
                jt.setQueueStatus(token.getQueueStatus());
                jt.setServingNumber(token.getServingNumber());
                MerchantListFragment.topics.set(selectedpos, jt);
                notifyDataSetChanged();
            }

        }
    }

    @Override
    public void manageQueueError() {

    }
}