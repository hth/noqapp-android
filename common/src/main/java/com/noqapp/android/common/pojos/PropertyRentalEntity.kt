package com.noqapp.android.common.pojos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noqapp.android.common.model.types.category.RentalTypeEnum

@Entity(tableName = "post_property_rental")
data class PropertyRentalEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "bed_room")
    var bedroom: Int,
    @ColumnInfo(name = "bath_room")
    var bathroom: Int,
    @ColumnInfo(name = "carpet_area")
    var carpetArea: Int,
    @ColumnInfo(name = "rental_type")
    var rentalType: RentalTypeEnum,
    @ColumnInfo(name = "coordinates")
    var coordinates: List<Double>,
    @ColumnInfo(name = "price")
    var price: Int,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "description")
    var description: String,
    @ColumnInfo(name = "address")
    var address: String?,
    @ColumnInfo(name = "city")
    var city: String?,
    @ColumnInfo(name = "town")
    var town: String?,
    @ColumnInfo(name = "country")
    var country: String?,
    @ColumnInfo(name = "landmark")
    var landmark: String?,
    @ColumnInfo(name = "available_from")
    var availableFrom: String?,
    @ColumnInfo(name = "images")
    var images: List<String>?
)
