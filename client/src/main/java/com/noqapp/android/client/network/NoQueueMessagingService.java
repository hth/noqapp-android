package com.noqapp.android.client.network;

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

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.database.utils.ReviewDB;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.views.activities.LaunchActivity;

import org.apache.commons.lang3.StringUtils;

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

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        clearNotifications(this);
        // Check if message contains a notification payload.
        if (remoteMessage.getData() != null) {
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            if (!isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
                pushNotification.putExtra("f", remoteMessage.getData().get("f"));
                pushNotification.putExtra("c", remoteMessage.getData().get("c"));
                if (remoteMessage.getData().get("f").equalsIgnoreCase(FirebaseMessageTypeEnum.C.getName())) {
                    pushNotification.putExtra("cs", remoteMessage.getData().get("cs"));
                    pushNotification.putExtra("ln", remoteMessage.getData().get("ln"));
                    pushNotification.putExtra("g", remoteMessage.getData().get("g"));
                }
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            } else {
                // app is in background, show the notification in notification tray
                //save data to database
                String payload = remoteMessage.getData().get("f");
                String codeQR = remoteMessage.getData().get("c");


                if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.P.getName())) {

                    JsonTokenAndQueue jtk = TokenAndQueueDB.getCurrentQueueObject(codeQR);

                    // un-subscribe from the topic
                    NoQueueMessagingService.unSubscribeTopics(jtk.getTopic());
                    sendNotification(title, body, codeQR);//pass codeQR to open review screen

                    /**
                     * Save codeQR of review & show the review screen on app
                     * resume if there is any record in Review DB for review key
                     * **/
                    ReviewDB.insert(ReviewDB.KEY_REVEIW, codeQR, codeQR);
                } else if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.C.getName())) {

                    String current_serving = remoteMessage.getData().get("cs");
                    JsonTokenAndQueue jtk = TokenAndQueueDB.getCurrentQueueObject(codeQR);
                    String go_to = remoteMessage.getData().get("g");
                    ReviewDB.insert(ReviewDB.KEY_GOTO, codeQR, go_to);
                    //update DB & after join screen
                    jtk.setServingNumber(Integer.parseInt(current_serving));
                    if (jtk.isTokenExpired()) {
                        //un-subscribe from the topic
                        NoQueueMessagingService.unSubscribeTopics(jtk.getTopic());
                    }
                    TokenAndQueueDB.updateJoinQueueObject(codeQR, current_serving, String.valueOf(jtk.getToken()));
                    sendNotification(title, body, null); // pass null to show only notification with no action
                }
            }
        }
    }

    private void sendNotification(String title, String messageBody, String codeqr) {
        Intent notificationIntent = new Intent(getApplicationContext(), LaunchActivity.class);
        if (null != codeqr) {
            notificationIntent.putExtra("CODEQR", codeqr);

        }
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), Constants.requestCodeNotification, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notifications_none_black_24dp)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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


    public static void subscribeTopics(String topic){
        FirebaseMessaging.getInstance().subscribeToTopic(topic+"_A");
    }

    public static void unSubscribeTopics(String topic){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic+"_A");
    }
}
