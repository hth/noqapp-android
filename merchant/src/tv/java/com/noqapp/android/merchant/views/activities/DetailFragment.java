package com.noqapp.android.merchant.views.activities;


import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;

import com.squareup.picasso.Picasso;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DetailFragment extends Fragment {
    private static final String ARG_LIST_DATA = "data";
    private TopicAndQueueTV topicAndQueueTV;
    private TextView title, tv_timing,tv_degree;
    private ImageView image, iv_banner, iv_banner1;
    private LinearLayout ll_list;

    public static DetailFragment newInstance(TopicAndQueueTV ad) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LIST_DATA, ad);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            topicAndQueueTV = (TopicAndQueueTV) getArguments().getSerializable(ARG_LIST_DATA);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_detail, container, false);
        image = fragmentView.findViewById(R.id.ad_image);
        iv_banner = fragmentView.findViewById(R.id.iv_banner);
        iv_banner1 = fragmentView.findViewById(R.id.iv_banner1);
        title = fragmentView.findViewById(R.id.ad_title);
        tv_timing = fragmentView.findViewById(R.id.tv_timing);
        tv_degree = fragmentView.findViewById(R.id.tv_degree);
        ll_list = fragmentView.findViewById(R.id.ll_list);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (TextUtils.isEmpty(topicAndQueueTV.getJsonQueueTV().getProfileImage())) {
            Picasso.with(getActivity()).load(R.drawable.profile_tv).into(image);
        } else {
            Picasso.with(getActivity()).load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + topicAndQueueTV.getJsonQueueTV().getProfileImage()).into(image);
        }
       // Picasso.with(getActivity()).load("http://businessplaces.in/wp-content/uploads/2017/07/ssdhospital-logo-2.jpg").into(iv_banner);
       // Picasso.with(getActivity()).load("https://steamuserimages-a.akamaihd.net/ugc/824566056082911413/D6CF5FF8C8E7C3C693E70B02C55CD2CB0E87D740/").into(iv_banner1);
        title.setText(topicAndQueueTV.getJsonTopic().getDisplayName());
        tv_degree.setText(" ( "+new AppUtils().getCompleteEducation(topicAndQueueTV.getJsonQueueTV().getEducation())+" ) ");
        tv_timing.setText("Timing: " + Formatter.convertMilitaryTo12HourFormat(topicAndQueueTV.getJsonTopic().getHour().getStartHour())
                + " - " + Formatter.convertMilitaryTo12HourFormat(topicAndQueueTV.getJsonTopic().getHour().getEndHour()));
        ll_list.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        if(null != topicAndQueueTV.getJsonQueueTV())
        for (int i = 0; i < topicAndQueueTV.getJsonQueueTV().getJsonQueuedPersonTVList().size(); i++) {
            View customView = inflater.inflate(R.layout.lay_text, null, false);
            TextView textView = customView.findViewById(R.id.tv_name);
            TextView tv_mobile = customView.findViewById(R.id.tv_mobile);
            textView.setText("( "+(i+1)+" ) "+ topicAndQueueTV.getJsonQueueTV().getJsonQueuedPersonTVList().get(i).getCustomerName());
            String phoneNo = topicAndQueueTV.getJsonQueueTV().getJsonQueuedPersonTVList().get(i).getCustomerPhone();
            if (null != phoneNo && phoneNo.length() >= 10) {
                String number = phoneNo.substring(0, 4) + "XXXXXX" + phoneNo.substring(phoneNo.length() - 3, phoneNo.length());
                tv_mobile.setText(number);
            }else{
                tv_mobile.setText("");
            }
            ll_list.addView(customView);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
