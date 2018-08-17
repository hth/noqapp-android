package com.noqapp.android.client.network;

import static com.noqapp.android.client.utils.Constants.BusinessType;
import static com.noqapp.android.client.utils.Constants.CodeQR;
import static com.noqapp.android.client.utils.Constants.CurrentlyServing;
import static com.noqapp.android.client.utils.Constants.FCM_TYPE;
import static com.noqapp.android.client.utils.Constants.Firebase_Type;
import static com.noqapp.android.client.utils.Constants.GoTo_Counter;
import static com.noqapp.android.client.utils.Constants.ISREVIEW;
import static com.noqapp.android.client.utils.Constants.LastNumber;
import static com.noqapp.android.client.utils.Constants.ORDER_STATE;
import static com.noqapp.android.client.utils.Constants.QRCODE;
import static com.noqapp.android.client.utils.Constants.QueueUserState;
import static com.noqapp.android.client.utils.Constants.QuserID;
import static com.noqapp.android.client.utils.Constants.TOKEN;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.database.DatabaseTable;
import com.noqapp.android.client.model.database.utils.NotificationDB;
import com.noqapp.android.client.model.database.utils.ReviewDB;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.model.types.QueueUserStateEnum;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.ReviewData;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.common.model.types.FCMTypeEnum;
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.apache.commons.lang3.StringUtils;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
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
                    pushNotification.putExtra(FCM_TYPE, remoteMessage.getData().get(FCM_TYPE));
                    if (remoteMessage.getData().get(Firebase_Type).equalsIgnoreCase(FirebaseMessageTypeEnum.C.getName())) {
                        pushNotification.putExtra(CurrentlyServing, remoteMessage.getData().get(CurrentlyServing));
                        pushNotification.putExtra(LastNumber, remoteMessage.getData().get(LastNumber));
                        pushNotification.putExtra(GoTo_Counter, remoteMessage.getData().get(GoTo_Counter));
                    }
                    if (remoteMessage.getData().get(Firebase_Type).equalsIgnoreCase(FirebaseMessageTypeEnum.P.getName())) {
                        pushNotification.putExtra(QueueUserState, remoteMessage.getData().get(QueueUserState));
                        pushNotification.putExtra(CurrentlyServing, remoteMessage.getData().get(CurrentlyServing));
                        pushNotification.putExtra(GoTo_Counter, remoteMessage.getData().get(GoTo_Counter));
                        pushNotification.putExtra(TOKEN, remoteMessage.getData().get(TOKEN));
                        pushNotification.putExtra(QuserID, remoteMessage.getData().get(QuserID));
                        // add notification to DB
                        String userStatus = remoteMessage.getData().get(QueueUserState);
                        if (null == userStatus) {
                            String businessType = remoteMessage.getData().get(BusinessType);
                            NotificationDB.insertNotification(NotificationDB.KEY_NOTIFY, remoteMessage.getData().get(CodeQR), body, title, businessType);
                        }
                    }
                    if (remoteMessage.getData().get(FCM_TYPE).equalsIgnoreCase(FCMTypeEnum.O.name())) {
                        pushNotification.putExtra(ORDER_STATE, remoteMessage.getData().get(ORDER_STATE));
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
                            String current_serving = remoteMessage.getData().get(Constants.CurrentlyServing);
                            String userStatus = remoteMessage.getData().get(Constants.QueueUserState);
                            String quserID = remoteMessage.getData().get(Constants.QuserID);
                            String token = remoteMessage.getData().get(Constants.TOKEN);
                            ArrayList<JsonTokenAndQueue> jsonTokenAndQueueArrayList = TokenAndQueueDB.getCurrentQueueObjectList(codeQR);
                            for (int i = 0; i < jsonTokenAndQueueArrayList.size(); i++) {
                                JsonTokenAndQueue jtk = jsonTokenAndQueueArrayList.get(i);
                                if (null != jtk && null != current_serving) {
                                    //update DB & after join screen
                                    jtk.setServingNumber(Integer.parseInt(current_serving));
                                    /*
                                     * Save codeQR of goto & show it in after join screen on app
                                     * Review DB for review key && current serving == token no.
                                     */

                                    if (jtk.isTokenExpired() && jsonTokenAndQueueArrayList.size() == 1) {
                                        //un subscribe the topic
                                        //TODO @chandra write logic for unsubscribe
                                        NoQueueMessagingService.unSubscribeTopics(jtk.getTopic());
                                    }
                                    TokenAndQueueDB.updateCurrentListQueueObject(codeQR, current_serving, String.valueOf(jtk.getToken()));
                                }
                            }

                            /*
                             * Save codeQR of review & show the review screen on app
                             * resume if there is any record in Review DB for review key
                             */
                            if (null == userStatus) {
                                String businessType = remoteMessage.getData().get(BusinessType);
                                // NotificationDB.insertNotification(NotificationDB.KEY_NOTIFY, remoteMessage.getData().get(CodeQR), body, title,businessType);
                                sendNotification(title, body, false);
                            } else if (userStatus.equalsIgnoreCase(QueueUserStateEnum.S.getName())) {
                                ReviewData reviewData = ReviewDB.getValue(codeQR,token);
                                if(null != reviewData){
                                    ContentValues cv = new ContentValues();
                                    cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN,1);
                                    ReviewDB.updateReviewRecord(codeQR,token,cv);
                                    // update
                                }else{
                                    //insert
                                    ContentValues cv = new ContentValues();
                                    cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN,1);
                                    cv.put(DatabaseTable.Review.CODE_QR, codeQR);
                                    cv.put(DatabaseTable.Review.TOKEN, token);
                                    cv.put(DatabaseTable.Review.Q_USER_ID, quserID);
                                    cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN,"-1");
                                    cv.put(DatabaseTable.Review.KEY_SKIP,"-1");
                                    cv.put(DatabaseTable.Review.KEY_GOTO,"");
                                    ReviewDB.insert(cv);
                                }
                                sendNotification(title, body, codeQR, true, token);//pass codeQR to open review screen
                            } else if (userStatus.equalsIgnoreCase(QueueUserStateEnum.N.getName())) {
                                ReviewData reviewData = ReviewDB.getValue(codeQR,token);
                                if(null != reviewData){
                                    ContentValues cv = new ContentValues();
                                    cv.put(DatabaseTable.Review.KEY_SKIP,-1);
                                    ReviewDB.updateReviewRecord(codeQR,token,cv);
                                    // update
                                }else{
                                    //insert
                                    ContentValues cv = new ContentValues();
                                    cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN,-1);
                                    cv.put(DatabaseTable.Review.CODE_QR, codeQR);
                                    cv.put(DatabaseTable.Review.TOKEN, token);
                                    cv.put(DatabaseTable.Review.Q_USER_ID, quserID);
                                    cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN,"-1");
                                    cv.put(DatabaseTable.Review.KEY_SKIP,"-1");
                                    cv.put(DatabaseTable.Review.KEY_GOTO,"");
                                    ReviewDB.insert(cv);
                                }
                                sendNotification(title, body, codeQR, false, token);//pass codeQR to open skip screen
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
                            sendNotification(title, body, false);
                            // add notification to DB
                            String userStatus = remoteMessage.getData().get(QueueUserState);
                            if (null == userStatus) {
                                String businessType = remoteMessage.getData().get(BusinessType);
                                NotificationDB.insertNotification(NotificationDB.KEY_NOTIFY, remoteMessage.getData().get(CodeQR), body, title, businessType);
                            }
                        }
                    } else if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.C.getName())) {
                        String current_serving = remoteMessage.getData().get(CurrentlyServing);
                        ArrayList<JsonTokenAndQueue> jsonTokenAndQueueArrayList = TokenAndQueueDB.getCurrentQueueObjectList(codeQR);
                        for (int i = 0; i < jsonTokenAndQueueArrayList.size(); i++) {
                            JsonTokenAndQueue jtk = jsonTokenAndQueueArrayList.get(i);
                            if (null != jtk) {
                                String go_to = remoteMessage.getData().get(GoTo_Counter);

                                /*
                                 * Save codeQR of goto & show it in after join screen on app
                                 * Review DB for review key && current serving == token no.
                                 */
                                if (Integer.parseInt(current_serving) == jtk.getToken()) {
                                    ReviewData reviewData = ReviewDB.getValue(codeQR, current_serving);
                                    if (null != reviewData) {
                                        ContentValues cv = new ContentValues();
                                        cv.put(DatabaseTable.Review.KEY_GOTO, go_to);
                                        ReviewDB.updateReviewRecord(codeQR, current_serving, cv);
                                        // update
                                    } else {
                                        //insert
                                        ContentValues cv = new ContentValues();
                                        cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, -1);
                                        cv.put(DatabaseTable.Review.CODE_QR, codeQR);
                                        cv.put(DatabaseTable.Review.TOKEN, current_serving);
                                        cv.put(DatabaseTable.Review.Q_USER_ID, jtk.getQueueUserId());
                                        cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1");
                                        cv.put(DatabaseTable.Review.KEY_SKIP, "-1");
                                        cv.put(DatabaseTable.Review.KEY_GOTO, go_to);
                                        ReviewDB.insert(cv);
                                    }
                                }
                                //update DB & after join screen
                                jtk.setServingNumber(Integer.parseInt(current_serving));
                                if (jtk.isTokenExpired() && jsonTokenAndQueueArrayList.size() == 1) {
                                    //un-subscribe from the topic
                                    NoQueueMessagingService.unSubscribeTopics(jtk.getTopic());
                                }

                                TokenAndQueueDB.updateCurrentListQueueObject(codeQR, current_serving, String.valueOf(jtk.getToken()));
                                sendNotification(title, body, true); // pass null to show only notification with no action
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error reading message " + e.getLocalizedMessage(), e);
                sendNotification(title, body, false);
            }
        }
    }

    private void sendNotification(String title, String messageBody, String codeQR, boolean isReview, String token) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon);
        Intent notificationIntent = new Intent(getApplicationContext(), LaunchActivity.class);
        if (null != codeQR) {
            notificationIntent.putExtra(QRCODE, codeQR);
            notificationIntent.putExtra(ISREVIEW, isReview);
            notificationIntent.putExtra(TOKEN, token);

        }
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), Constants.requestCodeNotification, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorMobile))
//                .setSmallIcon(getNotificationIcon())
//                .setLargeIcon(bm)
//                .setContentTitle(title)
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(10 /* ID of notification */, notificationBuilder.build());
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;

        String channelId = "channel-01";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorMobile))
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(bm)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setLights(Color.parseColor("#ffb400"), 50, 10)
                .setSound(defaultSoundUri);
        // PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), Constants.requestCodeNotification, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                Constants.requestCodeNotification,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }

    private void sendNotification(String title, String messageBody, boolean isVibrate) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        String channelId = "channel-01";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorMobile))
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(bm)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setLights(Color.parseColor("#ffb400"), 50, 10)
                .setSound(defaultSoundUri);
        if (isVibrate)
            mBuilder.setVibrate(new long[]{500, 500});
        Intent notificationIntent = new Intent(getApplicationContext(), LaunchActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), Constants.requestCodeNotification, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                Constants.requestCodeNotification,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());

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
