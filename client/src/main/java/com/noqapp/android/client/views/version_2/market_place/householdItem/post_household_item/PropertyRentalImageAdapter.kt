package com.noqapp.android.client.views.version_2.market_place.householdItem.post_household_item

import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.noqapp.android.client.databinding.ItemImagesBinding
import com.noqapp.android.client.utils.ImageUtils
import com.squareup.picasso.Picasso

class PropertyRentalImageAdapter(
    val propertyRentalImages: MutableList<String>,
    val onClicked: (String) -> Unit
) : RecyclerView.Adapter<PropertyRentalImageAdapter.PropertyRentalImagesViewHolder>() {
    var isClickEnable = true
    inner class PropertyRentalImagesViewHolder(private val itemImagesBinding: ItemImagesBinding) :
        RecyclerView.ViewHolder(itemImagesBinding.root) {

        fun bindImage(imagePath: String) {
            var thumbnail = ImageUtils.getThumbnail(
                itemImagesBinding.ivPropertyImage.context.contentResolver,
                imagePath
            )
            if (thumbnail != null) {
                itemImagesBinding.ivPropertyImage.setImageBitmap(thumbnail)
            } else {
                val THUMBSIZE = 64
                var thumb = ThumbnailUtils.extractThumbnail(
                    BitmapFactory.decodeFile(imagePath),
                    THUMBSIZE, THUMBSIZE
                )
                if (thumb != null) {
                    itemImagesBinding.ivPropertyImage.setImageBitmap(thumb)
                } else {
                    Glide
                        .with(itemImagesBinding.ivPropertyImage.context)
                        .load(imagePath)
                        .apply(RequestOptions().override(80, 80))
                        .into(itemImagesBinding.ivPropertyImage)
                }
            }


            if(isClickEnable){
                itemImagesBinding.ivDelete.visibility = android.view.View.VISIBLE
            }else{
                itemImagesBinding.ivDelete.visibility = android.view.View.INVISIBLE
            }
        }

        init {
            itemImagesBinding.ivDelete.setOnClickListener {
                if(isClickEnable) {
                    onClicked(propertyRentalImages[absoluteAdapterPosition])
                    propertyRentalImages.removeAt(absoluteAdapterPosition)
                    notifyDataSetChanged()
                }
            }

            itemImagesBinding.ivPropertyImage.setOnClickListener {
                onClicked(propertyRentalImages[absoluteAdapterPosition])
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
        notifyDataSetChanged()
    }

}