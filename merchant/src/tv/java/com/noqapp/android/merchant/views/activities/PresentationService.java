package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.ShowPersonInQAdapter;

import com.google.android.gms.cast.CastPresentation;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.util.List;

public class PresentationService extends CastRemoteDisplayLocalService {
    private DetailPresentation castPresentation;
    private JsonTopic jsonTopic;
    private List<JsonQueuedPerson> jsonQueuedPersonArrayList;

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
        jsonTopic = null;
        jsonQueuedPersonArrayList = null;
    }

    private void dismissPresentation() {
        if (castPresentation != null) {
            castPresentation.dismiss();
            castPresentation = null;
        }
    }

    public void setAdViewModel(JsonTopic jsonTopic, List<JsonQueuedPerson> jsonQueuedPersonArrayList) {
        this.jsonTopic = jsonTopic;
        this.jsonQueuedPersonArrayList = jsonQueuedPersonArrayList;
        if (castPresentation != null) {
            castPresentation.updateAdDetail(jsonTopic, jsonQueuedPersonArrayList);
        }
    }

    public class DetailPresentation extends CastPresentation {

        private ShowPersonInQAdapter peopleInQAdapter;

        private RecyclerView rv_queue_people;
        private TextView tv_counter_name;
        private TextView tv_title, tv_current_value, tv_timing;
        private ListView list_view;

        private Context context;

        public DetailPresentation(Context context, Display display) {
            super(context, display);
            this.context = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.merchant_detail_page_tv);
            tv_current_value = findViewById(R.id.tv_current_value);
            tv_title = findViewById(R.id.tv_title);
            tv_timing = findViewById(R.id.tv_timing);
            rv_queue_people = findViewById(R.id.rv_queue_people);
            list_view = findViewById(R.id.list_view);
            tv_counter_name = findViewById(R.id.tv_counter_name);
            rv_queue_people.setHasFixedSize(true);
            LinearLayoutManager horizontalLayoutManagaer
                    = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            rv_queue_people.setLayoutManager(horizontalLayoutManagaer);
            rv_queue_people.setItemAnimator(new DefaultItemAnimator());

            TextView tv_deviceId = findViewById(R.id.tv_deviceId);
            tv_deviceId.setText(UserUtils.getDeviceId());
            tv_deviceId.setVisibility(BuildConfig.BUILD_TYPE.equals("debug") ? View.VISIBLE : View.GONE);


            updateAdDetail(jsonTopic, jsonQueuedPersonArrayList);
        }

        public void updateAdDetail(JsonTopic jsonTopic, List<JsonQueuedPerson> jsonQueuedPersonArrayList) {
            if (null != jsonTopic) {
                String cName = "";
                if (TextUtils.isEmpty(cName))
                    tv_counter_name.setText("");
                else
                    tv_counter_name.setText(cName);

                tv_timing.setText("Timing: " + Formatter.convertMilitaryTo12HourFormat(jsonTopic.getHour().getStartHour())
                        + " - " + Formatter.convertMilitaryTo12HourFormat(jsonTopic.getHour().getEndHour()));
                tv_current_value.setText(String.valueOf(jsonTopic.getServingNumber()));
                tv_title.setText(jsonTopic.getDisplayName());
                list_view.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, new String[]{"qqq", "bbbb", "cccc", "ddddd"}));
                // peopleInQAdapter = new ShowPersonInQAdapter(jsonQueuedPersonArrayList, context);
                // rv_queue_people.setAdapter(peopleInQAdapter);
            }
        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }
    }
}