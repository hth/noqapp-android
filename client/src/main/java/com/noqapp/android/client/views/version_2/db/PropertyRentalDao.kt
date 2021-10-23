package com.noqapp.android.client.views.version_2.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.noqapp.android.common.pojos.PropertyRentalEntity

@Dao
interface PropertyRentalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPropertyRental(propertyRentalEntity: PropertyRentalEntity?)

    @Query("SELECT * FROM post_property_rental WHERE id=1")
    fun getPropertyRental(): LiveData<List<PropertyRentalEntity>>

    @Update
    suspend fun updatePropertyRental(propertyRentalEntity: PropertyRentalEntity)

    @Query("DELETE FROM post_property_rental")
    suspend fun deletePropertyRental()
}