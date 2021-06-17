package com.noqapp.android.client.views.version_2.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue
import com.noqapp.android.client.presenter.beans.ReviewData
import com.noqapp.android.client.views.version_2.db.NoQueueAppDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AfterJoinViewModel(application: Application): AndroidViewModel(application) {

    val currentQueueObjectLiveData = MutableLiveData<JsonTokenAndQueue>()

    fun insertTokenAndQueue(jsonTokenAndQueue: JsonTokenAndQueue) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                NoQueueAppDB.dbInstance(getApplication()).tokenAndQueueDao().saveJoinQueueObject(jsonTokenAndQueue)
            }
        }
    }

    fun getCurrentQueueObject(codeQR: String?, token: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            currentQueueObjectLiveData.value = NoQueueAppDB.dbInstance(getApplication()).tokenAndQueueDao().getCurrentQueueObject(codeQR, token?.toInt())
        }
    }

    fun getReviewData(qrCode: String?, token: String?): LiveData<ReviewData> {
        return NoQueueAppDB.dbInstance(getApplication()).reviewDao().getReviewData(qrCode, token)
    }

    fun deleteTokenAndQueue(codeQr: String?, token: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                NoQueueAppDB.dbInstance(getApplication()).tokenAndQueueDao().deleteTokenQueue(codeQr, token?.toInt())
            }
        }
    }

}