package com.noqapp.android.client.views.version_2.market_place.property_rental_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.client.BuildConfig
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ListItemMarketPlaceBinding
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.utils.ImageUtils
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.squareup.picasso.Picasso
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

class PropertyRentalAdapter(
    private val marketplaceList: MutableList<MarketplaceElastic>,
    val onClickListener: (MarketplaceElastic?, View) -> Unit
) :
    RecyclerView.Adapter<PropertyRentalAdapter.MarketPlaceViewHolder>() {

    inner class MarketPlaceViewHolder(private val listItemMarketPlaceBinding: ListItemMarketPlaceBinding) :
        RecyclerView.ViewHolder(listItemMarketPlaceBinding.root) {

        private var marketPlaceElastic: MarketplaceElastic? = null

        init {
            listItemMarketPlaceBinding.btnCallAgent.setOnClickListener {
                onClickListener(marketplaceList[absoluteAdapterPosition], it)
            }

            listItemMarketPlaceBinding.btnViewDetails.setOnClickListener {
                onClickListener(marketplaceList[absoluteAdapterPosition], it)
            }
        }

        fun bind(marketplaceElastic: MarketplaceElastic) {
            this.marketPlaceElastic = marketPlaceElastic
            val nf: NumberFormat = NumberFormat.getCurrencyInstance(Locale("en", marketplaceElastic.countryShortName))
            listItemMarketPlaceBinding.tvPropertyTitle.text = marketplaceElastic.title
            listItemMarketPlaceBinding.tvPrice.text = nf.format(BigDecimal(marketplaceElastic.productPrice)) + "/-"
            listItemMarketPlaceBinding.tvRating.text = marketplaceElastic.rating
            listItemMarketPlaceBinding.tvLocation.text = marketplaceElastic.townCity()
            listItemMarketPlaceBinding.tvPropertyViews.text = String.format(
                "%d%s",
                marketplaceElastic.viewCount,
                if (marketplaceElastic.viewCount > 1) {
                    listItemMarketPlaceBinding.tvPropertyViews.context.getString(R.string.txt_views)
                } else {
                    listItemMarketPlaceBinding.tvPropertyViews.context.getString(R.string.txt_view)
                }
            )

            if (marketplaceElastic.postImages.size > 0) {
                val displayImage = marketplaceElastic.postImages.iterator().next()
                val url = marketplaceElastic.businessType.name.lowercase() + "/" + marketplaceElastic.id + "/" + displayImage
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
