package com.noqapp.android.common.pojos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noqapp.android.common.model.types.category.RentalTypeEnum

@Entity(tableName = "post_property_rental")
data class PropertyRentalEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "bed_room")
    val bedroom: Int,
    @ColumnInfo(name = "bath_room")
    val bathroom: Int,
    @ColumnInfo(name = "carpet_area")
    val carpetArea: Int,
    @ColumnInfo(name = "rental_type")
    val rentalType: RentalTypeEnum,
    @ColumnInfo(name = "coordinates")
    val coordinates: List<Double>,
    @ColumnInfo(name = "price")
    val price: Int,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "address")
    val address: String,
    @ColumnInfo(name = "city")
    val city: String,
    @ColumnInfo(name = "town")
    val town: String,
    @ColumnInfo(name = "country")
    val country: String,
    @ColumnInfo(name = "landmark")
    val landmark: String
)