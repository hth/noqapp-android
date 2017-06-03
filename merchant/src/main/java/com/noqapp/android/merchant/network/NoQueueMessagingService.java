package com.noqapp.android.merchant.network;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

import java.util.List;

public class NoQueueMessagingService extends FirebaseMessagingService {

    private final static String TAG = NoQueueMessagingService.class.getSimpleName();

    public NoQueueMessagingService() {
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());


        // Check if message contains a notification payload.
        if (remoteMessage.getData() != null) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Log.d(TAG, "Message data payload: c- " + remoteMessage.getData().get("c"));
            Log.d(TAG, "Message data payload: f- " + remoteMessage.getData().get("f"));
            FirebaseMessageTypeEnum firebaseMessageTypeEnum = FirebaseMessageTypeEnum.valueOf(remoteMessage.getData().get("f"));

            Log.d(TAG, "Message data payload: q-" + remoteMessage.getData().get("q"));
            Log.d(TAG, "Message data payload: cs-" + remoteMessage.getData().get("cs"));
            Log.d(TAG, "Message data payload: ln-" + remoteMessage.getData().get("ln"));
            Log.d(TAG, "Message data payload: g-" + remoteMessage.getData().get("g"));
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            clearNotifications(this);
            if (!isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", body);
                pushNotification.putExtra("qrcode", remoteMessage.getData().get("c"));
                pushNotification.putExtra("status", remoteMessage.getData().get("q"));
                pushNotification.putExtra("current_serving", remoteMessage.getData().get("cs"));
                pushNotification.putExtra("lastno", remoteMessage.getData().get("ln"));
                pushNotification.putExtra("f", remoteMessage.getData().get("f"));
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);


            } else {
                // app is in background, show the notification in notification tray
                sendNotification(title, body);
            }

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNotification(String title, String messageBody) {
        Intent notificationIntent = new Intent(getApplicationContext(), LaunchActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), Constants.requestCodeNotification, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(10 /* ID of notification */, notificationBuilder.build());
    }

    /**
     * Method checks if the app is in background or not
     */
    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }
}
