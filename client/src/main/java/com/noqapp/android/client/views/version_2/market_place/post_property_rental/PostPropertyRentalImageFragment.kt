package com.noqapp.android.client.views.version_2.market_place.post_property_rental

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noqapp.android.client.databinding.FragmentUploadPropertyRentalImagesBinding
import com.noqapp.android.client.views.fragments.BaseFragment

class PostPropertyRentalImageFragment : BaseFragment() {

    private lateinit var fragmentPostPropertyRentalUpload: FragmentUploadPropertyRentalImagesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPostPropertyRentalUpload =
            FragmentUploadPropertyRentalImagesBinding.inflate(inflater, container, false)
        return fragmentPostPropertyRentalUpload.root
    }

}