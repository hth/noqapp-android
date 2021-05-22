package com.noqapp.android.merchant.network;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonQueueChangeServiceTime;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.fcm.data.JsonAlertData;
import com.noqapp.android.common.fcm.data.JsonChangeServiceTimeData;
import com.noqapp.android.common.fcm.data.JsonClientData;
import com.noqapp.android.common.fcm.data.JsonClientOrderData;
import com.noqapp.android.common.fcm.data.JsonData;
import com.noqapp.android.common.fcm.data.JsonMedicalFollowUp;
import com.noqapp.android.common.fcm.data.JsonTopicAppointmentData;
import com.noqapp.android.common.fcm.data.JsonTopicOrderData;
import com.noqapp.android.common.fcm.data.JsonTopicQueueData;
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech;
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.presenter.NotificationPresenter;
import com.noqapp.android.common.utils.TextToSpeechHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.NotificationApiCall;
import com.noqapp.android.merchant.model.database.DatabaseHelper;
import com.noqapp.android.merchant.model.database.utils.NotificationDB;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.AppInitialize;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NoQueueMessagingService extends FirebaseMessagingService implements NotificationPresenter {

    private final static String TAG = NoQueueMessagingService.class.getSimpleName();

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

        Map<String, String> mappedData = remoteMessage.getData();
        // Check if message contains a notification payload.
        if (!mappedData.isEmpty()) {
            Log.d(TAG, "Message data payload: " + mappedData);
            Log.d(TAG, "Message data payload: CodeQR " + mappedData.get(Constants.CODE_QR));
            Log.d(TAG, "Message data payload: FIREBASE_TYPE " + mappedData.get(Constants.FIREBASE_TYPE));

            Log.d(TAG, "Message data payload: QUEUE_STATUS " + mappedData.get(Constants.QUEUE_STATUS));
            Log.d(TAG, "Message data payload: CURRENTLY_SERVING " + mappedData.get(Constants.CURRENTLY_SERVING));
            Log.d(TAG, "Message data payload: LAST_NUMBER " + mappedData.get(Constants.LAST_NUMBER));
            Log.d(TAG, "Message data payload: GOTO_COUNTER " + mappedData.get(Constants.GOTO_COUNTER));
            String title = mappedData.get("title");
            String body = mappedData.get("body");
            clearNotifications(this);
            Log.e("M-Notification: ", mappedData.toString());

            MessageOriginEnum messageOrigin = MessageOriginEnum.valueOf(mappedData.get(Constants.MESSAGE_ORIGIN));

            ObjectMapper mapper = new ObjectMapper();
            JsonData jsonData = null;
            switch (messageOrigin) {
                case QA:
                    try {
                        jsonData = mapper.readValue(new JSONObject(mappedData).toString(), JsonTopicAppointmentData.class);
                        Log.e("FCM", jsonData.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Q:
                    try {
                        List<JsonTextToSpeech> jsonTextToSpeeches = null;
                        if (StringUtils.isNotBlank(mappedData.get("textToSpeeches"))) {
                            jsonTextToSpeeches = mapper.readValue(mappedData.get("textToSpeeches"), new TypeReference<List<JsonTextToSpeech>>() {});
                        }
                        //TODO(hth) Temp code. Removed as parsing issue.
                        mappedData.remove("textToSpeeches");

                        jsonData = mapper.readValue(new JSONObject(mappedData).toString(), JsonTopicQueueData.class);

                        if (null != jsonTextToSpeeches) {
                            jsonData.setJsonTextToSpeeches(jsonTextToSpeeches);
                        }
                        Log.e("FCM", jsonData.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CQO:
                    // Do nothing here
                    break;
                case QR:
                    try {
                        jsonData = mapper.readValue(new JSONObject(mappedData).toString(), JsonClientData.class);
                        Log.e("FCM Queue Review", jsonData.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case OR:
                    try {
                        jsonData = mapper.readValue(new JSONObject(mappedData).toString(), JsonClientOrderData.class);
                        Log.e("FCM Order Review", jsonData.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case O:
                    try {
                        List<JsonTextToSpeech> jsonTextToSpeeches = null;
                        if (StringUtils.isNotBlank(mappedData.get("textToSpeeches"))) {
                            jsonTextToSpeeches = mapper.readValue(mappedData.get("textToSpeeches"), new TypeReference<List<JsonTextToSpeech>>() {
                            });
                        }
                        //TODO(hth) Temp code. Removed as parsing issue.
                        mappedData.remove("textToSpeeches");

                        jsonData = mapper.readValue(new JSONObject(mappedData).toString(), JsonTopicOrderData.class);

                        if (null != jsonTextToSpeeches) {
                            jsonData.setJsonTextToSpeeches(jsonTextToSpeeches);
                        }
                        Log.e("FCM order ", jsonData.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case A:
                case D:
                    try {
                        jsonData = mapper.readValue(new JSONObject(mappedData).toString(), JsonAlertData.class);
                        Log.e("FCM Review store", jsonData.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MF:
                    try {
                        jsonData = mapper.readValue(new JSONObject(mappedData).toString(), JsonMedicalFollowUp.class);
                        Log.e("FCM Medical Followup", jsonData.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case QCT:
                    try {
                        JsonChangeServiceTimeData jsonChangeServiceTimeData = mapper.readValue(new JSONObject(mappedData).toString(), JsonChangeServiceTimeData.class);
                        List<JsonQueueChangeServiceTime> jsonQueueChangeServiceTimes = new LinkedList<>();
                        if (null != mappedData.get("qcsts")) {
                            jsonQueueChangeServiceTimes = mapper.readValue(mappedData.get("qcsts"), new TypeReference<List<JsonQueueChangeServiceTime>>() {});
                        }
                        jsonChangeServiceTimeData.setJsonQueueChangeServiceTimes(jsonQueueChangeServiceTimes);
                        jsonData = jsonChangeServiceTimeData;
                        Log.d("Update time slot", jsonChangeServiceTimeData.toString());
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.QCT);
                        FirebaseCrashlytics.getInstance().recordException(e);
                        e.printStackTrace();
                    }
                    break;
                case M:
                    //TODO implement
                case IE:
                    //TODO implement
                default:
                    // object = null;
            }
            if (jsonData != null && !TextUtils.isEmpty(jsonData.getId())) {
                callNotificationViewApi(jsonData.getId());
            }
            if (AppUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in background, show the notification in notification tray
                if (null == AppInitialize.dbHandler) {
                    AppInitialize.dbHandler = DatabaseHelper.getsInstance(getApplicationContext());
                }
                if (Objects.requireNonNull(mappedData.get(Constants.FIREBASE_TYPE)).equalsIgnoreCase(FirebaseMessageTypeEnum.P.getName())) {
                    NotificationDB.insertNotification(NotificationDB.KEY_NOTIFY, mappedData.get(Constants.CODE_QR), body, title);
                }
                sendNotification(title, body, remoteMessage);
            } else {
                // app is in foreground, broadcast the push message
                // add notification to DB
                if (Objects.requireNonNull(mappedData.get(Constants.FIREBASE_TYPE)).equalsIgnoreCase(FirebaseMessageTypeEnum.P.getName())) {
                    NotificationDB.insertNotification(NotificationDB.KEY_NOTIFY, mappedData.get(Constants.CODE_QR), body, title);
                }
                if ("com.noqapp.android.merchant.tv".equalsIgnoreCase(getPackageName())) {
                    if (null != jsonData && null != jsonData.getJsonTextToSpeeches()) {
                        new TextToSpeechHelper(getApplicationContext()).makeAnnouncement(jsonData.getJsonTextToSpeeches());
                    }
                }
                Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
                pushNotification.putExtra(Constants.MESSAGE, body);
                pushNotification.putExtra(Constants.QRCODE, mappedData.get(Constants.CODE_QR));
                pushNotification.putExtra(Constants.STATUS, mappedData.get(Constants.QUEUE_STATUS));
                pushNotification.putExtra(Constants.CURRENT_SERVING, mappedData.get(Constants.CURRENTLY_SERVING));
                pushNotification.putExtra(Constants.LASTNO, mappedData.get(Constants.LAST_NUMBER));
                pushNotification.putExtra(Constants.FIREBASE_TYPE, mappedData.get(Constants.FIREBASE_TYPE));
                pushNotification.putExtra("jsonData", jsonData);
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
            notificationIntent.putExtra(Constants.QRCODE, remoteMessage.getData().get(Constants.CODE_QR));
            notificationIntent.putExtra(Constants.STATUS, remoteMessage.getData().get(Constants.QUEUE_STATUS));
            notificationIntent.putExtra(Constants.CURRENT_SERVING, remoteMessage.getData().get(Constants.CURRENTLY_SERVING));
            notificationIntent.putExtra(Constants.LASTNO, remoteMessage.getData().get(Constants.LAST_NUMBER));
            notificationIntent.putExtra(Constants.FIREBASE_TYPE, remoteMessage.getData().get(Constants.FIREBASE_TYPE));
            if (null != LaunchActivity.getLaunchActivity()) {
                LaunchActivity.getLaunchActivity().updateListByNotification(notificationIntent);
            }
        }

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 2;

        String channelId = "channel-01";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelName = "Channel Name";
            int importance = AppInitialize.isNotificationSoundEnable()
                ? NotificationManager.IMPORTANCE_HIGH
                : NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark))
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(bm)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setLights(Color.parseColor("#ffb400"), 50, 10);
        if (AppInitialize.isNotificationSoundEnable()) {
            mBuilder.setSound(defaultSoundUri);
        } else {
            mBuilder.setPriority(NotificationCompat.PRIORITY_LOW);
        }
        // PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), Constants.requestCodeNotification, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(Constants.requestCodeNotification, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }

    private int getNotificationIcon() {
        return R.mipmap.notification_icon;
    }

    private void callNotificationViewApi(String notificationId) {
        com.noqapp.android.common.beans.body.Notification notification = new com.noqapp.android.common.beans.body.Notification();
        notification.setId(notificationId);
        NotificationApiCall notificationApiCall = new NotificationApiCall(this);
        if (UserUtils.isLogin()) {
            notificationApiCall.notificationViewed(
                UserUtils.getDeviceId(),
                UserUtils.getEmail(),
                UserUtils.getAuth(),
                notification);
        } else {
            notificationApiCall.notificationViewed(
                UserUtils.getDeviceId(),
                notification);
        }
    }

    @Override
    public void notificationResponse(JsonResponse jsonResponse) {
        // do nothing
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        // do nothing
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        // do nothing
    }

    @Override
    public void authenticationFailure() {
        // do nothing
    }
}
