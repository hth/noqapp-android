package com.noqapp.android.client.views.version_2

import android.annotation.SuppressLint
import android.content.*
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.noqapp.android.client.model.database.DatabaseTable
import com.noqapp.android.client.model.database.utils.ReviewDB
import com.noqapp.android.client.model.fcm.JsonClientTokenAndQueueData
import com.noqapp.android.client.network.NoQueueMessagingService
import com.noqapp.android.client.presenter.beans.ReviewData
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.utils.ShowAlertInformation
import com.noqapp.android.client.views.activities.AppInitialize
import com.noqapp.android.client.views.activities.BlinkerActivity
import com.noqapp.android.client.views.activities.LaunchActivity
import com.noqapp.android.client.views.version_2.db.NoQueueAppDB.Companion.dbInstance
import com.noqapp.android.common.customviews.CustomToast
import com.noqapp.android.common.fcm.data.*
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech
import com.noqapp.android.common.model.types.BusinessTypeEnum
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum
import com.noqapp.android.common.model.types.MessageOriginEnum
import com.noqapp.android.common.model.types.QueueUserStateEnum
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum
import com.noqapp.android.common.pojos.DisplayNotification
import com.noqapp.android.common.utils.CommonHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.StringUtils
import java.util.*

class FcmNotificationReceiver : BroadcastReceiver() {

    val TAG = FcmNotificationReceiver::class.java.simpleName

    var isRegistered = false
    fun register(context: Context?, filter: IntentFilter?) {
        try {
            if (!isRegistered) {
                LocalBroadcastManager.getInstance(context!!).registerReceiver(this, filter!!)
                Log.e("FCM Receiver: ", "register")
                isRegistered = true
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun unregister(context: Context) {
        if (isRegistered) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(this)
            Log.e("FCM Receiver: ", "unregister")
            isRegistered = false
        }
    }

    @SuppressLint("LongLogTag")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Constants.PUSH_NOTIFICATION) {
            // new push notification is received
            val payload = intent.getStringExtra(Constants.FIREBASE_TYPE)
            val codeQR = intent.getStringExtra(Constants.CODE_QR)
            Log.d("FcmNotificationReceiver", "payload=$payload codeQR=$codeQR")
            val jsonData = intent.getSerializableExtra("jsonData") as JsonData?
            if (jsonData is JsonTopicQueueData) {
                Log.e("onReceiveJsonTopicQdata", jsonData.toString())
            } else if (jsonData is JsonClientData) {
                Log.e("onReceiveJsonClientData", jsonData.toString())
            } else if (jsonData is JsonAlertData) {
                Log.e("onReceiveJsonAlertData", jsonData.toString())
            } else if (jsonData is JsonTopicOrderData) {
                Log.e("onReceiveJsonTopicOdata", jsonData.toString())
            } else if (jsonData is JsonClientTokenAndQueueData) {
                Log.e("JsonClientTokenAndQData", jsonData.toString())
            } else if (jsonData is JsonClientOrderData) {
                Log.e("JsonClientOrderData", jsonData.toString())
            } else if (jsonData is JsonChangeServiceTimeData) {
                Log.e("JsonChangeServiceTimeData", jsonData.toString())
            } else if (jsonData is JsonTopicAppointmentData) {
                Log.e("JsonTopicAppointData", jsonData.toString())

                val displayNotification = DisplayNotification()
                displayNotification.setType(DatabaseTable.Notification.KEY_NOTIFY)
                displayNotification.setCodeQR("")
                displayNotification.setBody(jsonData.body)
                displayNotification.setTitle(jsonData.title)
                displayNotification.setBusinessType(BusinessTypeEnum.PA)
                displayNotification.setImageUrl(jsonData.imageURL)
                displayNotification.setStatus(DatabaseTable.Notification.KEY_UNREAD)
                displayNotification.setCreatedDate(CommonHelper.changeUTCDateToString(Date()))
                dbInstance(context).notificationDao().insertNotification(displayNotification)

            } else if (jsonData is JsonMedicalFollowUp) {
                Log.e("JsonMedicalFollowUp", jsonData.toString())

                val displayNotification = DisplayNotification()
                displayNotification.setType(DatabaseTable.Notification.KEY_NOTIFY)
                displayNotification.setCodeQR(jsonData.codeQR)
                displayNotification.setBody(jsonData.body)
                displayNotification.setTitle(jsonData.title)
                displayNotification.setBusinessType(BusinessTypeEnum.PA)
                displayNotification.setImageUrl(jsonData.imageURL)
                displayNotification.setStatus(DatabaseTable.Notification.KEY_UNREAD)
                displayNotification.setCreatedDate(CommonHelper.changeUTCDateToString(Date()))
                dbInstance(context).notificationDao().insertNotification(displayNotification)

            }
            if (StringUtils.isNotBlank(payload) && payload.equals(FirebaseMessageTypeEnum.P.getName(), ignoreCase = true)) {
                if (jsonData is JsonAlertData) {

                    val displayNotification = DisplayNotification()
                    displayNotification.setType(DatabaseTable.Notification.KEY_NOTIFY)
                    displayNotification.setCodeQR(jsonData.codeQR)
                    displayNotification.setBody(jsonData.body)
                    displayNotification.setTitle(jsonData.title)
                    displayNotification.setBusinessType(BusinessTypeEnum.PA)
                    displayNotification.setImageUrl(jsonData.imageURL)
                    displayNotification.setStatus(DatabaseTable.Notification.KEY_UNREAD)
                    displayNotification.setCreatedDate(CommonHelper.changeUTCDateToString(Date()))
                    dbInstance(context).notificationDao().insertNotification(displayNotification)

                    //Show some meaningful msg to the end user
                    ShowAlertInformation.showInfoDisplayDialog(context, jsonData.getTitle(), jsonData.getLocalLanguageMessageBody(LaunchActivity.language))
                } else if (jsonData is JsonClientData) {
                    val token = jsonData.token.toString()
                    val qid = jsonData.queueUserId
                    if (jsonData.queueUserState.getName().equals(QueueUserStateEnum.S.getName(), ignoreCase = true)) {
                        /*
                             * Save codeQR of review & show the review screen on app
                             * resume if there is any record in Review DB for queue review key
                             */
                        dbInstance(context).reviewDao().getReviewData(codeQR, token).value?.let {
                            it.isReviewShown = "1"
                            dbInstance(context).reviewDao().update(it)
                        } ?: run {
                            val reviewData = ReviewData()
                            reviewData.setIsReviewShown("1")
                            reviewData.setCodeQR(codeQR)
                            reviewData.setToken(token)
                            reviewData.setQueueUserId(qid)
                            reviewData.setIsBuzzerShow("-1")
                            reviewData.setIsSkipped("-1")
                            reviewData.setGotoCounter("")
                            dbInstance(context).reviewDao().insertReviewData(reviewData)
                        }


                        // Clear the App Shared Preferences entry for this queue
                        val prefs = context.getSharedPreferences(Constants.APP_PACKAGE, Context.MODE_PRIVATE)
                        prefs.edit().remove(String.format(Constants.ESTIMATED_WAIT_TIME_PREF_KEY, codeQR)).apply()
                        prefs.edit().remove(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, codeQR)).apply()

                        //Todo call in HomeActivity
                        //callReviewActivity(codeQR, token)
                        /* this code is added to close the join & after join screen if the request is processed */
                        if (AppInitialize.activityCommunicator != null) {
                            AppInitialize.activityCommunicator.requestProcessed(codeQR, token)
                        }
                    } else if (jsonData.queueUserState.getName().equals(QueueUserStateEnum.N.getName(), ignoreCase = true)) {

                        dbInstance(context).reviewDao().getReviewData(codeQR, token).value?.let {
                            it.isReviewShown = "1"
                            dbInstance(context).reviewDao().update(it)
                        } ?: run {
                            val reviewData = ReviewData()
                            reviewData.setIsReviewShown("-1")
                            reviewData.setCodeQR(codeQR)
                            reviewData.setToken(token)
                            reviewData.setQueueUserId(qid)
                            reviewData.setIsBuzzerShow("-1")
                            reviewData.setIsSkipped("-1")
                            reviewData.setGotoCounter("")
                            dbInstance(context).reviewDao().insertReviewData(reviewData)
                        }

                        //       callSkipScreen(codeQR, token, qid)

                    }
                } else if (jsonData is JsonClientOrderData) {
                    val jsonClientOrderData = jsonData
                    val token = jsonClientOrderData.orderNumber.toString()
                    val qid = jsonClientOrderData.queueUserId
                    if (jsonData.purchaseOrderState.getName().equals(PurchaseOrderStateEnum.OD.getName(), ignoreCase = true)) {
                        /*
                             * Save codeQR of review & show the review screen on app
                             * resume if there is any record in Review DB for queue review key
                             */

                        dbInstance(context).reviewDao().getReviewData(codeQR, token).value?.let {
                            it.isReviewShown = "1"
                            dbInstance(context).reviewDao().update(it)
                        } ?: run {
                            val reviewData = ReviewData()
                            reviewData.setIsReviewShown("1")
                            reviewData.setCodeQR(codeQR)
                            reviewData.setToken(token)
                            reviewData.setQueueUserId(qid)
                            reviewData.setIsBuzzerShow("-1")
                            reviewData.setIsSkipped("-1")
                            reviewData.setGotoCounter("")
                            dbInstance(context).reviewDao().insertReviewData(reviewData)
                        }


                        //      callReviewActivity(codeQR, token)
                        /*
                             * this code is added to close the join & after join screen if the request is processed
                             * Update the order screen/ Join Screen if open
                             */
                        if (AppInitialize.activityCommunicator != null) {
                            AppInitialize.activityCommunicator.requestProcessed(codeQR, token)
                        }
                    }
                } else if (jsonData is JsonTopicOrderData) {
                    updateNotification(jsonData, codeQR, context)
                } else if (jsonData is JsonTopicQueueData) {
                    updateNotification(jsonData, codeQR, context)
                } else if (jsonData is JsonClientTokenAndQueueData) {
                    val jsonTokenAndQueueList = jsonData.tokenAndQueues

                    if (null != jsonTokenAndQueueList && jsonTokenAndQueueList.size > 0) {
                        GlobalScope.launch {
                            withContext(Dispatchers.IO) {
                                dbInstance(context).tokenAndQueueDao().saveCurrentQueue(jsonTokenAndQueueList)
                            }
                        }
                    }

                    val displayNotification = DisplayNotification()
                    displayNotification.setType(DatabaseTable.Notification.KEY_NOTIFY)
                    displayNotification.setCodeQR(jsonData.codeQR)
                    displayNotification.setBody(jsonData.body)
                    displayNotification.setTitle(jsonData.title)
                    displayNotification.setBusinessType(BusinessTypeEnum.PA)
                    displayNotification.setImageUrl(jsonData.imageURL)
                    displayNotification.setStatus(DatabaseTable.Notification.KEY_UNREAD)
                    displayNotification.setCreatedDate(CommonHelper.changeUTCDateToString(Date()))
                    dbInstance(context).notificationDao().insertNotification(displayNotification)



                    for (i in jsonTokenAndQueueList!!.indices) {
                        NoQueueMessagingService.subscribeTopics(jsonTokenAndQueueList[i].topic)
                    }

                }
            } else if (StringUtils.isNotBlank(payload) && payload.equals(FirebaseMessageTypeEnum.C.getName(), ignoreCase = true)) {
                if (jsonData is JsonAlertData) {
                    val displayNotification = DisplayNotification()
                    displayNotification.setType(DatabaseTable.Notification.KEY_NOTIFY)
                    displayNotification.setCodeQR(jsonData.codeQR)
                    displayNotification.setBody(jsonData.body)
                    displayNotification.setTitle(jsonData.title)
                    displayNotification.setBusinessType(BusinessTypeEnum.PA)
                    displayNotification.setImageUrl(jsonData.imageURL)
                    displayNotification.setStatus(DatabaseTable.Notification.KEY_UNREAD)
                    displayNotification.setCreatedDate(CommonHelper.changeUTCDateToString(Date()))
                    dbInstance(context).notificationDao().insertNotification(displayNotification)
                    /* Show some meaningful msg to the end user */
                    ShowAlertInformation.showInfoDisplayDialog(context, jsonData.getTitle(), jsonData.getLocalLanguageMessageBody(LaunchActivity.language))

                } else if (jsonData is JsonChangeServiceTimeData) {
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            dbInstance(context).tokenAndQueueDao().findByQRCode(jsonData.codeQR).observeForever { jsonTokenAndQueue ->
                                val jsonQueueChangeServiceTimes = jsonData.jsonQueueChangeServiceTimes
                                for (jsonQueueChangeServiceTime in jsonQueueChangeServiceTimes) {
                                    if (jsonQueueChangeServiceTime.token == jsonTokenAndQueue.token) {
                                        val body = """${jsonData.getBody()} Token: ${jsonQueueChangeServiceTime.displayToken} Previous: ${jsonQueueChangeServiceTime.oldTimeSlotMessage} Updated: ${jsonQueueChangeServiceTime.updatedTimeSlotMessage}"""
                                        ShowAlertInformation.showInfoDisplayDialog(context, jsonData.getTitle(), body)
                                        val displayNotification = DisplayNotification()
                                        displayNotification.type = DatabaseTable.Notification.KEY_NOTIFY
                                        displayNotification.codeQR = jsonData.codeQR
                                        displayNotification.body = jsonData.body
                                        displayNotification.title = jsonData.title
                                        displayNotification.businessType = BusinessTypeEnum.PA
                                        displayNotification.imageUrl = jsonData.imageURL
                                        displayNotification.status = DatabaseTable.Notification.KEY_UNREAD
                                        displayNotification.createdDate = CommonHelper.changeUTCDateToString(Date())
                                        dbInstance(context).notificationDao().insertNotification(displayNotification)

                                    }
                                }
                            }
                        }
                    }
                } else {
                    updateNotification(jsonData, codeQR, context)
                }
            } else {
                CustomToast().showToast(context, "UnSupported Notification reached: $payload")
                FirebaseCrashlytics.getInstance().log("UnSupported Notification reached: $payload")
            }
        }
    }

    private fun updateNotification(jsonData: JsonData?, codeQR: String?, context: Context) {
        try {
            var go_to = ""
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
                go_to = jsonTopicQueueData.goTo
                messageOrigin = jsonTopicQueueData.messageOrigin.name
                jsonTextToSpeeches = jsonData.getJsonTextToSpeeches()
                msgId = jsonTopicQueueData.messageId
            } else if (jsonData is JsonTopicOrderData) {
                val jsonTopicOrderData = jsonData
                currentServing = jsonTopicOrderData.currentlyServing.toString()
                displayServingNumber = jsonTopicOrderData.displayServingNumber
                go_to = jsonTopicOrderData.goTo
                messageOrigin = jsonTopicOrderData.messageOrigin.name
                purchaseOrderStateEnum = jsonTopicOrderData.purchaseOrderState
                jsonTextToSpeeches = jsonData.getJsonTextToSpeeches()
                msgId = jsonTopicOrderData.messageId
            }

            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    dbInstance(context).tokenAndQueueDao().getCurrentQueueObjectList(codeQR).observeForever {
                        val jsonTokenAndQueueArrayList = it
                        for (i in jsonTokenAndQueueArrayList.indices) {
                            val jtk = jsonTokenAndQueueArrayList[i]
                            if (null != jtk) {
                                /* update DB & after join screen */
                                if (currentServing.toInt() < jtk.servingNumber) {
                                    /* Do nothing - In Case of getting service no less than what the object have */
                                } else {
                                    jtk.servingNumber = currentServing.toInt()
                                    GlobalScope.launch {
                                        withContext(Dispatchers.IO) {
                                            dbInstance(context).tokenAndQueueDao().updateCurrentListQueueObject(codeQR, currentServing, displayServingNumber, jtk.token)
                                        }
                                    }
                                }
                                if (jsonData is JsonTopicOrderData && jtk.token - currentServing.toInt() <= 0) {
                                    jtk.purchaseOrderState = purchaseOrderStateEnum
                                }
                                /*
                                 * Save codeQR of goto & show it in after join screen on app
                                 * Review DB for review key && current serving == token no.
                                 */
                                if (currentServing.toInt() == jtk.token) {
                                    dbInstance(context).reviewDao().getReviewData(codeQR, currentServing).value?.let {
                                        it.gotoCounter = go_to
                                        dbInstance(context).reviewDao().update(it)
                                    } ?: run {
                                        val reviewData = ReviewData()
                                        reviewData.setIsReviewShown("-1")
                                        reviewData.setCodeQR(codeQR)
                                        reviewData.setToken(currentServing)
                                        reviewData.setQueueUserId(jtk.queueUserId)
                                        reviewData.setIsBuzzerShow("-1")
                                        reviewData.setIsSkipped("-1")
                                        reviewData.setGotoCounter("")
                                        dbInstance(context).reviewDao().insertReviewData(reviewData)
                                    }

                                }
                                if (jtk.isTokenExpired && jsonTokenAndQueueArrayList.size == 1) {
                                    /* Un-subscribe the topic */
                                    NoQueueMessagingService.unSubscribeTopics(jtk.topic)
                                }
                                if (AppInitialize.activityCommunicator != null) {
                                    val isUpdated = AppInitialize.activityCommunicator.updateUI(codeQR, jtk, go_to)
                                    if (isUpdated || jtk.servingNumber == jtk.token) {
                                        val reviewData = ReviewDB.getValue(codeQR, currentServing)
                                        if (null != reviewData) {
                                            if (reviewData.isBuzzerShow != "1") {
                                                val cv = ContentValues()
                                                cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "1")
                                                ReviewDB.updateReviewRecord(codeQR, currentServing, cv)
                                                val blinker = Intent(context, BlinkerActivity::class.java)
                                                blinker.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                                context.startActivity(blinker)
                                                if (AppInitialize.isMsgAnnouncementEnable()) {
                                                    //          makeAnnouncement(jsonTextToSpeeches, msgId)
                                                }
                                            } else {
                                                /* Blinker already shown */
                                            }
                                            /* update */
                                        } else {
                                            /* insert */
                                            val cv = ContentValues()
                                            cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, -1)
                                            cv.put(DatabaseTable.Review.CODE_QR, codeQR)
                                            cv.put(DatabaseTable.Review.TOKEN, currentServing)
                                            cv.put(DatabaseTable.Review.QID, jtk.queueUserId)
                                            cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "1")
                                            cv.put(DatabaseTable.Review.KEY_SKIP, "-1")
                                            cv.put(DatabaseTable.Review.KEY_GOTO, "")
                                            ReviewDB.insert(cv)
                                            val blinker = Intent(context, BlinkerActivity::class.java)
                                            blinker.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                            context.startActivity(blinker)
                                            if (AppInitialize.isMsgAnnouncementEnable()) {
                                                //        makeAnnouncement(jsonTextToSpeeches, msgId)
                                            }
                                        }
                                    }
                                }
                                try {
                                    /* In case of order update the order status */
                                    if (jsonData is JsonTopicOrderData) {
                                        if (messageOrigin.equals(MessageOriginEnum.O.name, ignoreCase = true) && currentServing.toInt() == jtk.token) {
                                            jtk.purchaseOrderState = jsonData.purchaseOrderState
                                            GlobalScope.launch {
                                                withContext(Dispatchers.IO){
                                                    dbInstance(context).tokenAndQueueDao().updateCurrentListOrderObject(codeQR, jtk.purchaseOrderState.getName(), jtk.token)
                                                }
                                            }
                                        }
                                    }
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                    throw e
                                }
                            } else {
                                Log.e(TAG, "codeQR=$codeQR current_serving=$currentServing goTo=$go_to")
                            }
                        }

                    }
                }
            }

        } catch (e: java.lang.Exception) {
            FirebaseCrashlytics.getInstance().log("Failed on update notification")
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG, "Failed on update notification " + e.localizedMessage)
        }
    }

}