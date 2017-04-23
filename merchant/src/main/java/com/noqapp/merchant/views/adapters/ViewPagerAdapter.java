package com.noqapp.merchant.views.adapters;

/**
 * Created by chandra on 4/16/17.
 */
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import com.noqapp.merchant.R;
import com.noqapp.merchant.model.ManageQueueModel;
import com.noqapp.merchant.model.types.QueueUserStateEnum;
import com.noqapp.merchant.presenter.beans.JsonTopic;
import com.noqapp.merchant.presenter.beans.body.Served;
import com.noqapp.merchant.views.activities.LaunchActivity;

import java.util.List;


public class ViewPagerAdapter  extends PagerAdapter {

    private Context context;
    private List<JsonTopic> arrayList;
    private LayoutInflater inflater;

    public ViewPagerAdapter(Context context, List<JsonTopic> arrayList) {
        this.context = context;
        this.arrayList = arrayList;

    }

    @Override
    public int getCount() {
        return arrayList.size();
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

        TextView tv_current_value = (TextView) itemView.findViewById(R.id.tv_current_value);
        TextView tv_total_value = (TextView) itemView.findViewById(R.id.tv_total_value);
        TextView tv_title= (TextView) itemView.findViewById(R.id.tv_title);
        Button btn_skip = (Button) itemView.findViewById(R.id.btn_skip);
        Button btn_next = (Button) itemView.findViewById(R.id.btn_next);
        final JsonTopic lq = arrayList.get(position);
        tv_current_value.setText(String.valueOf(lq.getServingNumber()));
        tv_total_value.setText(String.valueOf(lq.getToken()));
        tv_title.setText(lq.getDisplayName());
        String status = lq.getQueueStatus().getDescription();
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
                LaunchActivity.getLaunchActivity().progressDialog.show();
                Served served=new Served();
                served.setCodeQR(lq.getCodeQR());
                served.setQueueStatus(lq.getQueueStatus());
                served.setQueueUserState(QueueUserStateEnum.N);
                served.setServedNumber(lq.getServingNumber());

                ManageQueueModel.served("123213","b@r.com",
                        "$2a$15$ed3VSsc5x367CNiwQ3fKsemHSZUr.D3EVjHVjZ2cBTySc/l7gwPua",served);
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchActivity.getLaunchActivity().progressDialog.show();
                Served served=new Served();
                served.setCodeQR(lq.getCodeQR());
                served.setQueueStatus(lq.getQueueStatus());
                served.setQueueUserState(QueueUserStateEnum.S);
                served.setServedNumber(lq.getServingNumber());
                ManageQueueModel.served("123213","b@r.com",
                        "$2a$15$ed3VSsc5x367CNiwQ3fKsemHSZUr.D3EVjHVjZ2cBTySc/l7gwPua",served);
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
}