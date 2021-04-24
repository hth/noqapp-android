package com.noqapp.android.client.views.version_2.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.noqapp.android.client.presenter.beans.ReviewData

@Dao
interface ReviewDao {

    @Query("SELECT * FROM review_data WHERE code_qr=:codeQr AND token=:token")
    fun getReviewData(codeQr: String?, token: String): LiveData<ReviewData>

    @Insert
    fun insertReviewData(reviewData: ReviewData)

    @Query("SELECT * FROM review_data WHERE is_review_shown=1")
    fun getPendingReview(): LiveData<ReviewData>

    @Query("SELECT * FROM review_data WHERE is_skipped=1")
    fun getSkippedQueue(): LiveData<ReviewData>

    @Query("DELETE FROM review_data WHERE code_qr=:codeQr AND token=:token")
    fun deleteReviewData(codeQr: String, token: String)

    @Update
    fun update(reviewData: ReviewData)

    @Query("DELETE FROM review_data")
    fun clearReviewData()

}