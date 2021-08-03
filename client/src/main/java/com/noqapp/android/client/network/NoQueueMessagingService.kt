package com.noqapp.android.client.network

import android.app.*
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
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.noqapp.android.client.R
import com.noqapp.android.client.model.api.NotificationAcknowledgeApiImpl
import com.noqapp.android.client.model.open.NotificationAcknowledgeImpl
import com.noqapp.android.client.model.fcm.JsonClientTokenAndQueueData
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue
import com.noqapp.android.client.presenter.beans.ReviewData
import com.noqapp.android.client.utils.*
import com.noqapp.android.client.views.activities.AppInitialize
import com.noqapp.android.client.views.receivers.AlarmReceiver
import com.noqapp.android.client.views.version_2.HomeActivity
import com.noqapp.android.client.views.version_2.db.NoQueueAppDB
import com.noqapp.android.client.views.version_2.db.helper_models.ForegroundNotificationModel
import com.noqapp.android.common.beans.ErrorEncounteredJson
import com.noqapp.android.common.beans.JsonResponse
import com.noqapp.android.common.beans.body.Notification
import com.noqapp.android.common.fcm.data.*
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech
import com.noqapp.android.common.model.types.*
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum
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
        Log.d(TAG, "Subscribed to: $topic" + "_A")
        FirebaseMessaging.getInstance().subscribeToTopic(topic + "_A")
    }

    fun unSubscribeTopics(topic: String) {
        Log.d(TAG, "Unsubscribed from: $topic" + "_A")
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic + "_A")
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d("NEW_TOKEN", s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.from)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

        clearNotifications(applicationContext)

        // Check if message contains a notification payload.
        val mappedData = remoteMessage.data as MutableMap

        if (mappedData.isNotEmpty()) {
            val title = mappedData["title"]
            val body = mappedData["body"]
            val imageUrl = mappedData["imageURL"]
            val messageOrigin = MessageOriginEnum.valueOf(remoteMessage.data[Constants.MESSAGE_ORIGIN]!!)

            val objectMapper = ObjectMapper()
            val jsonPayloadStr = ObjectMapper().writeValueAsString(mappedData)
            var jsonData: JsonData? = null

            when (messageOrigin) {

                MessageOriginEnum.QA -> try {
                    objectMapper.readValue(jsonPayloadStr, JsonTopicAppointmentData::class.java)
                    Log.d("FCM", jsonData.toString())
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading message " + e.localizedMessage, e)
                    FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.QA)
                    FirebaseCrashlytics.getInstance().recordException(e)
                }

                MessageOriginEnum.Q -> try {
                    var jsonTextToSpeeches: List<JsonTextToSpeech?>? = null
                    val containsTextToSpeeches = mappedData.containsKey("textToSpeeches")
                    if (containsTextToSpeeches) {
                        jsonTextToSpeeches =
                            objectMapper.readValue(mappedData["textToSpeeches"], object : TypeReference<List<JsonTextToSpeech?>?>() {})
                        //TODO(hth) Temp code. Removed as parsing issue.
                        mappedData.remove("textToSpeeches")
                    }
                    jsonData = objectMapper.readValue(jsonPayloadStr, JsonTopicQueueData::class.java)
                    if (null != jsonTextToSpeeches) {
                        jsonData.setJsonTextToSpeeches(jsonTextToSpeeches)
                    }
                    Log.d("FCM", jsonData.toString())
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading message " + e.localizedMessage, e)
                    FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.Q)
                    FirebaseCrashlytics.getInstance().recordException(e)
                }

                MessageOriginEnum.CQO -> try {
                    val tokenAndQueues = objectMapper.readValue(mappedData["tqs"], object : TypeReference<List<JsonTokenAndQueue?>?>() {})
                    val jsonClientTokenAndQueueData = objectMapper.readValue(jsonPayloadStr, JsonClientTokenAndQueueData::class.java)
                    jsonClientTokenAndQueueData.tokenAndQueues = tokenAndQueues
                    jsonData = jsonClientTokenAndQueueData
                    Log.d("FCM", jsonData.toString())
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading message " + e.localizedMessage, e)
                    FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.CQO)
                    FirebaseCrashlytics.getInstance().recordException(e)
                }

                MessageOriginEnum.QR -> try {
                    jsonData = objectMapper.readValue(jsonPayloadStr, JsonClientData::class.java)
                    Log.d("FCM Queue Review", jsonData.toString())
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading message " + e.localizedMessage, e)
                    FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.QR)
                    FirebaseCrashlytics.getInstance().recordException(e)
                }

                MessageOriginEnum.OR -> try {
                    jsonData = objectMapper.readValue(jsonPayloadStr, JsonClientOrderData::class.java)
                    Log.d("FCM Order Review", jsonData.toString())
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading message " + e.localizedMessage, e)
                    FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.OR)
                    FirebaseCrashlytics.getInstance().recordException(e)
                }

                MessageOriginEnum.O -> try {
                    var jsonTextToSpeeches: List<JsonTextToSpeech?>? = null
                    val containsTextToSpeeches = mappedData.containsKey("textToSpeeches")
                    if (containsTextToSpeeches) {
                        jsonTextToSpeeches = objectMapper.readValue(mappedData["textToSpeeches"], object : TypeReference<List<JsonTextToSpeech?>?>() {})
                        mappedData.remove("textToSpeeches")
                    }
                    jsonData = objectMapper.readValue(jsonPayloadStr, JsonTopicOrderData::class.java)
                    if (null != jsonTextToSpeeches) {
                        jsonData.setJsonTextToSpeeches(jsonTextToSpeeches)
                    }

                    Log.d("FCM", jsonData.toString())
                    Log.d("FCM order ", jsonData.toString())
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading message " + e.localizedMessage, e)
                    FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.O)
                    FirebaseCrashlytics.getInstance().recordException(e)
                }

                MessageOriginEnum.A, MessageOriginEnum.D -> try {
                    jsonData = objectMapper.readValue(jsonPayloadStr, JsonAlertData::class.java)
                    Log.d("FCM JsonAlert", jsonData.toString())
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading message " + e.localizedMessage, e)
                    FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.D)
                    FirebaseCrashlytics.getInstance().recordException(e)
                }

                MessageOriginEnum.MF -> try {
                    jsonData = objectMapper.readValue(jsonPayloadStr, JsonMedicalFollowUp::class.java)
                    Log.d("FCM Medical Followup", jsonData.toString())
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading message " + e.localizedMessage, e)
                    FirebaseCrashlytics.getInstance().log("Failed to read message " + MessageOriginEnum.MF)
                    FirebaseCrashlytics.getInstance().recordException(e)
                }

                MessageOriginEnum.QCT -> {
                    FirebaseCrashlytics.getInstance().log("Still reading QCT message on client");
                    FirebaseCrashlytics.getInstance().recordException(Exception("Still reading QCT message on client"))
                }

                MessageOriginEnum.M -> {
                }

                MessageOriginEnum.IE -> {
                }
                else -> {
                    Log.d(TAG, "Reached un-reachable : $messageOrigin")
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
                        prefs.edit().putString(
                            String.format(Constants.CURRENTLY_SERVING_PREF_KEY, mappedData[Constants.CODE_QR]),
                            mappedData[Constants.CURRENTLY_SERVING]?.toString()
                        ).apply()
                        prefs.edit().putString(
                            String.format(Constants.DISPLAY_SERVING_NUMBER_PREF_KEY, mappedData[Constants.CODE_QR]),
                            mappedData[Constants.DISPLAY_SERVING_NUMBER]?.toString()
                        ).apply()
                    }
//                    val payload = mappedData[Constants.FIREBASE_TYPE]
//                    val codeQR = mappedData[Constants.CODE_QR]
//                    if (StringUtils.isNotBlank(payload) && payload.equals(FirebaseMessageTypeEnum.C.getName(), ignoreCase = true)) {
//                        if (!(jsonData is JsonChangeServiceTimeData)) {
//                            jsonData?.let {
//                                updateNotification(jsonData, codeQR)
//                                return
//                            }
//                        }
//                    }

                }

                //app is in background, show the notification in notification tray
                //save data to database
                val payload = mappedData[Constants.FIREBASE_TYPE]
                val codeQR = mappedData[Constants.CODE_QR]

                /*
                 * When u==S then it is re-view
                 * u==N then it is skip(Rejoin) Pending task
                 */
                if (StringUtils.isNotBlank(payload) && payload.equals(FirebaseMessageTypeEnum.P.getName(), ignoreCase = true)) {
                    if (StringUtils.isNotBlank(codeQR)) {
                        val currentServing = mappedData[Constants.CURRENTLY_SERVING]
                        if (currentServing != null) {

                            GlobalScope.launch(Dispatchers.IO) {
                                val jsonTokenAndQueueList = NoQueueAppDB.dbInstance(this@NoQueueMessagingService)
                                    .tokenAndQueueDao()
                                    .getCurrentQueueObjectList(codeQR)

                                jsonTokenAndQueueList?.forEach { jsonTokenAndQueue ->
                                    jsonTokenAndQueue.servingNumber = currentServing.toInt()
                                    if (jsonTokenAndQueue.isTokenExpired && jsonTokenAndQueueList.size == 1) {
                                        /* On this condition un-subscribe from queue and order. All are suppose to be unsubscribed upon review. */
                                        unSubscribeTopics(jsonTokenAndQueue.topic)
                                    }

                                    mappedData[Constants.DISPLAY_SERVING_NUMBER]?.let { displayServingNumber ->
                                        GlobalScope.launch {
                                            withContext(Dispatchers.IO) {
                                                NoQueueAppDB.dbInstance(this@NoQueueMessagingService)
                                                    .tokenAndQueueDao()
                                                    .updateCurrentListQueueObject(codeQR, currentServing, displayServingNumber, jsonTokenAndQueue.token)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (jsonData is JsonClientData) {
                            Log.e("In JsonClientData", jsonData.toString())
                            val token = jsonData.token.toString()
                            val qid = jsonData.queueUserId
                            if (jsonData.queueUserState.getName().equals(QueueUserStateEnum.S.getName(), ignoreCase = true)) {
                                /*
                                 * Save codeQR of review & show the review screen on app
                                 * resume if there is any record in Review DB for queue review key
                                 */
                                val reviewData = ReviewData()
                                reviewData.isReviewShown = "-1"
                                reviewData.codeQR = codeQR
                                reviewData.token = token
                                reviewData.queueUserId = qid
                                reviewData.isBuzzerShow = "-1"
                                reviewData.isSkipped = "-1"
                                reviewData.gotoCounter = ""
                                reviewData.type = Constants.NotificationTypeConstant.FOREGROUND

                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        NoQueueAppDB.dbInstance(this@NoQueueMessagingService)
                                            .reviewDao()
                                            .insertReviewData(reviewData)
                                    }
                                }

                                if (AppUtils.isAppIsInBackground(this@NoQueueMessagingService))
                                    sendNotification(
                                        title,
                                        jsonData.getLocalLanguageMessageBody(AppUtils.getSelectedLanguage(applicationContext)),
                                        codeQR,
                                        true,
                                        token,
                                        imageUrl)

                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        NoQueueAppDB.dbInstance(this@NoQueueMessagingService).foregroundNotificationDao().deleteForegroundNotifications(codeQR, token)
                                    }
                                }


                            } else if (jsonData.queueUserState.getName().equals(QueueUserStateEnum.N.getName(), ignoreCase = true)) {
                                val reviewData = ReviewData()
                                reviewData.isReviewShown = "-1"
                                reviewData.codeQR = codeQR
                                reviewData.token = token
                                reviewData.queueUserId = qid
                                reviewData.isBuzzerShow = "-1"
                                reviewData.isSkipped = "1"
                                reviewData.gotoCounter = ""
                                reviewData.type = Constants.NotificationTypeConstant.FOREGROUND

                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        NoQueueAppDB.dbInstance(this@NoQueueMessagingService).reviewDao().insertReviewData(reviewData)
                                    }
                                }

                                if (AppUtils.isAppIsInBackground(this@NoQueueMessagingService))
                                    sendNotification(
                                        title,
                                        jsonData.getLocalLanguageMessageBody(AppUtils.getSelectedLanguage(applicationContext)),
                                        codeQR,
                                        false,
                                        token,
                                        imageUrl)
                            }
                        } else if (jsonData is JsonClientOrderData) {
                            val token = jsonData.orderNumber.toString()
                            val qid = jsonData.queueUserId
                            if (jsonData.purchaseOrderState.getName().equals(PurchaseOrderStateEnum.OD.getName(), ignoreCase = true)) {
                                /*
                                * Save codeQR of review & show the review screen on app
                                * resume if there is any record in Review DB for queue review key
                                */
                                val reviewData = ReviewData()
                                reviewData.isReviewShown = "-1"
                                reviewData.codeQR = codeQR
                                reviewData.token = token
                                reviewData.queueUserId = qid
                                reviewData.isBuzzerShow = "-1"
                                reviewData.isSkipped = "-1"
                                reviewData.gotoCounter = ""
                                reviewData.type = Constants.NotificationTypeConstant.FOREGROUND

                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        NoQueueAppDB.dbInstance(this@NoQueueMessagingService).reviewDao().insertReviewData(reviewData)
                                    }
                                }

                                if (AppUtils.isAppIsInBackground(this@NoQueueMessagingService))
                                    sendNotification(
                                        title,
                                        jsonData.getLocalLanguageMessageBody(AppUtils.getSelectedLanguage(applicationContext)),
                                        codeQR,
                                        true,
                                        token,
                                        imageUrl)

                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        NoQueueAppDB.dbInstance(this@NoQueueMessagingService).foregroundNotificationDao().deleteForegroundNotifications(codeQR, token)
                                    }
                                }

                                /*
                                * this code is added to close the join & after join screen if the request is processed
                                * Update the order screen/ Join Screen if open
                                */
                                if (AppInitialize.activityCommunicator != null) {
                                    AppInitialize.activityCommunicator.requestProcessed(codeQR, token)
                                }
                            }
                        } else if (jsonData is JsonTopicOrderData) {
                            updateNotification(jsonData, codeQR, title, imageUrl)
                            return
                        } else if (jsonData is JsonTopicQueueData) {
                            updateNotification(jsonData, codeQR, title, imageUrl)
                            return
                        } else if (jsonData is JsonClientTokenAndQueueData) {
                            val jsonTokenAndQueueList = jsonData.tokenAndQueues
                            if (null != jsonTokenAndQueueList && jsonTokenAndQueueList.size > 0) {
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        NoQueueAppDB.dbInstance(this@NoQueueMessagingService)
                                            .tokenAndQueueDao()
                                            .saveCurrentQueue(jsonTokenAndQueueList)
                                    }
                                }
                            }

                            val displayNotification = DisplayNotification()
                            displayNotification.type = Constants.KEY_NOTIFY
                            displayNotification.codeQR = jsonData.codeQR
                            displayNotification.body = jsonData.getLocalLanguageMessageBody(AppUtils.getSelectedLanguage(applicationContext))
                            displayNotification.title = jsonData.title
                            displayNotification.businessType = BusinessTypeEnum.ZZ
                            displayNotification.imageUrl = jsonData.imageURL
                            displayNotification.key = getKey(jsonData.id)
                            displayNotification.status = Constants.KEY_UNREAD
                            displayNotification.createdDate = CommonHelper.changeUTCDateToString(Date())

                            GlobalScope.launch {
                                withContext(Dispatchers.IO) {
                                    NoQueueAppDB.dbInstance(this@NoQueueMessagingService).notificationDao().insertNotification(displayNotification)
                                }
                            }

                            for (i in jsonTokenAndQueueList.indices) {
                                subscribeTopics(jsonTokenAndQueueList[i].topic + "_A")
                            }

                            if (AppUtils.isAppIsInBackground(applicationContext)) {
                                sendNotification(
                                    title,
                                    jsonData.getLocalLanguageMessageBody(AppUtils.getSelectedLanguage(applicationContext)),
                                    false,
                                    imageUrl)
                            }
                        } else if (jsonData is JsonMedicalFollowUp) {
                            Log.e("Alert set:", "data is :$title ---- $body")
                            if (AppUtils.isAppIsInBackground(applicationContext))
                                sendNotification(
                                    title,
                                    jsonData.getLocalLanguageMessageBody(AppUtils.getSelectedLanguage(applicationContext)),
                                    true,
                                    imageUrl)

                            setAlarm(jsonData)
                        } else {
                            if (AppUtils.isAppIsInBackground(applicationContext)) {
                                sendNotification(
                                    title,
                                    jsonData?.getLocalLanguageMessageBody(AppUtils.getSelectedLanguage(applicationContext)),
                                    false,
                                    imageUrl)
                            }
                        }
                    } else {
                        if (AppUtils.isAppIsInBackground(applicationContext)) {
                            sendNotification(
                                title,
                                jsonData?.getLocalLanguageMessageBody(AppUtils.getSelectedLanguage(applicationContext)),
                                false,
                                imageUrl)
                        }
                    }

                    if (jsonData is JsonAlertData) {
                        Log.e("IN JsonAlertData", jsonData.toString())
                        val displayNotification = DisplayNotification()
                        displayNotification.type = Constants.KEY_NOTIFY
                        displayNotification.codeQR = jsonData.codeQR
                        //TODO(vivek) from my point of view it is wrong....lets discuss more bot it by vivek
                        displayNotification.body = jsonData.getLocalLanguageMessageBody(AppUtils.getSelectedLanguage(applicationContext))
                        displayNotification.title = jsonData.title
                        displayNotification.businessType = jsonData.businessType
                        displayNotification.imageUrl = jsonData.imageURL
                        displayNotification.key = getKey(jsonData.id)
                        displayNotification.status = Constants.KEY_UNREAD
                        displayNotification.createdDate = CommonHelper.changeUTCDateToString(Date())
                        displayNotification.popUpShown = false

                        GlobalScope.launch {
                            withContext(Dispatchers.IO) {
                                NoQueueAppDB.dbInstance(this@NoQueueMessagingService)
                                    .notificationDao()
                                    .insertNotification(displayNotification)
                            }
                        }
                    }

                } else if (StringUtils.isNotBlank(payload) && payload.equals(FirebaseMessageTypeEnum.C.getName(), ignoreCase = true)) {
                    when (jsonData) {
                        is JsonAlertData -> {
                            /* When app is on background. Adding to notification table. */
                            Log.e("In JsonAlertData", jsonData.toString())
                            val displayNotification = DisplayNotification()
                            displayNotification.type = Constants.KEY_NOTIFY
                            displayNotification.codeQR = jsonData.codeQR
                            displayNotification.key = getKey(jsonData.id)
                            //from my point of view it is wrong....lets discuss more bot it
                            displayNotification.body = jsonData.getLocalLanguageMessageBody(AppUtils.getSelectedLanguage(applicationContext))
                            displayNotification.title = jsonData.title
                            displayNotification.businessType = jsonData.businessType
                            displayNotification.imageUrl = jsonData.imageURL
                            displayNotification.status = Constants.KEY_UNREAD
                            displayNotification.createdDate = CommonHelper.changeUTCDateToString(Date())
                            displayNotification.popUpShown = false

                            GlobalScope.launch {
                                withContext(Dispatchers.IO) {
                                    NoQueueAppDB.dbInstance(this@NoQueueMessagingService).notificationDao().insertNotification(displayNotification)
                                }
                            }
                        }
                        is JsonTopicQueueData -> {
                            updateNotification(jsonData, codeQR, title, imageUrl)
                        }
                        is JsonTopicOrderData -> {
                            updateNotification(jsonData, codeQR, title, imageUrl)
                        }
                    }
                }

                if (jsonData is JsonMedicalFollowUp) {
                    Log.e("JsonMedicalFollowUp", jsonData.toString())
                    val displayNotification = DisplayNotification()
                    displayNotification.type = Constants.KEY_NOTIFY
                    displayNotification.codeQR = jsonData.codeQR
                    //from my point of view it is wrong....lets discuss more bot it
                    displayNotification.body = jsonData.body
                    displayNotification.key = getKey(jsonData.id)
                    displayNotification.title = jsonData.title
                    displayNotification.businessType = BusinessTypeEnum.ZZ
                    displayNotification.imageUrl = jsonData.imageURL
                    displayNotification.status = Constants.KEY_UNREAD
                    displayNotification.createdDate = CommonHelper.changeUTCDateToString(Date())

                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            NoQueueAppDB.dbInstance(this@NoQueueMessagingService).notificationDao().insertNotification(displayNotification)
                        }
                    }
                } else if (jsonData is JsonTopicAppointmentData) {
                    Log.e("JsonTopicAppointData", jsonData.toString())
                    val displayNotification = DisplayNotification()
                    displayNotification.type = Constants.KEY_NOTIFY
                    displayNotification.codeQR = ""
                    displayNotification.key = getKey(jsonData.id)
                    //from my point of view it is wrong....lets discuss more bot it
                    displayNotification.body = jsonData.getLocalLanguageMessageBody(AppUtils.getSelectedLanguage(applicationContext))
                    displayNotification.title = jsonData.title
                    displayNotification.businessType = BusinessTypeEnum.ZZ
                    displayNotification.imageUrl = jsonData.imageURL
                    displayNotification.status = Constants.KEY_UNREAD
                    displayNotification.createdDate = CommonHelper.changeUTCDateToString(Date())

                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            NoQueueAppDB.dbInstance(this@NoQueueMessagingService).notificationDao().insertNotification(displayNotification)
                        }
                    }
                }

            } catch (e: java.lang.Exception) {
                Log.e(TAG, "Error reading message " + e.localizedMessage, e)
                FirebaseCrashlytics.getInstance().log("Failed to parse message " + e.localizedMessage)
                FirebaseCrashlytics.getInstance().recordException(e)
                sendNotification(
                    title,
                    jsonData?.getLocalLanguageMessageBody(AppUtils.getSelectedLanguage(applicationContext)),
                    false,
                    imageUrl
                )
            }
        }
    }

    private fun updateNotification(
        jsonData: JsonData,
        codeQR: String?,
        title: String?,
        imageUrl: String?
    ) {
        try {
            var goTo = ""
            var messageOrigin = ""
            var currentServing = ""
            var displayServingNumber = ""
            var jsonTextToSpeeches: List<JsonTextToSpeech?>? = null
            var msgId = ""
            var purchaseOrderStateEnum = PurchaseOrderStateEnum.IN
            if (jsonData is JsonTopicQueueData) {
                val jsonTopicQueueData = jsonData
                currentServing = jsonTopicQueueData.currentlyServing.toString()
                displayServingNumber = jsonTopicQueueData.displayServingNumber
                jsonTopicQueueData.goTo?.let {
                    goTo = jsonTopicQueueData.goTo
                }
                messageOrigin = jsonTopicQueueData.messageOrigin.name
                jsonTextToSpeeches = jsonData.getJsonTextToSpeeches()
                msgId = jsonTopicQueueData.messageId
            } else if (jsonData is JsonTopicOrderData) {
                val jsonTopicOrderData = jsonData
                currentServing = jsonTopicOrderData.currentlyServing.toString()
                displayServingNumber = jsonTopicOrderData.displayServingNumber
                jsonTopicOrderData.goTo?.let {
                    goTo = jsonTopicOrderData.goTo
                }
                messageOrigin = jsonTopicOrderData.messageOrigin.name
                purchaseOrderStateEnum = jsonTopicOrderData.purchaseOrderState
                jsonTextToSpeeches = jsonData.getJsonTextToSpeeches()
                msgId = jsonTopicOrderData.messageId
            }

            GlobalScope.launch(Dispatchers.IO) {
                val jsonTokenAndQueueArrayList = NoQueueAppDB.dbInstance(this@NoQueueMessagingService).tokenAndQueueDao().getCurrentQueueObjectList(codeQR)
                jsonTokenAndQueueArrayList?.let {
                    for (i in jsonTokenAndQueueArrayList.indices) {
                        val jtk = jsonTokenAndQueueArrayList[i]
                        /* update DB & after join screen */
                        if (currentServing.toInt() < jtk.servingNumber) {
                            /* Do nothing - In Case of getting service no less than what the object have */
                        } else {
                            jtk.servingNumber = currentServing.toInt()

                            GlobalScope.launch {
                                withContext(Dispatchers.IO) {
                                    NoQueueAppDB.dbInstance(this@NoQueueMessagingService)
                                        .tokenAndQueueDao()
                                        .updateCurrentListQueueObject(
                                            codeQR,
                                            currentServing,
                                            displayServingNumber,
                                            jtk.token.toInt())
                                }
                            }

                        }

                        if (jsonData is JsonTopicOrderData && jtk.token == currentServing.toInt()) {
                            jtk.purchaseOrderState = purchaseOrderStateEnum
                        }

                        if (jtk.isTokenExpired && jsonTokenAndQueueArrayList.size == 1) {
                            /* Un-subscribe the topic */
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(jtk.topic + "_A")
                        }

                        val foregroundNotificationModel = ForegroundNotificationModel()
                        foregroundNotificationModel.currentServing = currentServing
                        foregroundNotificationModel.displayServingNumber = displayServingNumber
                        foregroundNotificationModel.jsonTextToSpeeches = jsonTextToSpeeches
                        foregroundNotificationModel.goTo = goTo
                        foregroundNotificationModel.messageOrigin = messageOrigin
                        foregroundNotificationModel.msgId = msgId
                        foregroundNotificationModel.purchaseOrderStateEnum = purchaseOrderStateEnum
                        foregroundNotificationModel.userCurrentToken = jtk.token.toString()
                        codeQR?.let { foregroundNotificationModel.qrCode = codeQR }
                        foregroundNotificationModel.forUpdateFlag = false

                        GlobalScope.launch {
                            withContext(Dispatchers.IO) {
                                NoQueueAppDB.dbInstance(this@NoQueueMessagingService)
                                    .foregroundNotificationDao()
                                    .insertForeGroundNotification(foregroundNotificationModel)
                            }
                        }

                        val prefs = applicationContext.getSharedPreferences(Constants.APP_PACKAGE, MODE_PRIVATE)
                        //val lastServingNumber = prefs.getString(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, codeQR), "0")
                        if (AppUtils.isAppIsInBackground(applicationContext)) {
                            // Check if User's turn then start Buzzer.

                            prefs.edit().putString(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, codeQR), currentServing).apply();
                            var notificationMessage = java.lang.String.format(applicationContext.getString(R.string.position_in_queue), jtk.afterHowLong())
                            try {
                                when (jtk.businessType) {
                                    BusinessTypeEnum.CD, BusinessTypeEnum.CDQ -> {
                                        val slot = jtk.timeSlotMessage
                                        notificationMessage = notificationMessage.toString() + String.format(applicationContext.getString(R.string.time_slot_formatted_newline), slot)
                                    }
                                    else -> {
                                        val avgServiceTime =
                                            if (jtk.averageServiceTime == 0L) {
                                                prefs.getLong(java.lang.String.format(Constants.ESTIMATED_WAIT_TIME_PREF_KEY, codeQR), 0)
                                            } else {
                                                jtk.averageServiceTime
                                            }
                                        val waitTime = TokenStatusUtils.calculateEstimatedWaitTime(
                                            avgServiceTime,
                                            jtk.afterHowLong(),
                                            QueueStatusEnum.N,
                                            jtk.startHour,
                                            applicationContext)
                                        if (!TextUtils.isEmpty(waitTime)) {
                                            notificationMessage = notificationMessage.toString() + String.format(applicationContext.getString(R.string.wait_time_formatted_newline), waitTime)
                                        }
                                    }
                                }
                            } catch (e: java.lang.Exception) {
                                Log.e(TAG, "Error setting wait time reason: " + e.localizedMessage, e)
                            }

                            // Check if User's turn then start Buzzer.
                            if (currentServing == jtk.token.toString()) {
                                val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager

                                if (keyguardManager.isKeyguardLocked) {
                                    sendNotification(
                                        title,
                                        notificationMessage,
                                        true,
                                        imageUrl,
                                        jtk.token - currentServing.toInt()
                                    )
                                } else {
                                    val homeIntent = Intent(this@NoQueueMessagingService, HomeActivity::class.java)
                                    homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(homeIntent)
                                }
                            } else {
                                sendNotification(
                                    title,
                                    notificationMessage,
                                    true,
                                    imageUrl,
                                    jtk.token - currentServing.toInt()
                                )
                            }
                        }

                        try {
                            //* In case of order update the order status *//*
                            if (jsonData is JsonTopicOrderData) {
                                if (messageOrigin.equals(MessageOriginEnum.O.name, ignoreCase = true) && currentServing.toInt() == jtk.token) {
                                    jtk.purchaseOrderState = jsonData.purchaseOrderState
                                    GlobalScope.launch {
                                        withContext(Dispatchers.IO) {
                                            NoQueueAppDB.dbInstance(this@NoQueueMessagingService)
                                                .tokenAndQueueDao()
                                                .updateCurrentListOrderObject(
                                                    codeQR,
                                                    jtk.purchaseOrderState,
                                                    jtk.token)
                                        }
                                    }
                                }
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            throw e
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            FirebaseCrashlytics.getInstance().log("Failed on update notification")
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG, "Failed on update notification " + e.localizedMessage)
            if (AppUtils.isAppIsInBackground(applicationContext)) {
                sendNotification(
                    title,
                    jsonData.getLocalLanguageMessageBody(AppUtils.getSelectedLanguage(this)),
                    false,
                    imageUrl)
            }
        }
    }

    private fun sendNotification(
        title: String?,
        messageBody: String?,
        codeQR: String?,
        isReview: Boolean,
        token: String,
        imageUrl: String?
    ) {
        CreateBigImageNotificationWithReview(
            title,
            messageBody,
            codeQR,
            isReview,
            token,
            imageUrl
        ).execute()
    }

    private fun sendNotification(
        title: String?,
        messageBody: String?,
        isVibrate: Boolean,
        imageUrl: String?
    ) {
        CreateBigImageNotification(title, messageBody, imageUrl, isVibrate).execute()
    }

    private fun sendNotification(
        title: String?,
        messageBody: String?,
        isVibrate: Boolean,
        imageUrl: String?,
        notificationPriority: Int
    ) {
        CreateBigImageQueueNotification(
            title,
            messageBody,
            imageUrl,
            isVibrate,
            notificationPriority
        ).execute()
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
            am.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent)
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

    inner class CreateBigImageQueueNotification(
        val title: String?,
        val messageBody: String?,
        val imageUrl: String?,
        val isVibrate: Boolean,
        val notificationPriority: Int
    ) : AsyncTask<String?, Void?, Bitmap?>() {
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
            val mBuilder = NotificationCompat.Builder(applicationContext, if (highImportance) channelWithSound else channelNoSound)
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
                mBuilder.setStyle(
                    NotificationCompat.BigPictureStyle() //Set the Image in Big picture Style with text.
                        .bigPicture(bitmap) //.setSummaryText(message)
                        .bigLargeIcon(null)
                )
            }
            if (isVibrate) {
                mBuilder.setVibrate(longArrayOf(500, 500))
            }
            val notificationIntent = Intent(applicationContext, HomeActivity::class.java)
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), Constants.requestCodeNotification, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            val stackBuilder = TaskStackBuilder.create(applicationContext)
            stackBuilder.addNextIntent(notificationIntent)
            val resultPendingIntent = stackBuilder.getPendingIntent(Constants.requestCodeNotification, PendingIntent.FLAG_UPDATE_CURRENT)
            mBuilder.setContentIntent(resultPendingIntent)
            notificationManager.notify(notificationId, mBuilder.build())
        }
    }

    inner class CreateBigImageNotification(
        val title: String?,
        val messageBody: String?,
        val imageUrl: String?,
        val isVibrate: Boolean
    ) : AsyncTask<String?, Void?, Bitmap?>() {
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
                mBuilder.setStyle(
                    NotificationCompat.BigPictureStyle() //Set the Image in Big picture Style with text.
                        .bigPicture(bitmap) //.setSummaryText(message)
                        .bigLargeIcon(null))
            }
            if (isVibrate) {
                mBuilder.setVibrate(longArrayOf(500, 500))
            }
            val notificationIntent = Intent(applicationContext, HomeActivity::class.java)
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), Constants.requestCodeNotification, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            val stackBuilder = TaskStackBuilder.create(applicationContext)
            stackBuilder.addNextIntent(notificationIntent)
            val resultPendingIntent = stackBuilder.getPendingIntent(Constants.requestCodeNotification, PendingIntent.FLAG_UPDATE_CURRENT)
            mBuilder.setContentIntent(resultPendingIntent)
            notificationManager.notify(notificationId, mBuilder.build())
        }
    }

    inner class CreateBigImageNotificationWithReview(
        private val title: String?,
        private val messageBody: String?,
        private val codeQR: String?,
        private val isReview: Boolean,
        private val token: String,
        private val imageUrl: String?
    ) : AsyncTask<String?, Void?, Bitmap?>() {
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
                mBuilder.setStyle(
                    NotificationCompat.BigPictureStyle() //Set the Image in Big picture Style with text.
                        .bigPicture(bitmap) //.setSummaryText(message)
                        .bigLargeIcon(null)
                )
            }
            val stackBuilder = TaskStackBuilder.create(applicationContext)
            stackBuilder.addNextIntent(notificationIntent)
            val resultPendingIntent = stackBuilder.getPendingIntent(Constants.requestCodeNotification, PendingIntent.FLAG_UPDATE_CURRENT)
            mBuilder.setContentIntent(resultPendingIntent)
            notificationManager.notify(notificationId, mBuilder.build())
        }
    }

    private fun callNotificationViewApi(notificationId: String) {
        val notification = Notification()
        notification.id = notificationId

        if (UserUtils.isLogin()) {
            val notificationAcknowledgeApiImpl = NotificationAcknowledgeApiImpl(this)
            notificationAcknowledgeApiImpl.notificationViewed(
                UserUtils.getDeviceId(),
                UserUtils.getEmail(),
                UserUtils.getAuth(),
                notification
            )
        } else {
            val notificationAcknowledgeImpl = NotificationAcknowledgeImpl(this)
            notificationAcknowledgeImpl.notificationViewed(UserUtils.getDeviceId(), notification)
        }
    }

    private fun getKey(key: String?): String {
        if (key != null && key.isNotBlank()) {
            return key
        } else {
            return UUID.randomUUID().toString()
        }
    }
}
