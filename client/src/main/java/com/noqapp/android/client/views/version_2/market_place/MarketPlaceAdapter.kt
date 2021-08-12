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

class MarketPlaceAdapter(val marketPlaceList: MutableList<MarketplaceElastic>) :
    RecyclerView.Adapter<MarketPlaceAdapter.MarketPlaceViewHolder>() {

    inner class MarketPlaceViewHolder(private val listItemMarketPlaceBinding: ListItemMarketPlaceBinding) :
        RecyclerView.ViewHolder(listItemMarketPlaceBinding.root) {

        fun bind(marketplaceElastic: MarketplaceElastic) {
            listItemMarketPlaceBinding.tvPropertyTitle.text = marketplaceElastic.title
            listItemMarketPlaceBinding.tvPrice.text = marketplaceElastic.productPrice

            if (marketplaceElastic.postImages.size > 0) {
                val displayImage = marketplaceElastic.postImages.iterator().next()
                Picasso.get().load(AppUtils.getImageUrls(BuildConfig.SERVICE_BUCKET, displayImage))
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
        holder.bind(marketPlaceList[position])
    }

    override fun getItemCount(): Int {
        return marketPlaceList.size
    }

    fun addMarketPlaces(marketPlaceList: List<MarketplaceElastic>) {
        this.marketPlaceList.clear()
        this.marketPlaceList.addAll(marketPlaceList)
        notifyDataSetChanged()
    }
}