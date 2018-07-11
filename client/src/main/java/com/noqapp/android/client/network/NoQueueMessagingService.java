package com.noqapp.android.client.network;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.database.utils.NotificationDB;
import com.noqapp.android.client.model.database.utils.ReviewDB;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.client.model.types.QueueUserStateEnum;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.views.activities.LaunchActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.noqapp.android.client.utils.Constants.CodeQR;
import static com.noqapp.android.client.utils.Constants.CurrentlyServing;
import static com.noqapp.android.client.utils.Constants.Firebase_Type;
import static com.noqapp.android.client.utils.Constants.GoTo_Counter;
import static com.noqapp.android.client.utils.Constants.ISREVIEW;
import static com.noqapp.android.client.utils.Constants.LastNumber;
import static com.noqapp.android.client.utils.Constants.QRCODE;
import static com.noqapp.android.client.utils.Constants.QueueUserState;

public class NoQueueMessagingService extends FirebaseMessagingService {

    private final static String TAG = NoQueueMessagingService.class.getSimpleName();

    public NoQueueMessagingService() {
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void subscribeTopics(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic + "_A");
    }

    public static void unSubscribeTopics(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic + "_A");
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN", s);
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
            try {
                if (!isAppIsInBackground(getApplicationContext())) {
                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
                    pushNotification.putExtra(Firebase_Type, remoteMessage.getData().get(Firebase_Type));
                    pushNotification.putExtra(CodeQR, remoteMessage.getData().get(CodeQR));
                    if (remoteMessage.getData().get(Firebase_Type).equalsIgnoreCase(FirebaseMessageTypeEnum.C.getName())) {
                        pushNotification.putExtra(CurrentlyServing, remoteMessage.getData().get(CurrentlyServing));
                        pushNotification.putExtra(LastNumber, remoteMessage.getData().get(LastNumber));
                        pushNotification.putExtra(GoTo_Counter, remoteMessage.getData().get(GoTo_Counter));
                    }
                    if (remoteMessage.getData().get(Firebase_Type).equalsIgnoreCase(FirebaseMessageTypeEnum.P.getName())) {
                        pushNotification.putExtra(QueueUserState, remoteMessage.getData().get(QueueUserState));
                        pushNotification.putExtra(CurrentlyServing, remoteMessage.getData().get(CurrentlyServing));
                        pushNotification.putExtra(GoTo_Counter, remoteMessage.getData().get(GoTo_Counter));

                        // add notification to DB
                        String userStatus = remoteMessage.getData().get(QueueUserState);
                        if (null == userStatus) {
                            NotificationDB.insertNotification(NotificationDB.KEY_NOTIFY, remoteMessage.getData().get(CodeQR), body, title);
                        }
                    }
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                } else {
                    // app is in background, show the notification in notification tray
                    //save data to database
                    String payload = remoteMessage.getData().get(Firebase_Type);
                    String codeQR = remoteMessage.getData().get(CodeQR);
                    /*
                     * When u==S then it is re-view
                     *      u==N then it is skip(Rejoin) Pending task
                     */

                    if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.P.getName())) {
                        if (StringUtils.isNotBlank(codeQR)) {
                            JsonTokenAndQueue jtk = TokenAndQueueDB.getCurrentQueueObject(codeQR);
                            String userStatus = remoteMessage.getData().get(Constants.QueueUserState);
                            // un-subscribe from the topic
                            NoQueueMessagingService.unSubscribeTopics(jtk.getTopic());

                            /*
                             * Save codeQR of review & show the review screen on app
                             * resume if there is any record in Review DB for review key
                             */
                            if (null == userStatus) {
                                NotificationDB.insertNotification(NotificationDB.KEY_NOTIFY, remoteMessage.getData().get(CodeQR), body, title);
                                sendNotification(title, body);
                            } else if (userStatus.equalsIgnoreCase(QueueUserStateEnum.S.getName())) {
                                ReviewDB.insert(ReviewDB.KEY_REVIEW, codeQR, codeQR);
                                sendNotification(title, body, codeQR, true);//pass codeQR to open review screen
                            } else if (userStatus.equalsIgnoreCase(QueueUserStateEnum.N.getName())) {
                                ReviewDB.insert(ReviewDB.KEY_SKIP, codeQR, codeQR);
                                sendNotification(title, body, codeQR, false);//pass codeQR to open skip screen
                            }
                        } else {
                            Log.w(TAG, "To implement this when a message like this is received");
                            //TODO something for this data
//                            {
//                                "content_available": true,
//                                      "data": {
//                                        "body": "World Android",
//                                        "cs": 0,
//                                        "f": "P",
//                                        "ln": 0,
//                                        "title": "Hello Android"
//                                      },
//                                "priority": "high",
//                                "to": "XXXXX"
//                            }
                            sendNotification(title, body);
                            // add notification to DB
                            String userStatus = remoteMessage.getData().get(QueueUserState);
                            if (null == userStatus) {
                                NotificationDB.insertNotification(NotificationDB.KEY_NOTIFY, remoteMessage.getData().get(CodeQR), body, title);
                            }
                        }
                    } else if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.C.getName())) {
                        String current_serving = remoteMessage.getData().get(CurrentlyServing);
                        JsonTokenAndQueue jtk = TokenAndQueueDB.getCurrentQueueObject(codeQR);
                        if (null == jtk) {
                            jtk = TokenAndQueueDB.getHistoryQueueObject(codeQR);
                        }
                        String go_to = remoteMessage.getData().get(GoTo_Counter);

                        /*
                         * Save codeQR of goto & show it in after join screen on app
                         * Review DB for review key && current serving == token no.
                         */
                        if (Integer.parseInt(current_serving) == jtk.getToken())
                            ReviewDB.insert(ReviewDB.KEY_GOTO, codeQR, go_to);
                        //update DB & after join screen
                        jtk.setServingNumber(Integer.parseInt(current_serving));
                        if (jtk.isTokenExpired()) {
                            //un-subscribe from the topic
                            NoQueueMessagingService.unSubscribeTopics(jtk.getTopic());
                        }
                        TokenAndQueueDB.updateJoinQueueObject(codeQR, current_serving, String.valueOf(jtk.getToken()));
                        sendNotification(title, body); // pass null to show only notification with no action
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error reading message " + e.getLocalizedMessage(), e);
                sendNotification(title, body);
            }
        }
    }

    private void sendNotification(String title, String messageBody, String codeQR, boolean isReview) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon);
        Intent notificationIntent = new Intent(getApplicationContext(), LaunchActivity.class);
        if (null != codeQR) {
            notificationIntent.putExtra(QRCODE, codeQR);
            notificationIntent.putExtra(ISREVIEW, isReview);

        }
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), Constants.requestCodeNotification, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorMobile))
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(bm)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(10 /* ID of notification */, notificationBuilder.build());
    }

    private void sendNotification(String title, String messageBody) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon);

        Intent notificationIntent = new Intent(getApplicationContext(), LaunchActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), Constants.requestCodeNotification, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorMobile))
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(bm)
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

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.notification_icon : R.mipmap.launcher;
    }
}
