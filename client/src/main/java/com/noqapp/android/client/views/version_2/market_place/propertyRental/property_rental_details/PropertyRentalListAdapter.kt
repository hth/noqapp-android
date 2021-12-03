package com.noqapp.android.client.views.version_2.market_place.propertyRental.property_rental_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.client.BuildConfig
import com.noqapp.android.client.R
import com.noqapp.android.client.databinding.ItemLoadingBinding
import com.noqapp.android.client.databinding.ListItemPropertyRentalBinding
import com.noqapp.android.client.utils.AppUtils
import com.noqapp.android.client.utils.ImageUtils
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic
import com.squareup.picasso.Picasso
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

class PropertyRentalListAdapter(
    private val marketplaceList: MutableList<MarketplaceElastic>,
    val onClickListener: (MarketplaceElastic?, View) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false

    inner class MarketPlaceViewHolder(private val listItemPropertyRentalBinding: ListItemPropertyRentalBinding) :
        RecyclerView.ViewHolder(listItemPropertyRentalBinding.root) {

        private var marketPlaceElastic: MarketplaceElastic? = null

        init {
            listItemPropertyRentalBinding.btnCallAgent.setOnClickListener {
                onClickListener(marketplaceList[absoluteAdapterPosition], it)
            }

            listItemPropertyRentalBinding.btnViewDetails.setOnClickListener {
                onClickListener(marketplaceList[absoluteAdapterPosition], it)
            }
        }

        fun bind(marketplaceElastic: MarketplaceElastic) {
            this.marketPlaceElastic = marketPlaceElastic

            val radius = listItemPropertyRentalBinding.ivMarketPlace.resources.getDimension(R.dimen.corner_radius)
            val shapeAppearanceModel = listItemPropertyRentalBinding.ivMarketPlace.shapeAppearanceModel.toBuilder()
                .setAllCornerSizes(radius)
                .build()
            listItemPropertyRentalBinding.ivMarketPlace.shapeAppearanceModel = shapeAppearanceModel

            val nf: NumberFormat = NumberFormat.getCurrencyInstance(Locale("en", marketplaceElastic.countryShortName))
            listItemPropertyRentalBinding.tvPropertyTitle.text = marketplaceElastic.title
            listItemPropertyRentalBinding.tvPrice.text = nf.format(BigDecimal(marketplaceElastic.productPrice)) + "/-"
            listItemPropertyRentalBinding.tvRating.text = marketplaceElastic.rating
            listItemPropertyRentalBinding.rbMarketPlaceRating.setStar(marketplaceElastic.rating.toFloat())
            listItemPropertyRentalBinding.tvLocation.text = marketplaceElastic.townCity()
            listItemPropertyRentalBinding.tvPropertyViews.text = String.format(
                "%d %s",
                marketplaceElastic.viewCount,
                if (marketplaceElastic.viewCount > 1) {
                    listItemPropertyRentalBinding.tvPropertyViews.context.getString(R.string.txt_views)
                } else {
                    listItemPropertyRentalBinding.tvPropertyViews.context.getString(R.string.txt_view)
                }
            )

            if (marketplaceElastic.postImages.size > 0) {
                val displayImage = marketplaceElastic.postImages.iterator().next()
                val url = marketplaceElastic.businessType.name.lowercase() + "/" + marketplaceElastic.id + "/" + displayImage
                Picasso.get().load(AppUtils.getImageUrls(BuildConfig.MARKETPLACE_BUCKET, url))
                    .placeholder(ImageUtils.getThumbPlaceholder(listItemPropertyRentalBinding.ivMarketPlace.context))
                    .error(ImageUtils.getThumbErrorPlaceholder(listItemPropertyRentalBinding.ivMarketPlace.context))
                    .into(listItemPropertyRentalBinding.ivMarketPlace)
            }
        }
    }

    inner class ItemLoadingViewHolder(itemLoadingBinding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(itemLoadingBinding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOADING -> ItemLoadingViewHolder(
                ItemLoadingBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            VIEW_TYPE_NORMAL -> MarketPlaceViewHolder(
                ListItemPropertyRentalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> ItemLoadingViewHolder(
                ItemLoadingBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MarketPlaceViewHolder)
            holder.bind(marketplaceList[position])
    }

    override fun getItemCount(): Int {
        return marketplaceList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == marketplaceList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    fun clear() {
        marketplaceList.clear()
        notifyDataSetChanged()
    }

    fun addMarketPlaces(marketPlaceList: List<MarketplaceElastic>) {
        val size = marketplaceList.size
        this.marketplaceList.addAll(marketPlaceList)
        notifyItemRangeInserted(size, marketplaceList.size - 1)
        //notifyDataSetChanged()
    }

    fun getItem(position: Int): MarketplaceElastic? {
        return if ( position != -1 && position < marketplaceList.size)
            marketplaceList[position]
        else null
    }

    fun addLoading() {
        isLoaderVisible = true
        marketplaceList.add(MarketplaceElastic())
        notifyItemInserted(marketplaceList.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position: Int = marketplaceList.size - 1
        val item: MarketplaceElastic? = getItem(position)
        item?.let {
            marketplaceList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
