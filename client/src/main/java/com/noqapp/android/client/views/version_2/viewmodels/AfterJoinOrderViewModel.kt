package com.noqapp.android.client.views.version_2.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue
import com.noqapp.android.client.presenter.beans.ReviewData
import com.noqapp.android.client.views.version_2.db.NoQueueAppDB
import com.noqapp.android.client.views.version_2.db.helper_models.ForegroundNotificationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AfterJoinOrderViewModel(application: Application) : AndroidViewModel(application) {

    val currentQueueObjectLiveData = MutableLiveData<JsonTokenAndQueue>()
    val currentQueueObjectListLiveData = MutableLiveData<List<JsonTokenAndQueue>?>()

    val foregroundNotificationLiveData: LiveData<ForegroundNotificationModel> = liveData {
        val foregroundNotification =
            NoQueueAppDB.dbInstance(getApplication()).foregroundNotificationDao()
                .getForegroundNotification()
        emitSource(foregroundNotification)
    }

    fun insertTokenAndQueue(jsonTokenAndQueue: JsonTokenAndQueue) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                NoQueueAppDB.dbInstance(getApplication()).tokenAndQueueDao()
                    .saveJoinQueueObject(jsonTokenAndQueue)
            }
        }
    }

    fun getCurrentQueueObject(codeQR: String?, token: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            currentQueueObjectLiveData.postValue(
                NoQueueAppDB.dbInstance(getApplication()).tokenAndQueueDao()
                    .getCurrentQueueObject(codeQR, token?.toInt()))
        }
    }

    fun getReviewData(qrCode: String?, token: String?): LiveData<ReviewData> {
        return NoQueueAppDB.dbInstance(getApplication()).reviewDao().getReviewData(qrCode, token)
    }

    fun deleteTokenAndQueue(codeQr: String?, token: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                NoQueueAppDB.dbInstance(getApplication()).tokenAndQueueDao()
                    .deleteTokenQueue(codeQr, token?.toInt())
            }
        }
    }

    fun getCurrentQueueObjectList(codeQR: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                currentQueueObjectListLiveData.postValue(
                    NoQueueAppDB.dbInstance(getApplication()).tokenAndQueueDao()
                        .getCurrentQueueObjectList(codeQR)
                )
            }
        }
    }

    fun deleteForegroundNotification() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                NoQueueAppDB.dbInstance(getApplication()).foregroundNotificationDao()
                    .deleteForegroundNotification()
            }
        }
    }

    fun getReviewData(reviewType: String): LiveData<ReviewData> {
        return NoQueueAppDB.dbInstance(getApplication()).reviewDao().getReviewData(reviewType)
    }

}