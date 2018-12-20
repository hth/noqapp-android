package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;

import com.google.android.gms.cast.CastPresentation;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PresentationService extends CastRemoteDisplayLocalService {
    private DetailPresentation castPresentation;
    private TopicAndQueueTV topicAndQueueTV;
    private int pos = 0;
    private int url_pos = 10;
    private List<String> urlList = new ArrayList<>();

    @Override
    public void onCreatePresentation(Display display) {
        dismissPresentation();
        castPresentation = new DetailPresentation(this, display);
        urlList.add("http://worldartsme.com/images/exercise-motivation-clipart-1.jpg");
        urlList.add("https://pbs.twimg.com/media/C6QQND6WUAAjhA6.jpg");
        urlList.add("https://i.pinimg.com/originals/2c/2c/da/2c2cda9b80b0a71c2ea2f7d360122164.jpg");
        urlList.add("https://i.pinimg.com/originals/81/56/11/815611f15aea20932f3cbf8040daa6c0.jpg");
        urlList.add("https://i.pinimg.com/originals/31/93/ba/3193bab4ab76549e0df8d60e2f402b08.jpg");
        urlList.add("http://binsbox.com/images/8-dental-tips-to-keep-smiling/8-dental-tips-to-keep-smiling0.jpg");
        urlList.add("https://i.pinimg.com/736x/6e/75/34/6e7534e0882e3e543419027bb00effb5--exercise--fitness-health-fitness.jpg");
        urlList.add("https://i.pinimg.com/originals/81/8e/f3/818ef38057ca7aef2040421238f5a90c.jpg");
        urlList.add("https://cdn.shopify.com/s/files/1/0366/1469/files/exercise_motivation_large.jpg?4441298293954673424");
        urlList.add("https://image.shutterstock.com/image-vector/motivational-quote-about-workout-fitness-260nw-755027176.jpg");
        try {
            castPresentation.show();
        } catch (WindowManager.InvalidDisplayException ex) {
            dismissPresentation();
        }
    }

    @Override
    public void onDismissPresentation() {
        dismissPresentation();
        topicAndQueueTV = null;
    }

    private void dismissPresentation() {
        if (castPresentation != null) {
            castPresentation.dismiss();
            castPresentation = null;
        }
    }

    public void setTopicAndQueueTV(TopicAndQueueTV ad, int position) {
        pos = position;
        topicAndQueueTV = ad;
        if (castPresentation != null) {
            castPresentation.updateDetail(ad);
        }
    }

    public class DetailPresentation extends CastPresentation {
        public ImageView image, iv_banner, iv_banner1, iv_advertisement;
        private TextView title, tv_timing, tv_degree;
        public LinearLayout ll_list;
        public Context context;

        public DetailPresentation(Context context, Display display) {
            super(context, display);
            this.context = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.presentation_detail);
            image = findViewById(R.id.ad_image);
            iv_banner = findViewById(R.id.iv_banner);
            iv_banner1 = findViewById(R.id.iv_banner1);
            iv_advertisement = findViewById(R.id.iv_advertisement);
            title = findViewById(R.id.ad_title);
            tv_timing = findViewById(R.id.tv_timing);
            tv_degree = findViewById(R.id.tv_degree);
            ll_list = findViewById(R.id.ll_list);
            updateDetail(topicAndQueueTV);
        }

        public void updateDetail(TopicAndQueueTV topicAndQueueTV) {
            if (TextUtils.isEmpty(topicAndQueueTV.getJsonQueueTV().getProfileImage())) {
                Picasso.with(context).load(R.drawable.profile_tv).into(image);
            } else {
                Picasso.with(context).load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + topicAndQueueTV.getJsonQueueTV().getProfileImage()).into(image,new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(context).load(R.drawable.profile_tv).into(image);
                    }
                });
            }
            // Picasso.with(getContext()).load("http://businessplaces.in/wp-content/uploads/2017/07/ssdhospital-logo-2.jpg").into(iv_banner);
            // Picasso.with(getContext()).load("https://steamuserimages-a.akamaihd.net/ugc/824566056082911413/D6CF5FF8C8E7C3C693E70B02C55CD2CB0E87D740/").into(iv_banner1);
            if (url_pos < urlList.size()) {
                Picasso.with(getContext()).load(urlList.get(url_pos)).into(iv_advertisement);
                iv_advertisement.setVisibility(View.VISIBLE);
            } else {
                iv_advertisement.setVisibility(View.GONE);
            }
            ++url_pos;
            if (url_pos == 20)
                url_pos = 0;
            title.setText(topicAndQueueTV.getJsonTopic().getDisplayName());
            tv_degree.setText(" ( " + new AppUtils().getCompleteEducation(topicAndQueueTV.getJsonQueueTV().getEducation()) + " ) ");
            tv_timing.setText("Timing: " + Formatter.convertMilitaryTo12HourFormat(topicAndQueueTV.getJsonTopic().getHour().getStartHour())
                    + " - " + Formatter.convertMilitaryTo12HourFormat(topicAndQueueTV.getJsonTopic().getHour().getEndHour()));
            if (pos % 2 == 0)
                ll_list.setBackgroundColor(Color.DKGRAY);
            else
                ll_list.setBackgroundColor(Color.LTGRAY);
            ll_list.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(context);
            if (null != topicAndQueueTV.getJsonQueueTV().getJsonQueuedPersonTVList())
                for (int i = 0; i < topicAndQueueTV.getJsonQueueTV().getJsonQueuedPersonTVList().size(); i++) {
                    View customView = inflater.inflate(R.layout.lay_text, null, false);
                    TextView textView = customView.findViewById(R.id.tv_name);
                    TextView tv_seq = customView.findViewById(R.id.tv_seq);
                    TextView tv_mobile = customView.findViewById(R.id.tv_mobile);
                    tv_seq.setText(String.valueOf((i + 1)));
                    textView.setText(topicAndQueueTV.getJsonQueueTV().getJsonQueuedPersonTVList().get(i).getCustomerName());
                    String phoneNo = topicAndQueueTV.getJsonQueueTV().getJsonQueuedPersonTVList().get(i).getCustomerPhone();
                    if (null != phoneNo && phoneNo.length() >= 10) {
                        String number = phoneNo.substring(0, 4) + "XXXXX" + phoneNo.substring(phoneNo.length() - 3, phoneNo.length());
                        tv_mobile.setText(number);
                    } else {
                        tv_mobile.setText("");
                    }
                    ll_list.addView(customView);
                }

        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }
    }
}