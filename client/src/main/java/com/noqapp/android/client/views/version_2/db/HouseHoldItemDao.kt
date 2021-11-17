package com.noqapp.android.client.views.version_2.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.noqapp.android.common.pojos.HouseHoldItemEntity

@Dao
interface HouseHoldItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHouseHoldItem(propertyRentalEntity: HouseHoldItemEntity?)

    @Query("SELECT * FROM post_house_hold_item WHERE id=1")
    fun getHouseHoldItem(): LiveData<List<HouseHoldItemEntity>>

    @Update
    suspend fun updateHouseHoldItem(propertyRentalEntity: HouseHoldItemEntity)

    @Query("DELETE FROM post_house_hold_item")
    suspend fun deleteHouseHoldItem()
}