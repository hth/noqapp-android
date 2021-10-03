package com.noqapp.android.client.views.version_2.market_place.post_property_rental

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noqapp.android.client.databinding.FragmentPostPropertyRentalDetailsBinding
import com.noqapp.android.client.views.fragments.BaseFragment

class PostPropertyRentalDetailsFragment : BaseFragment() {

    private lateinit var fragmentPostPropertyRentalDetails: FragmentPostPropertyRentalDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPostPropertyRentalDetails =
            FragmentPostPropertyRentalDetailsBinding.inflate(inflater, container, false)
        return fragmentPostPropertyRentalDetails.root
    }
}