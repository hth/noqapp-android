package com.noqapp.android.client.views.version_2.market_place.post_property_rental

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.client.databinding.ItemImagesBinding
import com.squareup.picasso.Picasso

class PropertyRentalImageAdapter(
    val propertyRentalImages: MutableList<String>,
    val onClicked: (String) -> Unit
) : RecyclerView.Adapter<PropertyRentalImageAdapter.PropertyRentalImagesViewHolder>() {

    inner class PropertyRentalImagesViewHolder(private val itemImagesBinding: ItemImagesBinding) :
        RecyclerView.ViewHolder(itemImagesBinding.root) {

        fun bindImage(imagePath: String) {
            Picasso.get().load("file://$imagePath").into(itemImagesBinding.ivPropertyImage)
        }

        init {
            itemImagesBinding.ivDelete.setOnClickListener {
                onClicked(propertyRentalImages[absoluteAdapterPosition])
                propertyRentalImages.removeAt(absoluteAdapterPosition)
                notifyDataSetChanged()
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