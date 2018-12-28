package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.JsonNameDatePair;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPersonTV;
import com.noqapp.android.merchant.presenter.beans.JsonVigyaapanTV;
import com.noqapp.android.merchant.utils.AppUtils;

import com.google.android.gms.cast.CastPresentation;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PresentationService extends CastRemoteDisplayLocalService {
    private DetailPresentation castPresentation;
    private TopicAndQueueTV topicAndQueueTV;
    private int pos = 0;
    private int url_pos = 0;
    private int no_of_q = 0;
    private int sequence = 1;
    private int buffer_size = 0;
    private int text_list_pos = 0;
    private List<String> urlList = new ArrayList<>();
    private List<String> textList = new ArrayList<>();
    private JsonVigyaapanTV jsonVigyaapanTV;

    @Override
    public void onCreatePresentation(Display display) {
        dismissPresentation();
        castPresentation = new DetailPresentation(this, display);
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

    public void setVigyaapan(JsonVigyaapanTV jsonVigyaapanTV, int no_of_q) {
        this.jsonVigyaapanTV = jsonVigyaapanTV;
        this.no_of_q = no_of_q;
        if (null != jsonVigyaapanTV) {
            switch (jsonVigyaapanTV.getVigyaapanType()) {
                case MV:
                    if (null != jsonVigyaapanTV.getImageUrls() && jsonVigyaapanTV.getImageUrls().size() > 0) {
                        urlList = jsonVigyaapanTV.getImageUrls();
                        buffer_size = urlList.size();
                    }
                    break;
                case PP:
                    buffer_size = 1;
                    break;
                default:
            }
        }

    }

    public class DetailPresentation extends CastPresentation {
        private ImageView image, image1, iv_advertisement, iv_profile;
        private TextView title, tv_timing, tv_degree, title1, tv_timing1, tv_degree1, tv_doctor_name, tv_doctor_category, tv_doctor_degree, tv_about_doctor, tv_info1;
        private LinearLayout ll_list, ll_profile, ll_no_list;
        private Context context;

        public DetailPresentation(Context context, Display display) {
            super(context, display);
            this.context = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.presentation_detail);
            image = findViewById(R.id.ad_image);
            image1 = findViewById(R.id.ad_image1);
            iv_advertisement = findViewById(R.id.iv_advertisement);
            iv_profile = findViewById(R.id.iv_profile);
            title = findViewById(R.id.ad_title);
            tv_timing = findViewById(R.id.tv_timing);
            tv_degree = findViewById(R.id.tv_degree);

            title1 = findViewById(R.id.ad_title1);
            tv_timing1 = findViewById(R.id.tv_timing1);
            tv_degree1 = findViewById(R.id.tv_degree1);
            tv_doctor_name = findViewById(R.id.tv_doctor_name);
            tv_doctor_category = findViewById(R.id.tv_doctor_category);
            tv_doctor_degree = findViewById(R.id.tv_doctor_degree);
            tv_about_doctor = findViewById(R.id.tv_about_doctor);
            tv_info1 = findViewById(R.id.tv_info1);
            ll_list = findViewById(R.id.ll_list);
            ll_no_list = findViewById(R.id.ll_no_list);
            ll_profile = findViewById(R.id.ll_profile);
            textList.add("Doctor is now available on <font color='#8c1515'><b>NoQApp</b></font>.");
            textList.add("Save time. Book appointment online on <font color='#8c1515'><b>NoQApp</b></font>.");
            textList.add("Forgot your medical file. Now medical records are securely available on <font color='#8c1515'><b>NoQApp</b></font>");
            textList.add("See all your medical records online on <font color='#8c1515'><b>NoQApp</b></font>.");
            textList.add("Get real time appointment updates on <font color='#8c1515'><b>NoQApp</b></font>.");
            textList.add("Front desk can book your appointment just by your phone number.");
            textList.add("Download <font color='#8c1515'><b>NoQApp</b></font> from Google Play Store.");
            updateDetail(topicAndQueueTV);
        }

        public void updateDetail(TopicAndQueueTV topicAndQueueTV) {
            if (null == topicAndQueueTV || null == topicAndQueueTV.getJsonQueueTV()) {
                if (null != jsonVigyaapanTV)
                    switch (jsonVigyaapanTV.getVigyaapanType()) {
                        case MV: {
                            ll_profile.setVisibility(View.GONE);
                            if (url_pos < urlList.size()) {
                                Picasso.with(getContext()).load(urlList.get(url_pos)).into(iv_advertisement);
                                iv_advertisement.setVisibility(View.VISIBLE);
                                ++url_pos;
                            } else {
                                url_pos = 0;
                                Picasso.with(getContext()).load(urlList.get(url_pos)).into(iv_advertisement);
                                iv_advertisement.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                        case PP:
                            ll_profile.setVisibility(View.VISIBLE);
                            Picasso.with(getContext()).load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + jsonVigyaapanTV.getJsonProfessionalProfileTV().getProfileImage()).into(iv_profile);
                            tv_doctor_name.setText(jsonVigyaapanTV.getJsonProfessionalProfileTV().getName());
                            tv_doctor_category.setText(jsonVigyaapanTV.getJsonProfessionalProfileTV().getProfessionType());
                            tv_doctor_degree.setText(getSelectedData(jsonVigyaapanTV.getJsonProfessionalProfileTV().getEducation()));
                            tv_about_doctor.setText(jsonVigyaapanTV.getJsonProfessionalProfileTV().getAboutMe());
                            break;
                        default:
                    }
            } else {
                if (sequence > no_of_q) {
                    if (null != jsonVigyaapanTV)
                        switch (jsonVigyaapanTV.getVigyaapanType()) {
                            case MV: {
                                ll_profile.setVisibility(View.GONE);
                                if (url_pos < urlList.size()) {
                                    Picasso.with(getContext()).load(urlList.get(url_pos)).into(iv_advertisement);
                                    iv_advertisement.setVisibility(View.VISIBLE);
                                    ++url_pos;
                                } else {
                                    iv_advertisement.setVisibility(View.GONE);
                                    url_pos = 0;
                                }
                            }
                            break;
                            case PP:
                                ll_profile.setVisibility(View.VISIBLE);
                                Picasso.with(getContext()).load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + jsonVigyaapanTV.getJsonProfessionalProfileTV().getProfileImage()).into(iv_profile);
                                tv_doctor_name.setText("Dr. " + jsonVigyaapanTV.getJsonProfessionalProfileTV().getName());
                                tv_doctor_category.setText(jsonVigyaapanTV.getJsonProfessionalProfileTV().getProfessionType());
                                tv_doctor_degree.setText(getSelectedData(jsonVigyaapanTV.getJsonProfessionalProfileTV().getEducation()));
                                tv_about_doctor.setText(jsonVigyaapanTV.getJsonProfessionalProfileTV().getAboutMe());
                                break;
                            default:
                        }

                } else {
                    iv_advertisement.setVisibility(View.GONE);
                    ll_profile.setVisibility(View.GONE);
                }

                if (sequence >= no_of_q + buffer_size) {
                    sequence = 0;
                }
                sequence++;
                if (null != topicAndQueueTV.getJsonQueueTV()) {
                    if (TextUtils.isEmpty(topicAndQueueTV.getJsonQueueTV().getProfileImage())) {
                        Picasso.with(context).load(R.drawable.profile_tv).into(image);
                        Picasso.with(context).load(R.drawable.profile_tv).into(image1);
                    } else {
                        Picasso.with(context).load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + topicAndQueueTV.getJsonQueueTV().getProfileImage()).into(image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(context).load(R.drawable.profile_tv).into(image);
                            }
                        });
                        Picasso.with(context).load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + topicAndQueueTV.getJsonQueueTV().getProfileImage()).into(image1, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(context).load(R.drawable.profile_tv).into(image1);
                            }
                        });
                    }
                    title.setText(topicAndQueueTV.getJsonTopic().getDisplayName());
                    if (!TextUtils.isEmpty(new AppUtils().getCompleteEducation(topicAndQueueTV.getJsonQueueTV().getEducation())))
                        tv_degree.setText(" ( " + new AppUtils().getCompleteEducation(topicAndQueueTV.getJsonQueueTV().getEducation()) + " ) ");
                    else
                        tv_degree.setText("");
                    tv_timing.setText("Timing: " + Formatter.convertMilitaryTo12HourFormat(topicAndQueueTV.getJsonTopic().getHour().getStartHour())
                            + " - " + Formatter.convertMilitaryTo12HourFormat(topicAndQueueTV.getJsonTopic().getHour().getEndHour()));

                    title1.setText(title.getText().toString());
                    tv_degree1.setText(tv_degree.getText().toString());
                    tv_timing1.setText(tv_timing.getText().toString());
                    if (text_list_pos >= textList.size()) {
                        text_list_pos = 0;
                    }
                    tv_info1.setText(Html.fromHtml(textList.get(text_list_pos)));
                    ++text_list_pos;

                    if (pos % 2 == 0)
                        ll_list.setBackground(ContextCompat.getDrawable(context, R.mipmap.temp_2));//"#85b8cb"));
                    else
                        ll_list.setBackground(ContextCompat.getDrawable(context, R.mipmap.pp_bg));   //Color.parseColor("#4a87ab"));
                    ll_list.removeAllViews();
                    LayoutInflater inflater = LayoutInflater.from(context);
                    if (null != topicAndQueueTV.getJsonQueueTV().getJsonQueuedPersonTVList()) {

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
                            TextView textView = customView.findViewById(R.id.tv_name);
                            TextView tv_seq = customView.findViewById(R.id.tv_seq);
                            TextView tv_mobile = customView.findViewById(R.id.tv_mobile);
                            tv_seq.setText(String.valueOf((data.get(i).getToken())));
                            textView.setText(data.get(i).getCustomerName());
                            String phoneNo = data.get(i).getCustomerPhone();
                            tv_mobile.setText(new AppUtils().hidePhoneNumberWithX(phoneNo));
                            ll_list.addView(customView);
                        }
                    }
                    if (ll_list.getChildCount() > 0)
                        ll_no_list.setVisibility(View.GONE);
                    else
                        ll_no_list.setVisibility(View.VISIBLE);
                }
            }

        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }
    }

    private String getSelectedData(List<JsonNameDatePair> temp) {
        String data = "";
        for (int i = 0; i < temp.size(); i++) {
            data += temp.get(i).getName() + ", ";
        }
        if (data.endsWith(", "))
            data = data.substring(0, data.length() - 2);
        return data;
    }

}