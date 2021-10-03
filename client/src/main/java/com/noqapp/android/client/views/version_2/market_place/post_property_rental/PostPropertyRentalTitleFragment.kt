package com.noqapp.android.client.views.version_2.market_place.post_property_rental

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noqapp.android.client.databinding.FragmentPostPropertyRentalTitleBinding

class PostPropertyRentalTitleFragment {

    private lateinit var fragmentPostPropertyRentalTitle: FragmentPostPropertyRentalTitleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPostPropertyRentalTitle =
            FragmentPostPropertyRentalTitleBinding.inflate(inflater, container, false)
        return fragmentPostPropertyRentalTitle.root
    }
}