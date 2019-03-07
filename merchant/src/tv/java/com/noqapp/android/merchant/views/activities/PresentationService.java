package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonNameDatePair;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ClientInQueueModel;
import com.noqapp.android.merchant.model.VigyaapanModel;
import com.noqapp.android.merchant.presenter.ClientInQueuePresenter;
import com.noqapp.android.merchant.presenter.VigyaapanPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonQueueTV;
import com.noqapp.android.merchant.presenter.beans.JsonQueueTVList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPersonTV;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.JsonVigyaapanTV;
import com.noqapp.android.merchant.presenter.beans.JsonVigyaapanTVList;
import com.noqapp.android.merchant.presenter.beans.body.QueueDetail;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.UserUtils;

import com.google.android.gms.cast.CastPresentation;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PresentationService extends CastRemoteDisplayLocalService implements ClientInQueuePresenter, VigyaapanPresenter {
    private DetailPresentation castPresentation;
    private int image_list_size = 0;
    private int profile_size = 0;
    private int url_pos = 0;
    private int no_of_q = 0;
    private int sequence = -1;
    private int text_list_pos = 0;
    private List<String> urlList = new ArrayList<>();
    private List<String> textList = new ArrayList<>();
    private JsonVigyaapanTV jsonVigyaapanTV_profile, jsonVigyaapanTV_images;
    private HashMap<String, JsonTopic> topicHashMap = new HashMap<>();
    private List<TopicAndQueueTV> topicAndQueueTVList = new ArrayList<>();
    private FetchLatestData fetchLatestData;
    private AsyncTaskRunner asyncTaskRunner;
    private final String LOOP_TIME = "9";
    private final String SERVER_LOOP_TIME = "5";
    private final int MILLI_SECONDS = 1000;
    private final int SECONDS = 60;
    private final int MINUTE = SECONDS * MILLI_SECONDS;
    private boolean callVigyapan = true;
    private boolean callFirstTime = true;
    private int timercount = 0;

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

    public void setVigyaapanList(JsonVigyaapanTVList jsonVigyaapanTVList, int no_of_q) {

        Log.e("setVigyaapanList", "called");
        if (jsonVigyaapanTVList.getJsonVigyaapanTVs().size() > 0) {
            for (int i = 0; i < jsonVigyaapanTVList.getJsonVigyaapanTVs().size(); i++) {
                JsonVigyaapanTV jsonVigyaapanTV = jsonVigyaapanTVList.getJsonVigyaapanTVs().get(i);
                if (null != jsonVigyaapanTV) {
                    switch (jsonVigyaapanTV.getVigyaapanType()) {
                        case MV:
                            if (null != jsonVigyaapanTV.getImageUrls() && jsonVigyaapanTV.getImageUrls().size() > 0) {
                                urlList = jsonVigyaapanTV.getImageUrls();
                                image_list_size = urlList.size();
                                jsonVigyaapanTV_images = jsonVigyaapanTV;
                                Log.e("Vigyapan: ", "Image URL called");
                            }
                            break;
                        case PP:
                            if (null != jsonVigyaapanTV.getJsonProfessionalProfileTV()) {
                                profile_size = 1;
                                jsonVigyaapanTV_profile = jsonVigyaapanTV;
                                Log.e("Vigyapan: ", "Profile called");
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
                Log.v("TV Data", jsonQueueTVList.getQueues().toString());
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
    public void vigyaapanResponse(JsonVigyaapanTVList JsonVigyaapanTVList) {
        setVigyaapanList(JsonVigyaapanTVList, topicAndQueueTVList.size());
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
            no_of_q = topicAndQueueTVList.size();
            updateDetail();
        }

        public void updateDetail() {

            TopicAndQueueTV topicAndQueueTV = null;
            try {

                ++sequence;
                if (sequence >= no_of_q + image_list_size + profile_size) {

                    Log.e("sequence", "sequence " + sequence + " no_of_q: " + no_of_q + " image_list_size: " + image_list_size + " profile_size: " + profile_size);
                    sequence = 0;
                    Log.e("sequence reset", "" + sequence);
                }
                Log.e("sequence", "" + sequence);
                if (sequence < no_of_q)
                    topicAndQueueTV = topicAndQueueTVList.get(sequence);
            } catch (Exception e) {
                e.printStackTrace();
            }


//            if (null == topicAndQueueTV || null == topicAndQueueTV.getJsonQueueTV()) {
//                if (null != jsonVigyaapanTV)
//                    switch (jsonVigyaapanTV.getVigyaapanType()) {
//                        case MV: {
//                            ll_profile.setVisibility(View.GONE);
//                            if (url_pos < urlList.size()) {
//                                Picasso.with(getContext()).load(urlList.get(url_pos)).into(iv_advertisement);
//                                iv_advertisement.setVisibility(View.VISIBLE);
//                                ++url_pos;
//                            } else {
//                                url_pos = 0;
//                                Picasso.with(getContext()).load(urlList.get(url_pos)).into(iv_advertisement);
//                                iv_advertisement.setVisibility(View.VISIBLE);
//                            }
//                        }
//                        break;
//                        case PP:
//                            if(null != jsonVigyaapanTV.getJsonProfessionalProfileTV()) {
//                                ll_profile.setVisibility(View.VISIBLE);
//                                Picasso.with(getContext()).load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + jsonVigyaapanTV.getJsonProfessionalProfileTV().getProfileImage()).into(iv_profile);
//                                tv_doctor_name.setText(jsonVigyaapanTV.getJsonProfessionalProfileTV().getName());
//                                tv_doctor_category.setText(jsonVigyaapanTV.getJsonProfessionalProfileTV().getProfessionType());
//                                tv_doctor_degree.setText(getSelectedData(jsonVigyaapanTV.getJsonProfessionalProfileTV().getEducation()));
//                                tv_about_doctor.setText(jsonVigyaapanTV.getJsonProfessionalProfileTV().getAboutMe());
//                            }
//                            break;
//                        default:
//                    }
//            }

            if (sequence >= no_of_q) {
                if (null != jsonVigyaapanTV_profile && no_of_q + profile_size == sequence + 1) {
                    Log.e("Inside Profile", "profile :" + sequence);
                    ll_profile.setVisibility(View.VISIBLE);
                    iv_advertisement.setVisibility(View.GONE);
                    ll_no_list.setVisibility(View.GONE);
                    String imageName = jsonVigyaapanTV_profile.getJsonProfessionalProfileTV().getProfileImage();
                    if (StringUtils.isNotBlank(imageName)) {
                        if (imageName.contains(".")) {
                            String[] file = imageName.split("\\.");
                            imageName = file[0] + "_o." + file[1];
                        }
                    } else {
                        imageName = "";
                    }

                    Picasso.with(getContext()).load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + imageName).into(iv_profile, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(DetailPresentation.this.context).load(R.drawable.profile_tv).into(iv_profile);
                        }
                    });
                    tv_doctor_name.setText("Dr. " + jsonVigyaapanTV_profile.getJsonProfessionalProfileTV().getName());
                    tv_doctor_category.setText(jsonVigyaapanTV_profile.getJsonProfessionalProfileTV().getProfessionType());
                    tv_doctor_degree.setText(getSelectedData(jsonVigyaapanTV_profile.getJsonProfessionalProfileTV().getEducation()));
                    tv_about_doctor.setText(jsonVigyaapanTV_profile.getJsonProfessionalProfileTV().getAboutMe());
                } else {
                    ll_profile.setVisibility(View.GONE);
                }

                if (null != jsonVigyaapanTV_images && sequence + 1 > no_of_q + profile_size) {
                    ll_profile.setVisibility(View.GONE);
                    ll_no_list.setVisibility(View.GONE);
                    Log.e("Inside Images", "Images: " + sequence);
                    if (url_pos < urlList.size()) {
                        Picasso.with(getContext()).load(urlList.get(url_pos)).into(iv_advertisement);
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
                ll_profile.setVisibility(View.GONE);
                iv_advertisement.setVisibility(View.GONE);
                ll_no_list.setVisibility(View.GONE);
                if (null != topicAndQueueTV && null != topicAndQueueTV.getJsonQueueTV()) {
                    if (TextUtils.isEmpty(topicAndQueueTV.getJsonQueueTV().getProfileImage())) {
                        Picasso.with(this.context).load(R.drawable.profile_tv).into(image);
                        Picasso.with(this.context).load(R.drawable.profile_tv).into(image1);
                    } else {
                        Picasso.with(this.context).load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + topicAndQueueTV.getJsonQueueTV().getProfileImage()).into(image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(DetailPresentation.this.context).load(R.drawable.profile_tv).into(image);
                            }
                        });
                        Picasso.with(this.context).load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + topicAndQueueTV.getJsonQueueTV().getProfileImage()).into(image1, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(DetailPresentation.this.context).load(R.drawable.profile_tv).into(image1);
                            }
                        });
                    }
                    title.setText(topicAndQueueTV.getJsonTopic().getDisplayName());
                    if (!TextUtils.isEmpty(new AppUtils().getCompleteEducation(topicAndQueueTV.getJsonQueueTV().getEducation()))) {
                        tv_degree.setText(" (" + new AppUtils().getCompleteEducation(topicAndQueueTV.getJsonQueueTV().getEducation()) + ") ");
                    } else {
                        tv_degree.setText("");
                    }
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

                    if (sequence % 2 == 0) {
                        ll_list.setBackground(ContextCompat.getDrawable(this.context, R.mipmap.temp_2));
                    } else {
                        ll_list.setBackground(ContextCompat.getDrawable(this.context, R.mipmap.pp_bg));
                    }
                    ll_list.removeAllViews();
                    LayoutInflater inflater = LayoutInflater.from(this.context);
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
                        if (data.size() > 0) {
                            View v = inflater.inflate(R.layout.lay_header, null, false);
                            ll_list.addView(v);
                        }
                        for (int i = 0; i < data.size(); i++) {
                            View customView = inflater.inflate(R.layout.lay_text, null, false);
                            CardView cardview = customView.findViewById(R.id.cardview);
                            TextView tv_name = customView.findViewById(R.id.tv_name);
                            TextView tv_seq = customView.findViewById(R.id.tv_seq);
                            TextView tv_mobile = customView.findViewById(R.id.tv_mobile);
                            tv_seq.setText(String.valueOf((data.get(i).getToken())));
                            tv_name.setText(data.get(i).getCustomerName());
                            String phoneNo = data.get(i).getCustomerPhone();
                            tv_mobile.setText(new AppUtils().hidePhoneNumberWithX(phoneNo));
                            if (topicAndQueueTV.getJsonTopic().getServingNumber() == data.get(i).getToken()) {
                                tv_mobile.setText("It's your turn");
                                cardview.setCardBackgroundColor(Color.parseColor("#8c1515"));
                                tv_name.setTextColor(Color.WHITE);
                                tv_mobile.setTextColor(Color.WHITE);
                            } else {
                                cardview.setCardBackgroundColor(Color.WHITE);
                                tv_name.setTextColor(Color.BLACK);
                                tv_mobile.setTextColor(Color.BLACK);
                            }
                            ll_list.addView(customView);
                        }
                    }
                    if (ll_list.getChildCount() > 0) {
                        ll_no_list.setVisibility(View.GONE);
                    } else {
                        ll_no_list.setVisibility(View.VISIBLE);
                    }
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
        if (data.endsWith(", ")) {
            data = data.substring(0, data.length() - 2);
        }
        return data;
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
                int timeInMinutes;
                if(callFirstTime) {
                    timeInMinutes = 2000;
                    callFirstTime = false;
                }else{
                    timeInMinutes = 6000;//Integer.parseInt(params[0]) * MINUTE;
                    timercount = timercount+timeInMinutes;
                }
                Thread.sleep(timeInMinutes);
                resp = "Slept for " + timeInMinutes + " minutes";
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
            if(timercount <= 2000 || timercount == 6000*10) {
                QueueDetail queueDetail = getQueueDetails(LaunchActivity.merchantListFragment.getTopics());
                ClientInQueueModel clientInQueueModel = new ClientInQueueModel(PresentationService.this);
                clientInQueueModel.toBeServedClients(
                        UserUtils.getDeviceId(),
                        LaunchActivity.getLaunchActivity().getEmail(),
                        LaunchActivity.getLaunchActivity().getAuth(), queueDetail);
            }else{
               // do nothing
            }
            if (callVigyapan) {
                VigyaapanModel vigyaapanModel = new VigyaapanModel();
                vigyaapanModel.setVigyaapanPresenter(PresentationService.this);
                vigyaapanModel.getAllVigyaapan(UserUtils.getDeviceId(),
                        LaunchActivity.getLaunchActivity().getEmail(),
                        LaunchActivity.getLaunchActivity().getAuth());
                callVigyapan = false;
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
            if ((actualTime.after(calendar1.getTime()) ||
                    actualTime.compareTo(calendar1.getTime()) == 0) &&
                    actualTime.before(calendar2.getTime())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
