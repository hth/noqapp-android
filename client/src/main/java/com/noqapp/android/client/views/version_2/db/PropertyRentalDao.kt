package com.noqapp.android.client.views.version_2.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.noqapp.android.common.pojos.PropertyRentalEntity

@Dao
interface PropertyRentalDao {
    @Insert
    fun insertPropertyRental(propertyRentalEntity: PropertyRentalEntity)

    @Query("SELECT * FROM post_property_rental WHERE id=:id")
    fun getPropertyRental(id: Int): LiveData<PropertyRentalEntity>

    @Update
    fun updatePropertyRental(propertyRentalEntity: PropertyRentalEntity)

    @Delete
    fun deletePropertyRental(propertyRentalEntity: PropertyRentalEntity)
}