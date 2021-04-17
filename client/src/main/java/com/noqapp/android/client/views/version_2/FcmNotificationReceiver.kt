package com.noqapp.android.client.views.version_2

import android.annotation.SuppressLint
import android.content.*
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.noqapp.android.client.model.database.DatabaseTable
import com.noqapp.android.client.model.database.utils.NotificationDB
import com.noqapp.android.client.model.database.utils.ReviewDB
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB
import com.noqapp.android.client.model.fcm.JsonClientTokenAndQueueData
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue
import com.noqapp.android.client.presenter.beans.ReviewData
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.utils.ShowAlertInformation
import com.noqapp.android.client.views.activities.AppInitialize
import com.noqapp.android.client.views.activities.LaunchActivity
import com.noqapp.android.client.views.version_2.db.NoqAppDB.Companion.getNoqAppDbInstance
import com.noqapp.android.common.customviews.CustomToast
import com.noqapp.android.common.fcm.data.*
import com.noqapp.android.common.model.types.BusinessTypeEnum
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum
import com.noqapp.android.common.model.types.QueueUserStateEnum
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum
import com.noqapp.android.common.pojos.DisplayNotification
import com.noqapp.android.common.utils.CommonHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import java.util.*

class FcmNotificationReceiver : BroadcastReceiver(), CoroutineScope {
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

                val displayNotification = DisplayNotification().setType(DatabaseTable.Notification.KEY_NOTIFY).setCodeQR("")
                        .setBody(jsonData.body).setTitle(jsonData.title)
                        .setBusinessType(BusinessTypeEnum.PA).setImageUrl(jsonData.imageURL)
                        .setStatus(DatabaseTable.Notification.KEY_UNREAD).setCreatedDate(CommonHelper.changeUTCDateToString(Date()))
                getNoqAppDbInstance(context).notificationDao().insertNotification(displayNotification)

            } else if (jsonData is JsonMedicalFollowUp) {
                Log.e("JsonMedicalFollowUp", jsonData.toString())

                val displayNotification = DisplayNotification().setType(DatabaseTable.Notification.KEY_NOTIFY).setCodeQR(jsonData.codeQR)
                        .setBody(jsonData.body).setTitle(jsonData.title)
                        .setBusinessType(BusinessTypeEnum.PA).setImageUrl(jsonData.imageURL)
                        .setStatus(DatabaseTable.Notification.KEY_UNREAD).setCreatedDate(CommonHelper.changeUTCDateToString(Date()))
                getNoqAppDbInstance(context).notificationDao().insertNotification(displayNotification)

            }
            if (StringUtils.isNotBlank(payload) && payload.equals(FirebaseMessageTypeEnum.P.getName(), ignoreCase = true)) {
                if (jsonData is JsonAlertData) {

                    val displayNotification = DisplayNotification().setType(DatabaseTable.Notification.KEY_NOTIFY).setCodeQR(jsonData.codeQR)
                            .setBody(jsonData.body).setTitle(jsonData.title)
                            .setBusinessType(BusinessTypeEnum.PA).setImageUrl(jsonData.imageURL)
                            .setStatus(DatabaseTable.Notification.KEY_UNREAD).setCreatedDate(CommonHelper.changeUTCDateToString(Date()))
                    getNoqAppDbInstance(context).notificationDao().insertNotification(displayNotification)

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
                        launch {
                            getNoqAppDbInstance(context).reviewDao().getReviewData(codeQR, token).value?.let {
                                it.isReviewShown = "1"
                                getNoqAppDbInstance(context).reviewDao().update(it)
                            } ?: run {
                                val reviewData = ReviewData().setIsReviewShown("1").setCodeQR(codeQR)
                                        .setToken(token).setqUserId(qid).setIsBuzzerShow("-1")
                                        .setIsSkipped("-1").setGotoCounter("")
                                getNoqAppDbInstance(context).reviewDao().insertReviewData(reviewData)
                            }

                        }

                        // Clear the App Shared Preferences entry for this queue
                        val prefs = context.getSharedPreferences(Constants.APP_PACKAGE, Context.MODE_PRIVATE)
                        prefs.edit().remove(String.format(Constants.ESTIMATED_WAIT_TIME_PREF_KEY, codeQR)).apply()
                        prefs.edit().remove(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, codeQR)).apply()

                        //Todo call in HomeActivity
                        //callReviewActivity(codeQR, token)
                        /* this code is added to close the join & after join screen if the request is processed */if (AppInitialize.activityCommunicator != null) {
                            AppInitialize.activityCommunicator.requestProcessed(codeQR, token)
                        }
                    } else if (jsonData.queueUserState.getName().equals(QueueUserStateEnum.N.getName(), ignoreCase = true)) {

                        launch {
                            getNoqAppDbInstance(context).reviewDao().getReviewData(codeQR, token).value?.let {
                                it.isReviewShown = "1"
                                getNoqAppDbInstance(context).reviewDao().update(it)
                            } ?: run {
                                val reviewData = ReviewData().setIsReviewShown("-1").setCodeQR(codeQR)
                                        .setToken(token).setqUserId(qid).setIsBuzzerShow("-1")
                                        .setIsSkipped("-1").setGotoCounter("")
                                getNoqAppDbInstance(context).reviewDao().insertReviewData(reviewData)
                            }
                            
                        callSkipScreen(codeQR, token, qid)
                    }
                } else if (jsonData is JsonClientOrderData) {
                    val token = jsonData.orderNumber.toString()
                    val qid = jsonData.queueUserId
                    if (jsonData.purchaseOrderState.getName().equals(PurchaseOrderStateEnum.OD.getName(), ignoreCase = true)) {
                        /*
                             * Save codeQR of review & show the review screen on app
                             * resume if there is any record in Review DB for queue review key
                             */

                        launch {
                            getNoqAppDbInstance(context).reviewDao().getReviewData(codeQR, token).value?.let {
                                it.isReviewShown = "1"
                                getNoqAppDbInstance(context).reviewDao().update(it)
                            } ?: run {
                                val reviewData = ReviewData().setIsReviewShown("1").setCodeQR(codeQR)
                                        .setToken(token).setqUserId(qid).setIsBuzzerShow("-1")
                                        .setIsSkipped("-1").setGotoCounter("")
                                getNoqAppDbInstance(context).reviewDao().insertReviewData(reviewData)
                            }

                        val reviewData = ReviewDB.getValue(codeQR, token)
                        if (null != reviewData) {
                            val cv = ContentValues()
                            cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, 1)
                            ReviewDB.updateReviewRecord(codeQR, token, cv)
                            /* update */
                        } else {
                            /* insert */
                            val cv = ContentValues()
                            cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, 1)
                            cv.put(DatabaseTable.Review.CODE_QR, codeQR)
                            cv.put(DatabaseTable.Review.TOKEN, token)
                            cv.put(DatabaseTable.Review.QID, qid)
                            cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1")
                            cv.put(DatabaseTable.Review.KEY_SKIP, "-1")
                            cv.put(DatabaseTable.Review.KEY_GOTO, "")
                            ReviewDB.insert(cv)
                        }
                        callReviewActivity(codeQR, token)
                        /*
                             * this code is added to close the join & after join screen if the request is processed
                             * Update the order screen/ Join Screen if open
                             */if (AppInitialize.activityCommunicator != null) {
                            AppInitialize.activityCommunicator.requestProcessed(codeQR, token)
                        }
                    }
                } else if (jsonData is JsonTopicOrderData) {
                    updateNotification(jsonData, codeQR)
                } else if (jsonData is JsonTopicQueueData) {
                    updateNotification(jsonData, codeQR)
                } else if (jsonData is JsonClientTokenAndQueueData) {
                    val jsonTokenAndQueueList = jsonData.tokenAndQueues
                    if (null != jsonTokenAndQueueList && jsonTokenAndQueueList.size > 0) {
                        TokenAndQueueDB.saveCurrentQueue(jsonTokenAndQueueList)
                    }
                    NotificationDB.insertNotification(
                            NotificationDB.KEY_NOTIFY,
                            jsonData.codeQR,
                            jsonData.getBody(),
                            jsonData.getTitle(),
                            BusinessTypeEnum.PA.getName(),
                            jsonData.getImageURL())


                    for (i in jsonTokenAndQueueList!!.indices) {
                        NoQueueMessagingService.subscribeTopics(jsonTokenAndQueueList[i].topic)
                    }
                    updateNotificationBadgeCount()
                    if (null != homeFragment) {
                        homeFragment.fetchCurrentAndHistoryList()
                    }
                }
            } else if (StringUtils.isNotBlank(payload) && payload.equals(FirebaseMessageTypeEnum.C.getName(), ignoreCase = true)) {
                if (jsonData is JsonAlertData) {
                    NotificationDB.insertNotification(
                            NotificationDB.KEY_NOTIFY,
                            jsonData.codeQR,
                            jsonData.getBody(),
                            jsonData.getTitle(),
                            jsonData.businessType.getName(),
                            jsonData.getImageURL())
                    /* Show some meaningful msg to the end user */ShowAlertInformation.showInfoDisplayDialog(this@LaunchActivity, jsonData.getTitle(), jsonData.getLocalLanguageMessageBody(LaunchActivity.language))
                    updateNotificationBadgeCount()
                } else if (jsonData is JsonChangeServiceTimeData) {
                    val jsonTokenAndQueue: JsonTokenAndQueue = TokenAndQueueDB.findByQRCode(jsonData.codeQR)
                    if (null != jsonTokenAndQueue) {
                        val jsonQueueChangeServiceTimes = jsonData.jsonQueueChangeServiceTimes
                        for (jsonQueueChangeServiceTime in jsonQueueChangeServiceTimes) {
                            if (jsonQueueChangeServiceTime.token == jsonTokenAndQueue.token) {
                                val body = """${jsonData.getBody()}
 Token: ${jsonQueueChangeServiceTime.displayToken}
 Previous: ${jsonQueueChangeServiceTime.oldTimeSlotMessage}
 Updated: ${jsonQueueChangeServiceTime.updatedTimeSlotMessage}"""
                                ShowAlertInformation.showInfoDisplayDialog(context, jsonData.getTitle(), body)
                                NotificationDB.insertNotification(
                                        NotificationDB.KEY_NOTIFY,
                                        jsonData.codeQR,
                                        body,
                                        jsonData.getTitle(),
                                        jsonData.businessType.getName(),
                                        jsonData.getImageURL())
                                updateNotificationBadgeCount()
                                if (null != homeFragment) {
                                    homeFragment.updateCurrentQueueList()
                                }
                            }
                        }
                    }
                } else {
                    updateNotification(jsonData, codeQR)
                }
            } else {
                CustomToast().showToast(context, "UnSupported Notification reached: $payload")
                FirebaseCrashlytics.getInstance().log("UnSupported Notification reached: $payload")
            }
        }
    }
}