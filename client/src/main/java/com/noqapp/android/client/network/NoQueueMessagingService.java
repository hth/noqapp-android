package com.noqapp.android.client.network;

import static com.noqapp.android.client.utils.Constants.CODE_QR;
import static com.noqapp.android.client.utils.Constants.FIREBASE_TYPE;
import static com.noqapp.android.client.utils.Constants.ISREVIEW;
import static com.noqapp.android.client.utils.Constants.QRCODE;
import static com.noqapp.android.client.utils.Constants.TOKEN;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
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
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.TokenStatusUtils;
import com.noqapp.android.client.views.activities.BlinkerActivity;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.MyApplication;
import com.noqapp.android.client.views.activities.NoQueueBaseActivity;
import com.noqapp.android.client.views.receivers.AlarmReceiver;
import com.noqapp.android.common.fcm.data.JsonAlertData;
import com.noqapp.android.common.fcm.data.JsonClientData;
import com.noqapp.android.common.fcm.data.JsonClientOrderData;
import com.noqapp.android.common.fcm.data.JsonData;
import com.noqapp.android.common.fcm.data.JsonMedicalFollowUp;
import com.noqapp.android.common.fcm.data.JsonTopicAppointmentData;
import com.noqapp.android.common.fcm.data.JsonTopicOrderData;
import com.noqapp.android.common.fcm.data.JsonTopicQueueData;
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.model.types.QueueUserStateEnum;
import com.noqapp.android.common.utils.CommonHelper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.apache.commons.lang3.StringUtils;

import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
        Log.d("NEW_TOKEN", s);
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

        Map<String, String> mappedData = remoteMessage.getData();
        if (!mappedData.isEmpty()) {
            String title = mappedData.get("title");
            String body = mappedData.get("body");
            String imageUrl = mappedData.get("imageURL");
            MessageOriginEnum messageOrigin = MessageOriginEnum.valueOf(remoteMessage.getData().get(Constants.MESSAGE_ORIGIN));

            ObjectMapper mapper = new ObjectMapper();
            JsonData jsonData = null;
            switch (messageOrigin) {
                case QA:
                    try {
                        jsonData = mapper.readValue(new JSONObject(remoteMessage.getData()).toString(), JsonTopicAppointmentData.class);
                        Log.e("FCM", jsonData.toString());
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.QA);
                        FirebaseCrashlytics.getInstance().recordException(e);
                        e.printStackTrace();
                    }
                    break;
                case Q:
                    try {
                        List<JsonTextToSpeech> jsonTextToSpeeches = null;
                        boolean containsTextToSpeeches = mappedData.containsKey("textToSpeeches");
                        if (containsTextToSpeeches) {
                            jsonTextToSpeeches = mapper.readValue(mappedData.get("textToSpeeches"), new TypeReference<List<JsonTextToSpeech>>() {});
                            //TODO(hth) Temp code. Removed as parsing issue.
                            mappedData.remove("textToSpeeches");
                        }

                        jsonData = mapper.readValue(new JSONObject(mappedData).toString(), JsonTopicQueueData.class);

                        if (null != jsonTextToSpeeches) {
                            jsonData.setJsonTextToSpeeches(jsonTextToSpeeches);
                        }
                        Log.e("FCM", jsonData.toString());
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.Q);
                        FirebaseCrashlytics.getInstance().recordException(e);
                        e.printStackTrace();
                    }
                    break;
                case CQO:
                    try {
                        JsonClientTokenAndQueueData jsonClientTokenAndQueueData = mapper.readValue(new JSONObject(mappedData).toString(), JsonClientTokenAndQueueData.class);

                        JsonTokenAndQueueList jsonTokenAndQueueList = new JsonTokenAndQueueList();
                        jsonTokenAndQueueList.setTokenAndQueues(mapper.readValue(mappedData.get("tqs"), new TypeReference<List<JsonTokenAndQueue>>() {}));
                        jsonClientTokenAndQueueData.setTokenAndQueues(jsonTokenAndQueueList.getTokenAndQueues());
                        jsonData = jsonClientTokenAndQueueData;
                        Log.e("FCM", jsonData.toString());
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.CQO);
                        FirebaseCrashlytics.getInstance().recordException(e);
                        e.printStackTrace();
                    }
                    break;
                case QR:
                    try {
                        jsonData = mapper.readValue(new JSONObject(mappedData).toString(), JsonClientData.class);
                        Log.e("FCM Queue Review", jsonData.toString());
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.QR);
                        FirebaseCrashlytics.getInstance().recordException(e);
                        e.printStackTrace();
                    }
                    break;
                case OR:
                    try {
                        jsonData = mapper.readValue(new JSONObject(mappedData).toString(), JsonClientOrderData.class);
                        Log.e("FCM Order Review", jsonData.toString());
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.OR);
                        FirebaseCrashlytics.getInstance().recordException(e);
                        e.printStackTrace();
                    }
                    break;
                case O:
                    try {
                        List<JsonTextToSpeech> jsonTextToSpeeches = null;
                        boolean containsTextToSpeeches = mappedData.containsKey("textToSpeeches");
                        if (containsTextToSpeeches) {
                            jsonTextToSpeeches = mapper.readValue(mappedData.get("textToSpeeches"), new TypeReference<List<JsonTextToSpeech>>() {});
                            //TODO(hth) Temp code. Removed as parsing issue.
                            mappedData.remove("textToSpeeches");
                        }

                        jsonData = mapper.readValue(new JSONObject(mappedData).toString(), JsonTopicOrderData.class);

                        if (null != jsonTextToSpeeches) {
                            jsonData.setJsonTextToSpeeches(jsonTextToSpeeches);
                        }
                        Log.e("FCM order ", jsonData.toString());
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.O);
                        FirebaseCrashlytics.getInstance().recordException(e);
                        e.printStackTrace();
                    }
                    break;
                case A:
                case D:
                    try {
                        jsonData = mapper.readValue(new JSONObject(mappedData).toString(), JsonAlertData.class);
                        Log.e("FCM Review store", jsonData.toString());
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.D);
                        FirebaseCrashlytics.getInstance().recordException(e);
                        e.printStackTrace();
                    }
                    break;
                case MF:
                    try {
                        jsonData = mapper.readValue(new JSONObject(mappedData).toString(), JsonMedicalFollowUp.class);
                        Log.e("FCM Medical Followup", jsonData.toString());
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.MF);
                        FirebaseCrashlytics.getInstance().recordException(e);
                        e.printStackTrace();
                    }
                    break;
                default:
                    // object = null;
            }
            try {
                if (!AppUtils.isAppIsInBackground(getApplicationContext())) {
                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
                    pushNotification.putExtra("jsonData", (Serializable) jsonData);
                    pushNotification.putExtra(FIREBASE_TYPE, mappedData.get(FIREBASE_TYPE));
                    pushNotification.putExtra(CODE_QR, mappedData.get(CODE_QR));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                    if (messageOrigin == MessageOriginEnum.Q) {
                        // Update Currently serving in app preferences
                        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Constants.APP_PACKAGE, Context.MODE_PRIVATE);
                        prefs.edit().putInt(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, mappedData.get(CODE_QR)), Integer.parseInt(mappedData.get(Constants.CURRENTLY_SERVING))).apply();
                    }
                } else {
                    // app is in background, show the notification in notification tray
                    //save data to database
                    String payload = mappedData.get(FIREBASE_TYPE);
                    String codeQR = mappedData.get(CODE_QR);

                    if (null == LaunchActivity.dbHandler) {
                        LaunchActivity.dbHandler = DatabaseHelper.getsInstance(getApplicationContext());
                    }
                    /*
                     * When u==S then it is re-view
                     *      u==N then it is skip(Rejoin) Pending task
                     */
                    if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.P.getName())) {
                        if (StringUtils.isNotBlank(codeQR)) {
                            String current_serving = mappedData.get(Constants.CURRENTLY_SERVING);
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
                            if (jsonData instanceof JsonAlertData) {
                                Log.e("IN JsonAlertData", jsonData.toString());
                                NotificationDB.insertNotification(
                                        NotificationDB.KEY_NOTIFY,
                                        ((JsonAlertData) jsonData).getCodeQR(),
                                        body,
                                        title,
                                        ((JsonAlertData) jsonData).getBusinessType() == null
                                                ? BusinessTypeEnum.PA.getName()
                                                : ((JsonAlertData) jsonData).getBusinessType().getName(), imageUrl);

                                sendNotification(title, body, false, imageUrl);
                            } else if (jsonData instanceof JsonClientData) {
                                Log.e("IN JsonClientData", jsonData.toString());
                                String token = String.valueOf(((JsonClientData) jsonData).getToken());
                                String qid = ((JsonClientData) jsonData).getQueueUserId();
                                if (((JsonClientData) jsonData).getQueueUserState().getName().equalsIgnoreCase(QueueUserStateEnum.S.getName())) {
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
                                    sendNotification(title, body, codeQR, true, token, imageUrl);//pass codeQR to open review screen
                                } else if (((JsonClientData) jsonData).getQueueUserState().getName().equalsIgnoreCase(QueueUserStateEnum.N.getName())) {
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
                                    sendNotification(title, body, codeQR, false, token, imageUrl);//pass codeQR to open skip screen
                                }
                            } else if (jsonData instanceof JsonClientTokenAndQueueData) {
                                List<JsonTokenAndQueue> jsonTokenAndQueueList = ((JsonClientTokenAndQueueData) jsonData).getTokenAndQueues();
                                if (null != jsonTokenAndQueueList && jsonTokenAndQueueList.size() > 0) {
                                    TokenAndQueueDB.saveCurrentQueue(jsonTokenAndQueueList);
                                }
                                NotificationDB.insertNotification(
                                        NotificationDB.KEY_NOTIFY,
                                        ((JsonClientTokenAndQueueData) jsonData).getCodeQR(),
                                        jsonData.getBody(),
                                        jsonData.getTitle(),
                                        BusinessTypeEnum.PA.getName(),
                                        imageUrl);

                                for (int i = 0; i < jsonTokenAndQueueList.size(); i++) {
                                    NoQueueMessagingService.subscribeTopics(jsonTokenAndQueueList.get(i).getTopic());
                                }
                                sendNotification(title, body, false, imageUrl);
                            } else if (jsonData instanceof JsonMedicalFollowUp) {
                                Log.e("Alert set:", "data is :" + title + " ---- " + body);
                                sendNotification(title, body, true, imageUrl);
                                setAlarm((JsonMedicalFollowUp) jsonData);
                            } else {
                                sendNotification(title, body, false, imageUrl);
                            }
                        } else {
                            sendNotification(title, body, false, imageUrl);
                            // add notification to DB
                            if (jsonData instanceof JsonAlertData) {
                                Log.e("IN JsonAlertData", jsonData.toString());
                                NotificationDB.insertNotification(
                                        NotificationDB.KEY_NOTIFY,
                                        ((JsonAlertData) jsonData).getCodeQR(),
                                        body,
                                        title,
                                        ((JsonAlertData) jsonData).getBusinessType() == null
                                                ? BusinessTypeEnum.PA.getName()
                                                : ((JsonAlertData) jsonData).getBusinessType().getName(), imageUrl);
                            }
                        }
                    } else if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.C.getName())) {
                        String goTo = "";
                        String currentServing = "";
                        if (jsonData instanceof JsonTopicQueueData) {
                            Log.e("IN JsonTopicQueueData", jsonData.toString());
                            currentServing = String.valueOf(((JsonTopicQueueData) jsonData).getCurrentlyServing());
                            goTo = ((JsonTopicQueueData) jsonData).getGoTo();
                        }
                        if (jsonData instanceof JsonTopicOrderData) {
                            Log.e("IN JsonTopicOrderData", jsonData.toString());
                            currentServing = String.valueOf(((JsonTopicOrderData) jsonData).getCurrentlyServing());
                            goTo = ((JsonTopicOrderData) jsonData).getGoTo();
                        }
                        ArrayList<JsonTokenAndQueue> jsonTokenAndQueueArrayList = TokenAndQueueDB.getCurrentQueueObjectList(codeQR);
                        for (int i = 0; i < jsonTokenAndQueueArrayList.size(); i++) {
                            JsonTokenAndQueue jtk = jsonTokenAndQueueArrayList.get(i);
                            if (null != jtk) {
                                boolean displayBuzzer = false;
                                /*
                                 * Save codeQR of goto & show it in after join screen on app
                                 * Review DB for review key && current serving == token no.
                                 */
                                if (Integer.parseInt(currentServing) == jtk.getToken()) {
                                    ReviewData reviewData = ReviewDB.getValue(codeQR, currentServing);
                                    if (null != reviewData) {
                                        ContentValues cv = new ContentValues();
                                        cv.put(DatabaseTable.Review.KEY_GOTO, goTo);
                                        ReviewDB.updateReviewRecord(codeQR, currentServing, cv);
                                        // update
                                    } else {
                                        //insert
                                        displayBuzzer = true;
                                        ContentValues cv = new ContentValues();
                                        cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, -1);
                                        cv.put(DatabaseTable.Review.CODE_QR, codeQR);
                                        cv.put(DatabaseTable.Review.TOKEN, currentServing);
                                        cv.put(DatabaseTable.Review.QID, jtk.getQueueUserId());
                                        cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1");
                                        cv.put(DatabaseTable.Review.KEY_SKIP, "-1");
                                        cv.put(DatabaseTable.Review.KEY_GOTO, goTo);
                                        ReviewDB.insert(cv);
                                    }

                                }
                                //update DB & after join screen
                                jtk.setServingNumber(Integer.parseInt(currentServing));
                                if (jtk.isTokenExpired() && jsonTokenAndQueueArrayList.size() == 1) {
                                    //un-subscribe from the topic
                                    NoQueueMessagingService.unSubscribeTopics(jtk.getTopic());
                                }
                                TokenAndQueueDB.updateCurrentListQueueObject(codeQR, currentServing, String.valueOf(jtk.getToken()));

                                // Check if user needs to be notified
                                int currentlyServingNumber = Integer.parseInt(currentServing);
                                SharedPreferences prefs = getApplicationContext().getSharedPreferences(Constants.APP_PACKAGE, Context.MODE_PRIVATE);
                                int lastServingNumber = prefs.getInt(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, codeQR), 0);

                                if (jtk.getToken() > currentlyServingNumber && lastServingNumber != currentlyServingNumber) {
                                    String notificationMessage = String.format(getApplicationContext().getString(R.string.position_in_queue), jtk.afterHowLong());

                                    prefs.edit().putInt(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, codeQR), currentlyServingNumber).apply();
                                    // Add wait time to notification message
                                    try {
                                        long avgServiceTime = jtk.getAverageServiceTime() != 0
                                                ? jtk.getAverageServiceTime()
                                                : prefs.getLong(String.format(Constants.ESTIMATED_WAIT_TIME_PREF_KEY, codeQR), 0);
                                        String waitTime = TokenStatusUtils.calculateEstimatedWaitTime(avgServiceTime, jtk.afterHowLong(), QueueStatusEnum.N, jtk.getStartHour());
                                        if (!TextUtils.isEmpty(waitTime)) {
                                            notificationMessage = notificationMessage + String.format("\nWait time: %1$s", waitTime);
                                        }
                                    } catch (Exception e) {
                                        Log.e("", "Error setting wait time reason: " + e.getLocalizedMessage(), e);
                                    }
                                    sendNotification(title, notificationMessage, true, imageUrl, jtk.getToken() - Integer.parseInt(currentServing)); // pass null to show only notification with no action
                                }

                                if (jtk.getToken() <= currentlyServingNumber) {
                                    // Clear the App Shared Preferences entry for this queue
                                    prefs.edit().remove(String.format(Constants.ESTIMATED_WAIT_TIME_PREF_KEY, codeQR)).apply();
                                    prefs.edit().remove(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, codeQR)).apply();
                                }

                                // Check if User's turn then start Buzzer.
                                if (displayBuzzer && currentlyServingNumber == jtk.getToken()) {
                                    Intent buzzerIntent = new Intent(this, BlinkerActivity.class);
                                    buzzerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(buzzerIntent);
                                    if (LaunchActivity.isMsgAnnouncementEnable()) {
                                        LaunchActivity.getLaunchActivity().makeAnnouncement(jsonData.getJsonTextToSpeeches(), mappedData.get("mi"));
                                    }
                                }
                            }
                        }
                    }
                    if (jsonData instanceof JsonMedicalFollowUp) {
                        Log.e("JsonMedicalFollowUp", jsonData.toString());
                        NotificationDB.insertNotification(
                                NotificationDB.KEY_NOTIFY,
                                ((JsonMedicalFollowUp) jsonData).getCodeQR(),
                                jsonData.getBody(),
                                jsonData.getTitle(),
                                BusinessTypeEnum.PA.getName(),
                                imageUrl);
                    } else if (jsonData instanceof JsonTopicAppointmentData) {
                        Log.e("JsonTopicAppointData", jsonData.toString());
                        NotificationDB.insertNotification(
                                NotificationDB.KEY_NOTIFY,
                                "",
                                jsonData.getBody(),
                                jsonData.getTitle(),
                                BusinessTypeEnum.PA.getName(),
                                jsonData.getImageURL());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error reading message " + e.getLocalizedMessage(), e);
                sendNotification(title, body, false, imageUrl);
            }
        }
    }

    private void sendNotification(String title, String messageBody, String codeQR, boolean isReview, String token, String imageUrl) {
        new CreateBigImageNotificationWithReview(title, messageBody, codeQR, isReview, token, imageUrl).execute();
    }

    private void sendNotification(String title, String messageBody, boolean isVibrate, String imageUrl) {
        new CreateBigImageNotification(title, messageBody, imageUrl, isVibrate).execute();
    }

    private void sendNotification(String title, String messageBody, boolean isVibrate, String imageUrl, int notificationPriority) {
        new CreateBigImageQueueNotification(title, messageBody, imageUrl, isVibrate, notificationPriority).execute();
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.notification_icon : R.mipmap.launcher;
    }

    private Bitmap getNotificationBitmap() {
        return BitmapFactory.decodeResource(getResources(), R.mipmap.launcher);
    }

    private void setAlarm(JsonMedicalFollowUp jsonMedicalFollowUp) {
        try {
            Date startDate = CommonHelper.stringToDate(jsonMedicalFollowUp.getPopFollowUpAlert());
            Date endDate = CommonHelper.stringToDate(jsonMedicalFollowUp.getFollowUpDay());
            long duration = endDate.getTime() - startDate.getTime();
            long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);
            Log.e("Difference in day: ", String.valueOf(diffInDays));

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
            FirebaseCrashlytics.getInstance().log("Failed to set alarm " + e.getLocalizedMessage());
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private class CreateBigImageQueueNotification extends AsyncTask<String, Void, Bitmap> {
        private String imageUrl = "";
        private String title, messageBody;
        private boolean isVibrate;
        private int notificationPriority;

        public CreateBigImageQueueNotification(String title, String message, String imageUrl, boolean isVibrate, int notificationPriority) {
            this.imageUrl = imageUrl;
            this.messageBody = message;
            this.title = title;
            this.isVibrate = isVibrate;
            this.notificationPriority = Math.abs(notificationPriority);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            if (TextUtils.isEmpty(imageUrl)) {
                return null;
            }

            Bitmap bitmap;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                return bitmap;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int notificationId = 1;
            String channelNoSound = "channel_q_no_sound";
            String channelWithSound = "channel_q_with_sound";

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String channelName = "Channel Name";
                NotificationChannel mChannel;
                if (notificationPriority <= 25) {
                    mChannel = new NotificationChannel(channelWithSound, channelName, NotificationManager.IMPORTANCE_HIGH);
                    mChannel.setSound(defaultSoundUri, null);
                } else {
                    mChannel = new NotificationChannel(channelNoSound, channelName, NotificationManager.IMPORTANCE_LOW);
                    mChannel.setSound(null, null);
                }
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),
                    notificationPriority <= 10 ? channelWithSound : channelNoSound)
                    .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorMobile))
                    .setSmallIcon(getNotificationIcon())
                    .setLargeIcon(getNotificationBitmap())
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    //.setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                    .setLights(Color.parseColor("#ffb400"), 50, 10);
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                if (notificationPriority <= 10) {
                    mBuilder.setPriority(Notification.PRIORITY_HIGH);
                    mBuilder.setSound(defaultSoundUri);
                }
            }

            if (bitmap != null) {
                mBuilder.setStyle(new NotificationCompat.BigPictureStyle()   //Set the Image in Big picture Style with text.
                        .bigPicture(bitmap)
                        //.setSummaryText(message)
                        .bigLargeIcon(null));
            }
            if (isVibrate) {
                mBuilder.setVibrate(new long[]{500, 500});
            }

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
    }

    private class CreateBigImageNotification extends AsyncTask<String, Void, Bitmap> {
        private String imageUrl = "";
        private String title, messageBody;
        private boolean isVibrate;

        public CreateBigImageNotification(String title, String message, String imageUrl, boolean isVibrate) {
            this.imageUrl = imageUrl;
            this.messageBody = message;
            this.title = title;
            this.isVibrate = isVibrate;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            if (TextUtils.isEmpty(imageUrl)) {
                return null;
            }

            Bitmap bitmap;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                return bitmap;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int notificationId = 1;
            String channelId = "channel-01";

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String channelName = "Channel Name";
                int importance = MyApplication.isNotificationSoundEnable()
                        ? NotificationManager.IMPORTANCE_HIGH
                        : NotificationManager.IMPORTANCE_LOW;
                NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorMobile))
                    .setSmallIcon(getNotificationIcon())
                    .setLargeIcon(getNotificationBitmap())
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                    .setLights(Color.parseColor("#ffb400"), 50, 10);
            if (MyApplication.isNotificationSoundEnable()) {
                mBuilder.setSound(defaultSoundUri);
            } else {
                mBuilder.setPriority(NotificationCompat.PRIORITY_LOW);
            }
            if (bitmap != null) {
                mBuilder.setStyle(new NotificationCompat.BigPictureStyle()   //Set the Image in Big picture Style with text.
                        .bigPicture(bitmap)
                        //.setSummaryText(message)
                        .bigLargeIcon(null));
            }
            if (isVibrate) {
                mBuilder.setVibrate(new long[]{500, 500});
            }

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
    }

    private class CreateBigImageNotificationWithReview extends AsyncTask<String, Void, Bitmap> {
        private String imageUrl;
        private String title, messageBody, codeQR, token;
        private boolean isReview;

        public CreateBigImageNotificationWithReview(String title, String messageBody, String codeQR, boolean isReview, String token, String imageUrl) {
            this.imageUrl = imageUrl;
            this.messageBody = messageBody;
            this.title = title;
            this.codeQR = codeQR;
            this.token = token;
            this.isReview = isReview;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            if (TextUtils.isEmpty(imageUrl)) {
                return null;
            }

            Bitmap bitmap;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                return bitmap;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
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
                int importance = MyApplication.isNotificationSoundEnable()
                        ? NotificationManager.IMPORTANCE_HIGH
                        : NotificationManager.IMPORTANCE_LOW;
                NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorMobile))
                    .setSmallIcon(getNotificationIcon())
                    .setLargeIcon(getNotificationBitmap())
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                    .setLights(Color.parseColor("#ffb400"), 50, 10);
            if (MyApplication.isNotificationSoundEnable()) {
                mBuilder.setSound(defaultSoundUri);
            } else {
                mBuilder.setPriority(NotificationCompat.PRIORITY_LOW);
            }
            if (bitmap != null) {
                mBuilder.setStyle(new NotificationCompat.BigPictureStyle()   //Set the Image in Big picture Style with text.
                        .bigPicture(bitmap)
                        //.setSummaryText(message)
                        .bigLargeIcon(null));
            }
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addNextIntent(notificationIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    Constants.requestCodeNotification,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            mBuilder.setContentIntent(resultPendingIntent);

            notificationManager.notify(notificationId, mBuilder.build());
        }
    }
}
