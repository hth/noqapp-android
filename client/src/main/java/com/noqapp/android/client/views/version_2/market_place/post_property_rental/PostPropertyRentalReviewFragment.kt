package com.noqapp.android.client.views.version_2.market_place.post_property_rental

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noqapp.android.client.databinding.FragmentPostPropertyRentalReviewBinding
import com.noqapp.android.client.views.fragments.BaseFragment

class PostPropertyRentalReviewFragment : BaseFragment() {

    private lateinit var fragmentPostPropertyRentalReview: FragmentPostPropertyRentalReviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPostPropertyRentalReview =
            FragmentPostPropertyRentalReviewBinding.inflate(inflater, container, false)
        return fragmentPostPropertyRentalReview.root
    }

}