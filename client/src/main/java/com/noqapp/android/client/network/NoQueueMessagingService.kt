package com.noqapp.android.client.network

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.AsyncTask
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.noqapp.android.client.R
import com.noqapp.android.client.model.NotificationApiCall
import com.noqapp.android.client.model.database.DatabaseHelper
import com.noqapp.android.client.model.database.DatabaseTable
import com.noqapp.android.client.model.database.utils.NotificationDB
import com.noqapp.android.client.model.fcm.JsonClientTokenAndQueueData
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueueList
import com.noqapp.android.client.presenter.beans.ReviewData
import com.noqapp.android.client.utils.*
import com.noqapp.android.client.views.activities.AppInitialize
import com.noqapp.android.client.views.activities.BlinkerActivity
import com.noqapp.android.client.views.activities.LaunchActivity
import com.noqapp.android.client.views.receivers.AlarmReceiver
import com.noqapp.android.client.views.version_2.HomeActivity
import com.noqapp.android.client.views.version_2.db.NoQueueAppDB
import com.noqapp.android.common.beans.ErrorEncounteredJson
import com.noqapp.android.common.beans.JsonQueueChangeServiceTime
import com.noqapp.android.common.beans.JsonResponse
import com.noqapp.android.common.beans.body.Notification
import com.noqapp.android.common.fcm.data.*
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech
import com.noqapp.android.common.model.types.*
import com.noqapp.android.common.pojos.DisplayNotification
import com.noqapp.android.common.presenter.NotificationPresenter
import com.noqapp.android.common.utils.CommonHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.StringUtils
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class NoQueueMessagingService : FirebaseMessagingService(), NotificationPresenter {

    private val TAG = NoQueueMessagingService::class.java.simpleName

    // Clears notification tray messages
    fun clearNotifications(context: Context) {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    fun subscribeTopics(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic + "_A")
    }

    fun unSubscribeTopics(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic + "_A")
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d("NEW_TOKEN", s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom())

        // Check if message contains a data payload.

        // Check if message contains a data payload.
        if (remoteMessage.getData().isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

        clearNotifications(applicationContext)

        // Check if message contains a notification payload.
        val mappedData = remoteMessage.data

        if (mappedData.isNotEmpty()) {
            val title = mappedData["title"]
            val body = mappedData["body"]
            val imageUrl = mappedData["imageURL"]
            val messageOrigin = MessageOriginEnum.valueOf(remoteMessage.data[Constants.MESSAGE_ORIGIN]!!)

            val gson = Gson()
            var jsonData: JsonData? = null
            when (messageOrigin) {
                MessageOriginEnum.QA -> try {
                    jsonData = gson.fromJson(mappedData.toString(), JsonTopicAppointmentData::class.java)
                    //             mapper.readValue(JSONObject(remoteMessage.data).toString(), JsonTopicAppointmentData::class.java)
                    Log.d("FCM", jsonData.toString())
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.QA)
                    FirebaseCrashlytics.getInstance().recordException(e)
                    e.printStackTrace()
                }
                MessageOriginEnum.Q -> try {
                    var jsonTextToSpeeches: List<JsonTextToSpeech?>? = null
                    val containsTextToSpeeches = mappedData.containsKey("textToSpeeches")
                    if (containsTextToSpeeches) {
                        jsonTextToSpeeches = gson.fromJson<List<JsonTextToSpeech?>?>(mappedData["textToSpeeches"], object : TypeToken<List<JsonTextToSpeech?>?>() {}.type)
                        //jsonTextToSpeeches = mapper.readValue(mappedData["textToSpeeches"], object : TypeReference<List<JsonTextToSpeech?>?>() {})
                        //TODO(hth) Temp code. Removed as parsing issue.
                        mappedData.remove("textToSpeeches")
                    }
                    jsonData = gson.fromJson(mappedData.toString(), JsonTopicQueueData::class.java)
                    //jsonData = mapper.readValue(JSONObject(mappedData).toString(), JsonTopicQueueData::class.java)
                    if (null != jsonTextToSpeeches) {
                        jsonData.setJsonTextToSpeeches(jsonTextToSpeeches)
                    }
                    Log.d("FCM", jsonData.toString())
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.Q)
                    FirebaseCrashlytics.getInstance().recordException(e)
                    e.printStackTrace()
                }
                MessageOriginEnum.CQO -> try {
                    val jsonTokenAndQueueList = JsonTokenAndQueueList()
                    jsonTokenAndQueueList.tokenAndQueues = gson.fromJson<List<JsonTokenAndQueue>>(mappedData["tqs"], object : TypeToken<List<JsonTokenAndQueue?>?>() {}.type)
                    val jsonClientTokenAndQueueData = gson.fromJson(mappedData.toString(), JsonClientTokenAndQueueData::class.java)
                    jsonClientTokenAndQueueData.tokenAndQueues = jsonTokenAndQueueList.tokenAndQueues
                    jsonData = jsonClientTokenAndQueueData
                    Log.d("FCM", jsonData.toString())
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.CQO)
                    FirebaseCrashlytics.getInstance().recordException(e)
                    e.printStackTrace()
                }
                MessageOriginEnum.QR -> try {
                    jsonData = gson.fromJson(mappedData.toString(), JsonClientData::class.java)
                    Log.d("FCM Queue Review", jsonData.toString())
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.QR)
                    FirebaseCrashlytics.getInstance().recordException(e)
                    e.printStackTrace()
                }
                MessageOriginEnum.OR -> try {
                    jsonData = gson.fromJson(mappedData.toString(), JsonClientOrderData::class.java)
                    Log.d("FCM Order Review", jsonData.toString())
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.OR)
                    FirebaseCrashlytics.getInstance().recordException(e)
                    e.printStackTrace()
                }
                MessageOriginEnum.O -> try {
                    var jsonTextToSpeeches: List<JsonTextToSpeech?>? = null
                    val containsTextToSpeeches = mappedData.containsKey("textToSpeeches")
                    if (containsTextToSpeeches) {
                        jsonTextToSpeeches = gson.fromJson(mappedData["textToSpeeches"], object : TypeToken<List<JsonTextToSpeech?>?>() {}.type)
                        //TODO(hth) Temp code. Removed as parsing issue.
                        mappedData.remove("textToSpeeches")
                    }
                    jsonData = gson.fromJson(mappedData.toString(), JsonTopicOrderData::class.java)
                    if (null != jsonTextToSpeeches) {
                        jsonData.setJsonTextToSpeeches(jsonTextToSpeeches)
                    }
                    Log.d("FCM order ", jsonData.toString())
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.O)
                    FirebaseCrashlytics.getInstance().recordException(e)
                    e.printStackTrace()
                }
                MessageOriginEnum.A, MessageOriginEnum.D -> try {
                    jsonData = gson.fromJson(mappedData.toString(), JsonAlertData::class.java)
                    Log.d("FCM Review store", jsonData.toString())
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.D)
                    FirebaseCrashlytics.getInstance().recordException(e)
                    e.printStackTrace()
                }
                MessageOriginEnum.MF -> try {
                    jsonData = gson.fromJson(mappedData.toString(), JsonMedicalFollowUp::class.java)
                    Log.d("FCM Medical Followup", jsonData.toString())
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.MF)
                    FirebaseCrashlytics.getInstance().recordException(e)
                    e.printStackTrace()
                }
                MessageOriginEnum.QCT -> try {
                    val jsonChangeServiceTimeData = gson.fromJson(mappedData.toString(), JsonChangeServiceTimeData::class.java)
                    var jsonQueueChangeServiceTimes: List<JsonQueueChangeServiceTime?>? = LinkedList()
                    if (null != mappedData["qcsts"]) {
                        jsonQueueChangeServiceTimes = gson.fromJson(mappedData["qcsts"], object : TypeToken<List<JsonQueueChangeServiceTime?>?>() {}.type)
                    }
                    jsonChangeServiceTimeData.jsonQueueChangeServiceTimes = jsonQueueChangeServiceTimes
                    jsonData = jsonChangeServiceTimeData
                    Log.d("Update time slot", jsonChangeServiceTimeData.toString())
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.QCT)
                    FirebaseCrashlytics.getInstance().recordException(e)
                    e.printStackTrace()
                }
                MessageOriginEnum.M -> {
                }
                MessageOriginEnum.IE -> {
                }
                else -> {
                }
            }

            if (jsonData != null && !TextUtils.isEmpty(jsonData.id)) {
                callNotificationViewApi(jsonData.id)
            }

            try {
                if (!AppUtils.isAppIsInBackground(applicationContext)) {
                    // app is in foreground, broadcast the push message
                    val pushNotification = Intent(Constants.PUSH_NOTIFICATION)
                    pushNotification.putExtra("jsonData", jsonData)
                    pushNotification.putExtra(Constants.FIREBASE_TYPE, mappedData[Constants.FIREBASE_TYPE])
                    pushNotification.putExtra(Constants.CODE_QR, mappedData[Constants.CODE_QR])
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
                    if (MessageOriginEnum.Q == messageOrigin) {
                        // Update Currently serving in app preferences
                        val prefs = applicationContext.getSharedPreferences(Constants.APP_PACKAGE, MODE_PRIVATE)
                        prefs.edit().putInt(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, mappedData[Constants.CODE_QR]), mappedData[Constants.CURRENTLY_SERVING]!!.toInt()).apply()
                        prefs.edit().putInt(String.format(Constants.DISPLAY_SERVING_NUMBER_PREF_KEY, mappedData[Constants.CODE_QR]), mappedData[Constants.DISPLAY_SERVING_NUMBER]!!.toInt()).apply()
                    }
                } else {
                    // app is in background, show the notification in notification tray
                    //save data to database
                    val payload = mappedData[Constants.FIREBASE_TYPE]
                    val codeQR = mappedData[Constants.CODE_QR]
                    if (null == AppInitialize.dbHandler) {
                        AppInitialize.dbHandler = DatabaseHelper.getsInstance(applicationContext)
                    }
                    /*
                     * When u==S then it is re-view
                     *      u==N then it is skip(Rejoin) Pending task
                     */
                    if (StringUtils.isNotBlank(payload) && payload.equals(FirebaseMessageTypeEnum.P.getName(), ignoreCase = true)) {
                        if (StringUtils.isNotBlank(codeQR)) {
                            val currentServing = mappedData[Constants.CURRENTLY_SERVING]
                            if (currentServing != null) {

                                NoQueueAppDB.dbInstance(this).tokenAndQueueDao().getCurrentQueueObjectList(codeQR).observeForever {
                                    it.forEach { jsonTokenAndQueue ->
                                        jsonTokenAndQueue.servingNumber = currentServing.toInt()
                                        if (jsonTokenAndQueue.isTokenExpired && it.size == 1) {
                                            //TODO @chandra write logic for unsubscribe
                                            unSubscribeTopics(jsonTokenAndQueue.topic)
                                        }

                                        mappedData[Constants.DISPLAY_SERVING_NUMBER]?.let { displayServingNumber ->
                                            GlobalScope.launch {
                                                withContext(Dispatchers.IO) {
                                                    NoQueueAppDB.dbInstance(this@NoQueueMessagingService).tokenAndQueueDao().updateCurrentListQueueObject(codeQR, currentServing, displayServingNumber, jsonTokenAndQueue.token)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (jsonData is JsonAlertData) {
                                Log.e("IN JsonAlertData", jsonData.toString())
                                val displayNotification = DisplayNotification()
                                displayNotification.type = DatabaseTable.Notification.KEY_NOTIFY
                                displayNotification.codeQR = jsonData.codeQR
                                //from my point of view it is wrong....lets discuss more bot it
                                displayNotification.body = jsonData.getLocalLanguageMessageBody(LaunchActivity.language)
                                displayNotification.title = jsonData.title
                                displayNotification.businessType = BusinessTypeEnum.PA
                                displayNotification.imageUrl = jsonData.imageURL
                                displayNotification.status = DatabaseTable.Notification.KEY_UNREAD
                                displayNotification.createdDate = CommonHelper.changeUTCDateToString(Date())

                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        NoQueueAppDB.dbInstance(this@NoQueueMessagingService).notificationDao().insertNotification(displayNotification)
                                    }
                                }

                                /*NotificationDB.insertNotification(
                                        NotificationDB.KEY_NOTIFY,
                                        jsonData.codeQR,
                                        jsonData.getLocalLanguageMe*//**//*ssageBody(LaunchActivity.language),
                                        title,
                                        jsonData.businessType.getName(),
                                        imageUrl)*/
                                sendNotification(title, jsonData.getLocalLanguageMessageBody(LaunchActivity.language), false, imageUrl)
                            } else if (jsonData is JsonClientData) {
                                Log.e("IN JsonClientData", jsonData.toString())
                                val token = jsonData.token.toString()
                                val qid = jsonData.queueUserId
                                if (jsonData.queueUserState.getName().equals(QueueUserStateEnum.S.getName(), ignoreCase = true)) {
                                    /*
                                     * Save codeQR of review & show the review screen on app
                                     * resume if there is any record in Review DB for queue review key
                                     */
                                    NoQueueAppDB.dbInstance(this).reviewDao().getReviewData(codeQR, token).observeForever {
                                        it?.let {
                                            it.isReviewShown = "1"
                                            GlobalScope.launch {
                                                withContext(Dispatchers.IO) {
                                                    NoQueueAppDB.dbInstance(this@NoQueueMessagingService).reviewDao().update(it)
                                                }
                                            }
                                        } ?: run {
                                            val reviewData = ReviewData()
                                            reviewData.isReviewShown = "1"
                                            reviewData.codeQR = codeQR
                                            reviewData.token = token
                                            reviewData.queueUserId = qid
                                            reviewData.isBuzzerShow = "-1"
                                            reviewData.isSkipped = "-1"
                                            reviewData.gotoCounter = ""

                                            GlobalScope.launch {
                                                withContext(Dispatchers.IO) {
                                                    NoQueueAppDB.dbInstance(this@NoQueueMessagingService).reviewDao().insertReviewData(reviewData)
                                                }
                                            }
                                        }
                                    }

                                    /*val reviewData: ReviewData = ReviewDB.getValue(codeQR, token)
                                    if (null != reviewData) {
                                        val cv = ContentValues()
                                        cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, 1)
                                        ReviewDB.updateReviewRecord(codeQR, token, cv)
                                        // update
                                    } else {
                                        //insert
                                        val cv = ContentValues()
                                        cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, 1)
                                        cv.put(DatabaseTable.Review.CODE_QR, codeQR)
                                        cv.put(DatabaseTable.Review.TOKEN, token)
                                        cv.put(DatabaseTable.Review.QID, qid)
                                        cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1")
                                        cv.put(DatabaseTable.Review.KEY_SKIP, "-1")
                                        cv.put(DatabaseTable.Review.KEY_GOTO, "")
                                        ReviewDB.insert(cv)
                                    }*/
                                    sendNotification(title, jsonData.getLocalLanguageMessageBody(LaunchActivity.language), codeQR, true, token, imageUrl) //pass codeQR to open review screen
                                } else if (jsonData.queueUserState.getName().equals(QueueUserStateEnum.N.getName(), ignoreCase = true)) {
                                    NoQueueAppDB.dbInstance(this).reviewDao().getReviewData(codeQR, token).observeForever {
                                        it?.let {
                                            it.isReviewShown = "1"
                                            GlobalScope.launch {
                                                withContext(Dispatchers.IO) {
                                                    NoQueueAppDB.dbInstance(this@NoQueueMessagingService).reviewDao().update(it)
                                                }
                                            }
                                        } ?: run {
                                            val reviewData = ReviewData()
                                            reviewData.isReviewShown = "-1"
                                            reviewData.codeQR = codeQR
                                            reviewData.token = token
                                            reviewData.queueUserId = qid
                                            reviewData.isBuzzerShow = "-1"
                                            reviewData.isSkipped = "-1"
                                            reviewData.gotoCounter = ""

                                            GlobalScope.launch {
                                                withContext(Dispatchers.IO) {
                                                    NoQueueAppDB.dbInstance(this@NoQueueMessagingService).reviewDao().insertReviewData(reviewData)
                                                }
                                            }
                                        }
                                    }

                                    /* val reviewData: ReviewData = ReviewDB.getValue(codeQR, token)
                                     if (null != reviewData) {
                                         val cv = ContentValues()
                                         cv.put(DatabaseTable.Review.KEY_SKIP, -1)
                                         ReviewDB.updateReviewRecord(codeQR, token, cv)
                                         // update
                                     } else {
                                         //insert
                                         val cv = ContentValues()
                                         cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, -1)
                                         cv.put(DatabaseTable.Review.CODE_QR, codeQR)
                                         cv.put(DatabaseTable.Review.TOKEN, token)
                                         cv.put(DatabaseTable.Review.QID, qid)
                                         cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1")
                                         cv.put(DatabaseTable.Review.KEY_SKIP, "-1")
                                         cv.put(DatabaseTable.Review.KEY_GOTO, "")
                                         ReviewDB.insert(cv)
                                     }*/

                                    sendNotification(title, jsonData.getLocalLanguageMessageBody(LaunchActivity.language), codeQR, false, token, imageUrl) //pass codeQR to open skip screen
                                }
                            } else if (jsonData is JsonClientTokenAndQueueData) {
                                val jsonTokenAndQueueList = jsonData.tokenAndQueues
                                if (null != jsonTokenAndQueueList && jsonTokenAndQueueList.size > 0) {
                                    GlobalScope.launch {
                                        withContext(Dispatchers.IO) {
                                            NoQueueAppDB.dbInstance(this@NoQueueMessagingService).tokenAndQueueDao().saveCurrentQueue(jsonTokenAndQueueList)
                                        }
                                    }
                                    //TokenAndQueueDB.saveCurrentQueue(jsonTokenAndQueueList)
                                }

                                val displayNotification = DisplayNotification()
                                displayNotification.type = DatabaseTable.Notification.KEY_NOTIFY
                                displayNotification.codeQR = jsonData.codeQR
                                displayNotification.body = jsonData.getLocalLanguageMessageBody(LaunchActivity.language)
                                displayNotification.title = jsonData.title
                                displayNotification.businessType = BusinessTypeEnum.PA
                                displayNotification.imageUrl = jsonData.imageURL
                                displayNotification.status = DatabaseTable.Notification.KEY_UNREAD
                                displayNotification.createdDate = CommonHelper.changeUTCDateToString(Date())

                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        NoQueueAppDB.dbInstance(this@NoQueueMessagingService).notificationDao().insertNotification(displayNotification)
                                    }
                                }

                                /*NotificationDB.insertNotification(
                                        NotificationDB.KEY_NOTIFY,
                                        jsonData.codeQR,
                                        jsonData.getLocalLanguageMessageBody(LaunchActivity.language),
                                        jsonData.getTitle(),
                                        BusinessTypeEnum.PA.getName(),
                                        imageUrl)*/
                                for (i in jsonTokenAndQueueList.indices) {
                                    subscribeTopics(jsonTokenAndQueueList[i].topic)
                                }
                                sendNotification(title, jsonData.getLocalLanguageMessageBody(LaunchActivity.language), false, imageUrl)
                            } else if (jsonData is JsonMedicalFollowUp) {
                                Log.e("Alert set:", "data is :$title ---- $body")
                                sendNotification(title, jsonData.getLocalLanguageMessageBody(LaunchActivity.language), true, imageUrl)
                                setAlarm(jsonData as JsonMedicalFollowUp)
                            } else {
                                sendNotification(title, jsonData!!.getLocalLanguageMessageBody(LaunchActivity.language), false, imageUrl)
                            }
                        } else {
                            sendNotification(title, jsonData!!.getLocalLanguageMessageBody(LaunchActivity.language), false, imageUrl)
                            if (jsonData is JsonAlertData) {
                                /* When app is on background. Adding to notification table. */
                                Log.e("IN JsonAlertData", jsonData.toString())
                                NotificationDB.insertNotification(
                                        NotificationDB.KEY_NOTIFY,
                                        jsonData.codeQR,
                                        jsonData.getLocalLanguageMessageBody(LaunchActivity.language),
                                        title,
                                        jsonData.businessType.getName(),
                                        imageUrl)
                            }
                        }
                    } else if (StringUtils.isNotBlank(payload) && payload.equals(FirebaseMessageTypeEnum.C.getName(), ignoreCase = true)) {
                        if (jsonData is JsonChangeServiceTimeData) {


                            NoQueueAppDB.dbInstance(this@NoQueueMessagingService).tokenAndQueueDao().findByQRCode(jsonData.codeQR).observeForever { jsonTokenAndQueue ->
                                val jsonQueueChangeServiceTimes = jsonData.jsonQueueChangeServiceTimes
                                for (jsonQueueChangeServiceTime in jsonQueueChangeServiceTimes) {
                                    if (jsonQueueChangeServiceTime.token == jsonTokenAndQueue.token) {
                                        val msg = """${jsonData.getBody()} Token: ${jsonQueueChangeServiceTime.displayToken} Previously: ${jsonQueueChangeServiceTime.oldTimeSlotMessage} Updated: ${jsonQueueChangeServiceTime.updatedTimeSlotMessage}"""
                                        ShowAlertInformation.showInfoDisplayDialog(this@NoQueueMessagingService, jsonData.getTitle(), body)
                                        val displayNotification = DisplayNotification()
                                        displayNotification.type = DatabaseTable.Notification.KEY_NOTIFY
                                        displayNotification.codeQR = jsonData.codeQR
                                        displayNotification.body = msg
                                        displayNotification.title = jsonData.title
                                        displayNotification.businessType = BusinessTypeEnum.PA
                                        displayNotification.imageUrl = jsonData.imageURL
                                        displayNotification.status = DatabaseTable.Notification.KEY_UNREAD
                                        displayNotification.createdDate = CommonHelper.changeUTCDateToString(Date())

                                        GlobalScope.launch {
                                            withContext(Dispatchers.IO) {
                                                NoQueueAppDB.dbInstance(this@NoQueueMessagingService).notificationDao().insertNotification(displayNotification)
                                            }
                                        }
                                    }
                                }
                            }

                            /*val jsonTokenAndQueue: JsonTokenAndQueue = TokenAndQueueDB.findByQRCode(jsonData.codeQR)
                            if (null != jsonTokenAndQueue) {
                                val jsonQueueChangeServiceTimes = jsonData.jsonQueueChangeServiceTimes
                                for (jsonQueueChangeServiceTime in jsonQueueChangeServiceTimes) {
                                    if (jsonQueueChangeServiceTime.token == jsonTokenAndQueue.token) {
                                        Log.e("In JsonChangeServiceTimeData", jsonData.toString())
                                        val msg = """
                                ${jsonData.getBody()}
                                Token: ${(jsonData as JsonChangeServiceTimeData).jsonQueueChangeServiceTimes[0].displayToken}
                                Previously: ${(jsonData as JsonChangeServiceTimeData).jsonQueueChangeServiceTimes[0].oldTimeSlotMessage}
                                Updated: ${(jsonData as JsonChangeServiceTimeData).jsonQueueChangeServiceTimes[0].updatedTimeSlotMessage}
                                """.trimIndent()
                                        NotificationDB.insertNotification(
                                                NotificationDB.KEY_NOTIFY,
                                                jsonData.codeQR,
                                                msg,
                                                jsonData.getTitle(),
                                                jsonData.businessType.getName(),
                                                jsonData.getImageURL())
                                        sendNotification(title, msg, true, imageUrl)
                                    }
                                }
                            }*/
                        } else {
                            var goTo: String? = ""
                            var currentServing = ""
                            var displayServingNumber: String? = ""
                            if (jsonData is JsonTopicQueueData) {
                                Log.e("In JsonTopicQueueData", jsonData.toString())
                                currentServing = jsonData.currentlyServing.toString()
                                displayServingNumber = jsonData.displayServingNumber
                                goTo = jsonData.goTo
                            }
                            if (jsonData is JsonTopicOrderData) {
                                Log.e("In JsonTopicOrderData", jsonData.toString())
                                currentServing = jsonData.currentlyServing.toString()
                                displayServingNumber = jsonData.displayServingNumber
                                goTo = jsonData.goTo
                            }

                            NoQueueAppDB.dbInstance(this).tokenAndQueueDao().getCurrentQueueObjectList(codeQR).observeForever { jsonTokenAndQueueArrayList ->
                                for (i in jsonTokenAndQueueArrayList.indices) {
                                    val jtk = jsonTokenAndQueueArrayList[i]
                                    var displayBuzzer = false
                                    /*
                                 * Save codeQR of goto & show it in after join screen on app
                                 * Review DB for review key && current serving == token no.
                                 */
                                    if (currentServing.toInt() == jtk.token) {

                                        NoQueueAppDB.dbInstance(this).reviewDao().getReviewData(codeQR, currentServing).observeForever {
                                            it?.let {
                                                it.isReviewShown = "1"
                                                GlobalScope.launch {
                                                    withContext(Dispatchers.IO) {
                                                        NoQueueAppDB.dbInstance(this@NoQueueMessagingService).reviewDao().update(it)
                                                    }
                                                }
                                            } ?: run {
                                                val reviewData = ReviewData()
                                                reviewData.isReviewShown = "-1"
                                                reviewData.codeQR = codeQR
                                                reviewData.token = currentServing
                                                reviewData.queueUserId = jtk.queueUserId
                                                reviewData.isBuzzerShow = "-1"
                                                reviewData.isSkipped = "-1"
                                                reviewData.gotoCounter = ""

                                                GlobalScope.launch {
                                                    withContext(Dispatchers.IO) {
                                                        NoQueueAppDB.dbInstance(this@NoQueueMessagingService).reviewDao().insertReviewData(reviewData)
                                                    }
                                                }
                                            }
                                        }


                                        /*val reviewData: ReviewData = ReviewDB.getValue(codeQR, currentServing)
                                        if (null != reviewData) {
                                            val cv = ContentValues()
                                            cv.put(DatabaseTable.Review.KEY_GOTO, goTo)
                                            ReviewDB.updateReviewRecord(codeQR, currentServing, cv)
                                            // update
                                        } else {
                                            //insert
                                            displayBuzzer = true
                                            val cv = ContentValues()
                                            cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, -1)
                                            cv.put(DatabaseTable.Review.CODE_QR, codeQR)
                                            cv.put(DatabaseTable.Review.TOKEN, currentServing)
                                            cv.put(DatabaseTable.Review.QID, jtk.queueUserId)
                                            cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1")
                                            cv.put(DatabaseTable.Review.KEY_SKIP, "-1")
                                            cv.put(DatabaseTable.Review.KEY_GOTO, goTo)
                                            ReviewDB.insert(cv)
                                        }*/
                                    }
                                    //update DB & after join screen
                                    jtk.servingNumber = currentServing.toInt()
                                    if (jtk.isTokenExpired && jsonTokenAndQueueArrayList.size == 1) {
                                        //un-subscribe from the topic
                                        unSubscribeTopics(jtk.topic)
                                    }

                                    GlobalScope.launch {
                                        withContext(Dispatchers.IO) {
                                            displayServingNumber?.let {
                                                NoQueueAppDB.dbInstance(this@NoQueueMessagingService).tokenAndQueueDao().updateCurrentListQueueObject(codeQR, currentServing, displayServingNumber, jtk.token.toInt())
                                            }
                                        }
                                    }

                                    // Check if user needs to be notified
                                    val currentlyServingNumber = currentServing.toInt()
                                    val prefs = applicationContext.getSharedPreferences(Constants.APP_PACKAGE, MODE_PRIVATE)
                                    val lastServingNumber = prefs.getInt(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, codeQR), 0)
                                    if (jtk.token > currentlyServingNumber && lastServingNumber != currentlyServingNumber) {
                                        var notificationMessage = String.format(applicationContext.getString(R.string.position_in_queue), jtk.afterHowLong())
                                        prefs.edit().putInt(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, codeQR), currentlyServingNumber).apply()
                                        // Add wait time to notification message
                                        try {
                                            when (jtk.businessType) {
                                                BusinessTypeEnum.CD, BusinessTypeEnum.CDQ -> {
                                                    val slot = jtk.timeSlotMessage
                                                    notificationMessage += String.format(applicationContext.getString(R.string.time_slot_formatted_newline), slot)
                                                }
                                                else -> {
                                                    val avgServiceTime = if (jtk.averageServiceTime != 0L) jtk.averageServiceTime else prefs.getLong(String.format(Constants.ESTIMATED_WAIT_TIME_PREF_KEY, codeQR), 0)
                                                    val waitTime = TokenStatusUtils.calculateEstimatedWaitTime(
                                                            avgServiceTime,
                                                            jtk.afterHowLong(),
                                                            QueueStatusEnum.N,
                                                            jtk.startHour,
                                                            applicationContext)
                                                    if (!TextUtils.isEmpty(waitTime)) {
                                                        notificationMessage += String.format(applicationContext.getString(R.string.wait_time_formatted_newline), waitTime)
                                                    }
                                                }
                                            }
                                        } catch (e: java.lang.Exception) {
                                            Log.e("", "Error setting wait time reason: " + e.localizedMessage, e)
                                        }
                                        sendNotification(title, notificationMessage, true, imageUrl, jtk.token - currentServing.toInt()) // pass null to show only notification with no action
                                    }
                                    if (jtk.token <= currentlyServingNumber) {
                                        // Clear the App Shared Preferences entry for this queue
                                        prefs.edit().remove(String.format(Constants.ESTIMATED_WAIT_TIME_PREF_KEY, codeQR)).apply()
                                        prefs.edit().remove(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, codeQR)).apply()
                                    }

                                    // Check if User's turn then start Buzzer.
                                    if (displayBuzzer && currentlyServingNumber == jtk.token) {
                                        val buzzerIntent = Intent(this, BlinkerActivity::class.java)
                                        buzzerIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(buzzerIntent)
                                        if (AppInitialize.isMsgAnnouncementEnable() && null != LaunchActivity.getLaunchActivity()) {
                                            LaunchActivity.getLaunchActivity().makeAnnouncement(jsonData!!.jsonTextToSpeeches, mappedData["mi"])
                                        }
                                    }
                                }

                            }

                            //val jsonTokenAndQueueArrayList: ArrayList<JsonTokenAndQueue> = TokenAndQueueDB.getCurrentQueueObjectList(codeQR)
                        }
                    }
                    if (jsonData is JsonMedicalFollowUp) {
                        Log.e("JsonMedicalFollowUp", jsonData.toString())
                        NotificationDB.insertNotification(
                                NotificationDB.KEY_NOTIFY,
                                jsonData.codeQR,
                                jsonData.getBody(),
                                jsonData.getTitle(),
                                BusinessTypeEnum.PA.getName(),
                                imageUrl)
                    } else if (jsonData is JsonTopicAppointmentData) {
                        Log.e("JsonTopicAppointData", jsonData.toString())
                        NotificationDB.insertNotification(
                                NotificationDB.KEY_NOTIFY,
                                "",
                                jsonData.getBody(),
                                jsonData.getTitle(),
                                BusinessTypeEnum.PA.getName(),
                                jsonData.getImageURL())
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.e(TAG, "Error reading message " + e.localizedMessage, e)
                FirebaseCrashlytics.getInstance().log("Failed to parse message " + e.localizedMessage)
                FirebaseCrashlytics.getInstance().recordException(e)
                sendNotification(title, jsonData?.getLocalLanguageMessageBody(LaunchActivity.language), false, imageUrl)
            }

        }
    }

    private fun sendNotification(title: String?, messageBody: String?, codeQR: String?, isReview: Boolean, token: String, imageUrl: String?) {
        CreateBigImageNotificationWithReview(title, messageBody, codeQR, isReview, token, imageUrl).execute()
    }

    private fun sendNotification(title: String?, messageBody: String?, isVibrate: Boolean, imageUrl: String?) {
        CreateBigImageNotification(title, messageBody, imageUrl, isVibrate).execute()
    }

    private fun sendNotification(title: String?, messageBody: String?, isVibrate: Boolean, imageUrl: String?, notificationPriority: Int) {
        CreateBigImageQueueNotification(title, messageBody, imageUrl, isVibrate, notificationPriority).execute()
    }

    private fun getNotificationIcon(): Int {
        return R.mipmap.notification_icon
    }

    private fun getNotificationBitmap(): Bitmap? {
        return BitmapFactory.decodeResource(resources, R.mipmap.launcher)
    }

    private fun setAlarm(jsonMedicalFollowUp: JsonMedicalFollowUp) {
        try {
            val startDate = CommonHelper.stringToDate(jsonMedicalFollowUp.popFollowUpAlert)
            val endDate = CommonHelper.stringToDate(jsonMedicalFollowUp.followUpDay)
            val duration = endDate.time - startDate.time
            val diffInDays = TimeUnit.MILLISECONDS.toDays(duration)
            Log.e("Difference in day: ", diffInDays.toString())
            val c = Calendar.getInstance()
            c.time = startDate
            val calendar = Calendar.getInstance()
            calendar[c[Calendar.YEAR], c[Calendar.MONTH]] = c[Calendar.DAY_OF_MONTH]
            calendar[Calendar.HOUR_OF_DAY] = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
            calendar[Calendar.MINUTE] = Calendar.getInstance()[Calendar.MINUTE] + 1
            calendar[Calendar.SECOND] = Calendar.getInstance()[Calendar.SECOND]
            val intent = Intent(this, AlarmReceiver::class.java)
            intent.putExtra("title", jsonMedicalFollowUp.title)
            intent.putExtra("body", jsonMedicalFollowUp.body)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
            Log.e("Alarm set", "Done Alarm")
        } catch (e: java.lang.Exception) {
            FirebaseCrashlytics.getInstance().log("Failed to set alarm " + e.localizedMessage)
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    override fun authenticationFailure() {

    }

    override fun responseErrorPresenter(eej: ErrorEncounteredJson?) {

    }

    override fun responseErrorPresenter(errorCode: Int) {

    }

    override fun notificationResponse(jsonResponse: JsonResponse?) {

    }

    inner class CreateBigImageQueueNotification(val title: String?, val messageBody: String?, val imageUrl: String?, val isVibrate: Boolean, val notificationPriority: Int) : AsyncTask<String?, Void?, Bitmap?>() {

        override fun doInBackground(vararg p0: String?): Bitmap? {
            if (TextUtils.isEmpty(imageUrl)) {
                return null
            }
            val bitmap: Bitmap
            return try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                bitmap = BitmapFactory.decodeStream(input)
                bitmap
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = 1
            val channelNoSound = "channel_q_no_sound"
            val channelWithSound = "channel_q_with_sound"
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val highImportance = abs(notificationPriority) <= 10 || abs(notificationPriority) % 5 == 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelName = "Channel Name"
                val mChannel: NotificationChannel
                if (highImportance) {
                    mChannel = NotificationChannel(channelWithSound, channelName, NotificationManager.IMPORTANCE_HIGH)
                    mChannel.setSound(defaultSoundUri, null)
                } else {
                    mChannel = NotificationChannel(channelNoSound, channelName, NotificationManager.IMPORTANCE_LOW)
                    mChannel.setSound(null, null)
                }
                notificationManager.createNotificationChannel(mChannel)
            }
            val mBuilder = NotificationCompat.Builder(applicationContext,
                    if (highImportance) channelWithSound else channelNoSound)
                    .setColor(ContextCompat.getColor(applicationContext, R.color.colorMobile))
                    .setSmallIcon(getNotificationIcon())
                    .setLargeIcon(getNotificationBitmap())
                    .setContentTitle(title)
                    .setContentText(messageBody) //.setAutoCancel(true)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
                    .setLights(Color.parseColor("#ffb400"), 50, 10)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                if (highImportance) {
                    mBuilder.priority = android.app.Notification.PRIORITY_HIGH
                } else {
                    mBuilder.priority = android.app.Notification.PRIORITY_DEFAULT
                }
                mBuilder.setSound(defaultSoundUri)
            }
            if (bitmap != null) {
                mBuilder.setStyle(NotificationCompat.BigPictureStyle() //Set the Image in Big picture Style with text.
                        .bigPicture(bitmap) //.setSummaryText(message)
                        .bigLargeIcon(null))
            }
            if (isVibrate) {
                mBuilder.setVibrate(longArrayOf(500, 500))
            }
            val notificationIntent = Intent(applicationContext, LaunchActivity::class.java)
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), Constants.requestCodeNotification, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            val stackBuilder = TaskStackBuilder.create(applicationContext)
            stackBuilder.addNextIntent(notificationIntent)
            val resultPendingIntent = stackBuilder.getPendingIntent(
                    Constants.requestCodeNotification,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
            mBuilder.setContentIntent(resultPendingIntent)
            notificationManager.notify(notificationId, mBuilder.build())
        }

    }

    inner class CreateBigImageNotification(val title: String?, val messageBody: String?, val imageUrl: String?, val isVibrate: Boolean) : AsyncTask<String?, Void?, Bitmap?>() {

        override fun doInBackground(vararg p0: String?): Bitmap? {
            if (TextUtils.isEmpty(imageUrl)) {
                return null
            }
            val bitmap: Bitmap
            return try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                bitmap = BitmapFactory.decodeStream(input)
                bitmap
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = 1
            val channelId = "channel-01"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelName = "Channel Name"
                val importance = if (AppInitialize.isNotificationSoundEnable()) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_LOW
                val mChannel = NotificationChannel(channelId, channelName, importance)
                notificationManager.createNotificationChannel(mChannel)
            }
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val mBuilder = NotificationCompat.Builder(applicationContext, channelId)
                    .setColor(ContextCompat.getColor(applicationContext, R.color.colorMobile))
                    .setSmallIcon(getNotificationIcon())
                    .setLargeIcon(getNotificationBitmap())
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
                    .setLights(Color.parseColor("#ffb400"), 50, 10)
            if (AppInitialize.isNotificationSoundEnable()) {
                mBuilder.setSound(defaultSoundUri)
            } else {
                mBuilder.priority = NotificationCompat.PRIORITY_LOW
            }
            if (bitmap != null) {
                mBuilder.setStyle(NotificationCompat.BigPictureStyle() //Set the Image in Big picture Style with text.
                        .bigPicture(bitmap) //.setSummaryText(message)
                        .bigLargeIcon(null))
            }
            if (isVibrate) {
                mBuilder.setVibrate(longArrayOf(500, 500))
            }
            val notificationIntent = Intent(applicationContext, LaunchActivity::class.java)
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), Constants.requestCodeNotification, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            val stackBuilder = TaskStackBuilder.create(applicationContext)
            stackBuilder.addNextIntent(notificationIntent)
            val resultPendingIntent = stackBuilder.getPendingIntent(
                    Constants.requestCodeNotification,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
            mBuilder.setContentIntent(resultPendingIntent)
            notificationManager.notify(notificationId, mBuilder.build())
        }

    }

    inner class CreateBigImageNotificationWithReview(private val title: String?, private val messageBody: String?, private val codeQR: String?, private val isReview: Boolean, private val token: String, private val imageUrl: String?) : AsyncTask<String?, Void?, Bitmap?>() {

        override fun doInBackground(vararg p0: String?): Bitmap? {
            if (TextUtils.isEmpty(imageUrl)) {
                return null
            }
            val bitmap: Bitmap
            return try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                bitmap = BitmapFactory.decodeStream(input)
                bitmap
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            val notificationIntent = Intent(applicationContext, HomeActivity::class.java)
            if (null != codeQR) {
                notificationIntent.putExtra(Constants.QRCODE, codeQR)
                notificationIntent.putExtra(Constants.ISREVIEW, isReview)
                notificationIntent.putExtra(Constants.TOKEN, token)
            }
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = 1
            val channelId = "channel-01"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelName = "Channel Name"
                val importance = if (AppInitialize.isNotificationSoundEnable()) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_LOW
                val mChannel = NotificationChannel(channelId, channelName, importance)
                notificationManager.createNotificationChannel(mChannel)
            }
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val mBuilder = NotificationCompat.Builder(applicationContext, channelId)
                    .setColor(ContextCompat.getColor(applicationContext, R.color.colorMobile))
                    .setSmallIcon(getNotificationIcon())
                    .setLargeIcon(getNotificationBitmap())
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
                    .setLights(Color.parseColor("#ffb400"), 50, 10)
            if (AppInitialize.isNotificationSoundEnable()) {
                mBuilder.setSound(defaultSoundUri)
            } else {
                mBuilder.priority = NotificationCompat.PRIORITY_LOW
            }
            if (bitmap != null) {
                mBuilder.setStyle(NotificationCompat.BigPictureStyle() //Set the Image in Big picture Style with text.
                        .bigPicture(bitmap) //.setSummaryText(message)
                        .bigLargeIcon(null))
            }
            val stackBuilder = TaskStackBuilder.create(getApplicationContext())
            stackBuilder.addNextIntent(notificationIntent)
            val resultPendingIntent = stackBuilder.getPendingIntent(
                    Constants.requestCodeNotification,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
            mBuilder.setContentIntent(resultPendingIntent)
            notificationManager.notify(notificationId, mBuilder.build())
        }
    }

    private fun callNotificationViewApi(notificationId: String) {
        val notification = Notification()
        notification.id = notificationId
        val notificationApiCall = NotificationApiCall(this)
        if (UserUtils.isLogin()) {
            notificationApiCall.notificationViewed(
                    UserUtils.getDeviceId(),
                    UserUtils.getEmail(),
                    UserUtils.getAuth(),
                    notification)
        } else {
            notificationApiCall.notificationViewed(
                    UserUtils.getDeviceId(),
                    notification)
        }
    }
}