package com.noqapp.android.merchant.views.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPersonTV;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.MarqueeSharedPreference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DetailFragment extends Fragment {
    private static final String ARG_LIST_DATA = "data";
    private TopicAndQueueTV topicAndQueueTV;
    private TextView title, tv_timing, tv_degree, title1, tv_timing1, tv_degree1, tv_info1;
    private ImageView image, image1;
    private LinearLayout ll_list, ll_no_list;
    private FrameLayout fl_left;
    public static DetailFragment newInstance(TopicAndQueueTV ad) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LIST_DATA, ad);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (null != getActivity())
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (getArguments() != null) {
            topicAndQueueTV = (TopicAndQueueTV) getArguments().getSerializable(ARG_LIST_DATA);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        image = view.findViewById(R.id.ad_image);
        title = view.findViewById(R.id.ad_title);
        tv_timing = view.findViewById(R.id.tv_timing);
        tv_degree = view.findViewById(R.id.tv_degree);
        image1 = view.findViewById(R.id.ad_image1);
        title1 = view.findViewById(R.id.ad_title1);
        tv_timing1 = view.findViewById(R.id.tv_timing1);
        tv_degree1 = view.findViewById(R.id.tv_degree1);
        tv_info1 = view.findViewById(R.id.tv_info1);
        ll_list = view.findViewById(R.id.ll_list);
        ll_no_list = view.findViewById(R.id.ll_no_list);
        fl_left = view.findViewById(R.id.fl_left);
        TextView tv_marquee = view.findViewById(R.id.tv_marquee);
        MarqueeSharedPreference.init(getContext().getApplicationContext());
        List<String>marqueeList = MarqueeSharedPreference.getMarquee();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < marqueeList.size(); i++) {
            str.append( getString(R.string.bullet) + " " + marqueeList.get(i) + " \t");
        }
        tv_marquee.setText(str.toString());
        tv_marquee.setMarqueeRepeatLimit(-1);
        tv_marquee.setSelected(true);
        if (LaunchActivity.isTvSplitViewEnable()) {
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.65f);
            LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.35f);
            fl_left.setLayoutParams(lp1);
            ll_no_list.setLayoutParams(lp0);
        }else{
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.0f);
            fl_left.setLayoutParams(lp1);
            ll_no_list.setLayoutParams(lp0);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != topicAndQueueTV && null != topicAndQueueTV.getJsonQueueTV()) {
            if (TextUtils.isEmpty(topicAndQueueTV.getJsonQueueTV().getProfileImage())) {
                Picasso.get().load(R.drawable.profile_tv).into(image);
                Picasso.get().load(R.drawable.profile_tv).into(image1);
            } else {
                Picasso.get().load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + topicAndQueueTV.getJsonQueueTV().getProfileImage()).into(image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(R.drawable.profile_tv).into(image);
                    }
                });
                Picasso.get().load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + topicAndQueueTV.getJsonQueueTV().getProfileImage()).into(image1, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(R.drawable.profile_tv).into(image1);
                    }
                });
            }
            title.setText(topicAndQueueTV.getJsonTopic().getDisplayName());
            tv_degree.setText(" ( " + AppUtils.getCompleteEducation(topicAndQueueTV.getJsonQueueTV().getEducation()) + " ) ");
            tv_timing.setText("Timing: " + Formatter.convertMilitaryTo12HourFormat(topicAndQueueTV.getJsonTopic().getHour().getStartHour())
                    + " - " + Formatter.convertMilitaryTo12HourFormat(topicAndQueueTV.getJsonTopic().getHour().getEndHour()));

            title1.setText(title.getText().toString());
            tv_degree1.setText(tv_degree.getText().toString());
            tv_timing1.setText(tv_timing.getText().toString());
            tv_info1.setText("");

            ll_list.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            if (null != topicAndQueueTV.getJsonQueueTV()) {

                List<JsonQueuedPersonTV> data = topicAndQueueTV.getJsonQueueTV().getJsonQueuedPersonTVList();
                Collections.sort(
                        data,
                        new Comparator<JsonQueuedPersonTV>() {
                            public int compare(JsonQueuedPersonTV lhs, JsonQueuedPersonTV rhs) {
                                return Integer.compare(lhs.getToken(), rhs.getToken());
                            }
                        }
                );
                for (int i = 0; i < data.size(); i++) {
                    View customView = inflater.inflate(R.layout.lay_text, null, false);
                    View cardview = customView.findViewById(R.id.cardview);
                    TextView textView = customView.findViewById(R.id.tv_name);
                    TextView tv_seq = customView.findViewById(R.id.tv_seq);
                    TextView tv_mobile = customView.findViewById(R.id.tv_mobile);
                    tv_seq.setText(String.valueOf((data.get(i).getDisplayToken())));
                    textView.setText(data.get(i).getCustomerName());
                    String phoneNo = data.get(i).getCustomerPhone();
                    tv_mobile.setText(AppUtils.hidePhoneNumberWithX(phoneNo));

                    if (topicAndQueueTV.getJsonTopic().getServingNumber() == data.get(i).getToken()) {
                        tv_mobile.setText("It's your turn");
                        Animation startAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.show_anim);
                        cardview.startAnimation(startAnimation);
                    } else {
                        Animation startAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.remove_anim);
                        cardview.startAnimation(startAnimation);
                    }

                    ll_list.addView(customView);
                }
            }

            ll_no_list.setVisibility(View.VISIBLE);


        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
}
