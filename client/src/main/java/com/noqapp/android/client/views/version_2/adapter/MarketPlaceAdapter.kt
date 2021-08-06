package com.noqapp.android.client.views.version_2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noqapp.android.client.databinding.ListItemMarketPlaceBinding

class MarketPlaceAdapter : RecyclerView.Adapter<MarketPlaceAdapter.MarketPlaceViewHolder>() {

    inner class MarketPlaceViewHolder(val listItemMarketPlaceBinding: ListItemMarketPlaceBinding) :
        RecyclerView.ViewHolder(listItemMarketPlaceBinding.root) {

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
    }

    override fun getItemCount(): Int {
        return 10
    }
}