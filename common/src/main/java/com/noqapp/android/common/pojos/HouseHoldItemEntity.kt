package com.noqapp.android.common.pojos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noqapp.android.common.model.types.category.ItemConditionEnum

@Entity(tableName = "post_house_hold_item")
data class HouseHoldItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "item_condition_type")
    var itemConditionType: String,
    @ColumnInfo(name = "item_category")
    var householdItemCategory: String,
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
    @ColumnInfo(name = "images")
    var images: List<String>
)
