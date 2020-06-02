package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.database.utils.NotificationDB;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.views.adapters.NotificationListAdapter;
import com.noqapp.android.common.pojos.DisplayNotification;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.List;

public class NotificationActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initActionsViews(false);
        ListView listview = findViewById(R.id.listview);
        RelativeLayout rl_empty = findViewById(R.id.rl_empty);
        tv_toolbar_title.setText(getString(R.string.screen_notification));
        List<DisplayNotification> notificationsList = NotificationDB.getNotificationsList();
        NotificationListAdapter adapter = new NotificationListAdapter(this, notificationsList);
        listview.setAdapter(adapter);
        if (notificationsList.size() <= 0) {
            listview.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            listview.setVisibility(View.VISIBLE);
            rl_empty.setVisibility(View.GONE);
        }
        if (AppUtils.isRelease()) {
            AnalyticsEvents.logContentEvent(AnalyticsEvents.EVENT_NOTIFICATION_SCREEN);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // mark all the entry as read
        NotificationDB.updateNotification();
    }
}
