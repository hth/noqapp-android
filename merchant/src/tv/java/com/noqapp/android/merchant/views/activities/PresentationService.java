package com.noqapp.android.merchant.views.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.cast.CastPresentation;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonAdvertisement;
import com.noqapp.android.common.beans.JsonAdvertisementList;
import com.noqapp.android.common.beans.JsonProfessionalProfileTV;
import com.noqapp.android.common.beans.JsonProfessionalProfileTVList;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
import com.noqapp.android.common.presenter.AdvertisementPresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.AdvertisementApiCalls;
import com.noqapp.android.merchant.model.ClientInQueueApiCalls;
import com.noqapp.android.merchant.presenter.ClientInQueuePresenter;
import com.noqapp.android.merchant.presenter.ProfessionalProfilesPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonQueueTV;
import com.noqapp.android.merchant.presenter.beans.JsonQueueTVList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPersonTV;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.body.QueueDetail;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.MarqueeSharedPreference;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.customviews.ScrollTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PresentationService extends CastRemoteDisplayLocalService implements
        ClientInQueuePresenter, AdvertisementPresenter, ProfessionalProfilesPresenter,
        MarqueeSharedPreference.OnPreferenceChangeListener {
    private DetailPresentation castPresentation;
    private int image_list_size = 0;
    private int url_pos = 0;
    private int profile_pos = 0;
    private int no_of_q = 0;
    private int sequence = -1;
    private List<String> urlList = new ArrayList<>();
    private JsonAdvertisement jsonAdvertisement_profile, jsonAdvertisement_images;
    private JsonProfessionalProfileTVList jsonProfessionalProfileTVList;
    private HashMap<String, JsonTopic> topicHashMap = new HashMap<>();
    private List<TopicAndQueueTV> topicAndQueueTVList = new ArrayList<>();
    private FetchLatestData fetchLatestData;
    private AsyncTaskRunner asyncTaskRunner;
    private final String LOOP_TIME = "9";
    private final String SERVER_LOOP_TIME = "5";
    private final int MILLI_SECONDS = 1000;
    private final int SECONDS = LaunchActivity.getTvRefreshTime() * 60;
    private final int MINUTE = SECONDS * MILLI_SECONDS;
    private boolean callAdvertisement = true;
    private boolean callFirstTime = true;
    private int timerCount = 0;
    private ScrollTextView scrolltext;
    private boolean isMarqueeInit = false;


    private List<String> marqueeList = new ArrayList<>();

    @Override
    public void onCreatePresentation(Display display) {
        dismissPresentation();
        castPresentation = new DetailPresentation(this, display);
        MarqueeSharedPreference.init(getApplicationContext());
        MarqueeSharedPreference.onPreferenceChangeListener = this;
        try {
            marqueeList = MarqueeSharedPreference.getMarquee();
            castPresentation.show();
        } catch (WindowManager.InvalidDisplayException ex) {
            dismissPresentation();
        }
    }

    @Override
    public void onDismissPresentation() {
        dismissPresentation();
    }

    private void dismissPresentation() {
        if (castPresentation != null) {
            castPresentation.dismiss();
            castPresentation = null;
        }
    }

    public void setTopicAndQueueTV(List<TopicAndQueueTV> topicAndQueueTVListTemp, boolean notificationUpdate) {
        if (notificationUpdate) {
            if (null != fetchLatestData) {
                fetchLatestData.cancel(true);
                fetchLatestData = null;
            }
        }
        setTopicAndQueueTV(topicAndQueueTVListTemp);
    }

    public void setTopicAndQueueTV(List<TopicAndQueueTV> topicAndQueueTVListTemp) {
        this.topicAndQueueTVList = topicAndQueueTVListTemp;
        if (castPresentation != null) {
            no_of_q = topicAndQueueTVList.size();
            castPresentation.updateDetail();
        }
        if (null == fetchLatestData) {
            fetchLatestData = new FetchLatestData();
            fetchLatestData.execute(String.valueOf(SERVER_LOOP_TIME));
        }
        if (null == asyncTaskRunner) {
            asyncTaskRunner = new AsyncTaskRunner();
            asyncTaskRunner.execute(LOOP_TIME);
        }

    }

    public void setAdvertisementList(JsonAdvertisementList jsonAdvertisementList, int no_of_q) {
        Log.e("setAdvertisementList", "called");
        if (null != jsonAdvertisementList && null != jsonAdvertisementList.getJsonAdvertisements() && jsonAdvertisementList.getJsonAdvertisements().size() > 0) {
            jsonAdvertisement_images = null;
            image_list_size = 0;
            urlList = new ArrayList<>();
            for (int i = 0; i < jsonAdvertisementList.getJsonAdvertisements().size(); i++) {
                JsonAdvertisement jsonAdvertisement = jsonAdvertisementList.getJsonAdvertisements().get(i);
                if (null != jsonAdvertisement) {
                    Log.e("data", jsonAdvertisement.toString());
                    switch (jsonAdvertisement.getAdvertisementType()) {
                        case MA:
                        case GI:
                            if (null != jsonAdvertisement.getImageUrls() && jsonAdvertisement.getImageUrls().size() > 0) {
                                image_list_size = image_list_size + jsonAdvertisement.getImageUrls().size();
                                if (null == jsonAdvertisement_images) {
                                    jsonAdvertisement_images = new JsonAdvertisement();
                                    for (int j = 0; j < jsonAdvertisement.getImageUrls().size(); j++) {
                                        jsonAdvertisement_images.getImageUrls().add(jsonAdvertisement.getAdvertisementId() + "/" + jsonAdvertisement.getImageUrls().get(j));
                                        urlList.add(jsonAdvertisement.getAdvertisementId() + "/" + jsonAdvertisement.getImageUrls().get(j));
                                    }
                                } else {
                                    for (int j = 0; j < jsonAdvertisement.getImageUrls().size(); j++) {
                                        jsonAdvertisement_images.getImageUrls().add(jsonAdvertisement.getAdvertisementId() + "/" + jsonAdvertisement.getImageUrls().get(j));
                                        urlList.add(jsonAdvertisement.getAdvertisementId() + "/" + jsonAdvertisement.getImageUrls().get(j));
                                    }
                                }
                                Log.e("Advertisement: ", "Image URL called");
                            }
                            break;
                        case PP:
                            if (null != jsonAdvertisement.getJsonProfessionalProfileTV()) {
                                jsonAdvertisement_profile = jsonAdvertisement;
                                Log.e("Advertisement: ", "Profile called");
                            }
                            break;
                        default:
                    }
                }
            }
        }
        this.no_of_q = no_of_q;
    }

    @Override
    public void clientInResponse(JsonQueueTVList jsonQueueTVList) {
        {
            if (null != jsonQueueTVList && null != jsonQueueTVList.getQueues()) {
                Log.v("Presentation TV Data", jsonQueueTVList.getQueues().toString());
                List<TopicAndQueueTV> topicAndQueueTVListTemp = new ArrayList<>();
                for (int i = 0; i < jsonQueueTVList.getQueues().size(); i++) {
                    JsonQueueTV jsonQueueTV = jsonQueueTVList.getQueues().get(i);
                    topicAndQueueTVListTemp.add(new TopicAndQueueTV().setJsonTopic(topicHashMap.get(jsonQueueTV.getCodeQR())).setJsonQueueTV(jsonQueueTV));
                }
                if (topicAndQueueTVListTemp.size() == 0) {
                    topicAndQueueTVListTemp.add(new TopicAndQueueTV().setJsonTopic(null).setJsonQueueTV(null));
                }
                topicAndQueueTVList = topicAndQueueTVListTemp;
            }
        }
        fetchLatestData = null;
    }

    @Override
    public void advertisementResponse(JsonAdvertisementList JsonAdvertisementList) {
        setAdvertisementList(JsonAdvertisementList, topicAndQueueTVList.size());
        fetchLatestData = null;
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        fetchLatestData = null;
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        fetchLatestData = null;
    }

    @Override
    public void authenticationFailure() {
        fetchLatestData = null;
    }

    @Override
    public void professionalProfilesResponse(JsonProfessionalProfileTVList jsonProfessionalProfileTVList) {
        this.jsonProfessionalProfileTVList = jsonProfessionalProfileTVList;
    }

    @Override
    public void onPreferenceChange() {
        marqueeList = MarqueeSharedPreference.getMarquee();
        String str = "";
        for (int i = 0; i < marqueeList.size(); i++) {
            str += getString(R.string.bullet) + " " + marqueeList.get(i) + " \t";
        }
        Log.e("List: ", str);
        scrolltext.setText(str);
        scrolltext.startScroll();
    }

    public class DetailPresentation extends CastPresentation {
        private ImageView image, image1, iv_advertisement;
        private TextView title, tv_timing, tv_degree, title1, tv_timing1, tv_degree1,
                tv_info1, tv_category, tv_category1, tv_experience;
        ;
        private LinearLayout ll_list, ll_no_list, ll_left;
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
            title = findViewById(R.id.ad_title);
            tv_timing = findViewById(R.id.tv_timing);
            tv_degree = findViewById(R.id.tv_degree);

            title1 = findViewById(R.id.ad_title1);
            tv_timing1 = findViewById(R.id.tv_timing1);
            tv_degree1 = findViewById(R.id.tv_degree1);
            tv_info1 = findViewById(R.id.tv_info1);
            tv_category = findViewById(R.id.tv_category);
            tv_category1 = findViewById(R.id.tv_category1);
            tv_experience = findViewById(R.id.tv_experience);
            ll_list = findViewById(R.id.ll_list);
            ll_no_list = findViewById(R.id.ll_no_list);
            ll_left = findViewById(R.id.ll_left);
            no_of_q = topicAndQueueTVList.size();
            scrolltext = findViewById(R.id.scrolltext);
            if (LaunchActivity.isTvSplitViewEnable()) {
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.65f);
                LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.35f);
                ll_left.setLayoutParams(lp1);
                ll_no_list.setLayoutParams(lp0);
            } else {
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.0f);
                ll_left.setLayoutParams(lp1);
                ll_no_list.setLayoutParams(lp0);
            }
            updateDetail();
        }

        public void updateDetail() {
            TopicAndQueueTV topicAndQueueTV = null;
            if (!isMarqueeInit) {
                String str = "";
                for (int i = 0; i < marqueeList.size(); i++) {
                    str += getString(R.string.bullet) + " " + marqueeList.get(i) + " \t";
                }
                Log.e("List: ", str);
                scrolltext.setText(str);
                scrolltext.startScroll();
                isMarqueeInit = true;
            }
            try {

                ++sequence;
                if (sequence >= no_of_q + image_list_size) {
                    Log.e("sequence", "sequence " + sequence + " no_of_q: " + no_of_q
                            + " image_list_size: " + image_list_size);
                    sequence = 0;
                    Log.e("sequence reset", "" + sequence);
                }
                Log.e("sequence", "" + sequence);
                if (sequence < no_of_q)
                    topicAndQueueTV = topicAndQueueTVList.get(sequence);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (sequence >= no_of_q) {
                if (null != jsonAdvertisement_images && sequence + 1 > no_of_q) {
                    ll_no_list.setVisibility(View.GONE);
                    Log.e("Inside Images", "Images: " + sequence);
                    if (url_pos < urlList.size()) {
                        String url = AppUtils.getImageUrls(BuildConfig.ADVERTISEMENT_BUCKET, urlList.get(url_pos));
                        Log.e("URL", url);
                        Picasso.get().load(url).into(iv_advertisement);
                        iv_advertisement.setVisibility(View.VISIBLE);
                        ++url_pos;
                    } else {
                        iv_advertisement.setVisibility(View.GONE);
                        url_pos = 0;
                    }
                } else {
                    iv_advertisement.setVisibility(View.GONE);
                }
            } else {
                Log.e("Check error", "Inside List: " + sequence);
                url_pos = 0;
                iv_advertisement.setVisibility(View.GONE);
                ll_no_list.setVisibility(View.VISIBLE);
                if (null != jsonProfessionalProfileTVList && jsonProfessionalProfileTVList.getJsonProfessionalProfileTV().size() > 0) {
                    // do something
                    if (profile_pos >= jsonProfessionalProfileTVList.getJsonProfessionalProfileTV().size()) {
                        profile_pos = 0;
                    }
                    JsonProfessionalProfileTV jsonProfessionalProfileTV = jsonProfessionalProfileTVList.getJsonProfessionalProfileTV().get(profile_pos);
                    ++profile_pos;
                    title1.setText(jsonProfessionalProfileTV.getName());
                    tv_category1.setText(jsonProfessionalProfileTV.getProfessionType());
                    tv_timing1.setText(jsonProfessionalProfileTV.getAboutMe());
                    if (!TextUtils.isEmpty(jsonProfessionalProfileTV.getPracticeStart())) {
                        try {
                            // Format - practiceStart='2017-08-07'
                            DateTime dateTime = new DateTime(CommonHelper.SDF_YYYY_MM_DD.parse(jsonProfessionalProfileTV.getPracticeStart()));
                            Period period = new Period(dateTime, new DateTime());
                            tv_experience.setText(String.valueOf(period.getYears()) + "+ yrs experience");
                            if (0 == period.getYears())
                                tv_experience.setText(" ");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    tv_degree1.setText(AppUtils.getCompleteEducation(jsonProfessionalProfileTV.getEducation()));

                    if (TextUtils.isEmpty(jsonProfessionalProfileTV.getProfileImage())) {
                        Picasso.get().load(R.drawable.profile_tv).into(image1);
                    } else {
                        Picasso.get().load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + jsonProfessionalProfileTV.getProfileImage()).into(image1, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(R.drawable.profile_tv).into(image1);
                            }
                        });
                    }
                }
                if (null != topicAndQueueTV && null != topicAndQueueTV.getJsonQueueTV()) {
                    if (TextUtils.isEmpty(topicAndQueueTV.getJsonQueueTV().getProfileImage())) {
                        Picasso.get().load(R.drawable.profile_tv).into(image);
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
                    }
                    title.setText(topicAndQueueTV.getJsonTopic().getDisplayName());
                    tv_category.setText(MedicalDepartmentEnum.valueOf(topicAndQueueTV.getJsonTopic().getBizCategoryId()).getDescription());
                    if (!TextUtils.isEmpty(AppUtils.getCompleteEducation(topicAndQueueTV.getJsonQueueTV().getEducation()))) {
                        tv_degree.setText(AppUtils.getCompleteEducation(topicAndQueueTV.getJsonQueueTV().getEducation()));
                    } else {
                        tv_degree.setText("");
                    }
                    tv_timing.setText("Timing: " + Formatter.convertMilitaryTo12HourFormat(topicAndQueueTV.getJsonTopic().getHour().getStartHour())
                            + " - " + Formatter.convertMilitaryTo12HourFormat(topicAndQueueTV.getJsonTopic().getHour().getEndHour()));

                    ll_list.removeAllViews();
                    LayoutInflater inflater = LayoutInflater.from(this.context);
                    if (null != topicAndQueueTV.getJsonQueueTV().getJsonQueuedPersonTVList()) {
                        List<JsonQueuedPersonTV> data = topicAndQueueTV.getJsonQueueTV().getJsonQueuedPersonTVList();
                        Collections.sort(data, (lhs, rhs) -> Integer.compare(lhs.getToken(), rhs.getToken()));
                        // if (data.size() > 0) {
                        View v = inflater.inflate(R.layout.lay_header, null, false);
                        ll_list.addView(v);
                        //  }
                        for (int i = 0; i < data.size(); i++) {
                            View customView = inflater.inflate(R.layout.lay_text, null, false);
                            View cardview = customView.findViewById(R.id.cardview);
                            TextView tv_name = customView.findViewById(R.id.tv_name);
                            TextView tv_seq = customView.findViewById(R.id.tv_seq);
                            TextView tv_mobile = customView.findViewById(R.id.tv_mobile);
                            tv_seq.setText(String.valueOf((data.get(i).getDisplayToken())));
                            tv_name.setText(data.get(i).getCustomerName());
                            String phoneNo = data.get(i).getCustomerPhone();
                            tv_mobile.setText(AppUtils.hidePhoneNumberWithX(phoneNo));
                            if (topicAndQueueTV.getJsonTopic().getServingNumber() == data.get(i).getToken()) {
                                tv_mobile.setText("It's your turn");
                                cardview.setBackgroundColor(Color.parseColor("#8c1515"));
                                tv_name.setTextColor(Color.WHITE);
                                tv_mobile.setTextColor(Color.WHITE);
                            } else {
                                cardview.setBackgroundColor(Color.WHITE);
                                tv_name.setTextColor(Color.BLACK);
                                tv_mobile.setTextColor(Color.BLACK);
                            }
                            ll_list.addView(customView);
                        }
                    }
                }
            }

        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        private String resp;

        @Override
        protected String doInBackground(String... params) {
            try {
                int time = Integer.parseInt(params[0]) * MILLI_SECONDS;
                Thread.sleep(time);
                resp = "Slept for " + params[0] + " seconds";
            } catch (InterruptedException e) {
                e.printStackTrace();
                resp = e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            Log.e("TV loop", "done");
            setTopicAndQueueTV(topicAndQueueTVList);
            new AsyncTaskRunner().execute(LOOP_TIME);
        }
    }

    private class FetchLatestData extends AsyncTask<String, String, String> {
        private String resp;

        @Override
        protected String doInBackground(String... params) {
            try {
                int timeInMilliSeconds;
                if (callFirstTime) {
                    timeInMilliSeconds = 2000;
                    callFirstTime = false;
                } else {
                    timeInMilliSeconds = 6000;//Integer.parseInt(params[0]) * MINUTE;
                    timerCount = timerCount + timeInMilliSeconds;
                }
                Thread.sleep(timeInMilliSeconds);
                resp = "Slept for " + timeInMilliSeconds + " milli seconds";
            } catch (InterruptedException e) {
                e.printStackTrace();
                resp = e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("datafetching", "done");
            // execution of result of Long time consuming operation
            if (timerCount <= 2000 || timerCount >= MINUTE) {
                QueueDetail queueDetail = getQueueDetails(LaunchActivity.merchantListFragment.getTopics());
                ClientInQueueApiCalls clientInQueueApiCalls = new ClientInQueueApiCalls(PresentationService.this);
                clientInQueueApiCalls.toBeServedClients(
                        UserUtils.getDeviceId(),
                        LaunchActivity.getLaunchActivity().getEmail(),
                        LaunchActivity.getLaunchActivity().getAuth(), queueDetail);
                Log.e("SERVER Called for data", "done");
                if (timerCount == MINUTE)
                    timerCount = 0;
            } else {
                // do nothing
                fetchLatestData = null;
            }
            if (callAdvertisement) {
                AdvertisementApiCalls advertisementApiCalls = new AdvertisementApiCalls();
                advertisementApiCalls.setAdvertisementPresenter(PresentationService.this);
                advertisementApiCalls.setProfessionalProfilesPresenter(PresentationService.this);
                advertisementApiCalls.getAllAdvertisements(UserUtils.getDeviceId(), LaunchActivity.getLaunchActivity().getEmail(), LaunchActivity.getLaunchActivity().getAuth());
                advertisementApiCalls.professionalProfiles(UserUtils.getDeviceId(), LaunchActivity.getLaunchActivity().getEmail(), LaunchActivity.getLaunchActivity().getAuth());
                callAdvertisement = false;
            }
        }

        private QueueDetail getQueueDetails(ArrayList<JsonTopic> jsonTopics) {
            QueueDetail queueDetail = new QueueDetail();
            List<String> tvObjects = new ArrayList<>();
            topicHashMap.clear();
            for (int i = 0; i < jsonTopics.size(); i++) {
                JsonTopic jsonTopic = jsonTopics.get(i);
                String start = Formatter.convertMilitaryTo24HourFormat(jsonTopic.getHour().getStartHour());
                String end = Formatter.convertMilitaryTo24HourFormat(jsonTopic.getHour().getEndHour());
                if (isTimeBetweenTwoTime(start, end)) {
                    tvObjects.add(jsonTopics.get(i).getCodeQR());
                    topicHashMap.put(jsonTopics.get(i).getCodeQR(), jsonTopics.get(i));
                }
            }
            queueDetail.setCodeQRs(tvObjects);
            return queueDetail;
        }
    }

    public static boolean isTimeBetweenTwoTime(String initialTime, String finalTime) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = new Date();
            String currentTime = formatter.format(date);
            //Start Time
            //all times are from java.util.Date
            Date inTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(initialTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(inTime);

            //Current Time
            Date checkTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(currentTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(checkTime);

            //End Time
            Date finTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(finalTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(finTime);

            if (finalTime.compareTo(initialTime) < 0) {
                calendar2.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }

            java.util.Date actualTime = calendar3.getTime();
            return (actualTime.after(calendar1.getTime()) || actualTime.compareTo(calendar1.getTime()) == 0) && actualTime.before(calendar2.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
