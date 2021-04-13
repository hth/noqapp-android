package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.database.utils.NotificationDB;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.ShowCustomDialog;
import com.noqapp.android.client.views.adapters.NotificationListAdapter;
import com.noqapp.android.client.views.version_2.adapter.NotificationAdapter;
import com.noqapp.android.common.pojos.DisplayNotification;

import java.util.List;

public class NotificationActivity extends BaseActivity implements NotificationListAdapter.DeleteRecord {
    private RecyclerView rvNotification;
    private RelativeLayout rl_empty;

    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notification);
        initActionsViews(false);
        rvNotification = findViewById(R.id.rvNotification);
        rl_empty = findViewById(R.id.rlEmpty);
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
        NotificationAdapter adapter = new NotificationAdapter(notificationsList, displayNotification -> {

            return null;
        });
        rvNotification.setAdapter(adapter);
        if (notificationsList.size() <= 0) {
            rvNotification.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
            iv_home.setVisibility(View.INVISIBLE);
        } else {
            iv_home.setVisibility(View.VISIBLE);
            rvNotification.setVisibility(View.VISIBLE);
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
