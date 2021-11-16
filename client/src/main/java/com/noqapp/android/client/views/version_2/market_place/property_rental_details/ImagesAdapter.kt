package com.noqapp.android.client.views.version_2.market_place.property_rental_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.client.databinding.LayoutItemImageBinding
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.utils.ImageUtils
import com.noqapp.android.client.BuildConfig
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.squareup.picasso.Picasso

class ImagesAdapter(val marketPlaceElastic: MarketplaceElastic) :
    RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val layoutItemImageBinding: LayoutItemImageBinding) :
        RecyclerView.ViewHolder(layoutItemImageBinding.root) {
        fun bindImage(imageUrl: String) {
            val url =
                marketPlaceElastic.businessType.name.lowercase() + "/" + marketPlaceElastic.id + "/" + imageUrl;
            Picasso.get().load(AppUtils.getImageUrls(BuildConfig.MARKETPLACE_BUCKET, url))
                .placeholder(ImageUtils.getThumbPlaceholder(layoutItemImageBinding.ivProperty.context))
                .error(ImageUtils.getThumbErrorPlaceholder(layoutItemImageBinding.ivProperty.context))
                .into(layoutItemImageBinding.ivProperty)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layoutItemImageBinding =
            LayoutItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(layoutItemImageBinding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bindImage(marketPlaceElastic.postImages.toMutableList()[position])
    }

    override fun getItemCount(): Int {
        return marketPlaceElastic.postImages.size
    }
}