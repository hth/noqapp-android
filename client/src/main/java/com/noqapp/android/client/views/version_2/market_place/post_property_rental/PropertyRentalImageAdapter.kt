package com.noqapp.android.client.views.version_2.market_place.post_property_rental

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.client.databinding.ItemImagesBinding
import kotlin.math.max
import kotlin.math.min

class PropertyRentalImageAdapter(
    val propertyRentalImages: MutableList<String>,
    val onImageClicked: (String) -> Unit
) : RecyclerView.Adapter<PropertyRentalImageAdapter.PropertyRentalImagesViewHolder>() {

    inner class PropertyRentalImagesViewHolder(private val itemImagesBinding: ItemImagesBinding) :
        RecyclerView.ViewHolder(itemImagesBinding.root) {

        fun bindImage(imagePath: String) {
            // Get the dimensions of the View
            val targetW: Int = itemImagesBinding.ivPropertyImage.width
            val targetH: Int = itemImagesBinding.ivPropertyImage.height

            val bmOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                BitmapFactory.decodeFile(imagePath, this)
                val photoW: Int = outWidth
                val photoH: Int = outHeight
                val scaleFactor: Int = max(1, min(photoW / targetW, photoH / targetH))
                inJustDecodeBounds = false
                inSampleSize = scaleFactor
                inPurgeable = true
            }
            BitmapFactory.decodeFile(imagePath, bmOptions)?.also { bitmap ->
                itemImagesBinding.ivPropertyImage.setImageBitmap(bitmap)
            }
        }

        init {
            itemImagesBinding.ivDelete.setOnClickListener {
                propertyRentalImages.removeAt(absoluteAdapterPosition)
                notifyDataSetChanged()
            }

            itemImagesBinding.ivPropertyImage.setOnClickListener {
                onImageClicked(propertyRentalImages[absoluteAdapterPosition])
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PropertyRentalImagesViewHolder {
        val itemImagesBinding =
            ItemImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PropertyRentalImagesViewHolder(itemImagesBinding)
    }

    override fun onBindViewHolder(holder: PropertyRentalImagesViewHolder, position: Int) {
        holder.bindImage(propertyRentalImages[position])
    }

    override fun getItemCount(): Int {
        return propertyRentalImages.size
    }

    fun addImage(imagePath: String) {
        propertyRentalImages.add(imagePath)
        notifyDataSetChanged()
    }

    fun getAllImages(): List<String> {
        return propertyRentalImages
    }

    fun addAllImages(images: List<String>?) {
        propertyRentalImages.clear()
        images?.let {
            propertyRentalImages.addAll(images)
        }
    }

}