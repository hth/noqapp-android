package com.noqapp.android.client.network;

import static com.noqapp.android.client.utils.Constants.CodeQR;
import static com.noqapp.android.client.utils.Constants.Firebase_Type;
import static com.noqapp.android.client.utils.Constants.ISREVIEW;
import static com.noqapp.android.client.utils.Constants.QRCODE;
import static com.noqapp.android.client.utils.Constants.TOKEN;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.database.DatabaseHelper;
import com.noqapp.android.client.model.database.DatabaseTable;
import com.noqapp.android.client.model.database.utils.NotificationDB;
import com.noqapp.android.client.model.database.utils.ReviewDB;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.model.fcm.JsonClientTokenAndQueueData;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueueList;
import com.noqapp.android.client.presenter.beans.ReviewData;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.receivers.AlarmReceiver;
import com.noqapp.android.common.fcm.data.JsonAlertData;
import com.noqapp.android.common.fcm.data.JsonClientData;
import com.noqapp.android.common.fcm.data.JsonClientOrderData;
import com.noqapp.android.common.fcm.data.JsonMedicalFollowUp;
import com.noqapp.android.common.fcm.data.JsonTopicOrderData;
import com.noqapp.android.common.fcm.data.JsonTopicQueueData;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.model.types.QueueUserStateEnum;
import com.noqapp.android.common.utils.CommonHelper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.apache.commons.lang3.StringUtils;

import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.AlarmManager;
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
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
            MessageOriginEnum messageOrigin = MessageOriginEnum.valueOf(remoteMessage.getData().get(Constants.MESSAGE_ORIGIN));

            Object object = null;
            switch (messageOrigin) {
                case Q:
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        object = mapper.readValue(new JSONObject(remoteMessage.getData()).toString(), JsonTopicQueueData.class);
                        Log.e("FCM", object.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CQO:
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonClientTokenAndQueueData jsonClientTokenAndQueueData = mapper.readValue(new JSONObject(remoteMessage.getData()).toString(), JsonClientTokenAndQueueData.class);
                        JsonTokenAndQueueList jsonTokenAndQueueList = new JsonTokenAndQueueList();
                        jsonTokenAndQueueList.setTokenAndQueues(mapper.readValue(remoteMessage.getData().get("tqs"), new TypeReference<List<JsonTokenAndQueue>>() {}));
                        jsonClientTokenAndQueueData.setTokenAndQueues(jsonTokenAndQueueList.getTokenAndQueues());
                        object = jsonClientTokenAndQueueData;
                        Log.e("FCM", object.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LaunchActivity.getLaunchActivity(), "Parsing exception", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case QR:
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        object = mapper.readValue(new JSONObject(remoteMessage.getData()).toString(), JsonClientData.class);
                        Log.e("FCM Queue Review", object.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case OR:
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        object = mapper.readValue(new JSONObject(remoteMessage.getData()).toString(), JsonClientOrderData.class);
                        Log.e("FCM Order Review", object.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case O:
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        object = mapper.readValue(new JSONObject(remoteMessage.getData()).toString(), JsonTopicOrderData.class);
                        Log.e("FCM order ", object.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case A:
                case D:
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        object = mapper.readValue(new JSONObject(remoteMessage.getData()).toString(), JsonAlertData.class);
                        Log.e("FCM Review store", object.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MF:
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        object = mapper.readValue(new JSONObject(remoteMessage.getData()).toString(), JsonMedicalFollowUp.class);
                        Log.e("FCM Medical Followup", object.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    // object = null;
            }
            try {
                if (!isAppIsInBackground(getApplicationContext())) {
                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
                    pushNotification.putExtra("object", (Serializable) object);
                    pushNotification.putExtra(Firebase_Type, remoteMessage.getData().get(Firebase_Type));
                    pushNotification.putExtra(CodeQR, remoteMessage.getData().get(CodeQR));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                } else {
                    // app is in background, show the notification in notification tray
                    //save data to database
                    String payload = remoteMessage.getData().get(Firebase_Type);
                    String codeQR = remoteMessage.getData().get(CodeQR);

                    if (null == LaunchActivity.dbHandler) {
                        LaunchActivity.dbHandler = DatabaseHelper.getsInstance(getApplicationContext());
                    }
                    /*
                     * When u==S then it is re-view
                     *      u==N then it is skip(Rejoin) Pending task
                     */
                    if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.P.getName())) {
                        if (StringUtils.isNotBlank(codeQR)) {
                            String current_serving = remoteMessage.getData().get(Constants.CurrentlyServing);
                            if (null != current_serving) {
                                ArrayList<JsonTokenAndQueue> jsonTokenAndQueueArrayList = TokenAndQueueDB.getCurrentQueueObjectList(codeQR);
                                for (int i = 0; i < jsonTokenAndQueueArrayList.size(); i++) {
                                    JsonTokenAndQueue jtk = jsonTokenAndQueueArrayList.get(i);
                                    if (null != jtk) {
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
                            }
                            if (object instanceof JsonAlertData) {
                                Log.e("IN JsonAlertData", ((JsonAlertData) object).toString());
                                NotificationDB.insertNotification(
                                        NotificationDB.KEY_NOTIFY,
                                        ((JsonAlertData) object).getCodeQR(),
                                        body,
                                        title,
                                        ((JsonAlertData) object).getBusinessType() == null ? BusinessTypeEnum.PA.getName() : ((JsonAlertData) object).getBusinessType().getName());

                                sendNotification(title, body, false);
                            } else if (object instanceof JsonClientData) {
                                Log.e("IN JsonClientData", ((JsonClientData) object).toString());
                                String token = String.valueOf(((JsonClientData) object).getToken());
                                String qid = ((JsonClientData) object).getQueueUserId();
                                if (((JsonClientData) object).getQueueUserState().getName().equalsIgnoreCase(QueueUserStateEnum.S.getName())) {
                                    /*
                                     * Save codeQR of review & show the review screen on app
                                     * resume if there is any record in Review DB for queue review key
                                     */
                                    ReviewData reviewData = ReviewDB.getValue(codeQR, token);
                                    if (null != reviewData) {
                                        ContentValues cv = new ContentValues();
                                        cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, 1);
                                        ReviewDB.updateReviewRecord(codeQR, token, cv);
                                        // update
                                    } else {
                                        //insert
                                        ContentValues cv = new ContentValues();
                                        cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, 1);
                                        cv.put(DatabaseTable.Review.CODE_QR, codeQR);
                                        cv.put(DatabaseTable.Review.TOKEN, token);
                                        cv.put(DatabaseTable.Review.QID, qid);
                                        cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1");
                                        cv.put(DatabaseTable.Review.KEY_SKIP, "-1");
                                        cv.put(DatabaseTable.Review.KEY_GOTO, "");
                                        ReviewDB.insert(cv);
                                    }
                                    sendNotification(title, body, codeQR, true, token);//pass codeQR to open review screen
                                } else if (((JsonClientData) object).getQueueUserState().getName().equalsIgnoreCase(QueueUserStateEnum.N.getName())) {
                                    ReviewData reviewData = ReviewDB.getValue(codeQR, token);
                                    if (null != reviewData) {
                                        ContentValues cv = new ContentValues();
                                        cv.put(DatabaseTable.Review.KEY_SKIP, -1);
                                        ReviewDB.updateReviewRecord(codeQR, token, cv);
                                        // update
                                    } else {
                                        //insert
                                        ContentValues cv = new ContentValues();
                                        cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, -1);
                                        cv.put(DatabaseTable.Review.CODE_QR, codeQR);
                                        cv.put(DatabaseTable.Review.TOKEN, token);
                                        cv.put(DatabaseTable.Review.QID, qid);
                                        cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1");
                                        cv.put(DatabaseTable.Review.KEY_SKIP, "-1");
                                        cv.put(DatabaseTable.Review.KEY_GOTO, "");
                                        ReviewDB.insert(cv);
                                    }
                                    sendNotification(title, body, codeQR, false, token);//pass codeQR to open skip screen
                                }
                            } else if (object instanceof JsonMedicalFollowUp) {
                                Log.e("Alert set:", "data is :" + title + " ---- " + body);
                                sendNotification(title, body, true);
                                setAlarm((JsonMedicalFollowUp) object);
                            } else {
                                sendNotification(title, body, false);
                            }
                        }else if (object instanceof JsonClientTokenAndQueueData) {
                            List<JsonTokenAndQueue> jsonTokenAndQueueList = ((JsonClientTokenAndQueueData) object).getTokenAndQueues();
                            if (null != jsonTokenAndQueueList && jsonTokenAndQueueList.size() > 0) {
                                TokenAndQueueDB.saveCurrentQueue(jsonTokenAndQueueList);
                            }else{
                                NotificationDB.insertNotification(
                                        NotificationDB.KEY_NOTIFY,
                                        "", ((JsonClientTokenAndQueueData) object).getBody(),
                                        ((JsonClientTokenAndQueueData) object).getTitle(), BusinessTypeEnum.PA.getName());
                            }
                            for (int i = 0; i < jsonTokenAndQueueList.size(); i++) {
                                NoQueueMessagingService.subscribeTopics(jsonTokenAndQueueList.get(i).getTopic());
                                if(i==0) {
                                    NotificationDB.insertNotification(
                                            NotificationDB.KEY_NOTIFY,
                                            jsonTokenAndQueueList.get(i).getCodeQR(), ((JsonClientTokenAndQueueData) object).getBody(),
                                            ((JsonClientTokenAndQueueData) object).getTitle(), BusinessTypeEnum.PA.getName());
                                }
                            }
                            sendNotification(title, body, false);
                        } else {
                            sendNotification(title, body, false);
                            // add notification to DB
                            if (object instanceof JsonAlertData) {
                                Log.e("IN JsonAlertData", ((JsonAlertData) object).toString());
                                NotificationDB.insertNotification(
                                        NotificationDB.KEY_NOTIFY,
                                        ((JsonAlertData) object).getCodeQR(),
                                        body,
                                        title,
                                        ((JsonAlertData) object).getBusinessType() == null ? BusinessTypeEnum.PA.getName() : ((JsonAlertData) object).getBusinessType().getName());
                            }
                        }
                    } else if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.C.getName())) {
                        String go_to = "";
                        String current_serving = "";
                        if (object instanceof JsonTopicQueueData) {
                            Log.e("IN JsonTopicQueueData", ((JsonTopicQueueData) object).toString());
                            current_serving = String.valueOf(((JsonTopicQueueData) object).getCurrentlyServing());
                            go_to = ((JsonTopicQueueData) object).getGoTo();
                        }
                        ArrayList<JsonTokenAndQueue> jsonTokenAndQueueArrayList = TokenAndQueueDB.getCurrentQueueObjectList(codeQR);
                        for (int i = 0; i < jsonTokenAndQueueArrayList.size(); i++) {
                            JsonTokenAndQueue jtk = jsonTokenAndQueueArrayList.get(i);
                            if (null != jtk) {
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
                                        cv.put(DatabaseTable.Review.QID, jtk.getQueueUserId());
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
                    if (object instanceof JsonMedicalFollowUp) {
                        Log.e("JsonMedicalFollowUp", ((JsonMedicalFollowUp) object).toString());
                        NotificationDB.insertNotification(
                                NotificationDB.KEY_NOTIFY,
                                ((JsonMedicalFollowUp) object).getCodeQR(), ((JsonMedicalFollowUp) object).getBody(),
                                ((JsonMedicalFollowUp) object).getTitle(), BusinessTypeEnum.PA.getName());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error reading message " + e.getLocalizedMessage(), e);
                sendNotification(title, body, false);
            }
        }
    }

    private void sendNotification(String title, String messageBody, String codeQR, boolean isReview, String token) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.notification_icon);
        Intent notificationIntent = new Intent(getApplicationContext(), LaunchActivity.class);
        if (null != codeQR) {
            notificationIntent.putExtra(QRCODE, codeQR);
            notificationIntent.putExtra(ISREVIEW, isReview);
            notificationIntent.putExtra(TOKEN, token);
        }
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setLights(Color.parseColor("#ffb400"), 50, 10)
                .setSound(defaultSoundUri);
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
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.notification_icon);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorMobile))
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(bm)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
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
        return useWhiteIcon ? R.mipmap.notification_icon : R.mipmap.launcher;
    }

    private void setAlarm(JsonMedicalFollowUp jsonMedicalFollowUp) {
        try {
            Date startDate = CommonHelper.stringToDate(jsonMedicalFollowUp.getPopFollowUpAlert());
            Date endDate = CommonHelper.stringToDate(jsonMedicalFollowUp.getFollowUpDay());
            long duration = endDate.getTime() - startDate.getTime();
            long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);
            Log.e("difference in day : ", String.valueOf(diffInDays));

            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
            Calendar calendar = Calendar.getInstance();
            calendar.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE) + 1);
            calendar.set(Calendar.SECOND, Calendar.getInstance().get(Calendar.SECOND));
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("title", jsonMedicalFollowUp.getTitle());
            intent.putExtra("body", jsonMedicalFollowUp.getBody());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(this.ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            Log.e("Alarm set", "Done Alarm");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
