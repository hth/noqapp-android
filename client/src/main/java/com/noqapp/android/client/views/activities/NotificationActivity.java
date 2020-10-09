package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.database.utils.NotificationDB;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.utils.ShowCustomDialog;
import com.noqapp.android.client.views.adapters.NotificationListAdapter;
import com.noqapp.android.common.pojos.DisplayNotification;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import java.util.List;

public class NotificationActivity extends BaseActivity implements NotificationListAdapter.DeleteRecord {
    private ListView listview;
    private RelativeLayout rl_empty;

    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initActionsViews(false);
        listview = findViewById(R.id.listview);
        rl_empty = findViewById(R.id.rl_empty);
        tv_toolbar_title.setText(getString(R.string.screen_notification));
        iv_home.setBackground(ContextCompat.getDrawable(this, R.drawable.delete_all));
        loadListData();
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

    private void loadListData() {
        List<DisplayNotification> notificationsList = NotificationDB.getNotificationsList();
        NotificationListAdapter adapter = new NotificationListAdapter(this, notificationsList, this);
        listview.setAdapter(adapter);
        if (notificationsList.size() <= 0) {
            listview.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
            iv_home.setVisibility(View.INVISIBLE);
        } else {
            iv_home.setVisibility(View.VISIBLE);
            listview.setVisibility(View.VISIBLE);
            rl_empty.setVisibility(View.GONE);
            iv_home.setOnClickListener(v -> {
                ShowCustomDialog showDialog = new ShowCustomDialog(NotificationActivity.this, true);
                showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                    @Override
                    public void btnPositiveClick() {
                        NotificationDB.clearNotificationTable();
                        loadListData();
                    }

                    @Override
                    public void btnNegativeClick() {
                        //Do nothing
                    }
                });
                showDialog.displayDialog("Clear Notifications", "Do you want to delete all notifications?");
            });
        }
    }

    @Override
    public void deleteNotification(DisplayNotification displayNotification) {
        ShowCustomDialog showDialog = new ShowCustomDialog(this, true);
        showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
            @Override
            public void btnPositiveClick() {
                NotificationDB.deleteNotification(displayNotification.getSequence(), displayNotification.getKey());
                loadListData();
            }

            @Override
            public void btnNegativeClick() {
                //Do nothing
            }
        });
        showDialog.displayDialog("Delete Notification", "Do you want to delete this notification?");
    }
}
