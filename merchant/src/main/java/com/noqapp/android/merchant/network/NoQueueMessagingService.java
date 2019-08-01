package com.noqapp.android.merchant.network;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.noqapp.android.common.fcm.data.JsonAlertData;
import com.noqapp.android.common.fcm.data.JsonClientData;
import com.noqapp.android.common.fcm.data.JsonClientOrderData;
import com.noqapp.android.common.fcm.data.JsonMedicalFollowUp;
import com.noqapp.android.common.fcm.data.JsonTopicAppointmentData;
import com.noqapp.android.common.fcm.data.JsonTopicOrderData;
import com.noqapp.android.common.fcm.data.JsonTopicQueueData;
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.database.DatabaseHelper;
import com.noqapp.android.merchant.model.database.utils.NotificationDB;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.MyApplication;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class NoQueueMessagingService extends FirebaseMessagingService {

    private final static String TAG = NoQueueMessagingService.class.getSimpleName();

    public NoQueueMessagingService() {
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN", s);
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
            Log.d(TAG, "Message data payload: CodeQR " + remoteMessage.getData().get(Constants.CodeQR));
            Log.d(TAG, "Message data payload: Firebase_Type " + remoteMessage.getData().get(Constants.Firebase_Type));

            Log.d(TAG, "Message data payload: QueueStatus " + remoteMessage.getData().get(Constants.QueueStatus));
            Log.d(TAG, "Message data payload: CurrentlyServing " + remoteMessage.getData().get(Constants.CurrentlyServing));
            Log.d(TAG, "Message data payload: LastNumber " + remoteMessage.getData().get(Constants.LastNumber));
            Log.d(TAG, "Message data payload: GoTo_Counter " + remoteMessage.getData().get(Constants.GoTo_Counter));
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            clearNotifications(this);
            Log.e("M-Notification: ",remoteMessage.getData().toString());

            MessageOriginEnum messageOrigin = MessageOriginEnum.valueOf(remoteMessage.getData().get(Constants.MESSAGE_ORIGIN));

            Object object = null;
            switch (messageOrigin) {
                case QA:
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        object = mapper.readValue(new JSONObject(remoteMessage.getData()).toString(), JsonTopicAppointmentData.class);
                        Log.e("FCM", object.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
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
                    // Do nothing here
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

            if (isAppIsInBackground(getApplicationContext())) {
                // app is in background, show the notification in notification tray
                if (null == LaunchActivity.dbHandler) {
                    LaunchActivity.dbHandler = DatabaseHelper.getsInstance(getApplicationContext());
                }
                if (remoteMessage.getData().get(Constants.Firebase_Type).equalsIgnoreCase(FirebaseMessageTypeEnum.P.getName())) {
                    NotificationDB.insertNotification(NotificationDB.KEY_NOTIFY, remoteMessage.getData().get(Constants.CodeQR), body, title);
                }
                sendNotification(title, body, remoteMessage);
            } else {
                // app is in foreground, broadcast the push message
                // add notification to DB
                if (remoteMessage.getData().get(Constants.Firebase_Type).equalsIgnoreCase(FirebaseMessageTypeEnum.P.getName())) {
                    NotificationDB.insertNotification(NotificationDB.KEY_NOTIFY, remoteMessage.getData().get(Constants.CodeQR), body, title);
                }
                Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
                pushNotification.putExtra(Constants.MESSAGE, body);
                pushNotification.putExtra(Constants.QRCODE, remoteMessage.getData().get(Constants.CodeQR));
                pushNotification.putExtra(Constants.STATUS, remoteMessage.getData().get(Constants.QueueStatus));
                pushNotification.putExtra(Constants.CURRENT_SERVING, remoteMessage.getData().get(Constants.CurrentlyServing));
                pushNotification.putExtra(Constants.LASTNO, remoteMessage.getData().get(Constants.LastNumber));
                pushNotification.putExtra(Constants.Firebase_Type, remoteMessage.getData().get(Constants.Firebase_Type));
                pushNotification.putExtra("object", (Serializable) object);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNotification(String title, String messageBody, RemoteMessage remoteMessage) {
        Intent notificationIntent = new Intent(getApplicationContext(), LaunchActivity.class);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.launcher);
        if (null != remoteMessage) {
            notificationIntent.putExtra(Constants.MESSAGE, messageBody);
            notificationIntent.putExtra(Constants.QRCODE, remoteMessage.getData().get(Constants.CodeQR));
            notificationIntent.putExtra(Constants.STATUS, remoteMessage.getData().get(Constants.QueueStatus));
            notificationIntent.putExtra(Constants.CURRENT_SERVING, remoteMessage.getData().get(Constants.CurrentlyServing));
            notificationIntent.putExtra(Constants.LASTNO, remoteMessage.getData().get(Constants.LastNumber));
            notificationIntent.putExtra(Constants.Firebase_Type, remoteMessage.getData().get(Constants.Firebase_Type));
            if (null != LaunchActivity.getLaunchActivity())
                LaunchActivity.getLaunchActivity().updateListByNotification(notificationIntent);
        }

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 2;

        String channelId = "channel-01";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelName = "Channel Name";
            int importance = MyApplication.isNotificationSoundEnable() ?
                    NotificationManager.IMPORTANCE_HIGH:NotificationManager.IMPORTANCE_LOW;
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
                .setLights(Color.parseColor("#ffb400"), 50, 10);
        if (MyApplication.isNotificationSoundEnable()) {
            mBuilder.setSound(defaultSoundUri);
        }else{
            mBuilder.setPriority(NotificationCompat.PRIORITY_LOW);
        }
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
}
