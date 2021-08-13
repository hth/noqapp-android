package com.noqapp.android.client.views.version_2.market_place

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.client.BuildConfig
import com.noqapp.android.client.databinding.ListItemMarketPlaceBinding
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.utils.ImageUtils
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.squareup.picasso.Picasso

class MarketPlaceAdapter(private val marketplaceList: MutableList<MarketplaceElastic>) :
    RecyclerView.Adapter<MarketPlaceAdapter.MarketPlaceViewHolder>() {

    inner class MarketPlaceViewHolder(private val listItemMarketPlaceBinding: ListItemMarketPlaceBinding) :
        RecyclerView.ViewHolder(listItemMarketPlaceBinding.root) {

        fun bind(marketplaceElastic: MarketplaceElastic) {
            listItemMarketPlaceBinding.tvPropertyTitle.text = marketplaceElastic.title
            listItemMarketPlaceBinding.tvPrice.text = marketplaceElastic.productPrice

            if (marketplaceElastic.postImages.size > 0) {
                val displayImage = marketplaceElastic.postImages.iterator().next().removePrefix("[").removeSuffix("]").split(", ")[0]
                val url = marketplaceElastic.businessType.name.lowercase() + "/" + marketplaceElastic.entityId + "/" + displayImage;
                Picasso.get().load(AppUtils.getImageUrls(BuildConfig.MARKETPLACE_BUCKET, url))
                    .placeholder(ImageUtils.getThumbPlaceholder(listItemMarketPlaceBinding.ivMarketPlace.context))
                    .error(ImageUtils.getThumbErrorPlaceholder(listItemMarketPlaceBinding.ivMarketPlace.context))
                    .into(listItemMarketPlaceBinding.ivMarketPlace)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketPlaceViewHolder {
        return MarketPlaceViewHolder(
            ListItemMarketPlaceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MarketPlaceViewHolder, position: Int) {
        holder.bind(marketplaceList[position])
    }

    override fun getItemCount(): Int {
        return marketplaceList.size
    }

    fun addMarketPlaces(marketPlaceList: List<MarketplaceElastic>) {
        this.marketplaceList.clear()
        this.marketplaceList.addAll(marketPlaceList)
        notifyDataSetChanged()
    }
}
