package com.noqapp.android.client.views.version_2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqapp.android.client.databinding.ActivityMarketPlaceBinding
import com.noqapp.android.client.views.version_2.adapter.MarketPlaceAdapter

class MarketPlaceFragment: Fragment() {

    lateinit var activityMarketPlaceBinding: ActivityMarketPlaceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activityMarketPlaceBinding = ActivityMarketPlaceBinding.inflate(inflater, container, false)
        return activityMarketPlaceBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityMarketPlaceBinding.rvMarketPlace.layoutManager = LinearLayoutManager(requireContext())
        activityMarketPlaceBinding.rvMarketPlace.adapter = MarketPlaceAdapter()
    }

}